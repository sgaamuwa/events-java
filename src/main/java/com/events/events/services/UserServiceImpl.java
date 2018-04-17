package com.events.events.services;

import com.events.events.models.User;
import com.events.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public User saveUser(User user){
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User getUserById(int id) {
        return userRepository.getOne(new Integer(id).toString());
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
