package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.services.EventService;
import com.intheknowyyc.api.services.UserService;
import com.intheknowyyc.api.utils.AuthenticatedUserUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void createNewEvent(@Valid @RequestBody Event event) {
        UserDetails userDetails = AuthenticatedUserUtil.getAuthenticatedUser();
        if (userDetails != null) {
            User user = userService.loadUserByUsername(userDetails.getUsername());
            event.setUser(user);
            eventService.createNewEvent(event);
        }
    }

    /**
     * Updates an existing event.
     *
     * @param eventId the ID of the event to update
     * @param request the updated event data
     */
    @PutMapping(path = "/{eventId}")
    public void updateEvent(
            @PathVariable int eventId,
            @Valid @RequestBody EventRequest request
    ) {
        eventService.updateEvent(eventId, request);
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId the ID of the event to delete
     */
    @DeleteMapping(path = "/{eventId}")
    public void deleteEvent(@PathVariable int eventId) {
        eventService.deleteEvent(eventId);
    }

}