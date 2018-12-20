package com.events.events.repository;

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
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

        entityManager.persist(new Friend(male, samuel));
        entityManager.persist(new Friend(joy, samuel));
        entityManager.persist(new Friend(bruce, samuel));
        entityManager.persist(new Friend(samuel, peace));
        entityManager.flush();

        List<Friend> returnedFriends = friendRepository.getAllFollowers(samuel.getUserId());

        Assert.assertEquals(returnedFriends.size(), 3);
    }

    @Test
    public void testCanRetrieveFriendsUserIsFollowing(){
        entityManager.persist(new Friend(male, samuel));
        entityManager.persist(new Friend(joy, bruce));
        entityManager.persist(new Friend(bruce, joy));
        entityManager.persist(new Friend(bruce, peace));
        entityManager.flush();

        List<Friend> returnedFriends = friendRepository.getAllFollowing(bruce.getUserId());

        Assert.assertEquals(returnedFriends.size(), 2);
    }

}
