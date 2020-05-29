package com.events.events.repository;

import com.events.events.EventsApplication;
import com.events.events.config.database.JPAConfiguration;
import com.events.events.models.Friend;
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

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EventsApplication.class, JPAConfiguration.class})
public class FriendRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FriendRepository friendRepository;

    private User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123", "sgaamuwa@email.com");
    private User male = new User("michael", "male", "mmale", "pass123", "mmale@email.com");
    private User bruce = new User("bruce", "bigirwenyka", "bbigirwenkya", "pass123", "bbigirwenkya@email.com");
    private User peace = new User("peace", "nakiyemba", "pnakiyemba", "pass123", "pnakiyemba@email.com");
    private User joy = new User("joy", "bawaya", "sgaamuwa", "pass123", "jbawaya@email.com");

    @Before
    public void setup(){
        entityManager.persist(samuel);
        entityManager.persist(male);
        entityManager.persist(bruce);
        entityManager.persist(peace);
        entityManager.persist(joy);
    }

    @Test
    public void testCanRetrieveFollowersForAUser(){
        Friend friend1 = new Friend(male, samuel);
        Friend friend2 = new Friend(joy, samuel);
        Friend friend3 = new Friend(bruce, samuel);
        Friend friend4 = new Friend(samuel, peace);

        //activate all the friendships
        friend1.setActive(true);
        friend2.setActive(true);
        friend3.setActive(true);
        friend4.setActive(true);

        entityManager.persist(friend1);
        entityManager.persist(friend2);
        entityManager.persist(friend3);
        entityManager.persist(friend4);
        entityManager.flush();

        List<Friend> returnedFriends = friendRepository.getAllFollowers(samuel.getUserId());

        Assert.assertEquals(returnedFriends.size(), 3);
    }

    @Test
    public void testCanRetrieveUserOneIsFollowingIfActivated(){
        Friend friend1 = new Friend(male, samuel);
        Friend friend2 = new Friend(joy, bruce);
        Friend friend3 = new Friend(bruce, joy);
        Friend friend4 = new Friend(bruce, peace);
        Friend friend5 = new Friend(bruce, samuel);

        friend1.setActive(true);
        friend2.setActive(true);
        friend3.setActive(true);
        friend4.setActive(true);

        entityManager.persist(friend1);
        entityManager.persist(friend2);
        entityManager.persist(friend3);
        entityManager.persist(friend4);
        entityManager.persist(friend5);
        entityManager.flush();

        List<Friend> returnedFriends = friendRepository.getAllFollowing(bruce.getUserId());

        Assert.assertEquals(returnedFriends.size(), 2);
    }

    @Test
    public void testDoesntRetrieveFollowersIfNotActivated(){
        Friend friend1 = new Friend(male, samuel);
        Friend friend2 = new Friend(joy, bruce);
        Friend friend3 = new Friend(bruce, joy);
        Friend friend4 = new Friend(bruce, peace);

        entityManager.persist(friend1);
        entityManager.persist(friend2);
        entityManager.persist(friend3);
        entityManager.persist(friend4);
        entityManager.flush();

        List<Friend> returnedFriends = friendRepository.getAllFollowing(bruce.getUserId());

        // should not retrieve any followers since they were not activated
        Assert.assertEquals(returnedFriends.size(), 0);
    }

}
