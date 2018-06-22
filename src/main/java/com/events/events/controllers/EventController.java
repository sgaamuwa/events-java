package com.events.events.controllers;

import com.events.events.models.Event;
import com.events.events.models.Views;
import com.events.events.services.EventService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
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
    @JsonView(Views.EventExtended.class)
    public Event createEvent(@Valid @RequestBody Event event, Principal principal){
        return eventService.saveEvent(event, principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET)
    @JsonView(Views.Summarised.class)
    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }

    @RequestMapping(method = RequestMethod.GET, params = "date")
    @JsonView(Views.Summarised.class)
    public List<Event> getAllEventsByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return eventService.getEventsByDate(date);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dateFrom", "dateTo"})
    @JsonView(Views.Summarised.class)
    public List<Event> getAllEventsBetweenSpecificDates(@RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom, @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo){
        return eventService.getEventsBetweenDates(dateFrom, dateTo);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"startingDate"})
    @JsonView(Views.Summarised.class)
    public List<Event> getAllEventsAfterDate(@RequestParam("startingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startingDate){
        return eventService.getEventsAfterDate(startingDate);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"endingDate"})
    @JsonView(Views.Summarised.class)
    public List<Event> getAllEventsBeforeDate(@RequestParam("endingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endingDate){
        return eventService.getEventsBeforeDate(endingDate);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(Views.EventExtended.class)
    public Event getEventById(@PathVariable("id") int id){
        return eventService.getEventById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @JsonView(Views.EventExtended.class)
    public void deleteEventById(@PathVariable("id") int id){
        eventService.deleteEvent(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @JsonView(Views.EventExtended.class)
    public Event updateEventById(@PathVariable("id") int id, @Valid @RequestBody Event event){
        return eventService.updateEvent(id, event);
    }


}
