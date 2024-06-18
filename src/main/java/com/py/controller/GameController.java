package com.py.controller;

import com.py.domain.RequestBody.AddHistory;
import com.py.domain.ResponseResult;
import com.py.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {
    @Autowired
    private GameService gameService;
    @PreAuthorize("@ex.hasAuthority('normal:game:get')")
    @GetMapping("/getGame")
    public ResponseResult getGame(int difficult){
        ResponseResult result = gameService.getGame(difficult);
        return result;
    }
    @PreAuthorize("@ex.hasAuthority('normal:game:multiple')")
    @GetMapping("/multiple")
    public DeferredResult<ResponseResult> multiple(Integer difficult) {
        DeferredResult<ResponseResult> deferredResult = new DeferredResult<>();

        CompletableFuture<ResponseResult> multipleFuture = gameService.multiple(difficult);
        multipleFuture.thenAccept(deferredResult::setResult)
                .exceptionally(ex -> {
                    deferredResult.setErrorResult(new ResponseResult<>(500, "匹配异常"));
                    return null;
                });

        return deferredResult;
    }
    @PreAuthorize("@ex.hasAuthority('normal:game:history')")
    @PostMapping("/addHistory")
    public ResponseResult addHistory(AddHistory addHistory){
        ResponseResult result = gameService.addhistory(addHistory);
        return result;
    }
    @PreAuthorize("@ex.hasAuthority('normal:game:history')")
    @GetMapping("/getHistory")
    public ResponseResult getHistory(){
        ResponseResult result = gameService.getHistory();
        return result;
    }

    @PostMapping("/cancelMatch")
    public ResponseResult cancelMatch() {
        ResponseResult result = gameService.cancelMatch();
        return result;
    }
}
