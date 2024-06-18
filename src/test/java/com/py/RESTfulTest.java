package com.py;

import com.py.service.KakuroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RESTfulTest {
    @Autowired
    private KakuroService kakuroService;
    @Test
    public void test1(){
        String string = kakuroService.generateKakuro(10);
        System.out.println(string);
    }
}
