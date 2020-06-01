package com.events.events.services;

import com.events.events.error.*;
import com.events.events.models.ConfirmationToken;
import com.events.events.models.Event;
import com.events.events.models.Friend;
import com.events.events.models.User;
import com.events.events.repository.ConfirmationTokenRepository;
import com.events.events.repository.FriendRepository;
import com.events.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.ExpiredAuthorizationException;
import org.springframework.social.InvalidAuthorizationException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if(!user.isPresent()){
            throw new UsernameNotFoundException("There is no user with the username: "+ username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.get().getUsername(),
                user.get().getPassword(),
                user.get().isEnabled(),
                true,
                true,
                true,
                Collections.emptyList());
    }

    @Transactional
    public User saveUser(User user){
        //check if the username exists
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new DuplicateCreationException("User with the username: "+user.getUsername()+" already exists");
        }else if(user.getPassword().trim().length() < 5){
            throw new AuthenticationException("New Password must be more than 5");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        user = userRepository.save(user);
        // create a confirmation token for the user and save it
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        // send a verification to the user
        emailService.composeVerificationEmail(user.getEmail(), confirmationToken);
        return user;
    }

    @Transactional
    public void activateUser(String token){
        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findByToken(token);
        if(!confirmationToken.isPresent()){
            throw new AuthenticationException("The provided token is not valid");
        }
        User user = confirmationToken.get().getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User getUserById(int id) {
        return verifyAndReturnUser(id);
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        User user = verifyAndReturnUser(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword, String username) {
        User user = verifyAndReturnUser(username);
        if(!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())){
            throw new AuthenticationException("Password does not match current password");
        } else if(bCryptPasswordEncoder.matches(newPassword, user.getPassword())){
            throw new AuthenticationException("New Password can't be the same as the old password");
        } else if(newPassword.trim().length() < 5){
            throw new AuthenticationException("New Password must be more than 5");
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addFriend(int userId, int friendId, String username) {
        User friend = verifyAndReturnUser(friendId);
        User user = verifyAndReturnUser(userId);
        // check that the user returned is the same user accessing the system
        if(!user.getUsername().equals(username)){
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }
        if(user.equals(friend)){
            throw new IllegalFriendActionException("Can't add self as a friend");
        }
        // check if the user is already following this person
        Friend newFriend = new Friend(user, friend);
        for(Friend singleFriend: user.getFriends()){
            if(singleFriend.equals(newFriend)){
                throw new IllegalFriendActionException("User is already following or requested to follow user with Id: " + friendId);
            }
        }
        // add the friend to the user
        friendRepository.save(newFriend);
    }

    @Override
    public List<User> getAllFollowing(int userId) {
        User user = verifyAndReturnUser(userId);
        List<User> friends = new ArrayList<>();
        for(Friend friend : user.getFriends()){
            friends.add(friend.getFriend());
        }
        return friends;
    }

    @Override
    public List<User> getAllFollowers(int userId){
        User user = verifyAndReturnUser(userId);
        List<Friend> friendList = friendRepository.getAllFollowers(user.getUserId());
        List<User> followers = new ArrayList<>();
        if(friendList.isEmpty()){
            throw new EmptyListException("There are no followers for the user: "+userId);
        }
        for(Friend friend : friendList){
            followers.add(friend.getOwner());
        }
        return followers;
    }

    @Override
    @Transactional
    public void acceptFollowRequest(int userId, int followerId, String username) {
        // check that the person cancelling the request exists
        User user = verifyAndReturnUser(userId);
        // check that the user that requested exists
        User follower = verifyAndReturnUser(followerId);
        // check that the user id and username belong to the same user
        if(!user.getUsername().equals(username)){
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }

        //check to see if that follow relationship exists
        Optional<Friend> friend = friendRepository.findById(new Friend.Key(follower, user));
        if(!friend.isPresent()){
            throw new IllegalFriendActionException("There is no request from:" + followerId);
        }
        if(friend.get().isActive()){
            throw new IllegalFriendActionException("You are already friends with user:" + followerId);
        }
        friend.get().setActive(true);
        friendRepository.save(friend.get());
    }

    @Override
    @Transactional
    public void rejectFollowRequest(int userId, int followerId, String username) {
        // check that the person cancelling the request exists
        User user = verifyAndReturnUser(userId);
        // check that the user that requested exists
        User follower = verifyAndReturnUser(followerId);
        // check that the user id and username belong to the same user
        if(!user.getUsername().equals(username)){
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }
        //check to see if that follow relationship exists
        Optional<Friend> friendship = friendRepository.findById(new Friend.Key(follower, user));
        if(!friendship.isPresent()){
            throw new IllegalFriendActionException("There is no request from:" + followerId);
        }

        // delete the friend relationship from the database, whether friends or not
        friendRepository.delete(friendship.get());

    }

    @Override
    @Transactional
    public void unFollowUser(int userId, int friendId, String username) {
        // check that the person cancelling the request exists
        User user = verifyAndReturnUser(userId);
        // check that the user that requested exists
        User friend = verifyAndReturnUser(friendId);
        // check that the user id and username belong to the same user
        if(!user.getUsername().equals(username)){
            throw new AuthorisationException("You do not have the required permission to complete this operation");
        }

        //check to see if that follow relationship exists
        Optional<Friend> friendship = friendRepository.findById(new Friend.Key(user, friend));
        if(!friendship.isPresent()){
            throw new IllegalFriendActionException("You are not following:" + friendId);
        }

        // delete the friend relationship from the database, whether friends or not
        friendRepository.delete(friendship.get());
    }

    @Override
    @Transactional
    public List<Event> listEventsByUser(int userId) {
        User user = verifyAndReturnUser(userId);
        if(user.getCreatedEvents().isEmpty()){
            throw new EmptyListException("There are no events for the user: "+ userId);
        }
        return user.getCreatedEvents();
    }

    @Override
    @Transactional
    public List<Event> listEventsUserIsAttending(int userId) {
        User user = verifyAndReturnUser(userId);
        if(user.getAttending().isEmpty()){
            throw new EmptyListException("The user: "+userId+" is not attending any events");
        }
        return user.getAttending();
    }

    @Override
    public void setFacebookIdAndToken(String token, String username) {
        //check that the token received is valid and belongs to the user
        try {
            Facebook facebook = new FacebookTemplate(token);
            String[] fields = {"id", "email", "first_name", "last_name"};
            org.springframework.social.facebook.api.User facebookUser = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);
            // get the user and set the token and facebook Id
            User user = userRepository.findByUsername(username).get();
            user.setFacebookId(facebookUser.getId());
            user.setAccessToken(token);
        }catch(InvalidAuthorizationException e){
            throw new AuthenticationException("Facebook token provided is invalid");
        }catch(ExpiredAuthorizationException e){
            throw new AuthenticationException("Facebook token provided is expired");
        }
        System.out.println("Got here");

    }

    //Helper methods to get users below here

    private User verifyAndReturnUser(int userId){
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            throw new NotFoundException("User with id: "+userId+" not found");
        }
        return user.get();
    }

    private User verifyAndReturnUser(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()){
            throw new NotFoundException("User with the username: "+username+" not found");
        }
        return user.get();
    }

}
