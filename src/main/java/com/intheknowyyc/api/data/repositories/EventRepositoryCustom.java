package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EventRepositoryCustom {
    Page<Event> findFilteredEvents(LocalDateTime startDate,
                                   LocalDateTime endDate,
                                   String eventType,
                                   String organizationName,
                                   String searchText,
                                   Pageable pageable);
}
