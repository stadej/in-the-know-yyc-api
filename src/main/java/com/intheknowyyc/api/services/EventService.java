package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.exceptions.ResourceNotFoundException;
import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.repositories.EventRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.intheknowyyc.api.utils.Constants.EVENT_NOT_FOUND_BY_ID;

/**
 * Service class for managing Event entities.
 * Provides methods to perform CRUD operations on Event entities.
 */
@Service
public class EventService {

    private final UserService userService;
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository, UserService userService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    public Page<Event> getFilteredEvents(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String eventType,
            String organizationName,
            String searchText,
            Pageable pageable
    ) {
        return eventRepository.findFilteredEvents(startDate, endDate, eventType, organizationName, searchText, pageable);
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId the ID of the event to retrieve
     * @return the event with the specified ID
     */
    public Event getEventById(int eventId) {
        return eventRepository.findEventById(eventId).orElseThrow(() -> new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));
    }

    /**
     * Creates a new event with the provided details.
     *
     * @param event the event to create
     * @return a new created event
     */
    public Event createNewEvent(@Valid Event event, String userName) {

        if (event.isFreeEvent() && event.getEventCost().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Event cost must be zero for free events.");
        } else if (!event.isFreeEvent() && event.getEventCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Event cost must be greater than zero for paid events.");
        }
        event.setUser(userService.loadUserByUsername(Objects.requireNonNullElse(userName, "user16@ex.com")));
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    /**
     * Updates an existing event with the provided details.
     *
     * @param eventId      the ID of the event to update
     * @param eventRequest the new event data
     */
    @Transactional
    public Event updateEvent(int eventId, EventRequest eventRequest) {
        return eventRepository.findById(eventId).map(event -> {
            event.setOrganizationName(eventRequest.getOrganizationName());
            event.setEventName(eventRequest.getEventName());
            event.setEventDescription(eventRequest.getEventDescription());
            event.setEventDate(eventRequest.getEventDate());
            event.setFreeEvent(eventRequest.isFreeEvent());
            event.setEventCost(eventRequest.getEventCost());
            if (event.isFreeEvent() && event.getEventCost().compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException("Event cost must be zero for free events.");
            } else if (!event.isFreeEvent() && event.getEventCost().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Event cost must be greater than zero for paid events.");
            }
            event.setEventLink(eventRequest.getEventLink());
            event.setEventType(eventRequest.getEventType());
            event.setUpdatedAt(LocalDateTime.now());
            return eventRepository.save(event);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId the ID of the event to delete
     */
    public void deleteEvent(int eventId) {
        if (eventRepository.findEventById(eventId).isPresent()) {
            eventRepository.deleteById(eventId);
        } else {
            throw new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId));
        }
    }
}