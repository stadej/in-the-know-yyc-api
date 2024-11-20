package com.intheknowyyc.api.services;

import com.intheknowyyc.api.data.exceptions.BadRequestException;
import com.intheknowyyc.api.data.exceptions.UserNotFoundException;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.intheknowyyc.api.utils.Constants.USER_NOT_FOUND_BY_EMAIL;
import static com.intheknowyyc.api.utils.Constants.USER_NOT_FOUND_BY_ID;

/**
 * Service class for managing User entities.
 * Provides methods to perform CRUD operations on User entities.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;



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
     * @param user the user data to register
     * @throws IllegalStateException if a user with the given email already exists
     */
    public User registerNewUser(User user) {
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new BadRequestException("User with this email already exists.");
        } else {
            return userRepository.save(user);
        }
    }

    /**
     * Updates an existing user with the provided details.
     *
     * @param user the updated user data
     */
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
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
