package com.intheknowyyc.api.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Column(length = 100)
    @NotBlank(message = "Please provide an organization name")
    private String organization_name;

    /**
     * Name of the event.
     * Maximum length is 100 characters.
     * Cannot be null.
     */
    @Column(
            length = 100,
            nullable = false
    )
    @NotBlank(message = "Please provide an event name")
    private String event_name;

    /**
     * Description of the event.
     * Maximum length is 65535 characters.
     * Cannot be null.
     */
    @Column(
            nullable = false,
            length = 65535
    )
    @NotBlank(message = "Please provide an event description")
    private String event_description;

    /**
     * Date and time when the event will take place.
     * Cannot be null.
     */
    @Column(nullable = false)
    private LocalDateTime event_date;

    /**
     * Indicates whether the event is free.
     */
    @NotNull
    private Boolean is_event_free;

    /**
     * Cost of the event.
     * Precision is 10 and scale is 2.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal event_cost;

    /**
     * Link to the event.
     * Cannot be null.
     */
    @Column(nullable = false)
    @NotBlank(message = "Please provide an event link")
    private String event_link;

    /**
     * Type of the event.
     * Maximum length is 100 characters.
     * Cannot be null.
     */
    @Column(
            length = 100,
            nullable = false
    )
    @NotBlank(message = "Please provide an event type")
    private String event_type;

    /**
     * Timestamp when the event was created.
     */
    private LocalDateTime created_at;

    /**
     * Timestamp when the event was last updated.
     */
    private LocalDateTime updated_at;

    /**
     * User who created the event.
     * Many-to-one relationship with the User entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}