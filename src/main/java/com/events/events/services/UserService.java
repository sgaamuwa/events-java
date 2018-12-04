package com.events.events.services;

import com.events.events.models.Event;
import com.events.events.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

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
     * @param userId
     * @param oldPassword
     * @param newPassword
     */
    void changePassword(int userId, String oldPassword, String newPassword);

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

    List<Event> listEventsByUser(int userId);
    List<Event> listEventsUserIsAttending(int userId);

    /**
     * This method takes the facebook token, requests the user's id and then stores the user's facebook id and token
     * @param token
     * @param username
     */
    void setFacebookIdAndToken(String token, String username);

}
