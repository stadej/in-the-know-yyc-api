package com.intheknowyyc.api.data.models;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a user entity in the system.
 * This class is mapped to the "users" table in the database.
 */
@Entity
@Table(name = "users")
@Data
public class User implements Serializable {

    /**
     * The unique identifier for the user.
     * This value is generated using a sequence.
     */
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * The unique username of the user.
     */
    @Column(unique = true)
    private String username;

    /**
     * The unique email address of the user.
     */
    @Column(unique = true)
    private String email;

    /**
     * The hashed password of the user.
     * This field is mandatory.
     */
    @Column(nullable = false)
    private String password_hash;

    /**
     * The full name of the user.
     */
    private String full_name;

    /**
     * The timestamp when the user was created.
     */
    private LocalDateTime created_at;

    /**
     * The timestamp when the user was last updated.
     */
    private LocalDateTime updated_at;
}