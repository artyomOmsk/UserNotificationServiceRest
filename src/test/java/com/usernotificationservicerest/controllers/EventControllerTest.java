package com.usernotificationservicerest.controllers;

import com.usernotificationservicerest.entites.Event;
import com.usernotificationservicerest.services.EventService;
import com.usernotificationservicerest.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EventController eventController;

    private Event event1;

    private Event event2;

    @BeforeEach
    public void setUp() {
        event1 = new Event();
        event1.setId(1L);
        event1.setMessage("event 1");
        event1.setTimestamp(LocalDateTime.now());

        event2 = new Event();
        event2.setId(2L);
        event2.setMessage("event 2");
        event2.setTimestamp(LocalDateTime.now());
    }

    @Test
    void getEventById_success() {
        when(eventService.findEventById(1L)).thenReturn(Optional.of(event1));

        ResponseEntity<Event> responseEntity = eventController.getEventById(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(event1, responseEntity.getBody());
    }

    @Test
    void getEventById_notFound() {
        when(eventService.findEventById(3L)).thenReturn(Optional.empty());

        ResponseEntity<Event> responseEntity = eventController.getEventById(3L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void getEvents_success() {
        when(eventService.findAll()).thenReturn(Arrays.asList(event1, event2));

        ResponseEntity<List<Event>> responseEntity = eventController.getEvents();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    void createEvent_success() {
        Event inputEvent = new Event();
        inputEvent.setMessage("event 3");
        inputEvent.setTimestamp(LocalDateTime.now());
        Event createdEvent = new Event();
        createdEvent.setId(3L);
        createdEvent.setMessage("event 3");
        createdEvent.setTimestamp(LocalDateTime.now());

        when(eventService.createEvent(inputEvent)).thenReturn(createdEvent);

        ResponseEntity<Event> responseEntity = eventController.createEvent(inputEvent);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(createdEvent, responseEntity.getBody());
        verify(notificationService, times(1)).handleEvent(createdEvent);
    }

    @Test
    void updateEvent_success() {
        Event updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setMessage("event1 updated");
        updatedEvent.setTimestamp(LocalDateTime.now());

        when(eventService.updateEvent(updatedEvent)).thenReturn(updatedEvent);

        ResponseEntity<Event> responseEntity = eventController.updateEvent(1L, updatedEvent);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedEvent, responseEntity.getBody());
    }

    @Test
    void updateEvent_illegalArgument() {
        Event updatedEvent = new Event();
        updatedEvent.setId(3L);
        updatedEvent.setMessage("event1 updated");
        updatedEvent.setTimestamp(LocalDateTime.now());

        when(eventService.updateEvent(updatedEvent)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<Event> responseEntity = eventController.updateEvent(3L, updatedEvent);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void updateEvent_mismatchedId() {
        Event updatedEvent = new Event();
        updatedEvent.setId(2L);
        updatedEvent.setMessage("event1 updated");
        updatedEvent.setTimestamp(LocalDateTime.now());

        ResponseEntity<Event> responseEntity = eventController.updateEvent(1L, updatedEvent);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void deleteEvent_success() {
        when(eventService.findEventById(1L)).thenReturn(Optional.of(event1));

        ResponseEntity<Void> responseEntity = eventController.deleteEvent(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(eventService, times(1)).deleteEvent(event1);
    }

    @Test
    void deleteEvent_notFound() {
        when(eventService.findEventById(3L)).thenReturn(Optional.empty());

        ResponseEntity<Void> responseEntity = eventController.deleteEvent(3L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
