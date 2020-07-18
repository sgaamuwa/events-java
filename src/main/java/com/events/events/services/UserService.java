package com.events.events.services;

import com.events.events.models.Event;
import com.events.events.models.User;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

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
     * This method is used to enable a user that uses the verification link in the email
     * @param token
     */
    void activateUser(String token);

    /**
     * This method returns a user based on the Id provided
     * @param id
     * @return
     */
    User getUserById(int id);

    /**
     * This method returns a user given the username
     * @param username
     * @return
     */
    User findUserByUsername(String username);

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
     * This method uploads a user's image and updates the user's object
     * @param userId
     * @param multipartFile
     * @return
     */
    User uploadUserImage(int userId, MultipartFile multipartFile);

    /**
     * This method downloads the user's image given the user's id
     * @param userId
     * @return
     */
    ByteArrayResource downloadUserImage(int userId);

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
     * @param username
     */
    @PreAuthorize("#username == authentication.principal.username")
    void addFriend(int userId, int friendId, String username);

    /**
     * This method returns all the users that this particular user is following"
     * @param userId
     */
    List<User> getAllFollowing(int userId);

    /**
     * This method returns all the users that are following this particular user
     * @param userId
     * @return
     */
    List<User> getAllFollowers(int userId);

    /**
     * This method returns users that have the search term in their firstName lastName or username
     * @param searchTerm
     * @return
     */
    List<User> searchUsers(String searchTerm);

    /**
     * This method returns the connections between the user and the user id they provide
     * Connections include, followed, followedBy, pendingRequest
     * @param userIds
     * @param username
     * @return
     */
    @PreAuthorize("#username == authentication.principal.username")
    List<Map<String, Object>> userConnections(int[] userIds, String username);

    /**
     * This method that accepts a follow request from the user with the given id
     * @param userId
     * @param followerId
     * @param username
     */
    @PreAuthorize("#username == authentication.principal.username")
    void acceptFollowRequest(int userId, int followerId, String username);

    /**
     * This is a method to reject a follow request from the user with the given id
     * @param userId
     * @param followerId
     * @param username
     */
    @PreAuthorize("#username == authentication.principal.username")
    void rejectFollowRequest(int userId, int followerId, String username);

    /**
     * This is a method to stop following someone
     * @param userId
     * @param friendId
     * @param username
     */
    @PreAuthorize("#username == authentication.principal.username")
    void unFollowUser(int userId, int friendId, String username);

    /**
     * This method takes the facebook token, requests the user's id and then stores the user's facebook id and token
     * @param token
     * @param username
     */
    void setFacebookIdAndToken(String token, String username);

}
