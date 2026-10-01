package com.smartnotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartnotifyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartnotifyApplication.class, args);
    }
}