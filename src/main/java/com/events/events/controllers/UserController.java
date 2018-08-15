package com.events.events.controllers;

import com.events.events.models.User;
import com.events.events.models.Views;
import com.events.events.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/signup",method = RequestMethod.POST)
    public User registerUser(@Valid @RequestBody User user){
        return userService.saveUser(user);
    }

    @RequestMapping(value = "/{id}/password", method = RequestMethod.POST)
    public ResponseEntity<String> changeUserPassword(@PathVariable("id") int id, @RequestParam("oldPassword") String oldPassword, @RequestParam("password") String password){
        userService.changePassword(id, oldPassword, password);
        return new ResponseEntity<>("Password was changed successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(Views.UserExtended.class)
    public User findOneUser(@PathVariable("id") int id ){
        return userService.getUserById(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    @JsonView(Views.Summarised.class)
    public List<User> findAllUsers(){
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUserById(@PathVariable("id") int id){
        userService.deleteUser(id);
    }

    @RequestMapping(value = "/facebook", method = RequestMethod.GET)
    public String getFacebookUserName(){
        return userService.facebookUserName();
    }

}
