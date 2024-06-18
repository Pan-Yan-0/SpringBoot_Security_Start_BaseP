package com.py.service;

import com.py.domain.ResponseResult;
import com.py.domain.User;
import org.springframework.stereotype.Service;

@Service
public interface LoginServcie {
    ResponseResult login(User user);

    ResponseResult logout();

}
