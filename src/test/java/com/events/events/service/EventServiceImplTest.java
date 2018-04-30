package com.events.events.service;

import com.events.events.error.DuplicateCreationException;
import com.events.events.error.NotFoundException;
import com.events.events.models.Event;
import com.events.events.models.User;
import com.events.events.repository.EventRepository;
import com.events.events.repository.UserRepository;
import com.events.events.services.EventService;
import com.events.events.services.EventServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
public class EventServiceImplTest {

    @TestConfiguration
    static class EventServiceImplTestContextConfiguration {

        @Bean
        public EventService eventService() {
            return new EventServiceImpl();
        }

    }

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setup(){
        User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123");
        User male = new User("michael", "male", "mmale", "pass123");
        Event cinemaMovie = new Event("Movie", "Acacia Mall", new Date(), samuel);
        Event beach = new Event("Beach", "Entebbe", new Date(), samuel);
        beach.setParticipants(new ArrayList<>(Arrays.asList(male)));
        Mockito.when(userRepository.findById(new Integer(1))).thenReturn(Optional.of(male));
        Mockito.when(eventRepository.findById(new Integer(1))).thenReturn(Optional.of(cinemaMovie));
        Mockito.when(eventRepository.findById(new Integer(2))).thenReturn(Optional.of(beach));
        Mockito.when(userRepository.findById(new Integer(23))).thenReturn(Optional.empty());
        Mockito.when(eventRepository.findById(new Integer(12))).thenReturn(Optional.empty());
    }

    @Test
    public void testAddingParticipantToEvent(){
        eventService.addSingleParticipantToEvent(1, 1);
        ArgumentCaptor<Event> argument = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(eventRepository, Mockito.atLeastOnce()).save(argument.capture());
        Event savedEvent = argument.getValue();

        //check that the saved event is the one expected
        Assert.assertEquals(savedEvent.getTitle(), "Movie");
        Assert.assertEquals(savedEvent.getCreator().getFirstName(), "samuel");
        Assert.assertEquals(savedEvent.getParticipants().size(), 1);
        Assert.assertEquals(savedEvent.getParticipants().get(0).getFirstName(), "michael");
    }

    @Test(expected = NotFoundException.class)
    public void testAddingParticipantToEventWithWrongUser(){
        eventService.addSingleParticipantToEvent(1, 23);
        // ensure the save method is never called
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test(expected = NotFoundException.class)
    public void testAddingParticipantToEventWithWrongEvent(){
        eventService.addSingleParticipantToEvent(12, 1);
        // ensure the save method is never called
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test(expected = DuplicateCreationException.class)
    public void testAddingParticipantToEventWithParticipant(){
        eventService.addSingleParticipantToEvent(2, 1);
        // ensure the save method is never called
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }
}
