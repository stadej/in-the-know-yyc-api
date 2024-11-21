package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.controllers.responses.PaginatedEventResponse;
import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.models.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationTest")
class EventControllerIntegrationTest extends BeforeControllerIntegrationTest {

    private Event event1;
    private Event event2;

    private EventRequest eventRequest;
    private EventRequest updatedEventRequest;

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

        eventRequest = new EventRequest();
        eventRequest.setOrganizationName("Tech Org");
        eventRequest.setEventName("Tech Conference");
        eventRequest.setEventDescription("Annual tech conference");
        eventRequest.setEventDate(LocalDateTime.now().plusDays(15));
        eventRequest.setEventType("Conference");
        eventRequest.setLocation("Tech City");
        eventRequest.setEventLink("http://tech-conference.com");
        eventRequest.setFreeEvent(true);

        updatedEventRequest = new EventRequest();
        updatedEventRequest.setOrganizationName("Tech Org Updated");
        updatedEventRequest.setEventName("Tech Conference Updated");
        updatedEventRequest.setEventDescription("Updated description for the tech conference");
        updatedEventRequest.setEventDate(LocalDateTime.now().plusDays(20));
        updatedEventRequest.setEventType("Updated Conference");
        updatedEventRequest.setLocation("Updated Tech City");
        updatedEventRequest.setEventLink("http://tech-conference-updated.com");
        updatedEventRequest.setFreeEvent(true);
    }

    @Test
    void shouldReturnAllApprovedEvents() {
        ResponseEntity<PaginatedEventResponse> response = restTemplate.getForEntity(
                "/events?status=APPROVED", PaginatedEventResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedEventResponse body = response.getBody();
        assertNotNull(body);

        List<Event> events = body.getContent();
        assertEquals(1, events.size());
        assertEquals(event1.getOrganizationName(), events.get(0).getOrganizationName());
    }

    @Test
    void shouldFilterEventsByDateRange_withAdminPermission() {
        ResponseEntity<PaginatedEventResponse> response = restTemplateWithAdminPermission().getForEntity(
                "/events?startDate=" + LocalDateTime.now().plusDays(1) +
                "&endDate=" + LocalDateTime.now().plusDays(20), PaginatedEventResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedEventResponse body = response.getBody();
        assertNotNull(body);

        List<Event> events = body.getContent();
        assertEquals(2, events.size());
        assertEquals(event1.getStatus(), events.get(0).getStatus());
        assertEquals(event2.getStatus(), events.get(1).getStatus());
    }

    @Test
    void shouldFilterEventsByEventType() {
        ResponseEntity<PaginatedEventResponse> response = restTemplateWithUserPermission().getForEntity("/events?eventType=Conference", PaginatedEventResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedEventResponse body = response.getBody();
        assertNotNull(body);

        List<Event> events = body.getContent();
        assertEquals(1, events.size());
        assertEquals(event1.getEventType(), events.get(0).getEventType());
    }

    @Test
    void shouldFilterEventsByOrganizationName() {
        ResponseEntity<PaginatedEventResponse> response = restTemplate.getForEntity(
                "/events?organizationName=Tech Org", PaginatedEventResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedEventResponse body = response.getBody();
        assertNotNull(body);

        List<Event> events = body.getContent();
        assertEquals(1, events.size());
        assertEquals(event1.getOrganizationName(), events.get(0).getOrganizationName());
    }

    @Test
    void shouldFilterEventsByLocation() {
        ResponseEntity<PaginatedEventResponse> response = restTemplate.getForEntity(
                "/events?location=Tech City", PaginatedEventResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedEventResponse body = response.getBody();
        assertNotNull(body);

        List<Event> events = body.getContent();
        assertEquals(1, events.size());
        assertEquals(event1.getLocation(), events.get(0).getLocation());
    }

    @Test
    void shouldSearchEventsByText() {
        ResponseEntity<PaginatedEventResponse> response = restTemplate.getForEntity(
                "/events?searchText=tech", PaginatedEventResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedEventResponse body = response.getBody();
        assertNotNull(body);

        List<Event> events = body.getContent();
        assertEquals(1, events.size());
        assertEquals(event1.getEventName(), events.get(0).getEventName());
    }

    @Test
    void shouldReturnPaginatedResults() {
        ResponseEntity<PaginatedEventResponse> response = restTemplateWithAdminPermission().getForEntity(
                "/events?page=0&size=1", PaginatedEventResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedEventResponse body = response.getBody();
        assertNotNull(body);

        List<Event> events = body.getContent();
        assertEquals(1, events.size());
    }

    @Test
    void shouldReturnEventById() {
        ResponseEntity<Event> response = restTemplate.getForEntity(
                "/events/{eventId}", Event.class, event1.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event event = response.getBody();
        assertNotNull(event);
        assertEquals(event1.getId(), event.getId());
        assertEquals(event1.getOrganizationName(), event.getOrganizationName());
        assertEquals(event1.getEventName(), event.getEventName());
        assertEquals(event1.getEventDescription(), event.getEventDescription());
    }

    @Test
    void testGetEventById_shouldReturnNotFoundForNonExistingEvent() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/events/{eventId}", String.class, 99999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldCreateApprovedEvent_withAdminPermission() {
        ResponseEntity<Event> response = restTemplateWithAdminPermission().postForEntity(
                "/events", eventRequest, Event.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Event createdEvent = response.getBody();
        assertNotNull(createdEvent);
        assertEquals(eventRequest.getEventName(), createdEvent.getEventName());
        assertEquals(EventStatus.APPROVED, createdEvent.getStatus());
    }

    @Test
    void shouldCreatePendingEvent_withUserPermission() {
        ResponseEntity<Event> response = restTemplateWithUserPermission().postForEntity(
                "/events", eventRequest, Event.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Event createdEvent = response.getBody();
        assertNotNull(createdEvent);
        assertEquals(eventRequest.getEventName(), createdEvent.getEventName());
        assertEquals(EventStatus.PENDING, createdEvent.getStatus());
    }

    @Test
    void testCreateEvent_shouldReturnBadRequestForInvalidEventData() {
        EventRequest invalidEventRequest = new EventRequest();
        invalidEventRequest.setOrganizationName("Tech Org");
        invalidEventRequest.setEventDescription("Annual tech conference");
        invalidEventRequest.setEventDate(LocalDateTime.now().plusDays(15));
        invalidEventRequest.setEventType("Conference");
        invalidEventRequest.setLocation("Tech City");
        invalidEventRequest.setEventLink("http://tech-conference.com");

        ResponseEntity<String> response = restTemplateWithUserPermission().postForEntity(
                "/events", invalidEventRequest, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateEvent_shouldReturnUnauthorizedForUnauthenticatedUser() {
        EventRequest request = new EventRequest();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/events", request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void shouldUpdateEvent_withAdminPermission() {
        ResponseEntity<Event> response = restTemplateWithAdminPermission().exchange(
                "/events/{eventId}", HttpMethod.PUT,
                new HttpEntity<>(updatedEventRequest), Event.class, event1.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event updatedEvent = response.getBody();
        assertNotNull(updatedEvent);
        assertEquals(updatedEventRequest.getEventName(), updatedEvent.getEventName());
        assertEquals(updatedEventRequest.getEventDescription(), updatedEvent.getEventDescription());
        assertEquals(updatedEventRequest.getLocation(), updatedEvent.getLocation());
    }

    @Test
    void testUpdateEvent_shouldReturnForbiddenForRegularUser() {
        ResponseEntity<String> response = restTemplateWithUserPermission().exchange(
                "/events/{eventId}", HttpMethod.PUT, new HttpEntity<>(updatedEventRequest), String.class, event1.getId());

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateEvent_shouldReturnNotFoundForNonExistingEvent() {
        ResponseEntity<String> response = restTemplateWithAdminPermission().exchange(
                "/events/{eventId}", HttpMethod.PUT, new HttpEntity<>(updatedEventRequest), String.class, 99999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateEvent_shouldReturnBadRequestForInvalidEventData() {
        EventRequest invalidEventRequest = new EventRequest();
        invalidEventRequest.setOrganizationName("Tech Org");
        invalidEventRequest.setEventDescription("Annual tech conference");
        invalidEventRequest.setEventDate(LocalDateTime.now().plusDays(15));
        invalidEventRequest.setEventType("Conference");
        invalidEventRequest.setLocation("Tech City");
        invalidEventRequest.setEventLink("http://tech-conference.com");

        ResponseEntity<String> response = restTemplateWithAdminPermission().exchange("/events/{eventId}", HttpMethod.PUT,
                new HttpEntity<>(invalidEventRequest), String.class, event1.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateEvent_shouldReturnUnauthorizedForUnauthenticatedUser() {
        ResponseEntity<String> response = restTemplate.exchange("/events/{eventId}", HttpMethod.PUT,
                new HttpEntity<>(new EventRequest()), String.class, event1.getId());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDeleteEvent_shouldDeleteEvent() {
        ResponseEntity<Void> response = restTemplateWithAdminPermission().exchange(
                "/events/{eventId}", HttpMethod.DELETE, null, Void.class, event1.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteEvent_shouldReturnForbidden_withUserPermission() {
        ResponseEntity<Void> response = restTemplateWithUserPermission().exchange(
                "/events/{eventId}", HttpMethod.DELETE, null, Void.class, event1.getId());

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
