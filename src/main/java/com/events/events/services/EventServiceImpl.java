package com.events.events.services;


import com.events.events.models.Event;
import com.events.events.models.User;
import com.events.events.repository.EventRepository;
import com.events.events.repository.UserRepository;
import com.events.events.error.NotFoundException;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        return null;
    }

    @Override
    public void deleteEvent(Event event) {

    }

    @Override
    public List<Event> listEventsByUser(User user) {
        return null;
    }

    @Override
    public List<Event> listEventsUserIsAttending(User user) {
        return null;
    }

    @Override
    public Event addMultipleParticipantsToEvent(int eventId, int[] participants) {
        List<Integer> userIds = new ArrayList<>();
        for(int participant : participants){
            userIds.add(participant);
        }
        List<User> users = userRepository.findAllById(userIds);
        Event event = eventRepository.findById(new Integer(eventId)).get();
        List<User> usersAttending = event.getParticipants();

        usersAttending.addAll(usersAttending);
        event.setParticipants(usersAttending);
        return eventRepository.save(event);
    }

    @Override
    public Event addSingleParticipantToEvent(int eventId, int userId) {
        User user = userRepository.findById(new Integer(userId)).get();
        Event event = eventRepository.findById(new Integer(eventId)).get();

        // check that the returned objects are not empty
        if(!userRepository.findById(new Integer(userId)).isPresent()){
            throw new NotFoundException("User with id: "+userId+" not found");
        }else if(!eventRepository.findById(new Integer(eventId)).isPresent()){
            throw new NotFoundException("Event with id: "+eventId+" not found");
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
            System.out.println("done");
        }
        return eventRepository.save(event);
    }
}
