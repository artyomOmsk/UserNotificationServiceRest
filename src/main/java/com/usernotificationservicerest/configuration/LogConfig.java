package com.usernotificationservicerest.configuration;

import com.usernotificationservicerest.services.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class LogConfig {

    @Bean
    public Logger getLoggerForEvents() {
        return LogManager.getLogger(NotificationService.class);
    }

    @Bean
    public DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    }


}
