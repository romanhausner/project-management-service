package org.rhausner.projectmanagement.projectmanagementservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ProjectStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    @JsonCreator
    public static ProjectStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value.replace("-", "_")))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown status: " + value));
    }

    @JsonValue
    public String toValue() {
        return name().toUpperCase();
    }
}
