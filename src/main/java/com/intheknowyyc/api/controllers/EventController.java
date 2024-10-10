package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.services.EventService;
import com.intheknowyyc.api.services.UserService;
import com.intheknowyyc.api.utils.AuthenticatedUserUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Event entities.
 * Provides endpoints to perform CRUD operations on Event entities.
 */
@RestController
@RequestMapping(path = "/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     * Retrieves all events.
     *
     * @return a list of all events
     */
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId the ID of the event to retrieve
     * @return the event with the specified ID
     */
    @GetMapping(path = "/{eventId}")
    public Event getOneById(@PathVariable int eventId) {
        return eventService.getEventById(eventId);
    }

    /**
     * Creates a new event.
     *
     * @param event the event to create
     */
    @PostMapping
    public ResponseEntity<Event> createNewEvent(@Valid @RequestBody Event event) {
        UserDetails userDetails = AuthenticatedUserUtil.getAuthenticatedUser();
        if (userDetails != null) {
            User user = userService.loadUserByUsername(userDetails.getUsername());
            event.setUser(user);
            eventService.createNewEvent(event);
        } else {
            event.setUser(userService.loadUserByUsername("user16@ex.com"));
            eventService.createNewEvent(event);
        }
        return ResponseEntity.ok(event);
    }

    /**
     * Updates an existing event.
     *
     * @param eventId the ID of the event to update
     * @param request the updated event data
     */
    @PutMapping(path = "/{eventId}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable int eventId,
            @Valid @RequestBody EventRequest request
    ) {
        Event event = eventService.updateEvent(eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId the ID of the event to delete
     */
    @DeleteMapping(path = "/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable int eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

}