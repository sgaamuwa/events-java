package com.events.events.controllers;

import com.events.events.models.Event;
import com.events.events.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping(value = "/{eventId}/uploadImage", method = RequestMethod.POST)
    public void uploadImageForEvent(@PathVariable("id") int id, @PathVariable("eventId") int eventId, @RequestPart(value = "image")MultipartFile multipartFile){
        eventService.uploadEventImage(eventId, id, multipartFile);
    }

    @RequestMapping(value = "/{eventId}/downloadImage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<ByteArrayResource> downloadEventImage(@PathVariable("id") int id, @PathVariable("eventId") int eventId){
        return new ResponseEntity<>(eventService.downloadEventImage(eventId, id), HttpStatus.OK);
    }
}
