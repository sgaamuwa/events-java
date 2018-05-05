package com.events.events.services;

import com.events.events.error.DuplicateCreationException;
import com.events.events.error.EmptyListException;
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
        //check if the username exists
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new DuplicateCreationException("User with the username: "+user.getUsername()+" already exists");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        return userRepository.save(user);
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

    private User verifyAndReturnUser(int userId){
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            throw new NotFoundException("User with id: "+userId+" not found");
        }
        return user.get();
    }

}
