package com.events.events.service;

import com.events.events.error.DuplicateCreationException;
import com.events.events.error.NotFoundException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder(){
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserRepository userRepository;

    private User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123");
    private User joy = new User("joy", "bawaya", "sgaamuwa", "pass123");

    @Before
    public void setup(){
        Mockito.when(userRepository.findById(new Integer(1))).thenReturn(Optional.of(samuel));
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(samuel, joy)));
        Mockito.when(userRepository.findById(new Integer(33))).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(samuel)).thenReturn(samuel);
    }

    @Test
    public void testUserServiceSaveHashesPassword(){
        userService.saveUser(samuel);
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(argument.capture());
        // check that the password is not equal to the one passed in
        Assert.assertNotEquals(argument.getValue().getPassword(), "pass123");
        // check that when checked with the encoder it is actually the same password
        Assert.assertTrue(bCryptPasswordEncoder.matches("pass123", argument.getValue().getPassword()));

    }

    @Test
    public void testDoesNotCreateUserWithExistentUsername(){
        Mockito.when(userRepository.findByUsername("sgaamuwa")).thenReturn(Optional.of(samuel));
        Throwable exception = assertThrows(DuplicateCreationException.class, () -> {
            userService.saveUser(joy);
        });
        Assert.assertEquals("User with the username: sgaamuwa already exists", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void testCreatesUserWhenValid(){
        Mockito.when(userRepository.findByUsername("sgaamuwa")).thenReturn(Optional.empty());
        User user = userService.saveUser(samuel);
        Mockito.verify(userRepository).save(samuel);
        Assert.assertEquals(user, samuel);
    }

    @Test
    public void testGetUserByIdWithValidUserId(){
        User user = userService.getUserById(1);
        Assert.assertEquals(user, samuel);
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserByIdWithInvalidUserId(){
        userService.getUserById(33);
        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.any(Integer.class));
    }

    @Test
    public void testGetAllUsers(){
        List<User> users = userService.getAllUsers();
        Assert.assertEquals(users.size(), 2);
        Assert.assertEquals(users.get(0), samuel);
        Assert.assertEquals(users.get(1), joy);
    }

    @Test
    public void testDeletesUserWithValidId(){
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        userService.deleteUser(1);
        Mockito.verify(userRepository).delete(argument.capture());
        Assert.assertEquals(argument.getValue(), samuel);
    }

    @Test
    public void testDeleteUserWithInvalidId(){

        Throwable exception = assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(33);
        });
        Assert.assertEquals("User with id: 33 not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).delete(Mockito.any(User.class));
    }

}
