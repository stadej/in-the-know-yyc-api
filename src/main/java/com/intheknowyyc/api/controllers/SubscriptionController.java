package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.SubscriptionRequest;
import com.intheknowyyc.api.data.exceptions.BadRequestException;
import com.intheknowyyc.api.data.exceptions.ResourceNotFoundException;
import com.intheknowyyc.api.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

/**
 * REST controller for managing Mailchimp subscription.
 * Provides endpoints to perform POST operation to Mailchimp API.
 */
@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Operation(summary = "Create new subscriptions",
            description = "Registers a new subscriber with email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email subscribed successfully.", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        String response = subscriptionService.subscribe(request);
        try {
            return ResponseEntity.ok().body(response);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
