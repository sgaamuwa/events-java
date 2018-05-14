package com.events.events.repository;

import com.events.events.models.Event;
import com.events.events.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EventRepositoryTest {

    @Autowired
    public TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    private User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123");
    private Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDate.now(), samuel);
    private User male = new User("michael", "male", "mmale", "pass123");
    private User bruce = new User("bruce", "bigirwenyka", "bbigirwenkya", "pass123");

    @Test
    public void canAddUsersToAnEvent(){

        List<User> participants = new ArrayList<>();
        participants.add(male);
        participants.add(bruce);

        cinemaMovie.setParticipants(participants);
        // save using the repository
        Event savedEvent = eventRepository.save(cinemaMovie);

        Event returned = entityManager.find(Event.class, savedEvent.getId());

        Assert.assertEquals(returned.getParticipants().get(0).getFirstName(), "michael");
        Assert.assertEquals(returned.getParticipants().get(1).getFirstName(), "bruce");
    }

    @Test
    public void canGetEventsByDate(){
        //save an event
        entityManager.persist(samuel);
        entityManager.persist(cinemaMovie);
        entityManager.flush();
        // retrieve it by date
        List<Event> events = eventRepository.findByDate(LocalDate.now());

        Assert.assertEquals(events.size(), 1);
        Assert.assertEquals(events.get(0).getTitle(), "Movie");
    }

    @Test
    public void canGetEventsBetweenDates(){
        Event beach = new Event("beach", "Entebbe", LocalDate.now().plusDays(2), samuel);
        Event jumping = new Event("jumping", "Jinja", LocalDate.now().plusDays(3), samuel);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDate.now(), samuel);

        entityManager.persist(samuel);
        entityManager.persist(beach);
        entityManager.persist(jumping);
        entityManager.persist(cinemaMovie);

        Assert.assertEquals(eventRepository.getEventsBetweenDates(LocalDate.now(), LocalDate.now().plusDays(2)).size(), 2);
        Assert.assertEquals(eventRepository.getEventsBetweenDates(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)).size(), 1);
    }

    @Test
    public void canGetEventsAfterCertainDate(){
        Event beach = new Event("beach", "Entebbe", LocalDate.now().plusDays(2), samuel);
        Event jumping = new Event("jumping", "Jinja", LocalDate.now().plusDays(3), samuel);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDate.now(), samuel);

        entityManager.persist(samuel);
        entityManager.persist(beach);
        entityManager.persist(jumping);
        entityManager.persist(cinemaMovie);

        Assert.assertEquals(eventRepository.getEventsAfterDate(LocalDate.now().plusDays(1)).size(), 2);
        Assert.assertEquals(eventRepository.getEventsAfterDate(LocalDate.now().minusDays(1)).size(), 3);
    }

    @Test
    public void canGetEventsBeforeCertainDate(){
        Event beach = new Event("beach", "Entebbe", LocalDate.now().plusDays(2), samuel);
        Event jumping = new Event("jumping", "Jinja", LocalDate.now().plusDays(3), samuel);
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDate.now(), samuel);

        entityManager.persist(samuel);
        entityManager.persist(beach);
        entityManager.persist(jumping);
        entityManager.persist(cinemaMovie);

        Assert.assertEquals(eventRepository.getEventsBeforeDate(LocalDate.now().plusDays(1)).size(), 1);
        Assert.assertEquals(eventRepository.getEventsBeforeDate(LocalDate.now().minusDays(1)).size(), 0);
        Assert.assertEquals(eventRepository.getEventsBeforeDate(LocalDate.now().plusDays(4)).size(), 3);
    }

}
