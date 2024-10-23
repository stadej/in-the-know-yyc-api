package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.SubscriptionRequest;
import com.intheknowyyc.api.data.models.Subscription;
import com.intheknowyyc.api.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Operation(summary = "Get all subscriptions",
            description = "Retrieve a list of all subscriptions. Only administrators can view this list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of subscriptions", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Subscription.class)), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/subscriptions")
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @Operation(summary = "Create new subscriptions",
            description = "Registers a new subscriber with email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscriber successfully created", content = {@Content(schema = @Schema(implementation = Subscription.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/subscribe")
    public ResponseEntity<Subscription> subscribe(@Valid @RequestBody SubscriptionRequest subscriptionRequest) {
        return ResponseEntity.ok(subscriptionService.subscribe(subscriptionRequest));
    }


    @Operation(summary = "Delete existing subscription",
            description = "Delete an subscription by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subscription not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/unsubscribe/{uuid}")
    public ResponseEntity<Void> unsubscribe(@PathVariable UUID uuid) {
        subscriptionService.unsubscribe(uuid);
        return ResponseEntity.noContent().build();
    }

}
