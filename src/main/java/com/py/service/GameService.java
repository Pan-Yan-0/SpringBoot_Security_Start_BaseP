package com.py.service;

import com.py.domain.RequestBody.AddHistory;
import com.py.domain.ResponseResult;
import io.netty.util.concurrent.Future;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface GameService {
    ResponseResult getGame(int difficult);

     CompletableFuture<ResponseResult> multiple(Integer difficult);

    ResponseResult addhistory(AddHistory addHistory);

    ResponseResult getHistory();

    ResponseResult cancelMatch();

    ResponseResult getGameById(Integer gameId);
}
