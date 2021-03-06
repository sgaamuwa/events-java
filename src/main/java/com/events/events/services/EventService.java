package com.events.events.services;

import com.events.events.models.Event;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * @param userId
     * @return
     */
    Event saveEvent(Event event, int userId);

    /**
     * This is a method to update an already existent event
     * @param event
     * @return Event
     */
    Event updateEvent(int eventId, int userId, Event event);

    /**
     * This is a method to upload an image for a given event
     * @param eventId
     * @param multipartFile
     * @param userId
     * @return
     */
    Event uploadEventImage(int eventId, int userId, MultipartFile multipartFile);

    /**
     * This method should return the image associated with an event
     * @param eventId
     * @param userId
     * @return
     */
    ByteArrayResource downloadEventImage(int eventId, int userId);

    /**
     * This is a method that deletes and event based on the id
     * @param eventId
     */
    void deleteEvent(int eventId, int userId);

    /**
     * This is a method to return one event based on the id
     * @param eventId
     * @param userId
     * @return Event
     */
    Event getEventById(int eventId, int userId);

    /**
     * This is a method to return all the events
     * @return List of Events
     */
    List<Event> getAllEvents();

    /**
     * This method returns all events that the user should be able to see
     * From friends and their own events
     * @param username
     * @return
     */
    List<Event> getAllEventsForUser(String username);

    /**
     * This method returns all events that are created by the user
     * @param userId
     * @return
     */
    List<Event> getAllEventsCreatedByUser(int userId);

    /**
     * This method returns all the events that a user has rsvp to attend
     * @param userId
     * @return
     */
    @PreAuthorize("#username == authentication.principal.username")
    List<Event> getAllEventsUserIsAttending(int userId, String username);

    /**
     * This method returns all the events that a user has been invited to
     * @param userId
     * @return
     */
    @PreAuthorize("#username == authentication.principal.username")
    List<Event> getAllEventsUserIsInvitedTo(int userId, String username);

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
     * This method adds invitees to an event
     * @param userId
     * @param eventId
     * @param invitees
     * @return
     */
    Event addInviteesToEvent(int userId, int eventId, int[] invitees);

    /**
     * This method deletes an invitee from an event
     * @param userId
     * @param eventId
     * @param inviteeId
     * @return
     */
    @PreAuthorize("#username == authentication.principal.username")
    Event deleteInviteeFromEvent(int userId, int eventId, int inviteeId, String username);

    /**
     * This is a method that returns all events happening on a given date
     * @param date
     * @return List
     */
    List<Event> getEventsByDate(LocalDateTime date);
    List<Event> getEventsBetweenDates(LocalDateTime dateFrom, LocalDateTime dateTo);
    List<Event> getEventsAfterDate(LocalDateTime date);
    List<Event> getEventsBeforeDate(LocalDateTime date);

    /**
     * This is a method to cancel an event by the user
     * @param eventId
     */
    void cancelEvent(int eventId);
    void sendEmail();

}
