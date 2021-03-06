package com.events.events.unit.service;

import com.events.events.error.*;
import com.events.events.models.Event;
import com.events.events.models.EventStatus;
import com.events.events.models.Friend;
import com.events.events.models.User;
import com.events.events.repository.EventRepository;
import com.events.events.repository.UserRepository;
import com.events.events.services.AWSS3Service;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

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
    private JavaMailSender javaMailSender;

    @MockBean
    private AWSS3Service awss3Service;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private UserRepository userRepository;

    final private User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123", "sgaamuwa@email.com");
    final private User male = new User("michael", "male", "mmale", "pass123", "mmale@email.com");
    final private Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
    final private Event beach = new Event("Beach", "Entebbe", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
    final private User bruce = new User("bruce", "bigirwenkya", "bbigirwenkya", "pass123", "bbigirwenkya@email.com");


    @Before
    public void setup(){
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(male));
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(bruce));
        Mockito.when(eventRepository.findById(1)).thenReturn(Optional.of(cinemaMovie));
        Mockito.when(eventRepository.findById(2)).thenReturn(Optional.of(beach));
        Mockito.when(userRepository.findById(23)).thenReturn(Optional.empty());
        Mockito.when(eventRepository.findById(12)).thenReturn(Optional.empty());
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
        Assert.assertTrue(savedEvent.getParticipants().contains(male));
    }

    @Test
    public void testAddingParticipantToEventWithWrongUser(){

        Throwable exception = assertThrows(NotFoundException.class, () -> eventService.addSingleParticipantToEvent(1, 23));
        Assert.assertEquals("User with id: 23 not found", exception.getMessage());
        // ensure the save method is never called
        Mockito.verify(eventRepository, Mockito.never()).save(any(Event.class));
    }

    @Test
    public void testAddingParticipantToEventWithWrongEvent(){

        Throwable exception = assertThrows(NotFoundException.class, () -> eventService.addSingleParticipantToEvent(12, 1));
        Assert.assertEquals("Event with id: 12 not found", exception.getMessage());
        // ensure the save method is never called
        Mockito.verify(eventRepository, Mockito.never()).save(any(Event.class));
    }

    @Test
    public void testAddingParticipantToEventWithParticipant(){
        beach.setParticipants(new HashSet<>(Arrays.asList(male)));
        Throwable exception = assertThrows(DuplicateCreationException.class, () -> {
            eventService.addSingleParticipantToEvent(2, 1);
        });
        Assert.assertEquals("User with id: 1 is already a participant", exception.getMessage());
        // ensure the save method is never called
        Mockito.verify(eventRepository, Mockito.never()).save(any(Event.class));
    }

    @Test
    public void testSaveEventWithOldDate(){
        User user = Mockito.mock(User.class);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusDays(1), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusDays(1).plusHours(2), user);

        Throwable exception = assertThrows(InvalidDateException.class, () -> {
            eventService.saveEvent(cinemaMovie);
        });
        Assert.assertEquals("Event date must be at least a day from now", exception.getMessage());

        Mockito.verify(eventRepository, Mockito.never()).save(any(Event.class));
    }

    @Test
    public void testSaveEventWithCurrentDate(){
        User user = Mockito.mock(User.class);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2), user);

        Throwable exception = assertThrows(InvalidDateException.class, () -> {
            eventService.saveEvent(cinemaMovie);
        });
        Assert.assertEquals("Event date must be at least a day from now", exception.getMessage());

        Mockito.verify(eventRepository, Mockito.never()).save(any(Event.class));
    }

    @Test
    public void testSaveEventWithFutureDate(){
        User user = Mockito.mock(User.class);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2).plusHours(2), user);
        ArgumentCaptor<Event> argument = ArgumentCaptor.forClass(Event.class);
        eventService.saveEvent(cinemaMovie);
        Mockito.verify(eventRepository, Mockito.atMost(1)).save(argument.capture());
        Assert.assertEquals(argument.getValue(), cinemaMovie);
    }

    @Test
    public void testDeleteEventWithValidId(){
        Event event = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2).plusHours(2), male);
        Mockito.when(eventRepository.findById(new Integer(1))).thenReturn(Optional.of(event));
        eventService.deleteEvent(1, 1);
        Mockito.verify(eventRepository, Mockito.atMost(1)).delete(event);
    }

    @Test
    public void testDeleteEventWithInvalidEventId(){

        Throwable exception = assertThrows(NotFoundException.class, () -> {
            eventService.deleteEvent(12, 1);
        });
        Assert.assertEquals("Event with id: 12 not found", exception.getMessage());
        Mockito.verify(eventRepository, Mockito.never()).delete(any(Event.class));
    }

    @Test
    public void testDeleteEventWithInvalidUserID(){
        Event event = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2).plusHours(2), bruce);
        Mockito.when(eventRepository.findById(new Integer(1))).thenReturn(Optional.of(event));
        Throwable exception = assertThrows(AuthorisationException.class, () -> {
            eventService.deleteEvent(1, 1);
        });
        Mockito.verify(eventRepository, Mockito.never()).delete(any(Event.class));
    }

    @Test
    public void testAddMultipleParticipantsAllValidUserIds(){
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);
        User user4 = Mockito.mock(User.class);
        List<User> users = new ArrayList<>(Arrays.asList(user1, user2, user3, user4));

        int[] participants = new int[] {1, 2, 3, 4};
        Integer[] userIds = Arrays.stream(participants).boxed().toArray(Integer[]::new);
        Mockito.when(userRepository.findAllById(Arrays.asList(userIds))).thenReturn(users);
        Mockito.when(eventRepository.save(cinemaMovie)).thenReturn(cinemaMovie);

        Event returnedEvent = eventService.addMultipleParticipantsToEvent(1, participants);

        Assert.assertEquals(returnedEvent.getParticipants().size(), 4);

    }

    @Test
    public void testAddMultipleParticipantsWithOneAlreadyAttending(){
        beach.setParticipants(new HashSet<>(Arrays.asList(male)));
        User user1 = Mockito.mock(User.class);
        List<User> users = new ArrayList<>(Arrays.asList(male, user1));

        int[] participants = new int[]{1, 2};
        Integer[] userIds = Arrays.stream(participants).boxed().toArray(Integer[]::new);

        Mockito.when(userRepository.findAllById(Arrays.asList(userIds))).thenReturn(users);
        Mockito.when(eventRepository.save(beach)).thenReturn(beach);

        Assert.assertEquals(beach.getParticipants().size(), 1);

        Event returnedEvent = eventService.addMultipleParticipantsToEvent(2, participants);

        Assert.assertEquals(returnedEvent, beach);
        Assert.assertEquals(returnedEvent.getParticipants().size(), 2);
    }

    @Test
    public void testCanCancelEvent(){
        Assert.assertEquals(cinemaMovie.getEventStatus(), EventStatus.OPEN);
        eventService.cancelEvent(1);
        ArgumentCaptor<Event> argument = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(eventRepository, Mockito.atMost(1)).save(argument.capture());
        Assert.assertEquals(argument.getValue().getEventStatus(), EventStatus.CANCELLED);
        Assert.assertEquals(cinemaMovie.getEventStatus(), EventStatus.CANCELLED);
    }

    @Test
    public void testCantAddUserToEventOnSameDayWithCreatedEvent(){
        male.setCreatedEvents(new ArrayList<>(Arrays.asList(cinemaMovie)));
        Event newEvent = new Event("event", "Mityana", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        Mockito.when(eventRepository.findById(56)).thenReturn(Optional.of(newEvent));
        Throwable exception = assertThrows(InvalidDateException.class, () -> {
            eventService.addSingleParticipantToEvent(56,1);
        });
        Assert.assertEquals("User has an event scheduled for this day", exception.getMessage());
    }

    @Test
    public void testCantAddUserToEventOnSameDayWithAttendingEvent(){
        male.setAttending(new ArrayList<>(Arrays.asList(cinemaMovie)));
        Event newEvent = new Event("event", "Mityana", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        Mockito.when(eventRepository.findById(56)).thenReturn(Optional.of(newEvent));
        Throwable exception = assertThrows(InvalidDateException.class, () -> {
            eventService.addSingleParticipantToEvent(56,1);
        });
        Assert.assertEquals("User is already attending an event on this day", exception.getMessage());
    }

    @Test
    public void testCantAddUserToCancelledEvent(){
        Event newEvent = new Event("event", "Mityana", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        newEvent.setEventStatus(EventStatus.CANCELLED);
        Mockito.when(eventRepository.findById(56)).thenReturn(Optional.of(newEvent));
        Throwable exception = assertThrows(InvalidDateException.class, () -> {
            eventService.addSingleParticipantToEvent(56,1);
        });
        Assert.assertEquals("The event is not open", exception.getMessage());
    }

    @Test
    public void testCantAddUserToClosedEvent(){
        Event newEvent = new Event("event", "Mityana", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        newEvent.setEventStatus(EventStatus.CLOSED);
        Mockito.when(eventRepository.findById(56)).thenReturn(Optional.of(newEvent));
        Throwable exception = assertThrows(InvalidDateException.class, () -> {
            eventService.addSingleParticipantToEvent(56,1);
        });
        Assert.assertEquals("The event is not open", exception.getMessage());
    }

    @Test
    public void testCanGetAllEventsByUsersFriends(){
        samuel.setUserId(1);
        male.setUserId(2);

        Event newEvent = new Event("event", "Mityana", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        Event newEvent2 = new Event("event", "Mityana", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), male);
        User user1 = Mockito.mock(User.class);

        Friend friend1 = new Friend(user1, samuel);
        Friend friend2 = new Friend(user1, male);

        friend1.setActive(true);
        friend2.setActive(true);

        Set<Friend> set = new HashSet<>();
        set.add(friend1);
        set.add(friend2);

        Mockito.when(eventRepository.findAllEventsByFriends(any(List.class))).thenReturn(Arrays.asList(newEvent, newEvent2));
        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.of(user1));

        Mockito.when(user1.getFriends()).thenReturn(set);

        Assert.assertEquals(eventService.getAllEventsForUser("username").size(), 2);
    }

    @Test
    public void testCantGetEventsIfFollowRequestWasNotAccepted(){
        samuel.setUserId(1);
        male.setUserId(2);

        Event newEvent = new Event("event", "Mityana", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        Event newEvent2 = new Event("event", "Mityana", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), male);
        User user1 = Mockito.mock(User.class);

        Friend friend1 = new Friend(user1, samuel);
        Friend friend2 = new Friend(user1, male);

        friend1.setActive(true);
        friend2.setActive(false);

        Set<Friend> set = new HashSet<>();
        set.add(friend1);
        set.add(friend2);

        Mockito.when(eventRepository.findAllEventsByFriends(Arrays.asList(new Integer(1), new Integer(0)))).thenReturn(Arrays.asList(newEvent));
        Mockito.when(userRepository.findByUsername("username")).thenReturn(Optional.of(user1));

        Mockito.when(user1.getFriends()).thenReturn(set);

        Assert.assertEquals(eventService.getAllEventsForUser("username").size(), 1);
        Mockito.verify(eventRepository).findAllEventsByFriends(Arrays.asList(new Integer(1), new Integer(0)));
    }

    @Test
    public void testCanDeleteInviteeFromEventIfInviteeIdIsValid(){
        User user = Mockito.mock(User.class);
        Set<User> invitees = new HashSet<>();
        invitees.addAll(Arrays.asList(user, male));
        beach.setInvitees(invitees);
        male.setInvites(new ArrayList<>(Arrays.asList(beach)));

        Mockito.when(userRepository.findById(3)).thenReturn(Optional.of(samuel));
        Mockito.when(userRepository.findByUsername("sgaamuwa")).thenReturn(Optional.of(samuel));
        Mockito.when(eventRepository.save(beach)).thenReturn(beach);
        Mockito.when(userRepository.save(male)).thenReturn(male);

        Event event = eventService.deleteInviteeFromEvent(3, 2, 1, "sgaamuwa");

        Assert.assertEquals(1, event.getInvitees().size());
        Assert.assertTrue(event.getInvitees().contains(user));

    }

    @Test
    public void testCantDeleteInviteeIfTheyAreNotInvitedToEvent(){
        User user = Mockito.mock(User.class);
        Set<User> invitees = new HashSet<>();
        invitees.addAll(Arrays.asList(user, male));
        beach.setInvitees(invitees);
        male.setInvites(new ArrayList<>(Arrays.asList(beach)));

        Mockito.when(userRepository.findById(3)).thenReturn(Optional.of(samuel));
        Mockito.when(userRepository.findByUsername("sgaamuwa")).thenReturn(Optional.of(samuel));
        Mockito.when(eventRepository.save(beach)).thenReturn(beach);
        Mockito.when(userRepository.save(male)).thenReturn(male);

        Throwable exception = assertThrows(BadRequestException.class, () -> {
            eventService.deleteInviteeFromEvent(3, 2, 2, "sgaamuwa");
        });

        Assert.assertEquals("User with id :2 is not invited to the event id: 2", exception.getMessage());

    }

    @Test
    public void testCantDeleteInviteeIfNotOwnerOfEvent(){
        User user = Mockito.mock(User.class);
        Set<User> invitees = new HashSet<>();
        invitees.addAll(Arrays.asList(user, male));
        beach.setInvitees(invitees);
        male.setInvites(new ArrayList<>(Arrays.asList(beach)));

        Mockito.when(userRepository.findById(3)).thenReturn(Optional.of(samuel));
        Mockito.when(userRepository.findByUsername("sgaamuwa")).thenReturn(Optional.of(samuel));
        Mockito.when(eventRepository.save(beach)).thenReturn(beach);
        Mockito.when(userRepository.save(male)).thenReturn(male);

        Throwable exception = assertThrows(AuthorisationException.class, () -> {
            eventService.deleteInviteeFromEvent(2, 2, 1, "sgaamuwa");
        });

        Assert.assertEquals("You do not have the required permission to complete this operation", exception.getMessage());

    }
}
