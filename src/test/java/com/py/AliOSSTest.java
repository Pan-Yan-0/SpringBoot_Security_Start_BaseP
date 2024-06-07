package com.py;

import com.py.utils.AliOSSUtils;
import com.py.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class AliOSSTest {
    @Autowired
    private AliOSSUtils aliOSSUtils;
    @Test
    public void testUpload(){
        String upload;
        try {
            upload = aliOSSUtils.upload("C:\\Users\\。\\Desktop\\SpringSecurity\\源码\\SanGeng_Security_Project_Right\\SanGeng_Security_Project\\Short_Video_App\\src\\test\\resources\\未命名文件-导出.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(upload);

    }
}
