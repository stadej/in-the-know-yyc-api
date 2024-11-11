package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.models.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EventRepositoryCustom {
    Page<Event> findFilteredEvents(LocalDateTime startDate,
                                   LocalDateTime endDate,
                                   String eventType,
                                   String organizationName,
                                   String location,
                                   String searchText,
                                   Pageable pageable,
                                   EventStatus status);
}
