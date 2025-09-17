package com.code.probationwork;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.code.probationwork.mapper")
public class ProbationworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProbationworkApplication.class, args);
    }

}
