package com.code.probationwork;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//springboot启动类
@SpringBootApplication
@EnableScheduling
@MapperScan("com.code.probationwork.mapper")
@EnableAsync
public class ProbationworkApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProbationworkApplication.class, args);
    }

}
