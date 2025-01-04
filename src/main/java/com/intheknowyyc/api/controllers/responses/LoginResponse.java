package com.intheknowyyc.api.controllers.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * Response object for login requests.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String error;

}
