package com.events.events.services;

import com.events.events.error.NotFoundException;
import com.events.events.models.Event;
import com.events.events.models.User;
import com.events.events.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public User saveUser(User user){
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User getUserById(int id) {
        return userRepository.getOne(new Integer(id));
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
    public List<Event> listEventsByUser(int userId) {
        User user = verifyAndReturnUser(userId);
        return user.getCreatedEvents();
    }

    @Override
    public List<Event> listEventsUserIsAttending(int userId) {
        User user = verifyAndReturnUser(userId);
        return user.getAttending();
    }

    private User verifyAndReturnUser(int userId){
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            throw new NotFoundException("User with id: "+userId+" not found");
        }
        return user.get();
    }

}
