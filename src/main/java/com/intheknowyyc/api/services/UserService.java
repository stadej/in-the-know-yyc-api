package com.intheknowyyc.api.services;

import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User with " + userId + " does not exist!"));
    }

    public void createNewUser(User user) {
        if (userRepository.findUserByEmail(user.getId().getEmail()).isEmpty() || userRepository.findUserByUsername(user.getId().getUsername()).isEmpty()){
            throw new IllegalStateException("User with this email or username already exists!");
        } else {
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUser(
            Integer userId,
            String username,
            String email,
            String full_name
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with " + userId + " does not exist!"));

        if (username != null &&
                !username.isEmpty() &&
                !Objects.equals(user.getId().getUsername(), username)) {
            user.getId().setUsername(username);
        }
        if (email != null &&
                !email.isEmpty() &&
                !Objects.equals(user.getId().getEmail(), email)) {
            user.getId().setEmail(email);
        }
        if (full_name != null &&
                !full_name.isEmpty() &&
                !Objects.equals(user.getFull_name(), full_name)) {
            user.setFull_name(full_name);
        }

    }

    public void deleteUser(int userId) {
        if (userRepository.existsById(userId)){
            userRepository.deleteById(userId);
        } else {
            throw new IllegalStateException("User with " + userId + " does not exist!");
        }
    }
}
