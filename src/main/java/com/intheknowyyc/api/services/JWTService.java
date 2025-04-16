package com.intheknowyyc.api.services;

import com.intheknowyyc.api.data.models.RefreshToken;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.repositories.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import com.intheknowyyc.api.data.exceptions.NoSuchAlgorithmExceprion;

/**
 * Service class for handling JWT tokens.
 */
@Service
public class JWTService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /**
     * Constructs a JWTService with a RefreshTokenRepository.
     *
     * @param refreshTokenRepository the repository for refresh tokens
     */
    public JWTService(RefreshTokenRepository refreshTokenRepository) {

        try {
            KeyGenerator generator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey key = generator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmExceprion("Algorithm not found");
        }
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Builds a JWT token with the given email, claims, and expiration time.
     *
     * @param email the email to include in the token
     * @param claims the claims to include in the token
     * @param expiration the expiration time of the token
     * @return the JWT token
     */
    public String buildToken(
            String email,
            Map<String, Objects> claims,
            long expiration) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .and()
                .signWith(generateKey())
                .compact();
    }

    /**
     * Generates an access token for the given email.
     *
     * @param email the email to generate the token for
     * @return the access token
     */
    public String generateAccessToken(String email) {

        Map<String, Objects> claims = new HashMap<>();

        return buildToken(email, claims, 15 * 60 * 1000);
    }

    /**
     * Generates a refresh token for the given email.
     *
     * @param email the email to generate the token for
     * @return the refresh token
     */
    public String generateRefreshToken(String email) {

        Map<String, Objects> claims = new HashMap<>();

        return buildToken(email, claims, 24 * 60 * 60 * 1000);
    }

    /**
     * Generates a secret key for the JWT token.
     *
     * @return the secret key
     */
    private SecretKey generateKey() {

        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);

    }

    /**
     * Extracts the email from the given token.
     *
     * @param token the token to extract the email from
     * @return the email
     */
    public String getEmailFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extracts the claims from the given token.
     *
     * @param token the token to extract the claims from
     * @param claimsResolver the function to resolve the claims
     * @param <T> the type of the claims
     * @return the claims
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    /**
     * Extracts all claims from the given token.
     *
     * @param token the token to extract the claims from
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates the access token for the given user details.
     *
     * @param token the token to validate
     * @param userDetails the user details to validate the token against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Validates the refresh token for the given user details.
     *
     * @param token the token to validate
     * @param userDetails the user details to validate the token against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if the given token is expired.
     *
     * @param token the token to check
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the given token.
     *
     * @param token the token to extract the expiration date from
     * @return the expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * Revokes the refresh token for the given user.
     *
     * @param user the user to revoke the token for
     */
    @Transactional
    public void revokeUserRefreshToken(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }

    /**
     * Saves the refresh token for the given user.
     *
     * @param user the user to save the token for
     * @param refreshToken the refresh token to save
     */
    public void saveUserRefreshToken(User user, String refreshToken) {
        RefreshToken token = new RefreshToken(
                refreshToken,
                Instant.now(),
                Instant.now().plusMillis(24 * 60 * 60 * 1000),
                user
        );
        refreshTokenRepository.save(token);
    }
}
