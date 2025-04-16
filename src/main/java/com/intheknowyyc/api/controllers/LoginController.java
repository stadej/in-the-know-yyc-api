package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.LoginRequest;
import com.intheknowyyc.api.controllers.responses.LoginResponse;
import com.intheknowyyc.api.services.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


/**
 * Controller for handling login requests.
 */
@RestController
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Endpoint for logging in a user with credentials.
     *
     * @param request the login request containing user credentials
     * @return a ResponseEntity with the result of the login operation
     */
    @Operation(summary = "Login user with credentials",
            description = "Login user to operate user account API requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Login failed.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @CrossOrigin(origins= {"http://localhost:3000"}, allowCredentials = "true" )
    @PostMapping("/cms/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return loginService.login(request);
    }

    /**
     * Endpoint for logging out a user.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if an error occurs while logging out
     */
    @Operation(summary = "Refresh access token",
            description = "Refresh JWT access token using refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Body contains accessToken and refreshToken.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Could not refresh token", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/cms/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@Parameter(description = "Refresh Token") @RequestParam(required = true) String token) throws IOException {
        return loginService.refreshToken(token);
    }

}
