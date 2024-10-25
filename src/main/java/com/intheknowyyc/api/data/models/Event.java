package com.intheknowyyc.api.data.models;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.converters.SpeakersConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing an event.
 * Maps to the "events" table in the database.
 */
@Entity
@Table(name = "events")
@Data
public class Event implements Serializable {

    /**
     * Unique identifier for the event.
     * Generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Name of the organization hosting the event.
     * Maximum length is 100 characters.
     */
    @Column(
            name = "organization_name",
            length = 100
    )
    @NotBlank(message = "Please provide an organization name")
    private String organizationName;

    /**
     * Name of the event.
     * Maximum length is 100 characters.
     * Cannot be null.
     */
    @Column(
            name = "event_name",
            length = 100,
            nullable = false
    )
    @NotBlank(message = "Please provide an event name")
    private String eventName;

    /**
     * Description of the event.
     * Maximum length is 65535 characters.
     * Cannot be null.
     */
    @Column(
            name = "event_description",
            nullable = false,
            length = 65535
    )
    @NotBlank(message = "Please provide an event description")
    private String eventDescription;

    /**
     * Date and time when the event will take place.
     * Cannot be null.
     */
    @Column(
            name = "event_date",
            nullable = false
    )
    private LocalDateTime eventDate;

    /**
     * Indicates whether the event is free.
     */
    @Column(name = "is_event_free")
    @NotNull
    private boolean freeEvent;

    /**
     * Cost of the event.
     * Precision is 10 and scale is 2.
     */
    @Column(
            name = "event_cost",
            precision = 10,
            scale = 2
    )
    private BigDecimal eventCost;

    /**
     * Link to the event.
     * Cannot be null.
     */
    @Column(
            name = "event_link",
            nullable = false
    )
    @NotBlank(message = "Please provide an event link")
    private String eventLink;

    /**
     * Type of the event.
     * Maximum length is 100 characters.
     * Cannot be null.
     */
    @Column(
            name = "event_type",
            length = 100,
            nullable = false
    )
    @NotBlank(message = "Please provide an event type")
    private String eventType;

    @Column(name = "location", nullable = false)
    @NotBlank(message = "Please provide an event location")
    private String location;

    @Column(name = "industry")
    private String industry;

    @Column(name = "speakers", columnDefinition = "TEXT")
    @Convert(converter = SpeakersConverter.class)
    private List<EventRequest.Speaker> speakers;

    /**
     * Name of event image file in cloud storage
     */
    @Column(
            name = "event_image"
    )
    private String eventImage;

    /**
     * Timestamp when the event was created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the event was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * User who created the event.
     * Many-to-one relationship with the User entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}