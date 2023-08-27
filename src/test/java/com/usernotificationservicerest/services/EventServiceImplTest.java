package com.usernotificationservicerest.services;

import com.usernotificationservicerest.entites.Event;
import com.usernotificationservicerest.repositories.EventRepository;
import com.usernotificationservicerest.services.impl.EventServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepository;

    private Event event1;

    private Event event2;


    @BeforeEach
    void setUp() {
        event1 = new Event();
        event1.setId(1L);
        event1.setMessage("Event 1");
        event1.setTimestamp(LocalDateTime.now());

        event2 = new Event();
        event2.setId(2L);
        event2.setMessage("Event 2");
        event2.setTimestamp(LocalDateTime.now());

    }

    @Test
    void findEventById_ifEventExists_shouldReturnOptionalEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        Optional<Event> event = eventService.findEventById(1L);
        Assertions.assertEquals(event1, event.orElse(null));
    }

    @Test
    void findEventById_ifEventDoesNotExists_shouldReturnFalse() {
        when(eventRepository.findById(3L)).thenReturn(Optional.empty());
        Optional<Event> event = eventService.findEventById(3L);
        Assertions.assertFalse(event.isPresent());

    }

    @Test
    void findAll_shouldReturnListOfEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));
        List<Event> events = eventService.findAll();
        Assertions.assertEquals(2, events.size());
    }

    @Test
    void createEvent_validEvent_saveEvent() {
        when(eventRepository.save(event1)).thenReturn(event1);
        Event savedEvent = eventService.createEvent(event1);
        Assertions.assertEquals(event1, savedEvent);
    }

    @Test
    void createEvent_withNullFields_shouldThrowException() {
        when(eventRepository.save(ArgumentMatchers.any(Event.class))).thenThrow(IllegalArgumentException.class);
        Event event = new Event();
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(event));
    }

    @Test
    void updateEvent_withCorrectFields_shouldUpdateEvent() {
        event1.setMessage("Test Event Message");
        when(eventRepository.save(event1)).thenReturn(event1);
        when(eventRepository.existsById(event1.getId())).thenReturn(true);
        Event updatedEvent = eventService.updateEvent(event1);
        Assertions.assertEquals(event1, updatedEvent);
    }

    @Test
    void updateEvent_withNullFields_shouldThrowException() {
        event1.setMessage(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(event1));
    }

    @Test
    void deleteEvent_shouldDeleteEvent() {
        eventService.deleteEvent(event1);
        verify(eventRepository, times(1)).delete(event1);
    }


}
