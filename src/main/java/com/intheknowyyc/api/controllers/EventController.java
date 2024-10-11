package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.translators.EventTranslator;
import com.intheknowyyc.api.services.EventService;
import com.intheknowyyc.api.utils.AuthenticatedUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Event entities.
 * Provides endpoints to perform CRUD operations on Event entities.
 */
@RestController
@RequestMapping(path = "/events")
@Tag(name = "Event Controller", description = "API for managing events")
@SecurityRequirement(name = "swagger-auth")
public class EventController {

    private final EventService eventService;
    private final EventTranslator eventTranslator;

    @Autowired
    public EventController(EventService eventService, EventTranslator eventTranslator) {
        this.eventService = eventService;
        this.eventTranslator = eventTranslator;
    }

    /**
     * Retrieves all events.
     *
     * @return a list of all events
     */
    @Operation(summary = "Get all events",
            description = "Retrieve a list of all events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of events", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Event.class)), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId the ID of the event to retrieve
     * @return the event with the specified ID
     */
    @Operation(summary = "Get event by ID",
            description = "Retrieve an event by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved event", content = {@Content(schema = @Schema(implementation = Event.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Event not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(path = "/{eventId}")
    public ResponseEntity<Event> getOneById(@PathVariable @Parameter(description = "ID of the event to retrieve") int eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    /**
     * Creates a new event.
     *
     * @param eventRequest the event to create
     */
    @Operation(summary = "Create a new event",
            description = "Create a new event. All users can create events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully", content = {@Content(schema = @Schema(implementation = Event.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Event> createNewEvent(@Valid @RequestBody @Parameter(description = "Details of the event to create") EventRequest eventRequest) {
        UserDetails userDetails = AuthenticatedUserUtil.getAuthenticatedUser();

        Event createdEvent = eventService.createNewEvent(
                eventTranslator.translateToEvent(eventRequest),
                userDetails != null ? userDetails.getUsername() : null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    /**
     * Updates an existing event.
     *
     * @param eventId the ID of the event to update
     * @param request the updated event data
     */
    @Operation(summary = "Update event by ID",
            description = "Update an event. Only administrators can update events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully", content = {@Content(schema = @Schema(implementation = Event.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Event not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{eventId}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable @Parameter(description = "ID of the event to update") int eventId,
            @Valid @RequestBody @Parameter(description = "Updated details of the event") EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, request));
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId the ID of the event to delete
     */
    @Operation(summary = "Delete event by ID",
            description = "Delete an event by its ID. Only administrators can delete events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Event not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable @Parameter(description = "ID of the event to delete") int eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

}