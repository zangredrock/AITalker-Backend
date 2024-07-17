package com.rocky.coordinatorservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@MapperScan("com.rocky.coordinatorservice.mapper")
@SpringBootApplication
public class CoordinatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoordinatorServiceApplication.class, args);
    }

}
