package com.events.events.service;

import com.events.events.models.User;
import com.events.events.repository.UserRepository;
import com.events.events.services.UserService;
import com.events.events.services.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;


    @Test
    public void testUserServiceSaveHashesPassword(){
        User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123");

        userService.saveUser(samuel);
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(argument.capture());
        // check that the password is not equal to the one passed in
        Assert.assertNotEquals(argument.getValue().getPassword(), "pass123");
        // check that when checked with the encoder it is actually the same password
        Assert.assertTrue(BCrypt.checkpw("pass123", argument.getValue().getPassword()));

    }

}
