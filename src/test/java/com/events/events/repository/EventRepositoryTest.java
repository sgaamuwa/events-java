package com.events.events.repository;

import com.events.events.EventsApplication;
import com.events.events.config.database.JPAConfiguration;
import com.events.events.models.Event;
import com.events.events.models.EventStatus;
import com.events.events.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EventsApplication.class, JPAConfiguration.class})
public class EventRepositoryTest {

    @Autowired
    public TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    private User samuel = new User("martha", "kyozira", "mkyozira", "pass123", "mkyozira@email.com");
    private Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(3), samuel);
    private User male = new User("michael", "male", "mmale", "pass123", "mmale@email.com");
    private User bruce = new User("bruce", "bigirwenyka", "bbigirwenkya", "pass123", "bbigirwenkya@email.com");

    @Before
    public void setup(){
        entityManager.persist(samuel);
        entityManager.persist(male);
        entityManager.persist(bruce);
    }

    @Test
    public void canAddUsersToAnEvent(){

        Set<User> participants = new HashSet<>();
        participants.add(male);
        participants.add(bruce);

        cinemaMovie.setParticipants(participants);
        // save using the repository
        Event savedEvent = eventRepository.save(cinemaMovie);

        Event returned = entityManager.find(Event.class, savedEvent.getEventId());

        Assert.assertTrue(returned.getParticipants().contains(male));
        Assert.assertTrue(returned.getParticipants().contains(bruce));
    }

    @Test
    public void canGetEventsByDate(){
        //save an event
        entityManager.persist(cinemaMovie);
        // retrieve it by date
        List<Event> events = eventRepository.findByStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));

        Assert.assertEquals(events.size(), 1);
        Assert.assertEquals(events.get(0).getTitle(), "Movie");
    }

    @Test
    public void canGetEventsBetweenDates(){
        Event beach = new Event("beach", "Entebbe", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2).plusHours(2), samuel);
        Event jumping = new Event("jumping", "Jinja", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2), samuel);

        entityManager.persist(beach);
        entityManager.persist(jumping);
        entityManager.persist(cinemaMovie);

        Assert.assertEquals(eventRepository.findByStartTimeBetween(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2)).size(), 4);
        Assert.assertEquals(eventRepository.findByStartTimeBetween(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusDays(1), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(1)).size(), 2);
    }

    @Test
    public void canGetEventsAfterCertainDate(){
        Event beach = new Event("beach", "Entebbe", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2).plusHours(2), samuel);
        Event jumping = new Event("jumping", "Jinja", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2), samuel);

        entityManager.persist(beach);
        entityManager.persist(jumping);
        entityManager.persist(cinemaMovie);

        Assert.assertEquals(eventRepository.findByStartTimeGreaterThan(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(1)).size(), 9);
        Assert.assertEquals(eventRepository.findByStartTimeGreaterThan(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusDays(1)).size(), 11);
    }

    @Test
    public void canGetEventsBeforeCertainDate(){
        Event beach = new Event("beach", "Entebbe", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2).plusHours(2), samuel);
        Event jumping = new Event("jumping", "Mauritious", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2), samuel);

        entityManager.persist(beach);
        entityManager.persist(jumping);
        entityManager.persist(cinemaMovie);

        Assert.assertEquals(eventRepository.findByStartTimeLessThan(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(1)).size(), 1);
        Assert.assertEquals(eventRepository.findByStartTimeLessThan(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusDays(1)).size(), 0);
        Assert.assertEquals(eventRepository.findByStartTimeLessThan(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(4)).size(), 6);
    }

    @Test
    public void canFindEventsByStatus(){
        Event beach = new Event("beach", "Entebbe", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2).plusHours(2), samuel);
        Event jumping = new Event("jumping", "Jinja", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), samuel);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2), samuel);

        beach.setEventStatus(EventStatus.CANCELLED);

        entityManager.persist(beach);
        entityManager.persist(jumping);
        entityManager.persist(cinemaMovie);

        Assert.assertEquals(eventRepository.findByEventStatus(EventStatus.OPEN).size(), 10);
        Assert.assertEquals(eventRepository.findByEventStatus(EventStatus.CANCELLED).size(), 1);
        Assert.assertEquals(eventRepository.findByEventStatus(EventStatus.CANCELLED).get(0), beach);
    }

    @Test
    public void canFindAllEventsByUsersFriends(){
        Event beach = new Event("trip", "Namayiba", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(2).plusHours(2), samuel);
        Event jumping = new Event("jumping", "Jinja", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(3).plusHours(2), male);
        Event nightDancing = new Event("nightDancing", "mukono", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(4), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(4).plusHours(2), samuel);
        Event quidditch = new Event("quidditch", "hogwarts", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(5), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(5).plusHours(2), male);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2), bruce);

        entityManager.persist(beach);
        entityManager.persist(jumping);
        entityManager.persist(cinemaMovie);
        entityManager.persist(nightDancing);
        entityManager.persist(quidditch);

        Assert.assertEquals(eventRepository.findAll().size(), 13);
        Assert.assertEquals(eventRepository.findAllEventsByFriends(Arrays.asList(samuel.getUserId(), male.getUserId())).size(), 4);
    }

}
