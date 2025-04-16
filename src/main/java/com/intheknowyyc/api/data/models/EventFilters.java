package com.intheknowyyc.api.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFilters {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String eventType;
    private String industry;
    private Boolean freeEvent;
    private String organizationName;
    private String location;
    private String searchText;
    private EventStatus status = EventStatus.APPROVED;
    private Pageable pageable;
}
