package com.usernotificationservicerest.services;


import com.usernotificationservicerest.entites.Event;

public interface NotificationService {

    void handleEvent(Event event);
}
