package com.intheknowyyc.api.services;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Service class for managing User entities.
 * Provides methods to perform CRUD operations on User entities.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructs a new UserService with the given UserRepository.
     *
     * @param userRepository the UserRepository to use for data access
     */
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
     * @throws IllegalStateException if no user with the given ID exists
     */
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User with " + userId + " does not exist!"));
    }

    /**
     * Creates a new user in the repository.
     *
     * @param user the user to create
     * @throws IllegalStateException if a user with the same email or username already exists
     */
    public void createNewUser(User user) {
        if (userRepository.findUserByEmail(user.getEmail()).isEmpty() || userRepository.findUserByUsername(user.getUsername()).isEmpty()){
            userRepository.save(user);
        } else {
            throw new IllegalStateException("User with this email or username already exists!");
        }
    }

    /**
     * Updates an existing user in the repository.
     *
     * @param userId the ID of the user to update
     * @param password_hash the new password hash for the user
     * @param full_name the new full name for the user
     * @throws IllegalStateException if no user with the given ID exists
     */
    @Transactional
    public void updateUser(
            long userId,
            String password_hash,
            String full_name
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with ID " + userId + " does not exist!"));
        if (full_name != null &&
                !full_name.isEmpty() &&
                !Objects.equals(user.getFull_name(), full_name)) {
            user.setFull_name(full_name);
        }
        if (password_hash != null &&
                !password_hash.isEmpty() &&
                !Objects.equals(user.getPassword_hash(), password_hash)) {
            user.setPassword_hash(password_hash);
        }
        user.setUpdated_at(LocalDateTime.now());
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     * @throws IllegalStateException if no user with the given ID exists
     */
    public void deleteUser(long userId) {
        if (userRepository.existsById(userId)){
            userRepository.deleteById(userId);
        } else {
            throw new IllegalStateException("User with ID" + userId + " does not exist!");
        }
    }
}
