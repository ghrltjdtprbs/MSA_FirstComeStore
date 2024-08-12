package com.firstcomestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FirstComeStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstComeStoreApplication.class, args);
    }

}
