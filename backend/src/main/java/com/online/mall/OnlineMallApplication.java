package com.online.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.online.mall.mapper")
@EnableAspectJAutoProxy
public class OnlineMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineMallApplication.class, args);
    }
}