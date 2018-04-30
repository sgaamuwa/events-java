package com.events.events.repository;

import com.events.events.models.Event;
import com.events.events.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    public TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void canAddUsersToAnEvent(){
        // set up the users that you want
        User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123");
        User male = new User("michael", "male", "mmale", "pass123");
        User bruce = new User("bruce", "bigirwenyka", "bbigirwenkya", "pass123");
        // create and event
        Event cinemaMovie = new Event("Movie", "Acacia Mall", LocalDate.now(), samuel);

        List<User> participants = new ArrayList<>();
        participants.add(male);
        participants.add(bruce);

        cinemaMovie.setParticipants(participants);
        // save using the repository
        eventRepository.save(cinemaMovie);

        Event returned = entityManager.find(Event.class, 1);

        Assert.assertEquals(returned.getParticipants().get(0).getFirstName(), "michael");
        Assert.assertEquals(returned.getParticipants().get(1).getFirstName(), "bruce");
    }

}
