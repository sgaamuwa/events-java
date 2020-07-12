package com.events.events.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.events.events.models.Event;
import com.events.events.models.User;
import com.events.events.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/users/{id}/events")
public class UserEventsController {

    @Autowired
    private EventService eventService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Event> getAllEventsCreatedByUser(@PathVariable("id") int id){
        return eventService.getAllEventsCreatedByUser(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Event postEventByUser(@PathVariable("id") int id, @Valid @RequestBody Event event){
        return addHateoasLinksToEvent(eventService.saveEvent(event, id), id);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    public Event getEventById(@PathVariable("id") int id, @PathVariable("eventId") int eventId){
        return addHateoasLinksToEvent(eventService.getEventById(eventId, id), id);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.PUT)
    public Event updateEventById(@PathVariable("id") int id, @PathVariable("eventId") int eventId, @Valid @RequestBody Event event){
        return addHateoasLinksToEvent(eventService.updateEvent(eventId, id, event), id);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.DELETE)
    public void deleteEventById(@PathVariable("id") int id, @PathVariable("eventId") int eventId){
        eventService.deleteEvent(eventId, id);
    }

    @RequestMapping(value = "/{eventId}/uploadImage", method = RequestMethod.POST)
    public ResponseEntity<Void> uploadImageForEvent(@PathVariable("id") int id, @PathVariable("eventId") int eventId, @RequestPart(value = "image")MultipartFile multipartFile){
        eventService.uploadEventImage(eventId, id, multipartFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{eventId}/downloadImage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<ByteArrayResource> downloadEventImage(@PathVariable("id") int id, @PathVariable("eventId") int eventId){
        return new ResponseEntity<>(eventService.downloadEventImage(eventId, id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{eventId}/invites", method = RequestMethod.POST)
    public Event postInvitesForEvent(@PathVariable("id") int id, @PathVariable("eventId") int eventId, @RequestBody Map<String, int[]> payload){
        return addHateoasLinksToEvent(eventService.addInviteesToEvent(id, eventId, payload.get("invitees")), id);
    }

    @RequestMapping(value = "/{eventId}/invites/{inviteeId}", method = RequestMethod.DELETE)
    public Event deleteInvitesForEvent(@PathVariable("id") int id, @PathVariable("eventId") int eventId, @PathVariable("inviteeId") int inviteeId, Principal principal){
        return addHateoasLinksToEvent(eventService.deleteInviteeFromEvent(id, eventId, inviteeId, principal.getName()), id);
    }

    @RequestMapping(value = "/attending", method = RequestMethod.GET)
    public List<Event> getAllEventsUserIsAttending(@PathVariable("id") int id, Principal principal){
        return eventService.getAllEventsUserIsAttending(id, principal.getName());
    }

    @RequestMapping(value = "/invites", method = RequestMethod.GET)
    public List<Event> getAllEventsUserIsInvitedTo(@PathVariable("id") int id, Principal principal){
        return eventService.getAllEventsUserIsInvitedTo(id, principal.getName());
    }

    private Event addHateoasLinksToEvent(Event event, int id){
        event.add(linkTo(methodOn(UserEventsController.class).getEventById(id, event.getEventId())).withSelfRel().withType("GET, PUT, UPDATE"));
        event.add(linkTo(methodOn(UserEventsController.class).uploadImageForEvent(id, event.getEventId(), null)).withRel("uploadEventImage"));
        event.add(linkTo(methodOn(UserEventsController.class).downloadEventImage(id, event.getEventId())).withRel("downloadEventImage"));
        event.add(linkTo(methodOn(UserEventsController.class).postInvitesForEvent(id, event.getEventId(), null)).withRel("placeInvite"));
        return event;
    }
}
