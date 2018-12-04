package com.events.events.controllers;

import com.events.events.models.User;
import com.events.events.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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
    public User findOneUser(@PathVariable("id") int id ){
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

}
