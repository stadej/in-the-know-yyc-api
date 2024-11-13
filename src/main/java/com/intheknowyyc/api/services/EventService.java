package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.exceptions.BadRequestException;
import com.intheknowyyc.api.data.exceptions.ResourceNotFoundException;
import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.models.EventFilters;
import com.intheknowyyc.api.data.models.EventStatus;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.repositories.EventRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.intheknowyyc.api.utils.Constants.EVENT_NOT_FOUND_BY_ID;

/**
 * Service class for managing Event entities.
 * Provides methods to perform CRUD operations on Event entities.
 */
@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Page<Event> getFilteredEvents(
            EventFilters eventFilters
    ) {
        return eventRepository.findFilteredEvents(eventFilters.getStartDate(),
                eventFilters.getEndDate(),
                eventFilters.getEventType(),
                eventFilters.getOrganizationName(),
                eventFilters.getLocation(),
                eventFilters.getSearchText(),
                eventFilters.getPageable(),
                eventFilters.getStatus());
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId the ID of the event to retrieve
     * @return the event with the specified ID
     */
    public Event getEventById(long eventId) {
        return eventRepository.findEventById(eventId).orElseThrow(() -> new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));
    }

    /**
     * Creates a new event with the provided details.
     *
     * @param event the event to create
     * @param user the user
     * @return a new created event
     */
    public Event createNewEvent(@Valid Event event, User user) {

        if (event.isFreeEvent() && event.getEventCost().compareTo(BigDecimal.ZERO) != 0) {
            throw new BadRequestException("Event cost must be zero for free events.");
        } else if (!event.isFreeEvent() && event.getEventCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Event cost must be greater than zero for paid events.");
        }

        boolean isAdmin = user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        event.setStatus(isAdmin ? EventStatus.APPROVED : EventStatus.PENDING);

        event.setUser(user);
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
    public Event updateEvent(long eventId, EventRequest eventRequest) {
        return eventRepository.findById(eventId).map(event -> {
            event.setOrganizationName(eventRequest.getOrganizationName());
            event.setEventName(eventRequest.getEventName());
            event.setEventDescription(eventRequest.getEventDescription());
            event.setEventDate(eventRequest.getEventDate());
            event.setFreeEvent(eventRequest.isFreeEvent());
            event.setEventCost(eventRequest.getEventCost());
            if (event.isFreeEvent() && event.getEventCost().compareTo(BigDecimal.ZERO) != 0) {
                throw new BadRequestException("Event cost must be zero for free events.");
            } else if (!event.isFreeEvent() && event.getEventCost().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Event cost must be greater than zero for paid events.");
            }
            event.setEventLink(eventRequest.getEventLink());
            event.setEventType(eventRequest.getEventType());
            event.setEventImage(eventRequest.getEventImage());
            event.setUpdatedAt(LocalDateTime.now());
            return eventRepository.save(event);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId the ID of the event to delete
     */
    public void deleteEvent(long eventId) {
        if (eventRepository.findEventById(eventId).isPresent()) {
            eventRepository.deleteById(eventId);
        } else {
            throw new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId));
        }
    }

    public Event approveEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));

        if (EventStatus.APPROVED.equals(event.getStatus())) {
            throw new BadRequestException("Event is already approved.");
        }

        event.setStatus(EventStatus.APPROVED);
        return eventRepository.save(event);
    }

    public Event rejectEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));

        if (event.getStatus() == EventStatus.APPROVED) {
            throw new BadRequestException("Cannot reject an already approved event.");
        }
        if (event.getStatus() == EventStatus.REJECTED) {
            throw new BadRequestException("Event is already rejected.");
        }

        event.setStatus(EventStatus.REJECTED);
        return eventRepository.save(event);
    }
}