package com.events.events.services;

import com.events.events.models.Event;
import com.events.events.models.User;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    /**
     * This is a method to save a new event in the database
     * @param event
     * @return Event
     */
    Event saveEvent(Event event);

    /**
     * This method to save a new event when a user is supplied
     * @param event
     * @param username
     * @return
     */
    Event saveEvent(Event event, String username);

    /**
     * This is a method to update an already existent event
     * @param event
     * @return Event
     */
    Event updateEvent(int eventId, Event event);

    /**
     * This is a method that deletes and event based on the id
     * @param eventId
     */
    void deleteEvent(int eventId);

    /**
     * This is a method to return one event based on the id
     * @param eventId
     * @return Event
     */
    Event getEventById(int eventId);

    /**
     * This is a method to return all the events
     * @return List of Events
     */
    List<Event> getAllEvents();

    /**
     * This is a method that adds a number of users to an event
     * @param eventId
     * @param participants
     * @return Event
     */
    Event addMultipleParticipantsToEvent(int eventId, int[] participants);

    /**
     * This is a method that adds a single user to the event
     * @param eventId
     * @param userId
     * @return Event
     */
    Event addSingleParticipantToEvent(int eventId, int userId);

    /**
     * This is a method that returns all events happening on a given date
     * @param date
     * @return List
     */
    List<Event> getEventsByDate(LocalDate date);
    List<Event> getEventsBetweenDates(LocalDate dateFrom, LocalDate dateTo);

}
