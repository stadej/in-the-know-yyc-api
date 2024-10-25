package com.intheknowyyc.api.data.translators;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.controllers.responses.PaginatedEventResponse;
import com.intheknowyyc.api.data.models.Event;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class EventTranslator {

    public Event translateToEvent(EventRequest eventRequest) {
        Event event = new Event();
        event.setOrganizationName(eventRequest.getOrganizationName());
        event.setEventName(eventRequest.getEventName());
        event.setEventDescription(eventRequest.getEventDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setFreeEvent(eventRequest.isFreeEvent());
        event.setEventCost(eventRequest.getEventCost());
        event.setEventLink(eventRequest.getEventLink());
        event.setEventType(eventRequest.getEventType());
        event.setEventImage(eventRequest.getEventImage());
        event.setLocation(eventRequest.getLocation());
        event.setIndustry(eventRequest.getIndustry());
        event.setSpeakers(eventRequest.getSpeakers());
        return event;
    }

    public PaginatedEventResponse translateToPaginatedResponse(Page<Event> eventPage) {
        PaginatedEventResponse response = new PaginatedEventResponse();
        response.setContent(eventPage.getContent());
        response.setSize(eventPage.getSize());
        response.setNumber(eventPage.getNumber());
        response.setTotalPages(eventPage.getTotalPages());
        response.setTotalElements(eventPage.getTotalElements());
        return response;
    }
}
