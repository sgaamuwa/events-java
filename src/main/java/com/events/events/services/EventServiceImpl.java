package com.events.events.services;


import com.events.events.error.DuplicateCreationException;
import com.events.events.error.InvalidDateException;
import com.events.events.models.Event;
import com.events.events.models.User;
import com.events.events.repository.EventRepository;
import com.events.events.repository.UserRepository;
import com.events.events.error.NotFoundException;
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

        if(event.getParticipants().isEmpty()){
            event.setParticipants(users);
        }else if(Collections.disjoint(users, event.getParticipants())){
            List<User> newParticipantsList = event.getParticipants();
            newParticipantsList.addAll(users);
            event.setParticipants(newParticipantsList);
        }

        return eventRepository.save(event);
    }

    @Override
    public Event addSingleParticipantToEvent(int eventId, int userId) {

        User user = verifyAndReturnUser(userId);
        Event event = verifyAndReturnEvent(eventId);

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

    private Event verifyAndReturnEvent(int eventId){
        Optional<Event> event = eventRepository.findById(new Integer(eventId));
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
}
