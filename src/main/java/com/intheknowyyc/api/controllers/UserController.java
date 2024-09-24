package com.intheknowyyc.api.controllers;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing User entities.
 * Provides endpoints to perform CRUD operations on User entities.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a new UserController with the given UserService.
     *
     * @param userService the UserService to use for user management
     */
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
    public User getOneById(@PathVariable long userId){
        return userService.getUserById(userId);
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     */
    @PostMapping
    public void createNewUser(@RequestBody User user) {
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());
        userService.createNewUser(user);
    }

    /**
     * Updates an existing user.
     *
     * @param userId the ID of the user to update
     * @param user the user data to update
     */
    @PutMapping(path = "/{userId}")
    public void updateUser(
            @PathVariable("userId") long userId,
            @RequestBody User user
    ) {
        userService.updateUser(userId, user.getPassword_hash(), user.getFull_name());
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     */
    @DeleteMapping(path = "/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        userService.deleteUser(userId);
    }

}