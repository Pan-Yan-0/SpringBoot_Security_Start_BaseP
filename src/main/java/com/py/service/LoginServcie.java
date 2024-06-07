package com.py.service;

import com.py.domain.ResponseResult;
import com.py.domain.User;

public interface LoginServcie {
    ResponseResult login(User user);

    ResponseResult logout();

}
