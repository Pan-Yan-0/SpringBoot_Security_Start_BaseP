package com.py.controller;

import com.py.domain.*;
import com.py.service.UserService;
import com.py.utils.RedisCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    /*
     * 注入
     * */
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserService userService;

    @GetMapping("/get")
    public ResponseResult get(){
        ResponseResult result = userService.get();
        return result;
    }
    /**
     * @Author PY
     * @CreateTime 2023/11/21
     */
    @PreAuthorize("@ex.hasAuthority('normal:user:update')")
    @PostMapping("/updateAvatar")
    public ResponseResult updateAvatar(MultipartFile avatar){
        ResponseResult result = userService.updateAvatar(avatar);
        return result;
    }
    /**
     * @Author PY
     * @CreateTime 2023/11/21
     */
    @PreAuthorize("@ex.hasAuthority('normal:user:update')")
    @PostMapping("/updateEmail")
    public ResponseResult updateEmail(@RequestBody UpdateEmailBody updateEmailBody){
        ResponseResult result = userService.updateEmail(updateEmailBody);
        return result;
    }
    /**
     * @Author PY
     * @CreateTime 2023/11/21
     * @param user
     * @return
     */
    @PreAuthorize("@ex.hasAuthority('normal:user:update')")
    @PostMapping("/update")
    public ResponseResult update(@RequestBody User user){
        ResponseResult result = userService.update(user);
        return result;
    }

    /**
     * @Author PY
     * @CreateTime 2023/11/21
     */
    @PostMapping("/forgetPassword/checkCode")
    public ResponseResult checkCode(@RequestBody CheckCode checkCode){
        ResponseResult result = userService.checkCode(checkCode);
        return result;
    }
    /**
     * @Author PY
     * @CreateTime 2023/11/21
     */
    @PostMapping("/forgetPassword/updatePassword")
    public ResponseResult forgetPasswordChange(@RequestBody User user){
        ResponseResult result = userService.forgetPasswordChange(user);
        return result;
    }
    /**
     * @Author PY
     * @CreateTime 2023/11/20
     */

    @PreAuthorize("@ex.hasAuthority('normal:user:update')")
    @PostMapping("/upDatePassword")
    public ResponseResult UpDatePasswordOf(@RequestBody UpdatePassword updatePassword){
        ResponseResult result = userService.updatePassword(updatePassword);
        return result;
    }
    /**
     * @Author PY
     * @CreateTime 2023/11/19
     * @Use
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody RegistBody registBody) {

        User user = new User();
        user.setNickName(registBody.getNickName());
        user.setEmail(registBody.getEmail());
        user.setPassword(registBody.getPassword());
        ResponseResult result = userService.register(user, registBody.getCode());
        return result;
    }

    /**
     * @Author PY
     * @CreateTime 2023/11/15
     * @Use 发送验证码
     */
    @PostMapping("/SendMail")
    public ResponseResult sendMail(@RequestBody SendMailRequest sendMailUtils) {

        Object cacheObject = redisCache.getCacheObject(sendMailUtils.getToUserMail() + sendMailUtils.getWhere());
        //拒绝在上次验证码有效期内重复使用该接口
        if (!Objects.isNull(cacheObject)) {

            log.info("该接口似乎被有心人正在滥用中！！");

            return new ResponseResult(1000, "重复发送验证码操作,禁止调用该接口！");
        }
        ResponseResult result = userService.sendMail(sendMailUtils.getToUserMail(), sendMailUtils.getWhere());
        log.info("正在使用发送验证码接口操作中");
        return result;
    }

}
