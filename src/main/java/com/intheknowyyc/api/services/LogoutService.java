package com.intheknowyyc.api.services;

import com.intheknowyyc.api.data.repositories.RefreshTokenRepository;
import com.intheknowyyc.api.data.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutService implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public LogoutService(RefreshTokenRepository refreshTokenRepository, JWTService jwtService, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken;
        if (header != null && header.startsWith("Bearer ")) {
            accessToken = header.substring(7);
        } else {
            return;
        }
        var user = userRepository.findUserByEmail(jwtService.getEmailFromToken(accessToken))
                .orElseThrow(() -> new RuntimeException("User not found"));
        var refreshToken = refreshTokenRepository
                .findTokenByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        refreshTokenRepository
                .findByToken(refreshToken.getToken())
                .ifPresent(token ->
                        refreshTokenRepository.deleteByToken(refreshToken.getToken())
                );
        SecurityContextHolder.clearContext();
    }
}
