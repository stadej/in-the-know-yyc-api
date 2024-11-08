package com.intheknowyyc.api.data.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TargetType {
    SITE,
    EVENT;

    @JsonCreator
    public static TargetType fromString(String value) {
        return TargetType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toLowerCase() {
        return this.name().toLowerCase();
    }
}
