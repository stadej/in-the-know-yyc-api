package com.intheknowyyc.api.services;

import com.intheknowyyc.api.controllers.requests.EventRequest;
import com.intheknowyyc.api.data.exceptions.BadRequestException;
import com.intheknowyyc.api.data.exceptions.ResourceNotFoundException;
import com.intheknowyyc.api.data.models.*;
import com.intheknowyyc.api.data.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class EventServiceTest {

    @MockBean
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    private User regularUser;
    private User adminUser;
    private Event event;
    private Event updatedEvent;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        regularUser = new User();
        regularUser.setRole(UserRole.ROLE_USER);

        adminUser = new User();
        adminUser.setRole(UserRole.ROLE_ADMIN);

        event = new Event();
        event.setId(1L);
        event.setOrganizationName("Existing Org");
        event.setEventName("Existing Event");
        event.setEventDescription("Existing Description");
        event.setEventDate(LocalDateTime.now().plusDays(10));
        event.setFreeEvent(false);
        event.setEventCost(new BigDecimal("100.00"));
        event.setEventLink("http://existing-event-link.com");
        event.setEventType("Conference");
        event.setLocation("Existing Location");

        updatedEvent = new Event();
        updatedEvent.setOrganizationName("Updated Org");
        updatedEvent.setEventName("Updated Event");
        updatedEvent.setEventDescription("Updated Description");
        updatedEvent.setEventDate(LocalDateTime.now().plusDays(20));
        updatedEvent.setFreeEvent(false);
        updatedEvent.setEventCost(new BigDecimal("200.00"));
        updatedEvent.setEventLink("http://updated-event-link.com");
        updatedEvent.setEventType("Seminar");
    }

    @Test
    void getFilteredEvents_ShouldReturnFilteredEvents() {
        Page<Event> page = new PageImpl<>(Collections.singletonList(event));
        when(eventRepository.findFilteredEvents(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        Page<Event> result = eventService.getFilteredEvents(new EventFilters());  // Provide sample filter values

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(event.getEventName(), result.getContent().get(0).getEventName());
    }

    @Test
    void getEventById_ShouldReturnEvent_WhenEventExists() {
        when(eventRepository.findEventById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals(event.getEventName(), result.getEventName());
    }

    @Test
    void getEventById_ShouldThrowException_WhenEventDoesNotExist() {
        when(eventRepository.findEventById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(1L));
    }

    @Test
    void testCreateNewEvent_AsAdmin_StatusApproved() {
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.createNewEvent(event, adminUser);

        assertEquals(EventStatus.APPROVED, result.getStatus());
        assertEquals(adminUser, result.getUser());
    }

    @Test
    void testCreateNewEvent_AsRegularUser_StatusPending() {
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.createNewEvent(event, regularUser);

        assertEquals(EventStatus.PENDING, result.getStatus());
        assertEquals(regularUser, result.getUser());
    }

    @Test
    void testCreateNewEvent_CorrectEventCostForFreeEvent() {
        event.setFreeEvent(true);
        event.setEventCost(BigDecimal.ZERO);

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.createNewEvent(event, regularUser);

        assertEquals(EventStatus.PENDING, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getEventCost());
    }

    @Test
    void testCreateNewEvent_CorrectEventCostForPaidEvent() {
        event.setFreeEvent(false);
        event.setEventCost(new BigDecimal("150.00"));

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.createNewEvent(event, adminUser);

        assertEquals(EventStatus.APPROVED, result.getStatus());
        assertEquals(new BigDecimal("150.00"), result.getEventCost());
    }


    @Test
    void testUpdateEvent_AllFieldsUpdated() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.updateEvent(1L, updatedEvent);

        assertEquals("Updated Org", result.getOrganizationName());
        assertEquals("Updated Event", result.getEventName());
        assertEquals("Updated Description", result.getEventDescription());
        assertEquals(updatedEvent.getEventDate(), result.getEventDate());
        assertFalse(result.getFreeEvent());
        assertEquals(new BigDecimal("200.00"), result.getEventCost());
        assertEquals("http://updated-event-link.com", result.getEventLink());
        assertEquals("Seminar", result.getEventType());
    }

    @Test
    void testUpdateEvent_SomeFieldsNull_NotUpdated() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        updatedEvent.setOrganizationName(null);
        updatedEvent.setEventCost(null);

        Event result = eventService.updateEvent(1L, updatedEvent);

        // Перевірка, що поля, які є null в updatedEvent, не були змінені
        assertEquals("Existing Org", result.getOrganizationName());
        assertEquals(new BigDecimal("100.00"), result.getEventCost());
    }

    @Test
    void testUpdateEvent_EventNotFound_ThrowsResourceNotFoundException() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.updateEvent(999L, updatedEvent));
    }

    @Test
    void testUpdateEvent_UpdateSpeakers() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<EventRequest.Speaker> speakers = new ArrayList<>();
        speakers.add(new EventRequest.Speaker("Speaker 1", "Topic 1"));
        speakers.add(new EventRequest.Speaker("Speaker 2", "Topic 2"));

        updatedEvent.setSpeakers(speakers);

        Event result = eventService.updateEvent(1L, updatedEvent);

        assertEquals(speakers, result.getSpeakers());
    }

    @Test
    void testDeleteEvent_SuccessfulDeletion() {
        when(eventRepository.findEventById(event.getId())).thenReturn(Optional.of(event));

        eventService.deleteEvent(event.getId());

        verify(eventRepository, times(1)).deleteById(event.getId());
    }

    @Test
    void testDeleteEvent_EventNotFound_ThrowsResourceNotFoundException() {
        when(eventRepository.findEventById(event.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(1L));
        verify(eventRepository, never()).deleteById(event.getId());
    }

    @Test
    void testApproveEvent_SuccessfulApproval() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event approvedEvent = eventService.approveEvent(event.getId());

        assertEquals(EventStatus.APPROVED, approvedEvent.getStatus());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testApproveEvent_AlreadyApproved_ThrowsBadRequestException() {
        event.setStatus(EventStatus.APPROVED);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThrows(BadRequestException.class, () -> eventService.approveEvent(1L));
        verify(eventRepository, never()).save(event);
    }

    @Test
    void testApproveEvent_EventNotFound_ThrowsResourceNotFoundException() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.approveEvent(1L));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testRejectEvent_SuccessfulRejection() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event rejectedEvent = eventService.rejectEvent(event.getId());

        assertEquals(EventStatus.REJECTED, rejectedEvent.getStatus());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testRejectEvent_AlreadyRejected_ThrowsBadRequestException() {
        event.setStatus(EventStatus.REJECTED);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThrows(BadRequestException.class, () -> eventService.rejectEvent(1L));
        verify(eventRepository, never()).save(event);
    }

    @Test
    void testRejectEvent_EventNotFound_ThrowsResourceNotFoundException() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.rejectEvent(1L));
        verify(eventRepository, never()).save(any(Event.class));
    }
}
