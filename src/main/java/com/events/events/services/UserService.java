package com.events.events.services;

import com.events.events.models.Event;
import com.events.events.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User saveUser(User user);
    User getUserById(int id);
    List<User> getAllUsers();
    void deleteUser(int userId);
    void changePassword(int userId, String oldPassword, String newPassword);
    List<Event> listEventsByUser(int userId);
    List<Event> listEventsUserIsAttending(int userId);
    String facebookUserName();

}
