package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.UserRequest;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationTest")
class UserControllerIntegrationTest extends BeforeControllerIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testGetAllUsers() {
        ResponseEntity<String> response = restTemplateWithAdminPermission().exchange(
                "/users", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetUserByIdAsUser() {
        ResponseEntity<User> response = restTemplateWithUserPermission().exchange(
                "/users/{userId}", HttpMethod.GET, null, User.class, getUser().getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(getUser().getEmail(), response.getBody().getEmail());
    }

    @Test
    void testGetUserByEmail() {
        ResponseEntity<User> response = restTemplateWithAdminPermission().exchange(
                "/users/email/{email}", HttpMethod.GET, null, User.class, getUser().getEmail());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(getUser().getEmail(), response.getBody().getEmail());
    }

    @Test
    void testCreateNewUser() {
        UserRequest newUser = new UserRequest();
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password");
        newUser.setFullName("New User");

        ResponseEntity<User> response = restTemplateWithAdminPermission().exchange(
                "/users", HttpMethod.POST, new HttpEntity<>(newUser), User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser@example.com", response.getBody().getEmail());
    }

    @Test
    void testUpdateUser() {
        UserRequest updatedUser = new UserRequest();
        updatedUser.setEmail("testuser@example.com");
        updatedUser.setPassword("newpassword");
        updatedUser.setFullName("Updated Test User");

        ResponseEntity<User> response = restTemplateWithUserPermission().exchange(
                "/users/{userId}", HttpMethod.PUT, new HttpEntity<>(updatedUser), User.class, getUser().getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(getUser().getEmail(), response.getBody().getEmail());
    }

    @Test
    void testUpdateUserForbiddenForOtherUser() {
        UserRequest updatedUser = new UserRequest();
        updatedUser.setEmail("anotheruser@example.com");
        updatedUser.setPassword("newpassword");
        updatedUser.setFullName("Another User");

        ResponseEntity<User> response = restTemplateWithUserPermission().exchange(
                "/users/{userId}", HttpMethod.PUT, new HttpEntity<>(updatedUser), User.class, 2);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateUserAsAdmin() {
        UserRequest updatedUser = new UserRequest();
        updatedUser.setEmail("updateduser@example.com");
        updatedUser.setPassword("newpassword");
        updatedUser.setFullName("Updated Test User");

        ResponseEntity<User> response = restTemplateWithAdminPermission().exchange(
                "/users/{userId}", HttpMethod.PUT, new HttpEntity<>(updatedUser), User.class, getUser().getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updateduser@example.com", response.getBody().getEmail());
    }

    @Test
    void testGetUserByNonExistentId() {
        ResponseEntity<User> response = restTemplateWithAdminPermission().exchange(
                "/users/{userId}", HttpMethod.GET, null, User.class, 999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserByNonExistentEmail() {
        ResponseEntity<User> response = restTemplateWithAdminPermission().exchange(
                "/users/email/{email}", HttpMethod.GET, null, User.class, "nonexistent@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateNewUserWithExistingEmail() {
        UserRequest duplicateUser = new UserRequest();
        duplicateUser.setEmail(getUser().getEmail());
        duplicateUser.setPassword("password");
        duplicateUser.setFullName("Duplicate User");

        ResponseEntity<String> response = restTemplateWithAdminPermission().exchange(
                "/users", HttpMethod.POST, new HttpEntity<>(duplicateUser), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateUserWithInvalidEmailFormat() {
        UserRequest newUser = new UserRequest();
        newUser.setEmail("invalid-email");
        newUser.setPassword("password");
        newUser.setFullName("Invalid Email User");

        ResponseEntity<String> response = restTemplateWithAdminPermission().exchange(
                "/users", HttpMethod.POST, new HttpEntity<>(newUser), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAccessGetAllUsersWithoutAuth() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/users", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}