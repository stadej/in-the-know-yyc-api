package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.UserRequest;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.translators.UserTranslator;
import com.intheknowyyc.api.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing User entities.
 * Provides endpoints to perform CRUD operations on User entities.
 */
@RestController
@RequestMapping(path = "/users")
@Tag(name = "User Controller", description = "API for managing users")
@SecurityRequirement(name = "bearer-auth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    @Operation(summary = "Get all users",
            description = "Retrieve a list of all users. Only administrators can view this list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user with the given ID
     */
    @Operation(summary = "Get user by ID",
            description = "Retrieve a user by ID. Only administrators or current user can view this information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved an user", content = {@Content(schema = @Schema(implementation = User.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping(path = "/{userId}")
    public ResponseEntity<User> getOneById(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return the user with the given email address
     */
    @Operation(summary = "Get user by email",
            description = "Retrieve a user by email address. Only administrators or current user can view this information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved an user", content = {@Content(schema = @Schema(implementation = User.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable @Valid String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Creates a new user.
     *
     * @param request the user data to create
     */
    @Operation(summary = "Create a new user",
            description = "Registers a new user with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created", content = {@Content(schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<User> createNewUser(@Valid @RequestBody @Parameter(description = "User registration data") UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerNewUser(UserTranslator.translateToUser(request, 0)));
    }

    /**
     * Updates an existing user.
     *
     * @param userId  the ID of the user to update
     * @param request the new user data
     */
    @Operation(summary = "Update user",
            description = "Update the details of a user. Only administrators or the user themselves can perform this operation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated", content = {@Content(schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @PutMapping(path = "/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable("userId") @Parameter(description = "ID of the user to be updated") int userId,
            @Valid @RequestBody @Parameter(description = "Updated user details") UserRequest request
    ) {
        return ResponseEntity
                .ok(userService.updateUser(UserTranslator.translateToUser(request, userId)));
    }

}