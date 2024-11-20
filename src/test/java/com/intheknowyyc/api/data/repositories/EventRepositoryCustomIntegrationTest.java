package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.models.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("integrationTest")
class EventRepositoryCustomIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        event1 = new Event();
        event1.setOrganizationName("Tech Org");
        event1.setEventName("Tech Conference");
        event1.setEventDescription("Annual tech conference");
        event1.setEventDate(LocalDateTime.now().plusDays(15));
        event1.setFreeEvent(true);
        event1.setEventCost(new BigDecimal("0.00"));
        event1.setEventLink("http://tech-conference.com");
        event1.setEventType("Conference");
        event1.setLocation("Tech City");
        event1.setStatus(EventStatus.APPROVED);
        eventRepository.save(event1);

        event2 = new Event();
        event2.setOrganizationName("Health Org");
        event2.setEventName("Health Workshop");
        event2.setEventDescription("Health and wellness workshop");
        event2.setEventDate(LocalDateTime.now().plusDays(5));
        event2.setFreeEvent(false);
        event2.setEventCost(new BigDecimal("50.00"));
        event2.setEventLink("http://health-workshop.com");
        event2.setEventType("Workshop");
        event2.setLocation("Health Center");
        event2.setStatus(EventStatus.PENDING);
        eventRepository.save(event2);
    }

    @Test
    void shouldFilterByEventType() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findFilteredEvents(
                null, null, "Conference", null, null, null, pageable, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(event1.getEventName(), result.getContent().get(0).getEventName());
    }

    @Test
    void shouldFilterByOrganizationName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findFilteredEvents(
                null, null, null, "Health Org", null, null, pageable, EventStatus.PENDING);

        assertEquals(1, result.getTotalElements());
        assertEquals(event2.getEventName(), result.getContent().get(0).getEventName());
    }

    @Test
    void shouldFilterByDateRange() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(10);
        LocalDateTime endDate = LocalDateTime.now().plusDays(20);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findFilteredEvents(
                startDate, endDate, null, null, null, null, pageable, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(event1.getEventName(), result.getContent().get(0).getEventName());
    }

    @Test
    void shouldFilterByLocation() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findFilteredEvents(
                null, null, null, null, "Health Center", null, pageable, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(event2.getEventName(), result.getContent().get(0).getEventName());
    }

    @Test
    void shouldFilterBySearchText() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findFilteredEvents(
                null, null, null, null, null, "wellness", pageable, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(event2.getEventName(), result.getContent().get(0).getEventName());
    }

    @Test
    void shouldFilterByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findFilteredEvents(
                null, null, null, null, null, null, pageable, EventStatus.APPROVED);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFilterByMultipleParameters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findFilteredEvents(
                null, null, "Conference", "Tech Org", "Tech City", null, pageable, EventStatus.APPROVED);

        assertEquals(1, result.getTotalElements());
        assertEquals(event1.getEventName(), result.getContent().get(0).getEventName());
    }

    @Test
    void shouldReturnEmptyPageWhenNoMatches() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findFilteredEvents(
                LocalDateTime.now().plusDays(100), LocalDateTime.now().plusDays(200),
                "Nonexistent Type", "Nonexistent Org", "Nowhere", "No match", pageable, EventStatus.REJECTED);

        assertTrue(result.isEmpty());
    }
}
