package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entities.
 * Provides methods to perform CRUD operations and custom queries on User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to find
     * @return an Optional containing the found user, or empty if no user was found
     */
    @Query("SELECT user FROM User user WHERE user.email=?1")
    Optional<User> findUserByEmail(String email);

}
