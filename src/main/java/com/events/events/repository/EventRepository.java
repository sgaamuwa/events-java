package com.events.events.repository;

import com.events.events.models.Event;
import com.events.events.models.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>{

    List<Event> findByStartTimeOrderByStartTimeAsc(LocalDateTime date);

    List<Event> findByEventStatus(EventStatus eventStatus);

    @Query(value = "SELECT * FROM events e WHERE e.user_id IN :friends ORDER BY start_time ASC ", nativeQuery = true)
    List<Event> findAllEventsByFriends(@Param("friends") List<Integer> friends);


    List<Event> findByStartTimeGreaterThanOrderByStartTimeAsc(LocalDateTime date);

    List<Event> findByStartTimeLessThanOrderByStartTimeAsc(LocalDateTime date);

    List<Event> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime dateFrom, LocalDateTime dateTo);

}
