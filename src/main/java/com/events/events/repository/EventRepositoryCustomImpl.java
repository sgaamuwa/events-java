package com.events.events.repository;

import com.events.events.models.Event;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;

@Repository
public class EventRepositoryCustomImpl implements EventRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> getEventsBetweenDates(LocalDate dateFrom, LocalDate dateTo){
        Query query = entityManager.createNativeQuery("SELECT * FROM events e WHERE e.date BETWEEN ? AND ?", Event.class);
        query.setParameter(1, dateFrom);
        query.setParameter(2, dateTo);

        return query.getResultList();
    }

    public List<Event> getEventsAfterDate(LocalDate date){
        Query query = entityManager.createNativeQuery("SELECT * FROM events e WHERE e.date >= ?", Event.class);
        query.setParameter(1, date);

        return query.getResultList();
    }

    public List<Event> getEventsBeforeDate(LocalDate date){
        Query query = entityManager.createNativeQuery("SELECT * FROM events e WHERE e.date <= ?", Event.class);
        query.setParameter(1, date);

        return query.getResultList();
    }
}
