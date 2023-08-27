package com.usernotificationservicerest.services;

import com.usernotificationservicerest.entites.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {

    Optional<Event> findEventById(Long id);

    List<Event> findAll();

    Event createEvent(Event event);

    Event updateEvent(Event event);

    void deleteEvent(Event event);
}
