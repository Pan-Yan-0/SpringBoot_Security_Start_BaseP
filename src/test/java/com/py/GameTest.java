package com.py;

import com.py.domain.Game;
import com.py.domain.RequestBody.AddHistory;
import com.py.domain.ResponseResult;
import com.py.mapper.GameMapper;
import com.py.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class GameTest {
    @Autowired
    private GameService gameService;
    @Autowired
    private GameMapper gameMapper;
    @Test
    public void getTest(){
        ResponseResult game = gameService.getGame(2);
        System.out.println(game.getData());
    }
    @Test
    public void history(){
        AddHistory addHistory = new AddHistory();
        addHistory.setDate(new Date());
        addHistory.setGameId(2);
        addHistory.setUserId(3);
        addHistory.setTime(50);
        gameMapper.addHistory(addHistory);
        List<AddHistory> history = gameMapper.getHistory(3);
        for (AddHistory addHistory1 : history) {
            System.out.println(addHistory1);
        }
    }
    @Test
    public void getHistory(){

    }
    @Test
    public void multiple(){
        CompletableFuture<ResponseResult> multiple = gameService.multiple(4);
        System.out.println(multiple);
    }
    @Test
    public void multiple1(){
        CompletableFuture<ResponseResult> multiple = gameService.multiple(4);
        System.out.println(multiple);
    }

}
