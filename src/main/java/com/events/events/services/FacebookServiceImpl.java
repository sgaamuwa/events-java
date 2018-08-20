package com.events.events.services;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FacebookServiceImpl implements FacebookService {

    @Override
    public List<String> getFriendsIds(String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        // get the friends id and return them as a String
        List<String> friendIds = facebook.friendOperations().getFriendIds();
        return friendIds;
    }

    @Override
    public List<String> getUsersFriendsLists(String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        return null;
    }

    @Override
    public List<String> getFriendsIdsInList(String accessToken, String listName){
        Facebook facebook = new FacebookTemplate(accessToken);
        return null;
    }
}
