package com.events.events.controllers;

import com.events.events.error.BadRequestException;
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
@RequestMapping("/v1/events")
public class EventController {

    @Autowired
    private EventService eventService;


    @RequestMapping(method = RequestMethod.GET)
    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<Event> getAllEventsForUser(Principal principal){
        return eventService.getAllEventsForUser(principal.getName());
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


    @RequestMapping(value = "/{id}/participants/{userId}", method = RequestMethod.POST)
    public Event addParticipantToEvent(@PathVariable("id") int id, @PathVariable("userId") int userId){
        return eventService.addSingleParticipantToEvent(id, userId);
    }


}
