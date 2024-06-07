package com.py;

import com.py.controller.UserController;
import com.py.domain.CheckCode;
import com.py.domain.ResponseResult;
import com.py.domain.User;
import com.py.mapper.UserMapper;
import com.py.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Test
    public void sendEmail(){
        ResponseResult result = userService.sendMail("2587719445@qq.com", "forgetpassword");
        System.out.println(result.getCode());
        System.out.println(result.getMsg());
    }
    @Test
    public void testDate(){
        Date date = new Date();
        System.out.println(date);
    }

    @Test
    public void testAdd(){

        User user = new User();
        user.setNickName("py");
        user.setPassword("1234");
        user.setPhonenumber("123123213");
        user.setEmail("2587719445@qq.com");
        userService.register(user,"759126");
    }

    @Test
    public void upDatePassword(){

    }

    @Test
    public void forgetPassword(){
        ResponseResult result = userService.checkCode(new CheckCode("2587719445@qq.com", "634176"));
        System.out.println(result.getMsg());
        System.out.println(result.getCode());
    }
    @Test
    public void forgetPasswordChange(){
        User user = new User();
        user.setUserName("2587719445@qq.com");
        user.setPassword("1234");
        ResponseResult result = userService.forgetPasswordChange(user);
        System.out.println(result.getMsg());
        System.out.println(result.getCode());

    }

    @Test
    public void updateFunction(){
        User user = new User();
        user.setId(8L);
        user.setStatus("1");
        user.setNickName("ikun");
        user.setPhonenumber("19994602340");
        user.setSex("1");
        user.setAvatar("12312312");
        user.setUserType("1");
        user.setUpdateBy(1L);
        user.setUpdateTime(new Date());
        user.setDelFlag(1);
        Integer update = userMapper.update(user);
        if (update == 0){
            System.out.println("不行");
        }else {
            System.out.println("ok");
        }
    }
}
