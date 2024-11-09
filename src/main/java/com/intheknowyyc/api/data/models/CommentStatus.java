package com.intheknowyyc.api.data.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommentStatus {
    PENDING,
    APPROVED,
    REJECTED;

    @JsonCreator
    public static CommentStatus fromString(String value) {
        return CommentStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toLowerCase() {
        return this.name().toLowerCase();
    }
}
