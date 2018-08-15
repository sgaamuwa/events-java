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
    public void testGetUserIdUsingLastName(){
        User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123", "sgaamuwa@email.com");
        User edward = new User("edward", "gaamuwa", "egaamuwa", "pass123", "egaamuwa@email.com");
        User david = new User("david", "gaamuwa", "dgaamuwa", "pass123", "dgaamuwa@email.com");
        User merab = new User("merab", "kawanguzi", "mkawanguzi", "pass123", "mkawanguzi@email.com");
        User joy = new User("joy", "bawaya", "jbawaya", "pass123", "jbawaya@email.com");

        Integer samId = entityManager.persistAndGetId(samuel, Integer.class);
        Integer edwardId = entityManager.persistAndGetId(edward, Integer.class);
        Integer davidId = entityManager.persistAndGetId(david, Integer.class);
        Integer merabId = entityManager.persistAndGetId(merab, Integer.class);
        Integer joyId = entityManager.persistAndGetId(joy, Integer.class);
        entityManager.flush();

        List<Integer> gaamuwaIds = Arrays.asList(samId, edwardId, davidId);
        List<Integer> nonGaamuwaIds = Arrays.asList(merabId, joyId);

        List<Integer> userIds = userRepository.getUserIdsForLastNames(Arrays.asList("gaamuwa", "bawaya"));
        List<Integer> userIds2 = userRepository.getUserIdsForLastNames(Arrays.asList("gaamuwa"));
        List<Integer> userIds3 = userRepository.getUserIdsForLastNames(Arrays.asList("gaamuwa", "bawaya", "kawanguzi"));

        Assert.assertEquals(userIds.size(), 4);
        Assert.assertEquals(userIds2.size(), 3);
        Assert.assertEquals(userIds3.size(), 5);
        Assert.assertEquals(new HashSet<>(gaamuwaIds), new HashSet<>(userRepository.getUserIdsForLastNames(Arrays.asList("gaamuwa"))));
        Assert.assertEquals(new HashSet<>(nonGaamuwaIds), new HashSet<>(userRepository.getUserIdsForLastNames(Arrays.asList("bawaya", "kawanguzi"))));

    }
}
