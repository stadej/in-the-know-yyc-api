package com.intheknowyyc.api.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a user entity in the system.
 * This class is mapped to the "users" table in the database.
 */
@Entity
@Table(name = "users")
@Data
public class User implements Serializable, UserDetails {

    /**
     * The unique identifier for the user.
     * This value is generated using a sequence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The unique email address of the user.
     */
    @Column(
            unique = true,
            length = 100
    )
    @NotBlank(message = "Please provide an email address")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * The hashed password of the user.
     * This field is mandatory.
     */
    @Column(nullable = false)
    @NotBlank(message = "Please provide a password")
    private String password;

    /**
     * The full name of the user.
     */
    @Column(
            name = "full_name",
            length = 100
    )
    @NotBlank(message = "Please provide a full name")
    private String fullName;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private boolean locked;

    private boolean expired;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL
    )
    private RefreshToken refreshToken;

    /**
     * The role of the user.
     * This is an enumeration that can be either ADMIN or USER.
     */
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_ADMIN;

    /**
     * Returns the authorities of the user.
     * @return The authorities of the user.
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Returns the password of the user.
     * @return The password of the user.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the email of the user.
     * @return The email of the user.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Checks if the user account is not expired.
     * @return True if the user account is not expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    /**
     * Checks if the user account is not locked.
     * @return True if the user account is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    /**
     * Checks if the user credentials are not expired.
     * @return True if the user credentials are not expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks if the user is enabled.
     * @return True if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}