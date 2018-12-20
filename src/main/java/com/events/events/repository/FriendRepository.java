package com.events.events.repository;

import com.events.events.models.Friend;
import com.events.events.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Friend.Key> {

    @Query(value = "SELECT * FROM friends f WHERE f.friend_id = :userId", nativeQuery = true)
    List<Friend> getAllFollowers(@Param("userId") int userId);

    @Query(value = "SELECT * FROM friends f where f.owner_id = :userId", nativeQuery = true)
    List<Friend> getAllFollowing(@Param("userId") int userId);
}
