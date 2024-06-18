package com.py.service.impl;

import com.alibaba.fastjson.JSON;
import com.py.domain.Game;
import com.py.domain.RequestBody.AddHistory;
import com.py.domain.ResponseBody.MultipleResponBody;
import com.py.domain.ResponseResult;
import com.py.mapper.GameMapper;
import com.py.mapper.UserMapper;
import com.py.service.GameService;
import com.py.service.KakuroService;
import com.py.utils.JwtUtil;
import com.py.utils.RedisCache;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GameServiceImpl implements GameService {
    private static final ArrayList<Integer> choice;

    static {
        choice = new ArrayList<>();
        choice.add(4);
        choice.add(6);
        choice.add(8);
        choice.add(10);
        choice.add(12);
    }

    private final ConcurrentHashMap<Long, CompletableFuture<ResponseResult>> matchTasks = new ConcurrentHashMap<>();
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private KakuroService kakuroService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private GameMapper gameMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public ResponseResult getGame(int difficult) {
        log.info("本次的难度：" + difficult);
        try {
            String string = kakuroService.generateKakuro(choice.get(difficult));
            Game game = JSON.parseObject(string, Game.class);
            game.setDifficulty(difficult);
            long count = mongoTemplate.count(new Query(), Game.class);
            game.setId((int) count);
            Game inserted = mongoTemplate.insert(game);
            System.out.println(inserted);
            return new ResponseResult<>(200, "good", game);
        } catch (Exception exception) {
            return new ResponseResult<>(403, "出现异常错误");
        }
    }
    /*
    * @TODO 大概没有测试，应该可以通过了
    * */

    @Async
    public CompletableFuture<ResponseResult> multiple(Integer difficult) {
        Long userId = getUserId();
        String key = difficult + ":waiting";

        CompletableFuture<ResponseResult> future = new CompletableFuture<>();
        matchTasks.put(userId, future);

        // 尝试获取当前的匹配用户
        String currentWaitingUser = redisCache.getCacheObject(key);

        if (currentWaitingUser == null) {
            // 生成新的游戏实例
            Game game = JSON.parseObject(kakuroService.generateKakuro(choice.get(difficult)), Game.class);
            game.setDifficulty(difficult);
            long count = mongoTemplate.count(new Query(), Game.class);
            game.setId((int) count);
            mongoTemplate.insert(game);
            String value = count + ":" + userId;
            // 没有人在匹配，将当前用户设置为等待匹配状态
            redisCache.setCacheObject(key, value, 60, TimeUnit.MINUTES);
            waitForMatch(difficult, userId, future,game);
        } else {
            // 有人正在匹配，进行匹配
            String game_oppose = redisCache.getCacheObject(key);
            String[] split = game_oppose.split(":");
            String gameId = split[0];
            String opposeId = split[1];
            redisCache.deleteObject(key);
            String success = difficult + ":success";
            redisCache.setCacheObject(success,userId.toString(),60,TimeUnit.MINUTES);
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("Id").is((int)Integer.valueOf(gameId)));
            List<Game> games = mongoTemplate.find(query, Game.class);
            if (games.isEmpty()){

            }
            MultipleResponBody multipleResponBody = new MultipleResponBody();
            multipleResponBody.setGame(games.get(0));
            multipleResponBody.setOpposeId(Long.valueOf(opposeId));
            multipleResponBody.setOpposeName(userMapper.getUserNameString(multipleResponBody.getOpposeId()));
            multipleResponBody.setAvatar(userMapper.getAvatarString(multipleResponBody.getOpposeId()));
            // 完成匹配任务
            future.complete(new ResponseResult<>(200, "匹配成功", multipleResponBody));
            matchTasks.remove(userId);
        }

        return future;
    }

    private void waitForMatch(Integer difficult, Long userId, CompletableFuture<ResponseResult> future,Game game) {

        String key = difficult + ":waiting";
        int waitTime = 60; // 等待总时间60秒
        int interval = 1;  // 每次检查间隔2秒

        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < waitTime / interval; i++) {
                try {
                    // 每隔2秒检查一次匹配状态
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException e) {
                    future.complete(new ResponseResult<>(500, "匹配被中断"));
                    matchTasks.remove(userId);
                    return;
                }
                System.out.println("已经进入");
                String success = difficult + ":success";
                String currentWaitingUser = redisCache.getCacheObject(success);
                redisCache.deleteObject(success);
                if (currentWaitingUser != null && !currentWaitingUser.equals(userId.toString())) {
                    // 有其他玩家加入匹配，进行匹配
                    redisCache.deleteObject(key);
                    // 生成返回的对战游戏类
                    MultipleResponBody multipleResponBody = new MultipleResponBody();
                    multipleResponBody.setGame(game);
                    Long oppose = Long.valueOf(currentWaitingUser);
                    multipleResponBody.setOpposeId(oppose);
                    multipleResponBody.setAvatar(userMapper.getAvatarString(oppose));
                    multipleResponBody.setOpposeName(userMapper.getUserNameString(multipleResponBody.getOpposeId()));
                    // 完成匹配任务
                    future.complete(new ResponseResult<>(200, "匹配成功", multipleResponBody));
                    matchTasks.remove(userId);
                    return;
                }
            }

            // 超时未匹配成功，返回超时消息
            redisCache.deleteObject(key);
            future.complete(new ResponseResult<>(408, "匹配超时"));
            matchTasks.remove(userId);
        });
    }

    //
    @Override
    public ResponseResult addhistory(AddHistory addHistory) {
        try {
            gameMapper.addHistory(addHistory);
        } catch (RuntimeException e) {
            System.out.println(e);
            return new ResponseResult<>(403, "出现异常数据");
        }
        return new ResponseResult<>(200, "添加成功");
    }
    /*
    * @TODO 此处返回出错
    * */
    @Override
    public ResponseResult getHistory() {
        List<AddHistory> addHistoryList;
        Long userId;
        try {
            userId = getUserId();
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseResult<>(403, "异常错误");
        }
        try {
            addHistoryList = gameMapper.getHistory(Math.toIntExact(userId));
        } catch (RuntimeException e) {
            System.out.println(e);
            return new ResponseResult<>(403, "数据库异常错误");
        }
        return new ResponseResult<>(200, "success", addHistoryList);
    }

    @Override
    public ResponseResult cancelMatch() {
        Long userId;
        try {
            userId = getUserId();
        } catch (Exception e) {
            return new ResponseResult(403, "异常错误");
        }
        CompletableFuture<ResponseResult> future = matchTasks.get(userId);
        if (future != null) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                return new ResponseResult<>(200, "匹配已取消");
            } else {
                return new ResponseResult<>(500, "取消匹配失败");
            }
        } else {
            return new ResponseResult<>(404, "没有进行中的匹配");
        }
    }

    @NotNull
    private Long getUserId() {
        // 获取用户的id
        String token = request.getHeader("token");
        //解析token
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        Long user_id;
        try {
            user_id = Long.parseLong(userid);
        } catch (NumberFormatException e) {
            throw e;
        }
        return user_id;
    }
}
