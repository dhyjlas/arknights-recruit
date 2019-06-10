package com.kaworu.arknights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class ArknightsRecruitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArknightsRecruitApplication.class, args);
    }

}
