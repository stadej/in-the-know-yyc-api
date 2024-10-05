package com.intheknowyyc.api.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Represents a request to register a new user.
 * Contains the data needed to create a new user in the system.
 */
@Data
public class UserRequest {

    @NotBlank(message = "Please provide an email address")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Please provide a password")
    private String password;

    @NotBlank(message = "Please provide a full name")
    private String fullName;


}
