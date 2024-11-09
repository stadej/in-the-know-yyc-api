package com.intheknowyyc.api.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Please, provide valid email address.")
    @NotBlank
    private String email;

    @NotBlank(message = "Please, provide email password.")
    private String password;

}
