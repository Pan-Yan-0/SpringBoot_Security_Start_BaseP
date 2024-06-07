package com.py.service;

import com.py.controller.UserController;
import com.py.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
public interface UserService {
    ResponseResult sendMail(String toUserMail,String where);

    ResponseResult register(User user,String code);

    ResponseResult updatePassword(UpdatePassword updataPassword);

    ResponseResult checkCode(CheckCode checkCode);

    ResponseResult forgetPasswordChange(User user);

    ResponseResult update(User user);

    ResponseResult updateEmail(UpdateEmailBody updateEmailBody);

    ResponseResult updateAvatar(MultipartFile file);

    ResponseResult get();
}
