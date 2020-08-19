package com.events.events.controllers;

import com.events.events.models.User;
import com.events.events.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

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

    @RequestMapping(value = "/{id}/uploadImage", method = RequestMethod.POST)
    public User uploadImageForUserById(@PathVariable("id") int id, @RequestPart(value = "image") MultipartFile multipartFile){
        return userService.uploadUserImage(id, multipartFile);
    }

    @RequestMapping(value = "/{id}/downloadImage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<ByteArrayResource> downloadImageForUserById(@PathVariable("id") int id){
        return new ResponseEntity<>(userService.downloadUserImage(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
    public List<User> getUserFollowers(@PathVariable("id") int id){
        // method to return all the users that are following this particular user
        return userService.getAllFollowers(id);
    }

    @RequestMapping(value = "/{id}/followers/{followerId}/accept", method = RequestMethod.POST)
    public ResponseEntity<String> acceptFollowRequest(@PathVariable("id") int id, @PathVariable("followerId") int followerId, Principal principal){
        userService.acceptFollowRequest(id, followerId, principal.getName());
        return new ResponseEntity<>("Request from user id: "+followerId+"accepted", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/followers/{followerId}/reject", method = RequestMethod.POST)
    public ResponseEntity<String> rejectFollowRequest(@PathVariable("id") int id, @PathVariable("followerId") int followerId, Principal principal){
        userService.rejectFollowRequest(id, followerId, principal.getName());
        return new ResponseEntity<>("Request from user id: "+followerId+"rejected", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/friends", method = RequestMethod.GET)
    public List<User> getUserFriends(@PathVariable("id") int id){
       // method to return all the users that this particular user is following
        return userService.getAllFollowing(id);
    }

    @RequestMapping(value = "/{id}/friends/{friendId}", method = RequestMethod.POST)
    public ResponseEntity<String> postFollowRequest(@PathVariable("id") int id, @PathVariable("friendId") int friendId, Principal principal){
        userService.addFriend(id, friendId, principal.getName());
        return new ResponseEntity<>("Friend with user id: "+friendId+" requested", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/friends/{friendId}/unfollow", method = RequestMethod.POST)
    public ResponseEntity<String> unFollowUser(@PathVariable("id") int id, @PathVariable("friendId") int friendId, Principal principal){
        userService.unFollowUser(id, friendId, principal.getName());
        return new ResponseEntity<>("You have stopped follow user with userId: "+id, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/friendships/lookup", method = RequestMethod.POST)
    public List<Map<String, Object>> getUserConnections(@PathVariable int id, @RequestBody Map<String, int[]> payload, Principal principal){
        return userService.userConnections(payload.get("ids"), id, principal.getName());
    }

    @RequestMapping(value = "/search", params = {"q"}, method = RequestMethod.GET)
    public List<User> searchUsers(@RequestParam("q") String searchTerm){
        return userService.searchUsers(searchTerm);
    }

    @RequestMapping(value = "/facebook/setToken", method = RequestMethod.POST)
    public ResponseEntity<String> setFacebookAccessToken(@RequestBody Map<String, String> payload, Principal principal){
        userService.setFacebookIdAndToken(payload.get("token"), principal.getName());
        return new ResponseEntity<>("Token for the user was set", HttpStatus.OK);
    }

}
