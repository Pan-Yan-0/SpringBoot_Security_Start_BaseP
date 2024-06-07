package com.py.controller;

import com.py.domain.ResponseResult;
import com.py.domain.User;
import com.py.service.LoginServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginServcie loginServcie;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        //登录
        return loginServcie.login(user);
    }

    @RequestMapping("/user/logout")
    public ResponseResult logout(){
        return loginServcie.logout();
    }
}
