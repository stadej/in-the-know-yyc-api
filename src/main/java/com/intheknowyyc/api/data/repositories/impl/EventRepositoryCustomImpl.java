package com.intheknowyyc.api.data.repositories.impl;

import com.intheknowyyc.api.data.models.Event;
import com.intheknowyyc.api.data.models.EventStatus;
import com.intheknowyyc.api.data.repositories.EventRepositoryCustom;
import com.intheknowyyc.api.data.repositories.helpers.ColumnNameResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Transactional
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Event> findFilteredEvents(LocalDateTime startDate, LocalDateTime endDate, String eventType, String industry, Boolean freeEvent, String organizationName, String location, String searchText, Pageable pageable, EventStatus status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM events e WHERE 1=1");
        StringBuilder sqlConditions = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();

        // Build SQL conditions
        appendConditionIfNotNull(sqlConditions, "e.event_date >= :startDate", startDate, "startDate", parameters);
        appendConditionIfNotNull(sqlConditions, "e.event_date <= :endDate", endDate, "endDate", parameters);
        appendConditionIfNotEmpty(sqlConditions, "e.event_type = :eventType", eventType, "eventType", parameters);
        appendConditionIfNotEmpty(sqlConditions, "e.industry = :industry", industry, "industry", parameters);
        appendConditionIfNotNull(sqlConditions, "e.is_event_free = :freeEvent", freeEvent, "freeEvent", parameters);
        appendConditionIfNotEmpty(sqlConditions, "e.location = :location", location, "location", parameters);
        appendConditionIfNotEmpty(sqlConditions, "e.organization_name = :organizationName", organizationName, "organizationName", parameters);
        appendSearchCondition(sqlConditions, searchText, parameters);

        if (status != null) {
            sqlConditions.append(" AND e.status = :status");
            parameters.put("status", status.name());
        }

        sql.append(sqlConditions);

        // Append sorting
        appendSorting(sql, pageable);

        // Execute main query
        List<Event> events = executeQuery(sql.toString(), parameters, pageable);

        // Execute count query
        long totalResults = executeCountQuery(sqlConditions, parameters);

        return new PageImpl<>(events, pageable, totalResults);
    }

    private void appendConditionIfNotNull(StringBuilder sqlConditions, String condition, Object value, String paramName, Map<String, Object> parameters) {
        if (value != null) {
            sqlConditions.append(" AND ").append(condition);
            parameters.put(paramName, value);
        }
    }

    private void appendConditionIfNotEmpty(StringBuilder sqlConditions, String condition, String value, String paramName, Map<String, Object> parameters) {
        if (value != null && !value.isEmpty()) {
            sqlConditions.append(" AND ").append(condition);
            parameters.put(paramName, value);
        }
    }

    private void appendSearchCondition(StringBuilder sqlConditions, String searchText, Map<String, Object> parameters) {
        if (searchText != null && !searchText.isEmpty()) {
            sqlConditions.append(" AND (LOWER(e.event_name) LIKE :searchText OR LOWER(e.organization_name) LIKE :searchText)");
            parameters.put("searchText", "%" + searchText.toLowerCase() + "%");
        }
    }

    private void appendSorting(StringBuilder sql, Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            sql.append(" ORDER BY ");
            List<String> orderConditions = pageable.getSort().stream()
                    .map(order -> "e." + ColumnNameResolver.getDatabaseColumnName(Event.class, order.getProperty()) + " " + (order.isAscending() ? "ASC" : "DESC"))
                    .collect(Collectors.toList());
            sql.append(String.join(", ", orderConditions));
        }
    }

    private List<Event> executeQuery(String sql, Map<String, Object> parameters, Pageable pageable) {
        Query query = entityManager.createNativeQuery(sql, Event.class);
        setParameters(query, parameters);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

    private long executeCountQuery(StringBuilder sqlConditions, Map<String, Object> parameters) {
        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM events e WHERE 1=1" + sqlConditions);
        setParameters(countQuery, parameters);
        return (long) countQuery.getSingleResult();
    }

    private void setParameters(Query query, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
    }
}
