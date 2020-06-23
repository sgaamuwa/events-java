package com.events.events.repository;

import com.events.events.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM users u WHERE u.first_name LIKE %:parameter% OR u.last_name LIKE %:parameter% OR u.username LIKE %:parameter%", nativeQuery=true)
    List<User> findBySearchTerm(@Param("parameter") String parameter);

    @Query(value = "SELECT id FROM users u WHERE u.facebook_id IN (:facebookIds)", nativeQuery = true)
    List<String> getUserIdsForUsersWithFacebookIds(@Param("facebookIds") List<String> facebookIds);

}
