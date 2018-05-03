package com.events.events.services;

import com.events.events.models.Event;
import com.events.events.models.User;

import java.time.LocalDate;
import java.util.List;

public interface EventService {

    Event saveEvent(Event event);
    Event updateEvent(Event event);
    void deleteEvent(int eventId);
    Event addMultipleParticipantsToEvent(int eventId, int[] participants);
    Event addSingleParticipantToEvent(int eventId, int userId);
    Event getEventsByDate(LocalDate date);
    List<Event> getEventsBetweenDates(LocalDate dateFrom, LocalDate dateTo);

}
