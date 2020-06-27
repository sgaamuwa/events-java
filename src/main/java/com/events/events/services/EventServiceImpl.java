package com.events.events.services;


import com.events.events.error.*;
import com.events.events.models.*;
import com.events.events.repository.EventRepository;
import com.events.events.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private AWSS3Service awss3Service;

    @Override
    @Transactional
    public Event saveEvent(Event event) {
        LOGGER.info("Saving event");
        if(event.getStartTime().isBefore(LocalDateTime.now().plusDays(1))){
            LOGGER.info("Saving event failed");
            LOGGER.error("Event was not in the acceptable range ");
            throw new InvalidDateException("Event date must be at least a day from now");
        }
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event saveEvent(Event event, int userId) {
        User user = verifyAndReturnUser(userId);
        event.setCreator(user);
        checkUserDoesNotHaveEventAtTheSameTime(user, event.getStartTime());
        return saveEvent(event);
    }

    @Override
    @Transactional
    public Event updateEvent(int eventId, int userId, Event event) {
        LOGGER.info(String.format("Updating event id: %d by user id: %d", eventId, userId));
        User user = verifyAndReturnUser(userId);
        if(!eventRepository.existsById(eventId)){
            LOGGER.info("Updating event failed");
            LOGGER.error("Event id: " + eventId + " does not exist");
            throw new NotFoundException("Event with id: "+eventId+" not found");
        }
        // check if the event was created by the person trying to update it
        if(!eventRepository.findById(eventId).get().getCreator().equals(user)){
            LOGGER.info("Updating event failed");
            LOGGER.error("User does not have permission to update the event");
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }
        event.setEventId(eventId);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event uploadEventImage(int eventId, int userId, MultipartFile multipartFile) {
        User user = verifyAndReturnUser(userId);
        Event event = verifyAndReturnEvent(eventId);
        if(!event.getCreator().equals(user)){
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }
        // check if the event has an image already and delete it
        if(event.getImageKey() != null && !event.getImageKey().isEmpty()){
            awss3Service.deleteFile(event.getImageKey());
        }
        String fileName = awss3Service.uploadFile(multipartFile, "eventImages");
        event.setImageKey(fileName);
        return eventRepository.save(event);
    }

    @Override
    public ByteArrayResource downloadEventImage(int eventId, int userId){
        Event event = verifyAndReturnEvent(eventId);
        byte[] imageFile = awss3Service.downloadFile(event.getImageKey());
        return new ByteArrayResource(imageFile);
    }

    @Override
    @Transactional
    public void deleteEvent(int eventId, int userId) {
        LOGGER.info(String.format("Deleting event id: %d by user id: %d", eventId, userId));
        Event event = verifyAndReturnEvent(eventId);
        User user = verifyAndReturnUser(userId);
        if(!event.getCreator().equals(user)){
            LOGGER.info("Deleting event failed");
            LOGGER.error("User does not have permission to delete the event");
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }
        eventRepository.delete(event);
    }

    @Override
    @Transactional
    public Event getEventById(int eventId, int userId) {
        LOGGER.info("Get Event Id: "+eventId+" started");
        Event event = verifyAndReturnEvent(eventId);
        User user = verifyAndReturnUser(userId);
        if(!event.getCreator().equals(user)){
            LOGGER.info("Get Event Id: "+eventId+" failed");
            LOGGER.error("User not associated with the corresponding events");
            throw new NotFoundException("User with id: "+userId+ "doesn't have an event with id:"+event);
        }
        LOGGER.info("Get Event Id: "+eventId+" completed");
        return event;
    }

    @Override
    @Transactional
    public List<Event> getAllEvents() {
        LOGGER.info("Get All events started");
        List<Event> events = eventRepository.findAll();
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events");
        }
        return events;
    }

    @Override
    @Transactional
    public List<Event> getAllEventsForUser(String username){
        LOGGER.info(String.format("Getting all events for user: %s started", username));
        Optional<User> user = userRepository.findByUsername(username);
        if(!user.isPresent()){
            LOGGER.info("Deleting event failed");
            LOGGER.error("User with username: "+username+ " does not exist" );
            throw new UsernameNotFoundException("User with username: "+username+" does not exist");
        }
        //set a check to make sure that the friend is active
        Set<Friend> friends = user.get().getFriends();
        //get the user ids for all the friends
        List<Integer> friendsUserIds = friends.stream().filter(friend -> friend.isActive()).map(friend -> friend.getFriend().getUserId()).collect(Collectors.toList());
        // user should also get the events that they have created
        friendsUserIds.add(user.get().getUserId());
        List<Event> events = eventRepository.findAllEventsByFriends(friendsUserIds);
        //return events that are public or private ones where they have been invited
        events = events.stream().filter(event -> event.getEventPermission().equals(EventPermission.PUBLIC) || event.getInvitees().contains(user)).collect(Collectors.toList());
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events");
        }
        LOGGER.info("Get All Events for user completed");
        return events;
    }

    @Override
    @Transactional
    public List<Event> getAllEventsCreatedByUser(int userId) {
        LOGGER.info("Get All Events Created by User id: "+userId+" started");
        User user = verifyAndReturnUser(userId);
        if(user.getCreatedEvents().isEmpty()){
            throw new EmptyListException("There are no events for the user: "+ userId);
        }
        LOGGER.info("Get All Events Created by User id: "+userId+" completed");
        return user.getCreatedEvents();
    }

    @Override
    @Transactional
    public List<Event> getAllEventsUserIsAttending(int userId, String username) {
        LOGGER.info("Get All Events User id: "+userId+" is attending started");
        User user = checkUserIdBelongsToCurrentUser(userId, username);
        if(user.getAttending().isEmpty()){
            throw new EmptyListException("The user: "+userId+" is not attending any events");
        }
        LOGGER.info("Get All Events User id: "+userId+" is attending completed");
        return user.getAttending();
    }

    @Override
    public List<Event> getAllEventsUserIsInvitedTo(int userId, String username) {
        LOGGER.info("Get All Events User id: "+userId+" is invited to started");
        User user = checkUserIdBelongsToCurrentUser(userId, username);
        if(user.getInvites().isEmpty()){
            throw new EmptyListException("The user: "+userId+" has no invites");
        }
        LOGGER.info("Get All Events User id: "+userId+" is invited to completed");
        return user.getInvites();
    }

    @Override
    @Transactional
    public Event addMultipleParticipantsToEvent(int eventId, int[] participants) {
        LOGGER.info(String.format("Add participants: %s to event, event id: %d started", participants.toString(), eventId));
        // retrieve the users and event
        Integer[] userIds = Arrays.stream(participants).boxed().toArray(Integer[]::new);
        List<User> users = userRepository.findAllById(new ArrayList<>(Arrays.asList(userIds)));

        //check the returned objects are not empty
        if(users.isEmpty()){
            LOGGER.info("Add participants to event failed");
            LOGGER.error("Users with the supplied ids do not exist" );

            throw new NotFoundException("No users with the provided ids");
        }else if(!eventRepository.findById(eventId).isPresent()){
            LOGGER.info("Add participants to event failed");
            LOGGER.error("Event with the id: "+eventId+"not found" );

            throw new NotFoundException("Event with id: "+eventId+" not found");
        }

        Event event = eventRepository.findById(eventId).get();

        // check that the event date has not passed
        checkEventDateHasNotPassedAndEventIsOpen(event);
        for(User user : users){
            checkUserDoesNotHaveEventAtTheSameTime(user, event.getStartTime());
        }

        Set<User> newParticipantsList = event.getParticipants();
        newParticipantsList.addAll(users);
        event.setParticipants(newParticipantsList);

        LOGGER.info(String.format("Add participants: %s to event, event id: %d completed", participants.toString(), eventId));
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event addSingleParticipantToEvent(int eventId, int userId) {
        LOGGER.info(String.format("Add participant: %d to event, event id: %d started", userId, eventId));
        User user = verifyAndReturnUser(userId);
        Event event = verifyAndReturnEvent(eventId);
        // check that the event date has not passed
        checkEventDateHasNotPassedAndEventIsOpen(event);
        checkUserDoesNotHaveEventAtTheSameTime(user, event.getStartTime());
        // check that the user does not exist in the set
        Set<User> participants = event.getParticipants();
        if(!participants.contains(user)){
            participants.add(user);
            event.setParticipants(participants);
        }else{
            LOGGER.info("Add participants to event failed");
            LOGGER.error("User with id: "+userId+" is already a participant" );
            throw new DuplicateCreationException("User with id: "+userId+" is already a participant");
        }
        LOGGER.info(String.format("Add participant: %d to event, event id: %d completed", userId, eventId));
        return eventRepository.save(event);
    }

    @Override
    public Event addInviteesToEvent(int userId, int eventId, int[] inviteesList) {
        LOGGER.info("Adding invitees to event id: "+ eventId);
        User user = verifyAndReturnUser(userId);
        Event event = verifyAndReturnEvent(eventId);

        if(!event.getCreator().equals(user)){
            LOGGER.info("Adding invitees failed");
            LOGGER.error("User does not have permission to add invitees to the event");
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }
        List<User> invitees = userRepository.findAllById(Arrays.stream(inviteesList).boxed().collect(Collectors.toList()));
        // filter down the invitees to only those that follow the user
        invitees = invitees.stream().filter(
                invitee -> invitee.getFriends().stream().filter(
                        friend -> friend.isActive()).collect(Collectors.toSet()
                ).contains(new Friend(invitee, user))
        ).collect(Collectors.toList());
        // add them to the event invites
        Set<User> eventInvitees = event.getInvitees();
        eventInvitees.addAll(invitees);
        event.setInvitees(eventInvitees);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public List<Event> getEventsByDate(LocalDateTime date) {
        LOGGER.info(String.format("Get events by date: %s started", date.toString()));

        List<Event> events = eventRepository.findByStartTimeOrderByStartTimeAsc(date);
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events for the date: "+ date);
        }

        LOGGER.info(String.format("Get events by date: %s completed", date.toString()));
        return events;
    }

    @Override
    @Transactional
    public List<Event> getEventsBetweenDates(LocalDateTime dateFrom, LocalDateTime dateTo) {
        LOGGER.info(String.format("Get events between date: %s and date: %s started", dateFrom.toString(), dateTo.toString()));
        List<Event> events = eventRepository.findByStartTimeBetweenOrderByStartTimeAsc(dateFrom, dateTo);
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events for between the dates: "+ dateFrom + " and "+ dateTo);
        }
        LOGGER.info(String.format("Get events between date: %s and date: %s completed", dateFrom.toString(), dateTo.toString()));
        return events;
    }

    @Override
    @Transactional
    public List<Event> getEventsAfterDate(LocalDateTime date) {
        LOGGER.info(String.format("Get events after date: %s started", date.toString()));
        List<Event> events = eventRepository.findByStartTimeGreaterThanOrderByStartTimeAsc(date);
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events after the date: "+ date);
        }
        LOGGER.info(String.format("Get events after date: %s completed", date.toString()));
        return events;
    }

    @Override
    @Transactional
    public List<Event> getEventsBeforeDate(LocalDateTime date) {
        LOGGER.info(String.format("Get events before date: %s started", date.toString()));
        List<Event> events = eventRepository.findByStartTimeLessThanOrderByStartTimeAsc(date);
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events before the date: "+ date);
        }
        LOGGER.info(String.format("Get events by date: %s completed", date.toString()));
        return events;
    }

    @Override
    @Transactional
    public void cancelEvent(int eventId) {
        LOGGER.info(String.format("Cancel event, eventId: %d started", eventId));
        Event event = verifyAndReturnEvent(eventId);
        if(event.getEventStatus() == EventStatus.OPEN){
            event.setEventStatus(EventStatus.CANCELLED);
        }else{
            LOGGER.info("Add participants to event failed");
            LOGGER.error("Event is not open and can't be cancelled" );
            throw new DuplicateCreationException("Can only cancel OPEN events");
        }
        LOGGER.info(String.format("Cancel event, eventId: %d completed", eventId));
        eventRepository.save(event);
    }

    @Override
    public void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("sgaamuwa@gmail.com");
        message.setSubject("bla bla bla");
        message.setText("no no no");
        javaMailSender.send(message);
    }

    private Event verifyAndReturnEvent(int eventId){
        Optional<Event> event = eventRepository.findById(eventId);
        if(!event.isPresent()){
            LOGGER.error("Event Id not found: "+ eventId);
            throw new NotFoundException("Event with id: "+eventId+" not found");
        }
        return event.get();
    }

    private User verifyAndReturnUser(int userId){
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            LOGGER.error("User Id not found: "+ userId);
            throw new NotFoundException("User with id: "+userId+" not found");
        }
        return user.get();
    }

    private User checkUserIdBelongsToCurrentUser(int userId, String username){
        // check that the user id and username belong to the same user
        User user = verifyAndReturnUser(userId);
        if(!user.getUsername().equals(username)){
            LOGGER.error("User Id does not belong to current user");
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }
        return user;
    }

    private void checkEventDateHasNotPassedAndEventIsOpen(Event event){
        if(event.getEventStatus() != EventStatus.OPEN){
            LOGGER.error("Event status is not open, can't add participants");
            throw new InvalidDateException("The event is not open");
        }
        if(LocalDateTime.now().isAfter(event.getStartTime().minusDays(1))){
            LOGGER.error("Event date is passed, can't add participants");
            throw new InvalidDateException("The date to add participants is passed");
        }
    }

    private void checkUserDoesNotHaveEventAtTheSameTime(User user, LocalDateTime date){
        // check that they do not have an event created for that day
        for(Event event : user.getCreatedEvents()){
            if(event.getStartTime().equals(date) && event.getEventStatus() != EventStatus.CANCELLED){
                LOGGER.error("User trying to join event on conflicting day");
                throw new InvalidDateException("User has an event scheduled for this day");
            }
        }
        // check that they are not attending an event that day
        for(Event event : user.getAttending()){
            if(event.getStartTime().equals(date) && event.getEventStatus() != EventStatus.CANCELLED){
                LOGGER.error("User trying to join event on conflicting day");
                throw new InvalidDateException("User is already attending an event on this day");
            }
        }
    }
}
