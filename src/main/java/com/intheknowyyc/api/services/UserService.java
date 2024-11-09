package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.UserRequest;
import com.intheknowyyc.api.data.exceptions.UserNotFoundException;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.models.UserRole;
import com.intheknowyyc.api.data.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.intheknowyyc.api.utils.Constants.USER_NOT_FOUND_BY_EMAIL;
import static com.intheknowyyc.api.utils.Constants.USER_NOT_FOUND_BY_ID;

/**
 * Service class for managing User entities.
 * Provides methods to perform CRUD operations on User entities.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user with the given ID
     */
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID, userId)));
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email)));
    }

    /**
     * Registers a new user in the repository.
     *
     * @param request the user data to register
     * @throws IllegalStateException if a user with the given email already exists
     */
    public User registerNewUser(@RequestBody UserRequest request) {
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("User with this email already exists.");
        } else {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setRole(UserRole.ROLE_USER);
            userRepository.save(user);
            return user;
        }
    }

    /**
     * Updates an existing user with the provided details.
     *
     * @param userId the ID of the user to update
     * @param request the updated user data
     * @throws IllegalStateException if no user with the given ID exists
     */
    @Transactional
    public User updateUser(int userId, @Valid UserRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalStateException(String.format(USER_NOT_FOUND_BY_ID, userId)));
        if(!request.getEmail().isBlank() && !Objects.equals(request.getEmail(), user.getEmail())){
            user.setEmail(request.getEmail());
        }
        if(!request.getPassword().isBlank() && !Objects.equals(request.getPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (!request.getFullName().isBlank() && !Objects.equals(request.getFullName(), user.getFullName())){
            user.setFullName(request.getFullName());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Loads a user by their email address.
     *
     * @param email the email address of the user to load
     * @return the user with the given email address
     */
    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email)));
    }


}
