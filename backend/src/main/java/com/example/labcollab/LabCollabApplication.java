package com.example.labcollab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.labcollab.mapper")
public class LabCollabApplication {
    public static void main(String[] args) {
        SpringApplication.run(LabCollabApplication.class, args);
    }
}
