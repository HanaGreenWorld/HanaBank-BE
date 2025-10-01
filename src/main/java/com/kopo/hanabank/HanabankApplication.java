package com.kopo.hanabank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HanabankApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanabankApplication.class, args);
    }
}