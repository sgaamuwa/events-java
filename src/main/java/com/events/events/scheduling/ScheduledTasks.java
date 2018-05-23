package com.events.events.scheduling;

import com.events.events.models.Event;
import com.events.events.models.EventStatus;
import com.events.events.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private EventRepository eventRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleClosingEventsDayBefore(){
        // close every event that is 24 hours away
        List<Event> events = eventRepository.findByDate(LocalDate.now().plusDays(1));
        for (Event event : events){
            event.setEventStatus(EventStatus.CLOSED);
        }
        eventRepository.saveAll(events);
    }

}
