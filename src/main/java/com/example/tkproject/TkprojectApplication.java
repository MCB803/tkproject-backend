package com.example.tkproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TkprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TkprojectApplication.class, args);
    }

}
