package com.events.events.services;

import com.amazonaws.services.s3.AmazonS3;
import com.events.events.error.*;
import com.events.events.models.ConfirmationToken;
import com.events.events.models.Event;
import com.events.events.models.Friend;
import com.events.events.models.User;
import com.events.events.repository.ConfirmationTokenRepository;
import com.events.events.repository.FriendRepository;
import com.events.events.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.ExpiredAuthorizationException;
import org.springframework.social.InvalidAuthorizationException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AWSS3Service awss3Service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

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
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).get();
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
    public User uploadUserImage(int userId, MultipartFile multipartFile) {
        LOGGER.info("Uploading image for user with id: "+ userId +" started");
        User user = verifyAndReturnUser(userId);
        // check if the event has an image already and delete it
        if(user.getImageKey() != null && !user.getImageKey().isEmpty()){
            awss3Service.deleteFile(user.getImageKey());
        }
        String fileName = awss3Service.uploadFile(multipartFile, "userImages");
        user.setImageKey(fileName);
        LOGGER.info("Uploading image for user with id: "+ userId + " completed");
        return userRepository.save(user);
    }

    @Override
    public ByteArrayResource downloadUserImage(int userId) {
        LOGGER.info("Downloading image for user with id: "+ userId +" started");
        User user = verifyAndReturnUser(userId);
        if(user.getImageKey() == null){
            LOGGER.error("User with id: " + userId + " doesn't have an image uploaded");
            throw new NotFoundException("User does not have an image for this id");
        }
        byte[] imageFile = awss3Service.downloadFile(user.getImageKey());
        LOGGER.info("Downloading image for user with id: "+ userId + " completed");
        return new ByteArrayResource(imageFile);
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
    public List<User> searchUsers(String searchTerm) {
        LOGGER.info("Searching for user: "+ searchTerm);
        List<User> users =  userRepository.findBySearchTerm(searchTerm);
        if(users.isEmpty()){
            LOGGER.info("Searching for user: "+ searchTerm + " completed");
            throw new EmptyListException("There are no users who fit the search term: "+ searchTerm);
        }
        LOGGER.info("Searching for user: "+ searchTerm + " completed");
        return users;
    }

    @Override
    public List<Map<String, Object>> userConnections(int[] userIds, String username) {
        LOGGER.info("Generating user connections");
        User currentUser = verifyAndReturnUser(username);
        List<User> searchedUsers = userRepository.findAllById(Arrays.stream(userIds).boxed().collect(Collectors.toList()));

        if(searchedUsers.isEmpty()){
            LOGGER.error("Ids provided do not exist");
            throw new BadRequestException("UserIDs provided do not match any in the system");
        }

        List<Map<String, Object>> userConnections = new ArrayList<>();

        for(User user : searchedUsers){
            Map<String, Object> userMap = new HashMap<>();
            List<String> connections = new ArrayList<>();
            userMap.put("id", user.getUserId());
            // check if the user is following them and it is active
            if(currentUser.getFriends().contains(new Friend(currentUser, user)) && friendRepository.findById(new Friend.Key(currentUser, user)).get().isActive()){
                connections.add("following");
            }
            // check if the user is following them and it is not yet active
            if(currentUser.getFriends().contains(new Friend(currentUser, user)) && !friendRepository.findById(new Friend.Key(currentUser, user)).get().isActive()){
                connections.add("requestedFollow");
            }
            // check if they are following the user and it is active
            if(user.getFriends().contains(new Friend(user, currentUser)) && friendRepository.findById(new Friend.Key(user, currentUser)).get().isActive()){
                connections.add("followedBy");
            }
            // check if they are following the user and it is not yet active
            if(user.getFriends().contains(new Friend(user, currentUser)) && !friendRepository.findById(new Friend.Key(user, currentUser)).get().isActive()){
                connections.add("pendingRequest");
            }

            userMap.put("connections", connections);
            userConnections.add(userMap);
        }

        return userConnections;
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
