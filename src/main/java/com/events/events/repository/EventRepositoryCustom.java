package com.events.events.repository;

import com.events.events.models.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventRepositoryCustom {
    List<Event> getEventsBetweenDates(LocalDate dateFrom, LocalDate dateTo);
    List<Event> getEventsAfterDate(LocalDate date);
    List<Event> getEventsBeforeDate(LocalDate date);
}
