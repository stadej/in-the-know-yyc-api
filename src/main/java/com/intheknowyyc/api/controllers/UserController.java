package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/all")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping(path = "/new")
    public void createNewUser(@RequestBody User user) {
        userService.createNewUser(user);
    }

    @PutMapping(path = "/{userId}")
    public void updateUser(
            @PathVariable("userId") int userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String full_name
    ) {
        userService.updateUser(userId, username, email, full_name);
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteUser(@PathVariable("userId") int userId) {
        userService.deleteUser(userId);
    }

}
