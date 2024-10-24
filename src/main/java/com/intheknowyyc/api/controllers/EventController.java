package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.controllers.responses.ErrorResponse;
import com.intheknowyyc.api.controllers.responses.PaginatedEventResponse;
import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.translators.EventTranslator;
import com.intheknowyyc.api.services.EventService;
import com.intheknowyyc.api.utils.AuthenticatedUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
     * Retrieves a paginated list of events with optional filters and sorting.
     *
     * @param startDate        optional filter by start date (example: "2024-10-01T10:00:00")
     * @param endDate          optional filter by end date (example: "2024-12-01T18:00:00")
     * @param eventType        optional filter by event type (example: "conference")
     * @param organizationName optional filter by organization name (example: "TechOrg")
     * @param searchText       optional text search for event details (example: "technology")
     * @param page             page number (default: 0, example: 0)
     * @param size             number of records per page (default: 10, example: 10)
     * @param sortField        field to sort by (example: "eventDate")
     * @param sortDirection    sorting direction: 'asc' or 'desc' (example: "asc")
     * @return a paginated list of filtered events
     */
    @Operation(summary = "Get filtered and paginated events",
            description = "Retrieve a list of events with optional filters and pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of events", content = {@Content(schema = @Schema(implementation = PaginatedEventResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PaginatedEventResponse> getFilteredEvents(
            @Parameter(description = "Start date for filtering events", example = "2024-10-01T10:00:00")
            @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date for filtering events", example = "2024-12-01T18:00:00")
            @RequestParam(required = false) LocalDateTime endDate,
            @Parameter(description = "Type of the event", example = "conference")
            @RequestParam(required = false) String eventType,
            @Parameter(description = "Organization name", example = "TechOrg")
            @RequestParam(required = false) String organizationName,
            @Parameter(description = "Search text for filtering events", example = "technology")
            @RequestParam(required = false) String searchText,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by", example = "eventDate")
            @RequestParam(required = false) String sortField,
            @Parameter(description = "Sorting direction: 'asc' or 'desc'", example = "asc")
            @RequestParam(required = false) @Pattern(regexp = "^(asc|desc)?$", message = "Sorting direction must be 'asc' or 'desc'") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(page, size);
        if (sortField != null && !sortField.isEmpty()) {
            Sort sorting = Sort.unsorted();
            if (!sortDirection.isEmpty()) {
                sorting = "asc".equalsIgnoreCase(sortDirection)
                        ? Sort.by(Sort.Order.asc(sortField))
                        : Sort.by(Sort.Order.desc(sortField));
            }
            pageable = PageRequest.of(page, size, sorting);
        }

        Page<Event> events = eventService.getFilteredEvents(startDate,
                endDate,
                eventType,
                organizationName,
                searchText,
                pageable);

        return ResponseEntity.ok(eventTranslator.translateToPaginatedResponse(events));
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
            description = "Create a new event. Only administrators can create events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully", content = {@Content(schema = @Schema(implementation = Event.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
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