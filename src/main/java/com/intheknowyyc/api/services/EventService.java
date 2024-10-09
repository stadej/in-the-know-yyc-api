package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.repositories.EventRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.intheknowyyc.api.utils.Constants.EVENT_NOT_FOUND_BY_ID;

/**
 * Service class for managing Event entities.
 * Provides methods to perform CRUD operations on Event entities.
 */
@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieves all events from the repository.
     *
     * @return a list of all events
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId the ID of the event to retrieve
     * @return the event with the specified ID
     * @throws IllegalStateException if the event does not exist
     */
    public Event getEventById(int eventId) {
        return eventRepository.findEventById(eventId).orElseThrow(() -> new IllegalStateException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));
    }

    /**
     * Creates a new event with the provided details.
     *
     * @param event the event to create
     */
    public void createNewEvent(@Valid Event event) {
        if (event.getIsEventFree() && event.getEventCost().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Event cost must be zero for free events.");
        } else if (!event.getIsEventFree() && event.getEventCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Event cost must be greater than zero for paid events.");
        }
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
    }

    /**
     * Updates an existing event with the provided details.
     *
     * @param eventId the ID of the event to update
     * @param request the new event data
     */
    @Transactional
    public Event updateEvent(
            int eventId,
            EventRequest request
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new IllegalStateException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));
        if(!Objects.equals(request.getOrganizationName(), event.getOrganizationName())) {
            event.setOrganizationName(request.getOrganizationName());
        }
        if (!Objects.equals(request.getEventName(), event.getEventName())) {
            event.setEventName(request.getEventName());
        }
        if (!Objects.equals(request.getEventDescription(), event.getEventDescription())) {
            event.setEventDescription(request.getEventDescription());
        }
        if (!Objects.equals(request.getEventDate(), event.getEventDate())) {
            event.setEventDate(request.getEventDate());
        }
        event.setIsEventFree(request.getIsEventFree());
        event.setEventCost(request.getEventCost());
        if (event.getIsEventFree() && event.getEventCost().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Event cost must be zero for free events.");
        } else if (!event.getIsEventFree() && event.getEventCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Event cost must be greater than zero for paid events.");
        }
        if (!Objects.equals(request.getEventLink(), event.getEventLink())) {
            event.setEventLink(request.getEventLink());
        }
        if (!Objects.equals(request.getEventType(), event.getEventType())) {
            event.setEventType(request.getEventType());
        }
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
        return event;
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId the ID of the event to delete
     * @throws IllegalStateException if the event does not exist
     */
    public void deleteEvent(int eventId) {
        if(eventRepository.findEventById(eventId).isPresent()) {
            eventRepository.deleteById(eventId);
        } else {
            throw new IllegalStateException(String.format(EVENT_NOT_FOUND_BY_ID, eventId));
        }
    }
}