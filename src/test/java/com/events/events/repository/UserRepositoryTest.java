package com.events.events.repository;

import com.events.events.models.User;
import com.sun.tools.corba.se.idl.InterfaceGen;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername(){
        User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123", "sgaamuwa@email.com");
        entityManager.persist(samuel);
        entityManager.flush();

        Optional<User> returnedUser = userRepository.findByUsername("sgaamuwa");

        Assert.assertEquals(samuel, returnedUser.get());
    }

    @Test
    public void testGetUserIdUsingFacebookIds(){
        User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123", "sgaamuwa@email.com");
        User edward = new User("edward", "gaamuwa", "egaamuwa", "pass123", "egaamuwa@email.com");
        User david = new User("david", "gaamuwa", "dgaamuwa", "pass123", "dgaamuwa@email.com");
        User merab = new User("merab", "kawanguzi", "mkawanguzi", "pass123", "mkawanguzi@email.com");

        samuel.setFacebookId("12345");
        edward.setFacebookId("21435");
        david.setFacebookId("54321");
        merab.setFacebookId("45231");

        Integer samId = entityManager.persistAndGetId(samuel, Integer.class);
        Integer edwardId = entityManager.persistAndGetId(edward, Integer.class);
        Integer davidId = entityManager.persistAndGetId(david, Integer.class);
        Integer merabId = entityManager.persistAndGetId(merab, Integer.class);
        entityManager.flush();

        List<Integer> singleId = Arrays.asList(samId);
        List<Integer> multipleIds = Arrays.asList(samId, edwardId, davidId, merabId);

        List<String> userIds = userRepository.getUserIdsForUsersWithFacebookIds(Arrays.asList("12345"));
        List<String> userIds2 = userRepository.getUserIdsForUsersWithFacebookIds(Arrays.asList("12345", "21435", "54321", "45231"));

        Assert.assertEquals(userIds.size(), 1);
        Assert.assertEquals(userIds2.size(), 4);
        Assert.assertEquals(new HashSet<>(singleId), new HashSet<>(userIds));
        Assert.assertEquals(new HashSet<>(multipleIds), new HashSet<>(userIds2));
    }
}
