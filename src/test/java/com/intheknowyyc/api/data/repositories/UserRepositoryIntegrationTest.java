package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.models.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("integrationTest")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("testpassword");
        testUser.setFullName("Test User");
        testUser.setRole(UserRole.ROLE_USER);
        testUser.setLocked(false);
        testUser.setExpired(false);
        userRepository.save(testUser);
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        Optional<User> foundUser = userRepository.findUserByEmail(testUser.getEmail());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    void whenFindByEmailWithNonExistingEmail_thenReturnEmpty() {
        Optional<User> foundUser = userRepository.findUserByEmail("nonexistent@example.com");

        assertThat(foundUser).isEmpty();
    }
}
