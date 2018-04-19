package com.events.events.services;

import com.events.events.models.Event;
import com.events.events.models.User;

import java.util.List;

public interface EventService {

    Event saveEvent(Event event);
    Event updateEvent(Event event);
    void deleteEvent(Event event);
    List<Event> listEventsByUser(User user);
    List<Event> listEventsUserIsAttending(User user);
    Event addMultipleParticipantsToEvent(int eventId, int[] participants);
    Event addSingleParticipantToEvent(int eventId, int userId);

}
