package com.events.events.controllers;

import com.events.events.models.Event;
import com.events.events.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@Controller
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
