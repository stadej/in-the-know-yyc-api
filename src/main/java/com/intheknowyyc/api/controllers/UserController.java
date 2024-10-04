package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.UserRequest;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for managing User entities.
 * Provides endpoints to perform CRUD operations on User entities.
 */
@RestController
@RequestMapping(path = "/users")
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
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user with the given ID
     */
    @GetMapping(path = "/{userId}")
    public User getOneById(@PathVariable int userId){
        return userService.getUserById(userId);
    }

    /**
     * Creates a new user.
     *
     * @param request the user data to create
     */
    @PostMapping
    public void createNewUser(@Valid @RequestBody UserRequest request) {
        userService.registerNewUser(request);
    }

    /**
     * Updates an existing user.
     *
     * @param userId the ID of the user to update
     * @param request the new user data
     */
    @PutMapping(path = "/{userId}")
    public void updateUser(
            @PathVariable("userId") int userId,
            @Valid
            @RequestBody UserRequest request
    ) {
        userService.updateUser(userId, request);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     */
    @DeleteMapping(path = "/{userId}")
    public void deleteUser(@PathVariable("userId") int userId) {
        userService.deleteUser(userId);
    }

}