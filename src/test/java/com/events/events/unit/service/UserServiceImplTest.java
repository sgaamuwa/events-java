package com.events.events.unit.service;

import com.events.events.error.*;
import com.events.events.models.Friend;
import com.events.events.models.User;
import com.events.events.repository.ConfirmationTokenRepository;
import com.events.events.repository.FriendRepository;
import com.events.events.repository.UserRepository;
import com.events.events.services.AWSS3Service;
import com.events.events.services.EmailService;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

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

    @MockBean
    private EmailService emailService;

    @MockBean
    private AWSS3Service awss3Service;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private FriendRepository friendRepository;

    @MockBean
    private ConfirmationTokenRepository confirmationTokenRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    private User samuel = new User("samuel", "gaamuwa", "sgaamuwa", "pass123", "sgaamuwa@email.com");
    private User joy = new User("joy", "bawaya", "jbawaya", "pass123", "jbawaya@email.com");

    @Before
    public void setup(){
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(samuel));
        Mockito.when(userRepository.findByUsername("sgaamuwa")).thenReturn(Optional.of(samuel));
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(samuel, joy)));
        Mockito.when(userRepository.findById(33)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(samuel)).thenReturn(samuel);
    }

    @Test
    public void testUserServiceSaveHashesPassword(){
        Mockito.when(userRepository.findByUsername("sgaamuwa")).thenReturn(Optional.empty());
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
        joy.setUsername("sgaamuwa");
        Throwable exception = assertThrows(DuplicateCreationException.class, () -> {
            userService.saveUser(joy);
        });
        Assert.assertEquals("User with the username: sgaamuwa already exists", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
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
        Mockito.verify(userRepository, Mockito.never()).findById(any(Integer.class));
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
        Mockito.verify(userRepository, Mockito.never()).delete(any(User.class));
    }

    @Test
    public void changeUserPasswordValid(){
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        // ensure that the password stored is encoded
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        userService.changePassword("pass123", "newPassword", "sgaamuwa");
        Mockito.verify(userRepository, Mockito.atMost(1)).save(argument.capture());
        Assert.assertEquals(argument.getValue().getFirstName(), "samuel");
        Assert.assertTrue(bCryptPasswordEncoder.matches("newPassword", argument.getValue().getPassword()));
    }

    @Test
    public void changeUserPasswordWithFalsePassword(){
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        Throwable exception = assertThrows(AuthenticationException.class, () -> {
            userService.changePassword("pass1234", "newPassword", "sgaamuwa");
        });
        Assert.assertEquals("Password does not match current password", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    public void changeUserPasswordWithSamePassword(){
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        Throwable exception = assertThrows(AuthenticationException.class, () -> {
            userService.changePassword( "pass123", "pass123", "sgaamuwa");
        });
        Assert.assertEquals("New Password can't be the same as the old password", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    public void changeUserPasswordWithShortPassword(){
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        Throwable exception = assertThrows(AuthenticationException.class, () -> {
            userService.changePassword( "pass123", "pass", "sgaamuwa");
        });
        Assert.assertEquals("New Password must be more than 5", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    public void changeUserPasswordWithShortPasswordWithSpaces(){
        samuel.setPassword(bCryptPasswordEncoder.encode(samuel.getPassword()));
        Throwable exception = assertThrows(AuthenticationException.class, () -> {
            userService.changePassword( "pass123", "   pass    ", "sgaamuwa");
        });
        Assert.assertEquals("New Password must be more than 5", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    public void testAddsFriendWithValidId(){
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        userService.addFriend(2,1,"jbawaya");
        Mockito.verify(friendRepository, Mockito.atMost(1)).save(new Friend(samuel, joy));
    }

    @Test
    public void testThrowsIllegalFriendActionExceptionIfUserAndFriendTheSame(){
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Throwable exception = assertThrows(IllegalFriendActionException.class, () -> {
            userService.addFriend(2,2, "jbawaya");
        });
        Assert.assertEquals("Can't add self as a friend", exception.getMessage());
    }

    @Test
    public void testCanGetFollowersForUser(){
        User peace = new User("peace", "nakiyemba", "pnakiyemba", "pass123", "pnakiyemba@email.com");
        samuel.setUserId(1);
        List<Friend> friends = Arrays.asList(new Friend(joy, samuel), new Friend(peace, samuel));
        Mockito.when(friendRepository.getAllFollowers(1)).thenReturn(friends);
        List<User> friendsList = userService.getAllFollowers(1);
        Assert.assertEquals(friendsList.size(), 2);
    }

    @Test
    public void testCanAcceptFollowRequest(){
        // have Samuel request to follow joy
        Friend friend = new Friend(samuel, joy);

        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, joy))).thenReturn(Optional.of(friend));
        userService.acceptFollowRequest(2,1, "jbawaya");
        Mockito.verify(friendRepository, Mockito.atMost(1)).save(friend);
    }

    @Test
    public void testThrowsExceptionIfThereIsNoRequestOnAcceptFollowRequest(){
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, joy))).thenReturn(Optional.empty());
        Throwable exception = assertThrows(IllegalFriendActionException.class, () -> {
            userService.acceptFollowRequest(2,1, "jbawaya");
        });
        Assert.assertEquals("There is no request from:1", exception.getMessage());
    }

    @Test
    public void testThrowsExceptionIfFollowRequestAlreadyAcceptedOnAcceptFollowRequest(){
        Friend friend = new Friend(samuel, joy);
        friend.setActive(true);

        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, joy))).thenReturn(Optional.of(friend));

        Throwable exception = assertThrows(IllegalFriendActionException.class, () -> {
            userService.acceptFollowRequest(2,1, "jbawaya");
        });

        Assert.assertEquals("You are already friends with user:1", exception.getMessage());
    }

    @Test
    public void testCanRejectFollowRequest(){
        // have Samuel request to follow joy
        Friend friend = new Friend(samuel, joy);

        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, joy))).thenReturn(Optional.of(friend));
        userService.rejectFollowRequest(2,1, "jbawaya");
        Mockito.verify(friendRepository, Mockito.atMost(1)).delete(friend);
    }

    @Test
    public void testThrowsExceptionIfThereIsNoRequestOnRejectFollowRequest(){
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, joy))).thenReturn(Optional.empty());
        Throwable exception = assertThrows(IllegalFriendActionException.class, () -> {
            userService.rejectFollowRequest(2,1, "jbawaya");
        });
        Assert.assertEquals("There is no request from:1", exception.getMessage());
    }

    @Test
    public void testCanUnFollowUser(){
        // have samuel follow joy and activate it
        Friend friend = new Friend(samuel, joy);
        friend.setActive(true);

        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Mockito.when(friendRepository.findById(new Friend.Key(joy, samuel))).thenReturn(Optional.of(friend));
        userService.unFollowUser(2,1, "jbawaya");
        Mockito.verify(friendRepository, Mockito.atMost(1)).delete(friend);
    }

    @Test
    public void testCanUnFollowUserWhenNotAccepted(){
        // have samuel follow joy and activate it
        Friend friend = new Friend(samuel, joy);

        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(joy));
        Mockito.when(friendRepository.findById(new Friend.Key(joy, samuel))).thenReturn(Optional.of(friend));
        userService.unFollowUser(2,1, "jbawaya");
        Mockito.verify(friendRepository, Mockito.atMost(1)).delete(friend);
    }

    @Test
    public void testCanUploadImageForUser(){
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doNothing().when(awss3Service).deleteFile("fileName");
        Mockito.when(awss3Service.uploadFile(multipartFile, "userImages")).thenReturn("myFileNameInS3");
        User user = userService.uploadUserImage(1, multipartFile);
        Assert.assertEquals(user.getImageKey(), "myFileNameInS3");
    }

    @Test
    public void testCanSearchForUser(){
        Mockito.when(userRepository.findBySearchTerm("aamu")).thenReturn(Arrays.asList(samuel, joy));
        List<User> users = userService.searchUsers("aamu");
        Assert.assertEquals(2, users.size());
        Assert.assertTrue(users.containsAll(Arrays.asList(samuel, joy)));
    }

    @Test
    public void testThrowsExceptionIfNoUsersWithSearchTerm(){
        Mockito.when(userRepository.findBySearchTerm("aamu")).thenReturn(Collections.emptyList());
        Throwable exception = assertThrows(EmptyListException.class, () -> {
            userService.searchUsers("aamu");
        });
        Assert.assertEquals("There are no users who fit the search term: aamu", exception.getMessage());
    }

    @Test
    public void testCanGetConnectionsForUser(){
        List<Integer> users = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);
        User user4 = Mockito.mock(User.class);
        Friend friend1 = new Friend(samuel, user1);
        friend1.setActive(true);
        Friend friend2 = new Friend(user1, samuel);
        friend2.setActive(true);
        Friend friend3 = new Friend(user2, samuel);
        friend3.setActive(true);
        Friend friend4 = new Friend(user3, samuel);
        Friend friend5 = new Friend(samuel, user4);

        Mockito.when(user1.getUserId()).thenReturn(6);
        Mockito.when(user2.getUserId()).thenReturn(7);
        Mockito.when(user3.getUserId()).thenReturn(8);
        Mockito.when(user4.getUserId()).thenReturn(9);

        samuel.setFriends(new HashSet<>(Arrays.asList(friend1, friend5)));
        Mockito.when(user1.getFriends()).thenReturn(new HashSet<>(Arrays.asList(friend2)));
        Mockito.when(user2.getFriends()).thenReturn(new HashSet<>(Arrays.asList(friend3)));
        Mockito.when(user3.getFriends()).thenReturn(new HashSet<>(Arrays.asList(friend4)));
        Mockito.when(user4.getFriends()).thenReturn(new HashSet<>());

        Mockito.when(userRepository.findAllById(users)).thenReturn(Arrays.asList(user1, user2, user3, user4));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, user1))).thenReturn(Optional.of(friend1));
        Mockito.when(friendRepository.findById(new Friend.Key(user1, samuel))).thenReturn(Optional.of(friend2));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, user2))).thenReturn(Optional.empty());
        Mockito.when(friendRepository.findById(new Friend.Key(user2, samuel))).thenReturn(Optional.of(friend3));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, user3))).thenReturn(Optional.empty());
        Mockito.when(friendRepository.findById(new Friend.Key(user3, samuel))).thenReturn(Optional.of(friend4));
        Mockito.when(friendRepository.findById(new Friend.Key(samuel, user4))).thenReturn(Optional.of(friend5));
        Mockito.when(friendRepository.findById(new Friend.Key(user4, samuel))).thenReturn(Optional.empty());

        List<Map<String, Object>> returnedList = userService.userConnections(new int[]{1,2,3,4}, "sgaamuwa");

        Assert.assertTrue(returnedList.size() == 4);
        Assert.assertTrue(((Integer) returnedList.get(0).get("id")).equals(6));
        Assert.assertTrue(((Integer) returnedList.get(1).get("id")).equals(7));
        Assert.assertTrue(((Integer) returnedList.get(2).get("id")).equals(8));
        Assert.assertTrue(((Integer) returnedList.get(3).get("id")).equals(9));
        Assert.assertTrue(((List<String>)returnedList.get(0).get("connections")).containsAll(Arrays.asList("following", "followedBy")));
        Assert.assertTrue(((List<String>)returnedList.get(1).get("connections")).containsAll(Arrays.asList("followedBy")));
        Assert.assertTrue(((List<String>)returnedList.get(2).get("connections")).containsAll(Arrays.asList("pendingRequest")));
        Assert.assertTrue(((List<String>)returnedList.get(3).get("connections")).containsAll(Arrays.asList("requestedFollow")));
    }

    @Test
    public void testReturnsExceptionIfUserIdsDoNotExist(){
        List<Integer> users = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        Mockito.when(userRepository.findAllById(users)).thenReturn(Collections.emptyList());

        Throwable exception = assertThrows(BadRequestException.class, () -> {
            userService.userConnections(new int[]{1,2,3,4}, "sgaamuwa");;
        });

        Assert.assertTrue(exception.getMessage().equals("UserIDs provided do not match any in the system"));
    }


}
