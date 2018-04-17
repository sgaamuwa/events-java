package com.events.events.services;

import com.events.events.models.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);
    User getUserById(int id);
    List<User> getAllUsers();
    void deleteUser(User user);

}
