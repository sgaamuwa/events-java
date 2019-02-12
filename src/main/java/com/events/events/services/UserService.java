package com.events.events.services;

import com.events.events.models.Event;
import com.events.events.models.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {

    /**
     * This method is used to save a user into the system
     * @param user
     * @return
     */
    User saveUser(User user);

    /**
     * This method returns a user based on the Id provided
     * @param id
     * @return
     */
    User getUserById(int id);

    /**
     * This method returns all the users in the system
     * @return
     */
    List<User> getAllUsers();

    /**
     * This method deletes a user based on the id provided
     * @param userId
     */
    void deleteUser(int userId);

    /**
     * This method changes the password of the user to the new one provided
     * @param oldPassword
     * @param newPassword
     * @param username
     */
    @PreAuthorize("#username == authentication.principal.username")
    void changePassword(String oldPassword, String newPassword, String username);

    /**
     * This method is used to add a friend to user
     * @param userId
     * @param friendId
     */
    void addFriend(int userId, int friendId);

    /**
     * This method returns all the friends of a user
     * @param userId
     */
    List<User> getAllFriends(int userId);

    /**
     * This method returns all the users that are following this particular user
     * @param userId
     * @return
     */
    List<User> getAllFollowers(int userId);

    /**
     * This method takes a map with user input i.e. the information about the follow request and the requester's id
     * @param userId
     * @param userInput
     */
    void acceptFollowRequest(int userId, Map<String, Object> userInput);


    List<Event> listEventsByUser(int userId);
    List<Event> listEventsUserIsAttending(int userId);

    /**
     * This method takes the facebook token, requests the user's id and then stores the user's facebook id and token
     * @param token
     * @param username
     */
    void setFacebookIdAndToken(String token, String username);

}
