package com.events.events.controllers;

import com.events.events.models.User;
import com.events.events.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/signup",method = RequestMethod.POST)
    public User registerUser(@Valid @RequestBody User user){
        user.add(linkTo(UserController.class).withSelfRel());
        return userService.saveUser(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User findOneUser(@PathVariable("id") int id, Principal principal ){
        return userService.getUserById(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> findAllUsers(){
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUserById(@PathVariable("id") int id){
        userService.deleteUser(id);
    }

    @RequestMapping(value = "/facebook/setToken", method = RequestMethod.POST)
    public ResponseEntity<String> setFacebookAccessToken(@RequestBody Map<String, String> payload, Principal principal){
        userService.setFacebookIdAndToken(payload.get("token"), principal.getName());
        return new ResponseEntity<>("Token for the user was set", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/followRequest", method = RequestMethod.POST)
    public ResponseEntity<String> requestFollow(@PathVariable("id") int id, Principal principal){
        userService.addFriend(id, principal.getName());
        return new ResponseEntity<>("Follower with user id: "+id+" requested", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/followers/", method = RequestMethod.GET)
    public List<User> getUserFriends(@PathVariable("id") int id){
       return userService.getAllFriends(id);
    }


}
