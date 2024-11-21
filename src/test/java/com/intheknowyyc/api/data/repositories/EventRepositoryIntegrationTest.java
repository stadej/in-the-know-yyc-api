package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.models.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("integrationTest")
class EventRepositoryIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    private Event savedEvent;

    @BeforeEach
    void setUp() {
        Event event = new Event();
        event.setId(1L);
        event.setOrganizationName("Event Org");
        event.setEventName("Event");
        event.setEventDescription("Description");
        event.setEventDate(LocalDateTime.now().plusDays(10));
        event.setFreeEvent(false);
        event.setEventCost(new BigDecimal("100.00"));
        event.setEventLink("http://event-link.com");
        event.setEventType("Conference");
        event.setLocation("Location");
        event.setStatus(EventStatus.APPROVED);

        savedEvent = eventRepository.save(event);
    }

    @Test
    void findEventById_shouldReturnEvent_whenEventExists() {
        Optional<Event> foundEvent = eventRepository.findEventById(savedEvent.getId());

        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getEventName()).isEqualTo(savedEvent.getEventName());
    }

    @Test
    void findEventById_shouldReturnEmpty_whenEventDoesNotExist() {
        Optional<Event> foundEvent = eventRepository.findEventById(999L);

        assertThat(foundEvent).isEmpty();
    }
}
