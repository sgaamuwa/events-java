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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
        userService.activateUser(token);
        return new ResponseEntity<>("User verification successful", HttpStatus.OK);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity<String> changeUserPassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("password") String password, Principal principal){
        userService.changePassword(oldPassword, password, principal.getName());
        return new ResponseEntity<>("Password was changed successfully", HttpStatus.OK);
    }
}
