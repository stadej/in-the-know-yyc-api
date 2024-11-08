package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Service class for handling user login.
 */
@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, JWTService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates the user and generates a JWT token if successful.
     *
     * @param request the login request containing email and password
     * @return a ResponseEntity containing the JWT token if authentication is successful,
     *         or an error message if authentication fails
     */
    public ResponseEntity<String> login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            return ResponseEntity.ok().body(jwtService.generateToken(request.getEmail()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }

    }

}
