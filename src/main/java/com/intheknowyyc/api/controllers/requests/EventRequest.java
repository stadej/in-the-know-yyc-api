package com.intheknowyyc.api.controllers.requests;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request object for creating a new event.
 */
@Data
public class EventRequest {

    // The organization_name field is annotated with @NotBlank to ensure that the organization name is provided.
    @NotBlank(message = "Please provide an organization name")
    private String organizationName;

    // The event_name field is annotated with @NotBlank to ensure that the event name is provided.
    @NotBlank(message = "Please provide an event name")
    private String eventName;

    // The event_description field is annotated with @NotBlank to ensure that the event description is provided.
    @NotBlank(message = "Please provide an event description")
    private String eventDescription;

    // The event_date field is annotated with @NotNull to ensure that the event date is provided.
    @NotNull(message = "Please provide an event date")
    private LocalDateTime eventDate;

    // The is_event_free field is annotated with @NotNull to ensure that the event cost is provided.
    @NotNull(message = "Please confirm if the event is free")
    private boolean freeEvent;

    // The event_cost field.
    @Digits(integer = 10, fraction = 2)
    private BigDecimal eventCost;

    // The event_link field is annotated with @NotBlank to ensure that the event link is provided.
    @NotBlank(message = "Please provide an event link")
    private String eventLink;

    // The event_type field is annotated with @NotBlank to ensure that the event type is provided.
    @NotBlank(message = "Please provide an event type")
    private String eventType;

}
