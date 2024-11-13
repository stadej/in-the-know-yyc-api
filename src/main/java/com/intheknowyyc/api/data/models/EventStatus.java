package com.intheknowyyc.api.data.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventStatus {
    PENDING, APPROVED, REJECTED;

    @JsonCreator
    public static EventStatus fromString(String value) {
        return EventStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toLowerCase() {
        return this.name().toLowerCase();
    }
}
