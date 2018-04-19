package com.events.events.repository;

import com.events.events.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindById(){
        User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123");
        entityManager.persist(samuel);
        entityManager.flush();

        Optional<User> returnedUser = userRepository.findById(new Integer(1));

        Assert.assertEquals("samuel", returnedUser.get().getFirstName());

    }
}
