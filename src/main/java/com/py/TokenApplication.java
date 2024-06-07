package com.py;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.py.mapper")
public class TokenApplication {
    public static void main(String[] args) {
        SpringApplication.run(TokenApplication.class,args);
    }
}
