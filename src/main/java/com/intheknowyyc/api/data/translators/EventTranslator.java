package com.intheknowyyc.api.data.translators;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.models.Event;
import org.springframework.stereotype.Service;

@Service
public class EventTranslator {

    public Event translateToEvent(EventRequest eventRequest){
        Event event = new Event();
        event.setOrganizationName(eventRequest.getOrganizationName());
        event.setEventName(eventRequest.getEventName());
        event.setEventDescription(eventRequest.getEventDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setFreeEvent(eventRequest.isFreeEvent());
        event.setEventCost(eventRequest.getEventCost());
        event.setEventLink(eventRequest.getEventLink());
        event.setEventType(eventRequest.getEventType());
        return event;
    }
}
