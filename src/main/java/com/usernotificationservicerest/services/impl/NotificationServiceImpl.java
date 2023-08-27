package com.usernotificationservicerest.services.impl;

import com.usernotificationservicerest.entites.Event;
import com.usernotificationservicerest.entites.InformationPeriod;
import com.usernotificationservicerest.entites.User;
import com.usernotificationservicerest.repositories.UserRepository;
import com.usernotificationservicerest.services.NotificationService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final Logger logger;

    private final DateTimeFormatter formatter;

    private final UserRepository userRepository;

    private final Map<User, List<Event>> delayedNotifications = new HashMap<>();

    public NotificationServiceImpl(Logger logger, DateTimeFormatter formatter, UserRepository userRepository) {
        this.logger = logger;
        this.formatter = formatter;
        this.userRepository = userRepository;
    }

    public void sendNotificationToUser(Event event, User user) {
        String timestamp = LocalDateTime.now().format(formatter);
        String notificationMessage = String.format("%s Пользователю %s отправлено сообщение с текстом: %s",
                timestamp, user.getFullName(), event.getMessage());
        logger.info(notificationMessage);

    }

    public boolean shouldNotifyNow(User user, Event event) {
        LocalDateTime eventTime = event.getTimestamp();
        for (InformationPeriod period : user.getInformationPeriods()) {
            if (eventTime.getDayOfWeek().equals(period.getDayOfWeek()) &&
                    period.getStartTime().isBefore(LocalTime.now()) &&
                    period.getEndTime().isAfter(LocalTime.now())) {
                return true;
            }
        }
        return false;
    }

    public void saveNotificationForLater(User user, Event event) {
        delayedNotifications.putIfAbsent(user, new ArrayList<>());
        delayedNotifications.get(user).add(event);
    }

    @Scheduled(fixedRate = 10000)
    public void processDelayedNotifications() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Event> userDelayedNotifications = delayedNotifications.get(user);
            if (userDelayedNotifications != null && !userDelayedNotifications.isEmpty()) {
                Iterator<Event> iterator = userDelayedNotifications.iterator();
                while (iterator.hasNext()) {
                    Event event = iterator.next();
                    if (shouldNotifyNow(user, event)) {
                        sendNotificationToUser(event, user);
                        iterator.remove();
                    }
                }
            }
        }
    }

    @Override
    public void handleEvent(Event event) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (shouldNotifyNow(user, event)) {
                sendNotificationToUser(event, user);
            } else {
                saveNotificationForLater(user, event);
            }
        }
    }
}
