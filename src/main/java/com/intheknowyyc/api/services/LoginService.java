package com.intheknowyyc.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intheknowyyc.api.controllers.requests.LoginRequest;
import com.intheknowyyc.api.controllers.responses.LoginResponse;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.repositories.RefreshTokenRepository;
import com.intheknowyyc.api.data.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;

// import com.intheknowyyc.api.config.filter.SecurityConfig;

/**
 * Service class for handling user login.
 */
@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    @Autowired
    public LoginService(AuthenticationManager authenticationManager, JWTService jwtService, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Authenticates the user and generates a JWT token if successful.
     *
     * @param request the login request containing email and password
     * @return a ResponseEntity containing the JWT token if authentication is successful,
     *         or an error message if authentication fails
     */
    public ResponseEntity<LoginResponse> login(LoginRequest request) {

        LoginResponse response = new LoginResponse();

        try {
            this.authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var accessToken = jwtService.generateAccessToken(request.getEmail());
            var refreshToken = jwtService.generateRefreshToken(request.getEmail());
            User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow();
            jwtService.revokeUserRefreshToken(user);
            jwtService.saveUserRefreshToken(user, refreshToken);
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            response.setError("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Refreshes the access token using the refresh token.
     *
     * @param token the refresh token
     * @throws IOException if an error occurs while refreshing the token
     */
    public ResponseEntity<LoginResponse> refreshToken(String token) throws IOException {
        LoginResponse authResponse = new LoginResponse();
        String email = jwtService.getEmailFromToken(token);

        if (email != null && refreshTokenRepository.findByToken(token).isPresent()) {
            var user = userRepository.findUserByEmail(email).orElseThrow();
            if (jwtService.validateRefreshToken(token, user)) {
                var accessToken = jwtService.generateAccessToken(email);
                authResponse.setAccessToken(accessToken);
                authResponse.setRefreshToken(token);

                return ResponseEntity.ok(authResponse);
            } else {
                authResponse.setError("Could not Refresh Token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
            }
        } else {
            authResponse.setError("Could not Refresh Token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }
    }

}
