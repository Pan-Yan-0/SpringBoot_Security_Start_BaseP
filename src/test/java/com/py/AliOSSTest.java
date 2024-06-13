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
            upload = aliOSSUtils.upload("C:\\Users\\。\\Desktop\\IKUN_JAVA\\src\\test\\resources\\未命名文件-导出.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(upload);

    }
}
