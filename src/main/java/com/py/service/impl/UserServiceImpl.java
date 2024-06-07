package com.py.service.impl;

import com.py.domain.*;
import com.py.domain.ResponseBody.UserInform;
import com.py.mapper.UserMapper;
import com.py.service.UserService;
import com.py.utils.AliOSSUtils;
import com.py.utils.JwtUtil;
import com.py.utils.RedisCache;
import com.py.utils.SendMailUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author PY
 * @CreateTime 2023/11/15
 * @UpdateTime 2023/11/19
 * @TestSendMail 2023/11/15
 * @TestRegister 2023/11/19
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    //设置邮箱发送信息
    public static final String MESSAGE_REGIST = "欢迎创建伪抖音账户，您的注册验证码为：";
    public static final String MESSAGE_UPDATEPASSWORD = "请您牢记你修改后的密码！您的注册验证码为：";
    public static final String MESSAGE_FORGETPASSWORD = "您正在使用忘记密码操作，请您牢记修改后的密码！您的注册验证码为：";
    public static final String MESSAGE_UPDATEEMAIL = "您正在修改您的注册邮箱！！您的注册验证码为：";
    public static final String MESSAGE_FINALLY_REMIND = "。验证码有效时间为五分钟，请尽快完成使用！假如不是本人操作，请立刻联系管理员！或者修改密码！！";

    //设置无效时间,单位：分钟
    private static final Integer INEFFECTIVE = 5;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AliOSSUtils aliOSSUtils;
    /**
     * 直接注入request来获取token
     */
    @Autowired
    private HttpServletRequest request;

    //生成验证码
    private String generateVerificationCode() {
        int codeLength = 6; // 验证码长度
        int minDigit = 0; // 最小数字值
        int maxDigit = 9; // 最大数字值

        StringBuilder codeBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < codeLength; i++) {
            int digit = random.nextInt(maxDigit - minDigit + 1) + minDigit;
            codeBuilder.append(digit);
        }

        return codeBuilder.toString();
    }

    /**
     * @param toUserMail
     * @param where
     * @return
     * @Use 发送验证码
     */
    @Override
    public ResponseResult sendMail(String toUserMail, String where) {
        log.info(toUserMail + "，正在对其发送验证码操作！！");
        try {
            //获取验证码
            String code = generateVerificationCode();
            //打印验证码
            System.out.println("验证码为：" + code);
            StringBuilder message = new StringBuilder();
            //加入此处的邮件信息
            if (where.equalsIgnoreCase("regist")) {
                message.append(MESSAGE_REGIST);
            } else if (where.equalsIgnoreCase("updateemail")) {
                message.append(MESSAGE_UPDATEEMAIL);
            } else if (where.equalsIgnoreCase("updatepassword")) {
                message.append(MESSAGE_UPDATEPASSWORD);
            } else if (where.equalsIgnoreCase("forgetpassword")) {
                message.append(MESSAGE_FORGETPASSWORD);
            } else {
                log.info("客户端是个sb写错参数！！");
            }


            //邮箱信息中加入验证码
            message.append(code);

            //加入提示信息
            message.append(MESSAGE_FINALLY_REMIND);

            //本处代码报错正常，仅仅只是因为网络问题会报错，故使用try抛出问题
            //发送邮箱验证码
            boolean success = SendMailUtils.sendMail(toUserMail, message.toString(), where + " Email");
            if (!success) {
                log.error("网络问题！！,发送验证码失败！！");
                return new ResponseResult(405, "发送验证码失败");
            }
            //SendMailUtils.sendMail(toUserMail,"你好，这是一封测试邮件，无需回复。","测试邮件");
            else {
                //此处使用redis来存储验证码，设置5分钟后自动清楚，并且不会保留在redis中
                redisCache.setCacheObject(toUserMail + where, code, 5, TimeUnit.MINUTES);
                return new ResponseResult(200, "成功发送验证码");

            }

        } catch (RuntimeException e) {
            log.error("发送验证码失败！！");
            log.error(String.valueOf(e));
            return new ResponseResult(404, "发送验证码失败");
        }
    }

    /**
     * @param user
     * @param code
     * @return
     * @Author PY
     * @Use 注册服务
     */
    @Override
    @Transactional
    public ResponseResult register(User user, String code) {
        try {
            String email = user.getEmail();
            log.info(email + "正在注册中");
            if (email == null) {
                return new ResponseResult<>(403, "邮箱为空！！");
            }
            User selectUser = userMapper.selectByEmail(email);
            if (!Objects.isNull(selectUser)) {
                return new ResponseResult<>(403, "该邮箱已经被注册！！");
            }
            String object = redisCache.getCacheObject(email + "regist");
            if (!object.equals(code)) {
                return new ResponseResult<>(403, "验证码错误或者已经过期！！");
            }
            /*
             * 判断必须参数是否为空
             * */
            if (user.getNickName() == null || user.getPassword() == null || user.getPhonenumber() == null) {
                log.error("客户端发送的参数有为空的！！！");
                return new ResponseResult<>(403, "邮箱或者密码为空！！！");
            }
            // 加密密码
            String encode = passwordEncoder.encode(user.getPassword());
            user.setPassword(encode);
            user.setCreateTime(new Date());
            user.setUserName(email);
            user.setUserType("1");
            user.setStatus("0");
            user.setSex("0");
            Long userId = userMapper.addUser(user);
            userId = user.getId();
            System.out.println(userId);
            if (userId != 0) {
                //给用户设置权限
                userMapper.addAuthentic(userId, 4L);
                log.info(email + "注册成功");
                return new ResponseResult<>(200, "注册成功！！");
            } else {
                log.error(email + "注册失败");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseResult<>(403, "太遗憾了，请联系管理员！");
            }

        } catch (RuntimeException e) {
            log.error("register的Impl错误！！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseResult<>(403,"注册失败！！请联系管理员！！");
        }

    }

    /**
     * @param updatePassword
     * @return
     * @Author PY
     * @Use 更改已经登录之后的用户的密码
     */
    @Override
    public ResponseResult updatePassword(UpdatePassword updatePassword) {
        // 从token中获取id值
        Long user_id = getUserId();
        //获得邮箱
        String email = userMapper.selectEmailById(user_id);
        log.info(email + ",正在修改密码");
        String redisCode;
        try {
            redisCode = redisCache.getCacheObject(email + "updatepassword");

        } catch (RuntimeException e) {
            log.error(email + "十分错误异常逻辑，前端并没有使用发送验证码接口就使用更改密码接口，而且还有token");
            return new ResponseResult<>(404, "没有发送验证码导致的异常错误！！");
        }
        if (!redisCode.equals(updatePassword.getCode())) {
            log.error(email + ",验证码错误！！！");
            return new ResponseResult<>(403, "验证码错误！！！！");
        }
        redisCache.deleteObject(email + "updatepassword");
        //加密新密码
        String encode = passwordEncoder.encode(updatePassword.getPassword());
        Integer ok = userMapper.updateUserPassWord(user_id, encode);
        if (ok != 0) {
            log.info(email + ",修改密码成功");
            return new ResponseResult<>(200, "修改密码成功，请重新登录！");
        } else {
            return new ResponseResult<>(403, "修改失败，请联系管理员处理！");
        }
    }

    /**
     * @param checkCode
     * @return
     * @Use 检查忘记密码的验证码是否正确
     */
    @Override
    public ResponseResult checkCode(CheckCode checkCode) {
        String userName = checkCode.getUserName();
        String code = checkCode.getCode();
        String rightCode;
        // 去redis查找是否有这个操作
        try {
            rightCode = redisCache.getCacheObject(userName + "forgetpassword");
        } catch (Exception e) {
            log.error(userName + "使用忘记密码操作但是没有发送验证码或者验证码错误");
            return new ResponseResult<>(403, "验证码已经过期或者没有发送验证码");
        }
        if (Objects.isNull(rightCode)) {
            log.error(userName + "使用忘记密码操作但是没有发送验证码或者验证码错误");
            return new ResponseResult<>(403, "验证码已经过期或者没有发送验证码");
        }
        if (!rightCode.equals(code)) {
            log.error(userName + "验证码错误！！");
            return new ResponseResult<>(403, "验证码错误！！！");
        }
        redisCache.deleteObject(userName + "forgetpassword");
        // 将这个验证码验证成功存储到redis中，下一步操作中将从redis中判断是否是已经验证验证码了
        redisCache.setCacheObject(userName + "forgetPasswordTrue", "1", 10, TimeUnit.MINUTES);
        return new ResponseResult<>(200, "请在十分钟内填写完新密码，否则请重新开始操作！！");
    }

    /**
     * @param user
     * @return
     */
    @Transactional
    @Override
    public ResponseResult forgetPasswordChange(User user) {
        String username = user.getUserName();
        String redisCode;
        try {
            redisCode = redisCache.getCacheObject(username + "forgetPasswordTrue");
        } catch (RuntimeException e) {
            log.error(username + "出现十分异常的操作！！");
            log.error(e.toString());
            return new ResponseResult<>(403, "异常操作！");
        }

        if (Objects.isNull(redisCode)) {
            log.error(username + "出现十分异常的操作！！");
            return new ResponseResult<>(403, "异常操作！");
        }
        redisCache.deleteObject(username + "forgetPasswordTrue");
        String password = user.getPassword();
        String encode = passwordEncoder.encode(password);
        Integer ok = userMapper.updatePasswordByUserName(username, encode);
        if (ok == 0) {
            log.error(username + "出现异常情况！！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseResult<>(403, "更改密码失败请联系管理员！");
        }

        return new ResponseResult<>(200, "修改密码成功，请重新登录！");

    }

    @Transactional
    @Override
    public ResponseResult update(User user) {
        Long user_id = getUserId();
        user.setId(user_id);
        Integer ok = userMapper.update(user);
        if (ok == 0) {
            log.error(user_id.toString() + "修改用户信息失败！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseResult<>(403, "更新失败！");
        }

        return new ResponseResult<>(200, "修改成功！");
    }

    @Transactional
    @Override
    public ResponseResult updateEmail(UpdateEmailBody updateEmailBody) {
        // 获取用户的id
        Long user_id = getUserId();
        // 获取邮箱去redis中查询验证码
        String email = userMapper.selectEmailById(user_id);
        String rightCode;
        try {
            rightCode = redisCache.getCacheObject(email + "updateemail");
        } catch (RuntimeException e) {
            log.error(email + "没有发送验证码操作！！");
            return new ResponseResult<>(403, "验证码没有发送！");
        }
        if (Objects.isNull(rightCode)) {
            log.error(email + "没有发送验证码操作！！");
            return new ResponseResult<>(403, "验证码没有发送！");
        }
        if (!rightCode.equals(updateEmailBody.getCode())) {
            log.error(email + "验证码错误！！");
            return new ResponseResult<>(403, "验证码错误！！");
        }
        redisCache.deleteObject(email + "updateemail");
        Integer ok = userMapper.updateEmail(user_id, updateEmailBody.getEmail());
        if (ok == 0) {
            log.error("写入数据库出现异常！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseResult<>(403, "修改邮箱失败，请联系管理员！");
        }
        Integer ok1 = userMapper.updateUserName(user_id, updateEmailBody.getEmail());
        if (ok1 == 0) {
            log.error("写入数据库出现异常！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseResult<>(403, "修改邮箱失败，请联系管理员！");
        }
        return new ResponseResult<>(200, "修改邮箱成功！");
    }

    @Override
    public ResponseResult updateAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            log.error("不符合，为空！！！");
        }
        Long user_id = getUserId();
        String url;
        try {
            url = aliOSSUtils.upload(file);
        } catch (IOException e) {
            log.error("上传图片失败！！");
            log.error(e.toString());
            return new ResponseResult<>(403, "上传失败！！");
        }
        if (Objects.isNull(url)) {
            log.error("上传图片失败！！");
            return new ResponseResult<>(403, "上传失败！！");
        }
        User user = new User();
        user.setId(user_id);
        user.setAvatar(url);
        Integer update;
        try {

            update = userMapper.update(user);
        } catch (RuntimeException e) {
            log.error(user_id + ",用户更新头像出现异常情况！！");
            return new ResponseResult<>(403, "出现异常情况请联系管理员");
        }
        if (update == 0) {
            log.error(user_id + ",用户更新头像出现异常情况！！");
            return new ResponseResult<>(403, "出现异常情况请联系管理员");
        }
        return new ResponseResult<>(200, "更新成功", url);
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

    @Transactional
    @Override
    public ResponseResult get() {
        Long userId = getUserId();
        User user = userMapper.selectBaseInformById(userId);
        if (Objects.isNull(user)) {
            log.error(userId + "获取用户信息从数据库中失败！");
            return new ResponseResult<>(403, "获取用户信息失败！！");
        }
        Integer fanNumById;
        try {
            fanNumById = userMapper.getFanNumById(userId);
        } catch (RuntimeException e) {
            log.error(userId + "获取用户信息从数据库中失败！");
            return new ResponseResult<>(403, "获取用户信息失败！！");
        }
        Integer subscribeNumById;
        try {
            subscribeNumById = userMapper.getSubscribeNumById(userId);
        } catch (RuntimeException e) {
            log.error(userId + "获取用户信息从数据库中失败！");
            return new ResponseResult<>(403, "获取用户信息失败！！");
        }
        UserInform userInform = new UserInform(user, fanNumById, subscribeNumById);

        return new ResponseResult<>(200, "获取用户信息成功！", userInform);
    }
}
