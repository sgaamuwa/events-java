package com.events.events.services;


import com.events.events.error.DuplicateCreationException;
import com.events.events.error.InvalidDateException;
import com.events.events.models.Event;
import com.events.events.models.User;
import com.events.events.repository.EventRepository;
import com.events.events.repository.UserRepository;
import com.events.events.error.NotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Event saveEvent(Event event) {
        if(event.getDate().isBefore(LocalDate.now().plusDays(1))){
            throw new InvalidDateException("Event date must be at least a day from now");
        }
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        return null;
    }

    @Override
    public void deleteEvent(int eventId) {
        Event event = verifyAndReturnEvent(eventId);
        eventRepository.delete(event);
    }

    @Override
    public Event addMultipleParticipantsToEvent(int eventId, int[] participants) {
        // retrieve the users and event
        Integer[] userIds = Arrays.stream(participants).boxed().toArray(Integer[]::new);
        List<User> users = userRepository.findAllById(new ArrayList<>(Arrays.asList(userIds)));

        //check the returned objects are not empty
        if(users.isEmpty()){
            throw new NotFoundException("No users with the provided ids");
        }else if(!eventRepository.findById(new Integer(eventId)).isPresent()){
            throw new NotFoundException("Event with id: "+eventId+" not found");
        }

        Event event = eventRepository.findById(new Integer(eventId)).get();

        // check that the event date has not passed
        if(!checkEventDateHasNotPassed(event.getDate())){
            throw new InvalidDateException("The date to add participants is passed");
        }

        List<User> newParticipantsList = (List<User>) CollectionUtils.union(event.getParticipants(), users);
        event.setParticipants(newParticipantsList);

        return eventRepository.save(event);
    }

    @Override
    public Event addSingleParticipantToEvent(int eventId, int userId) {

        User user = verifyAndReturnUser(userId);
        Event event = verifyAndReturnEvent(eventId);
        // check that the event date has not passed
        if(!checkEventDateHasNotPassed(event.getDate())){
            throw new InvalidDateException("The date to add participants is passed");
        }
        // check that the user does not exist in the
        if(event.getParticipants().isEmpty()){
            List<User> participants = event.getParticipants();
            participants.add(user);
            event.setParticipants(participants);
        }
        else if(!event.getParticipants().stream().anyMatch(participant -> participant.equals(user))){
            List<User> participants = event.getParticipants();
            participants.add(user);
            event.setParticipants(participants);
        }else{
            throw new DuplicateCreationException("User with id: "+userId+" is already a participant");
        }
        return eventRepository.save(event);
    }

    @Override
    public Event getEventsByDate(LocalDate date) {
        return null;
    }

    @Override
    public List<Event> getEventsBetweenDates(LocalDate dateFrom, LocalDate dateTo) {
        return null;
    }

    private Event verifyAndReturnEvent(int eventId){
        Optional<Event> event = eventRepository.findById(new Integer(eventId));
        if(!event.isPresent()){
            throw new NotFoundException("Event with id: "+eventId+" not found");
        }
        return event.get();
    }

    private User verifyAndReturnUser(int userId){
        if(!userRepository.existsById(userId)){
            throw new NotFoundException("User with id: "+userId+" not found");
        }

        return userRepository.findById(userId).get();
    }

    private boolean checkEventDateHasNotPassed(LocalDate eventDate){
        if(LocalDate.now().isAfter(eventDate.minusDays(1))){
            return false;
        }
        return true;
    }
}
