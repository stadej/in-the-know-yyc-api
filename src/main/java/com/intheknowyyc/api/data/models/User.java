package com.intheknowyyc.api.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User implements Serializable {
    @EmbeddedId
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private UserId id;
    private String password_hash;
    private String full_name;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
