package com.intheknowyyc.api.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a subscription entity.
 */
@Entity
@Table(name = "subscriptions")
@Data
public class Subscription implements Serializable {

    /**
     * The unique identifier for the subscription.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The UUID of the subscription.
     */
    @Column(
            name = "uuid",
            updatable = false,
            nullable = false,
            columnDefinition = "BINARY(16)"
    )
    private UUID uuid;

    /**
     * The email address of the subscriber.
     * Must be unique and valid.
     */
    @Column(
            unique = true,
            length = 100
    )
    @NotBlank(message = "Please provide an email address")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * The date and time when the subscription was created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}