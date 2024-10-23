package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Subscription entities.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    /**
     * Finds a Subscription by email.
     *
     * @param email the email of the Subscription
     * @return an Optional containing the Subscription if found, or empty if not found
     */
    @Query("SELECT s FROM Subscription s WHERE s.email = ?1")
    Optional<Subscription> findByEmail(String email);

    /**
     * Checks if a Subscription exists by email.
     *
     * @param uuid the email to check
     * @return true if a Subscription with the given email exists, false otherwise
     */
    boolean existsByUuid(UUID uuid);

    /**
     * Deletes a Subscription by email.
     *
     * @param uuid the email of the Subscription to delete
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Subscription s WHERE s.uuid = ?1")
    void deleteByUuid(UUID uuid);
}
