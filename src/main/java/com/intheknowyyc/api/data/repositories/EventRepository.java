package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing Event data.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    /**
     * Finds an Event by its ID.
     *
     * @param eventId the ID of the event
     * @return an Optional containing the found Event, or empty if not found
     */
    @Query("SELECT event FROM Event event WHERE event.id=?1")
    Optional<Event> findEventById(long eventId);

}
