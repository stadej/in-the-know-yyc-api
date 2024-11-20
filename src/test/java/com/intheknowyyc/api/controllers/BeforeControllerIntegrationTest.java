package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.models.UserRole;
import com.intheknowyyc.api.data.repositories.EventRepository;
import com.intheknowyyc.api.data.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

public abstract class BeforeControllerIntegrationTest {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected EventRepository eventRepository;

    private User testUser;

    private User testAdmin;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("testuser@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setFullName("Test User");
        testUser.setRole(UserRole.ROLE_USER);
        testUser = userRepository.save(testUser);

        testAdmin = new User();
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPassword(passwordEncoder.encode("adminpassword"));
        testAdmin.setRole(UserRole.ROLE_ADMIN);
        testAdmin.setFullName("Test Admin");
        testAdmin = userRepository.save(testAdmin);
    }

    protected User getUser(){
        return testUser;
    }

    protected User getAdmin(){
        return testAdmin;
    }

    protected RestTemplate restTemplateWithUserPermission(){
        return restTemplate.withBasicAuth(testUser.getEmail(), "password").getRestTemplate();
    }

    protected RestTemplate restTemplateWithAdminPermission(){
        return restTemplate.withBasicAuth(testAdmin.getEmail(), "adminpassword").getRestTemplate();
    }

    @AfterEach
    void cleanUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }
}
