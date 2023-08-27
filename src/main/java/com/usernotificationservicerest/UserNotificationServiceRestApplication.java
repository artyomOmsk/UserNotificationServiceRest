package com.usernotificationservicerest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UserNotificationServiceRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserNotificationServiceRestApplication.class, args);
    }

}
