package com.events.events.repository;

import com.events.events.models.Event;
import com.events.events.models.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>{

    List<Event> findByDate(LocalDate date);

    List<Event> findByEventStatus(EventStatus eventStatus);

    @Query(value = "SELECT * FROM events e WHERE e.user_id IN (:friends)", nativeQuery = true)
    List<Event> findAllEventsByFriends(@Param("friends") List<Integer> friends);

    @Query(value = "SELECT * FROM events e WHERE e.date >= :date", nativeQuery = true)
    List<Event> getEventsAfterDate(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM events e WHERE e.date <= :date", nativeQuery = true)
    List<Event> getEventsBeforeDate(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM events e WHERE e.date BETWEEN :dateFrom AND :dateTo", nativeQuery = true)
    List<Event> getEventsBetweenDates(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

}
