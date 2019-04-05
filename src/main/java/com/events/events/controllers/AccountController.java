package com.events.events.controllers;

import com.events.events.models.User;
import com.events.events.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public User registerUser(@Valid @RequestBody User user){
        user.add(linkTo(UserController.class).withSelfRel());
        return userService.saveUser(user);
    }

    @RequestMapping(value = "/confirmAccount", method = RequestMethod.POST)
    public ResponseEntity<String> confirmUserAccount(@RequestParam("token") String token){
        return new ResponseEntity<>("Got the token: "+token, HttpStatus.OK);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity<String> changeUserPassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("password") String password, Principal principal){
        userService.changePassword(oldPassword, password, principal.getName());
        return new ResponseEntity<>("Password was changed successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/followRequests/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> acceptFollowRequest(@PathVariable("id") int id, @RequestBody Map<String, Object> body){
        return new ResponseEntity<>("The user accepted the follow", HttpStatus.OK);
    }

    @RequestMapping(value = "/followRequests", method = RequestMethod.GET)
    public ResponseEntity<String> getAllFollowerRequests(){
        return new ResponseEntity<>("This is the follower Requests", HttpStatus.OK);
    }
}
