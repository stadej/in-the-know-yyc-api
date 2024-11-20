package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.UserRequest;
import com.intheknowyyc.api.data.exceptions.BadRequestException;
import com.intheknowyyc.api.data.exceptions.UserNotFoundException;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.models.UserRole;
import com.intheknowyyc.api.data.repositories.UserRepository;
import com.intheknowyyc.api.data.translators.UserTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword(new BCryptPasswordEncoder(4).encode("password"));
        user.setFullName("Test User");
        user.setRole(UserRole.ROLE_USER);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("Test User", users.get(0).getFullName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1);

        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getFullName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(2);
        });

        assertEquals("User not found by id: 2", exception.getMessage());
        verify(userRepository, times(1)).findById(2);
    }

    @Test
    void testGetUserByEmail_UserExists() {
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByEmail("test@example.com");

        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getFullName());
        verify(userRepository, times(1)).findUserByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        when(userRepository.findUserByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByEmail("notfound@example.com");
        });

        assertEquals("User not found by email: notfound@example.com", exception.getMessage());
        verify(userRepository, times(1)).findUserByEmail("notfound@example.com");
    }

    @Test
    void testRegisterNewUser_UserAlreadyExists() {
        UserRequest request = new UserRequest("test@example.com", "password", "Test User");
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            userService.registerNewUser(UserTranslator.translateToUser(request, 0));
        });

        assertEquals("User with this email already exists.", exception.getMessage());
        verify(userRepository, times(1)).findUserByEmail("test@example.com");
    }

    @Test
    void testRegisterNewUser_Success() {
        User userR = new User();
        userR.setId(1);
        userR.setEmail("newuser@example.com");
        userR.setPassword("password");
        userR.setFullName("New User");
        when(userRepository.findUserByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(userR)).thenReturn(userR);

        User newUser = userService.registerNewUser(userR);

        assertNotNull(newUser);
        assertEquals("newuser@example.com", newUser.getEmail());
        assertEquals("New User", newUser.getFullName());
        verify(userRepository, times(1)).findUserByEmail("newuser@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        User userR = new User();
        userR.setId(1);
        userR.setEmail("updated@example.com");
        userR.setPassword("newpassword");
        userR.setFullName("Updated User");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(userR)).thenReturn(userR);

        User updatedUser = userService.updateUser(userR);

        assertNotNull(updatedUser);
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Updated User", updatedUser.getFullName());
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(userRepository, times(1)).findUserByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findUserByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.loadUserByUsername("notfound@example.com");
        });

        assertEquals("User not found by email: notfound@example.com", exception.getMessage());
        verify(userRepository, times(1)).findUserByEmail("notfound@example.com");
    }
}

