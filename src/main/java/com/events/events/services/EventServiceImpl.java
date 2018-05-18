package com.events.events.services;


import com.events.events.error.DuplicateCreationException;
import com.events.events.error.EmptyListException;
import com.events.events.error.InvalidDateException;
import com.events.events.models.Event;
import com.events.events.models.User;
import com.events.events.repository.EventRepository;
import com.events.events.repository.UserRepository;
import com.events.events.error.NotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Event saveEvent(Event event) {
        if(event.getDate().isBefore(LocalDate.now().plusDays(1))){
            throw new InvalidDateException("Event date must be at least a day from now");
        }
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event saveEvent(Event event, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(!user.isPresent()){
            throw new UsernameNotFoundException("User with username: "+username+" does not exist");
        }
        event.setCreator(user.get());
        return saveEvent(event);
    }

    @Override
    @Transactional
    public Event updateEvent(int eventId, Event event) {
        if(!eventRepository.existsById(eventId)){
            throw new NotFoundException("Event with id: "+eventId+" not found");
        }
        event.setId(eventId);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public void deleteEvent(int eventId) {
        Event event = verifyAndReturnEvent(eventId);
        eventRepository.delete(event);
    }

    @Override
    @Transactional
    public Event getEventById(int eventId) {
        return verifyAndReturnEvent(eventId);
    }

    @Override
    @Transactional
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events");
        }
        return events;
    }

    @Override
    @Transactional
    public Event addMultipleParticipantsToEvent(int eventId, int[] participants) {
        // retrieve the users and event
        Integer[] userIds = Arrays.stream(participants).boxed().toArray(Integer[]::new);
        List<User> users = userRepository.findAllById(new ArrayList<>(Arrays.asList(userIds)));

        //check the returned objects are not empty
        if(users.isEmpty()){
            throw new NotFoundException("No users with the provided ids");
        }else if(!eventRepository.findById(eventId).isPresent()){
            throw new NotFoundException("Event with id: "+eventId+" not found");
        }

        Event event = eventRepository.findById(eventId).get();

        // check that the event date has not passed
        if(!checkEventDateHasNotPassed(event.getDate())){
            throw new InvalidDateException("The date to add participants is passed");
        }

        List<User> newParticipantsList = (List<User>) CollectionUtils.union(event.getParticipants(), users);
        event.setParticipants(newParticipantsList);

        return eventRepository.save(event);
    }

    @Override
    @Transactional
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
    @Transactional
    public List<Event> getEventsByDate(LocalDate date) {
        List<Event> events = eventRepository.findByDate(date);
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events for the date: "+ date);
        }
        return events;
    }

    @Override
    @Transactional
    public List<Event> getEventsBetweenDates(LocalDate dateFrom, LocalDate dateTo) {
        List<Event> events = eventRepository.getEventsBetweenDates(dateFrom, dateTo);
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events for between the dates: "+ dateFrom + " and "+ dateTo);
        }
        return events;
    }

    @Override
    public List<Event> getEventsAfterDate(LocalDate date) {
        List<Event> events = eventRepository.getEventsAfterDate(date);
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events after the date: "+ date);
        }
        return events;
    }

    @Override
    public List<Event> getEventsBeforeDate(LocalDate date) {
        List<Event> events = eventRepository.getEventsBeforeDate(date);
        if(events.isEmpty()){
            throw new EmptyListException("There are no available events before the date: "+ date);
        }
        return events;
    }

    private Event verifyAndReturnEvent(int eventId){
        Optional<Event> event = eventRepository.findById(eventId);
        if(!event.isPresent()){
            throw new NotFoundException("Event with id: "+eventId+" not found");
        }
        return event.get();
    }

    private User verifyAndReturnUser(int userId){
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            throw new NotFoundException("User with id: "+userId+" not found");
        }
        return user.get();
    }

    private boolean checkEventDateHasNotPassed(LocalDate eventDate){
        if(LocalDate.now().isAfter(eventDate.minusDays(1))){
            return false;
        }
        return true;
    }
}
