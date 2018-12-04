package com.events.events.controllers;

import com.events.events.models.Event;
import com.events.events.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @RequestMapping(method = RequestMethod.POST)
    public Event createEvent(@Valid @RequestBody Event event, Principal principal){
        return eventService.saveEvent(event, principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }

    @RequestMapping(method = RequestMethod.GET, params = "date")
    public List<Event> getAllEventsByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return eventService.getEventsByDate(date);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dateFrom", "dateTo"})
    public List<Event> getAllEventsBetweenSpecificDates(@RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom, @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo){
        return eventService.getEventsBetweenDates(dateFrom, dateTo);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"startingDate"})
    public List<Event> getAllEventsAfterDate(@RequestParam("startingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startingDate){
        return eventService.getEventsAfterDate(startingDate);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"endingDate"})
    public List<Event> getAllEventsBeforeDate(@RequestParam("endingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endingDate){
        return eventService.getEventsBeforeDate(endingDate);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event getEventById(@PathVariable("id") int id){
        return eventService.getEventById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteEventById(@PathVariable("id") int id){
        eventService.deleteEvent(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Event updateEventById(@PathVariable("id") int id, @Valid @RequestBody Event event){
        return eventService.updateEvent(id, event);
    }


}
