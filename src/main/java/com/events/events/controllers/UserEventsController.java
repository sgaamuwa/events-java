package com.events.events.controllers;

import com.events.events.models.Event;
import com.events.events.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/users/{id}/events")
public class UserEventsController {

    @Autowired
    private EventService eventService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Event> getEventsByUser(@PathVariable("id") int id){
        return eventService.getEventsByUser(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Event postEventByUser(@PathVariable("id") int id, @Valid @RequestBody Event event){
        return eventService.saveEvent(event, id);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.PUT)
    public Event updateEventById(@PathVariable("id") int id, @PathVariable("eventId") int eventId, @Valid @RequestBody Event event){
        return eventService.updateEvent(eventId, id, event);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.DELETE)
    public void deleteEventById(@PathVariable("id") int id, @PathVariable("eventId") int eventId){
        eventService.deleteEvent(eventId, id);
    }
}
