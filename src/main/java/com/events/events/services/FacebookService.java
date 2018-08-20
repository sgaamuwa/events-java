package com.events.events.services;

import java.util.List;

public interface FacebookService {

    List<String> getFriendsIds(String accessToken);
    List<String> getUsersFriendsLists(String accessToken);
    List<String> getFriendsIdsInList(String accessToken, String listName);
}
