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
        if (event.getIs_event_free() && event.getEvent_cost().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Event cost must be zero for free events.");
        } else if (!event.getIs_event_free() && event.getEvent_cost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Event cost must be greater than zero for paid events.");
        }
        event.setCreated_at(LocalDateTime.now());
        event.setUpdated_at(LocalDateTime.now());
        eventRepository.save(event);
    }

    /**
     * Updates an existing event with the provided details.
     *
     * @param eventId the ID of the event to update
     * @param request the new event data
     */
    @Transactional
    public void updateEvent(
            int eventId,
            EventRequest request
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new IllegalStateException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));
        if(!Objects.equals(request.getOrganization_name(), event.getOrganization_name())) {
            event.setOrganization_name(request.getOrganization_name());
        }
        if (!Objects.equals(request.getEvent_name(), event.getEvent_name())) {
            event.setEvent_name(request.getEvent_name());
        }
        if (!Objects.equals(request.getEvent_description(), event.getEvent_description())) {
            event.setEvent_description(request.getEvent_description());
        }
        if (!Objects.equals(request.getEvent_date(), event.getEvent_date())) {
            event.setEvent_date(request.getEvent_date());
        }
        event.setIs_event_free(request.getIs_event_free());
        event.setEvent_cost(request.getEvent_cost());
        if (event.getIs_event_free() && event.getEvent_cost().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Event cost must be zero for free events.");
        } else if (!event.getIs_event_free() && event.getEvent_cost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Event cost must be greater than zero for paid events.");
        }
        if (!Objects.equals(request.getEvent_link(), event.getEvent_link())) {
            event.setEvent_link(request.getEvent_link());
        }
        if (!Objects.equals(request.getEvent_type(), event.getEvent_type())) {
            event.setEvent_type(request.getEvent_type());
        }
        event.setUpdated_at(LocalDateTime.now());
        eventRepository.save(event);
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId the ID of the event to delete
     * @throws IllegalStateException if the event does not exist
     */
    public void deleteEvent(int eventId) {
        if(eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
        } else {
            throw new IllegalStateException(String.format(EVENT_NOT_FOUND_BY_ID, eventId));
        }
    }
}