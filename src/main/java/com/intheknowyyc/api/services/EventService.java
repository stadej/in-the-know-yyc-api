package com.intheknowyyc.api.services;

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

        boolean isAdmin = user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        event.setStatus(isAdmin ? EventStatus.APPROVED : EventStatus.PENDING);

        event.setUser(user);
        return eventRepository.save(event);
    }

    /**
     * Updates an existing event with the provided details.
     *
     * @param eventId      the ID of the event to update
     * @param updatedEvent the new event data
     */
    public Event updateEvent(long eventId, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(EVENT_NOT_FOUND_BY_ID, eventId)));

        if (updatedEvent.getOrganizationName() != null) {
            existingEvent.setOrganizationName(updatedEvent.getOrganizationName());
        }
        if (updatedEvent.getEventName() != null) {
            existingEvent.setEventName(updatedEvent.getEventName());
        }
        if (updatedEvent.getEventDescription() != null) {
            existingEvent.setEventDescription(updatedEvent.getEventDescription());
        }
        if (updatedEvent.getEventDate() != null) {
            existingEvent.setEventDate(updatedEvent.getEventDate());
        }
        if (updatedEvent.getFreeEvent() != null) {
            existingEvent.setFreeEvent(updatedEvent.getFreeEvent());
        }
        if (updatedEvent.getEventCost() != null) {
            existingEvent.setEventCost(updatedEvent.getEventCost());
        }

        if (updatedEvent.getEventLink() != null) {
            existingEvent.setEventLink(updatedEvent.getEventLink());
        }
        if (updatedEvent.getEventType() != null) {
            existingEvent.setEventType(updatedEvent.getEventType());
        }
        if (updatedEvent.getEventImage() != null) {
            existingEvent.setEventImage(updatedEvent.getEventImage());
        }
        if (updatedEvent.getLocation() != null) {
            existingEvent.setLocation(updatedEvent.getLocation());
        }
        if (updatedEvent.getIndustry() != null) {
            existingEvent.setIndustry(updatedEvent.getIndustry());
        }
        if (updatedEvent.getSpeakers() != null) {
            existingEvent.setSpeakers(updatedEvent.getSpeakers());
        }

        return eventRepository.save(existingEvent);
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

        if (event.getStatus() == EventStatus.REJECTED) {
            throw new BadRequestException("Event is already rejected.");
        }

        event.setStatus(EventStatus.REJECTED);
        return eventRepository.save(event);
    }
}
