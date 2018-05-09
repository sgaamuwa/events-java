package com.events.events.repository;

import com.events.events.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, EventRepositoryCustom {

    List<Event> findByDate(LocalDate date);
}
