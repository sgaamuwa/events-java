package com.events.events.services;

import com.events.events.error.AuthenticationException;
import com.events.events.error.DuplicateCreationException;
import com.events.events.error.EmptyListException;
import com.events.events.error.NotFoundException;
import com.events.events.models.Event;
import com.events.events.models.User;
import com.events.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if(!user.isPresent()){
            throw new UsernameNotFoundException("There is no user with the username: "+ username);
        }

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), Collections.emptyList());
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
    public void changePassword(int userId, String oldPassword, String newPassword) {
        User user = verifyAndReturnUser(userId);
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
    public String facebookUserName() {
        Facebook facebook = new FacebookTemplate("EAAFO9ZCCXJuMBAIkYojNtQ6FG4ZCNSrv3M8Fy7s0ywuBFuJDe9HRKGl0JK0qeyT1aMLs0QYKslGo83eWwBleGrHxIlIL2BK1lYwcauG2U5vDP051KTIZCN9l7ZBO8TWxQResuznQ2x1kh13sAUsZCIEs39oePHcMzHJ4Kj2Nxmw53o3H5k8LoAzZCx3WEJEdvW6SY2LZBpWkZBnUbOhcjJZBGI6blUOJZAmbHEznYBW4TVrXZBTIoZCiGQzg");
        String[] fields = {"id", "email", "first_name", "last_name"};
        return facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields).getEmail();
    }

    private User verifyAndReturnUser(int userId){
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            throw new NotFoundException("User with id: "+userId+" not found");
        }
        return user.get();
    }

}
