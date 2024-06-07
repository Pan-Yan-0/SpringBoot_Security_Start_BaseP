package com.py.service.impl;

import com.py.domain.LoginUser;
import com.py.domain.User;
import com.py.mapper.MenuMapper;
import com.py.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //查询用户信息

        User user = userMapper.selectByUser(username);
        //如果没有查询到用户就抛出异常
        if(Objects.isNull(user)){
            throw new RuntimeException("用户名或者密码错误");
        }

//        List<String> list = new ArrayList<>(Arrays.asList("test","admin"));
        List<String> list = menuMapper.selectPermsByUserId(user.getId());
        //把数据封装成UserDetails返回
        LoginUser loginUser = new LoginUser(user, list);
       int a = 1;
        return loginUser;
    }
}
