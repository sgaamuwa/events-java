package com.events.events.service;

import com.events.events.error.AuthenticationException;
import com.events.events.error.DuplicateCreationException;
import com.events.events.error.IllegalFriendActionException;
import com.events.events.error.NotFoundException;
import com.events.events.models.User;
import com.events.events.repository.UserRepository;
import com.events.events.services.UserService;
import com.events.events.services.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    private User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123", "sgaamuwa@email.com");
    private User joy = new User("joy", "bawaya", "sgaamuwa", "pass123", "jbawaya@email.com");

    @Before
    public void setup(){
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(samuel));
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(samuel, joy)));
        Mockito.when(userRepository.findById(33)).thenReturn(Optional.empty());
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

    @Test
    public void changeUserPasswordValid(){
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        // ensure that the password stored is encoded
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        userService.changePassword(1, "pass123", "newPassword");
        Mockito.verify(userRepository, Mockito.atMost(1)).save(argument.capture());
        Assert.assertEquals(argument.getValue().getFirstName(), "samuel");
        Assert.assertTrue(bCryptPasswordEncoder.matches("newPassword", argument.getValue().getPassword()));
    }

    @Test
    public void changeUserPasswordWithFalsePassword(){
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        Throwable exception = assertThrows(AuthenticationException.class, () -> {
            userService.changePassword(1, "pass1234", "newPassword");
        });
        Assert.assertEquals("Password does not match current password", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void changeUserPasswordWithSamePassword(){
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        Throwable exception = assertThrows(AuthenticationException.class, () -> {
            userService.changePassword(1, "pass123", "pass123");
        });
        Assert.assertEquals("New Password can't be the same as the old password", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void changeUserPasswordWithShortPassword(){
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        Throwable exception = assertThrows(AuthenticationException.class, () -> {
            userService.changePassword(1, "pass123", "pass");
        });
        Assert.assertEquals("New Password must be more than 5", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void changeUserPasswordWithShortPasswordWithSpaces(){
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        Throwable exception = assertThrows(AuthenticationException.class, () -> {
            userService.changePassword(1, "pass123", "   pass    ");
        });
        Assert.assertEquals("New Password must be more than 5", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void testAddsFriendWithValidId(){
        joy.setUserId(2);
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        userService.addFriend(1,2);
        Assert.assertEquals(samuel.getFriends().size(), 1);
        Mockito.verify(userRepository).save(samuel);
    }

    @Test
    public void testThrowsIllegalFriendActionExceptionIfUserAndFriendTheSame(){
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Throwable exception = assertThrows(IllegalFriendActionException.class, () -> {
            userService.addFriend(1, 2);
        });
        Assert.assertEquals("Can't add self as a friend", exception.getMessage());
    }

}
