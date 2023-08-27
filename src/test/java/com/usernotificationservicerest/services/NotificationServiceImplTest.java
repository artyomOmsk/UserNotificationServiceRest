package com.usernotificationservicerest.services;

import com.usernotificationservicerest.entites.Event;
import com.usernotificationservicerest.entites.InformationPeriod;
import com.usernotificationservicerest.entites.User;
import com.usernotificationservicerest.repositories.UserRepository;
import com.usernotificationservicerest.services.impl.NotificationServiceImpl;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mock
    private UserRepository userRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;

    private Event event;

    private InformationPeriod period;

    @BeforeEach
    public void setUp() {
        period = new InformationPeriod();
        period.setId(1L);
        period.setDayOfWeek(LocalDateTime.now().getDayOfWeek());
        period.setStartTime(LocalTime.now().minusHours(1));
        period.setEndTime(LocalTime.now().plusHours(1));

        user = new User();
        user.setId(1L);
        user.setFullName("Ivanov Ivan");
        user.setInformationPeriods(Collections.singletonList(period));

        event = new Event();
        event.setId(1L);
        event.setMessage("Test message");
        event.setTimestamp(LocalDateTime.now());
    }

    @Test
    void shouldNotifyNow_true() {
        boolean result = notificationService.shouldNotifyNow(user, event);
        Assertions.assertTrue(result);
    }

    @Test
    void shouldNotifyNow_false() {
        period.setStartTime(LocalTime.now().plusHours(1));
        period.setEndTime(LocalTime.now().plusHours(2));
        boolean result = notificationService.shouldNotifyNow(user, event);
        Assertions.assertFalse(result);
    }

    @Test
    void handleEvent_sendsImmediateNotification() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        notificationService.handleEvent(event);
        verify(logger, times(1)).info(anyString());
    }

    @Test
    void handleEvent_savesNotificationForLater() {
        Event delayedEvent = new Event();
        delayedEvent.setId(2L);
        delayedEvent.setMessage("Delayed event");
        delayedEvent.setTimestamp(LocalDateTime.now());
        period.setStartTime(LocalTime.now().plusHours(1));
        period.setEndTime(LocalTime.now().plusHours(2));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        notificationService.handleEvent(delayedEvent);
        verify(logger, never()).info(anyString());
    }

    @Test
    void processDelayedNotifications_sendsDelayedNotification() {
        Event delayedEvent = new Event();
        delayedEvent.setId(2L);
        delayedEvent.setMessage("Delayed event");
        delayedEvent.setTimestamp(LocalDateTime.now());
        period.setStartTime(LocalTime.now().plusHours(1));
        period.setEndTime(LocalTime.now().plusHours(2));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        notificationService.saveNotificationForLater(user, delayedEvent);
        notificationService.processDelayedNotifications();
        verify(logger, never()).info(anyString());
        period.setStartTime(LocalTime.now().minusHours(1));
        notificationService.processDelayedNotifications();
        verify(logger, times(1)).info(anyString());
    }
}
