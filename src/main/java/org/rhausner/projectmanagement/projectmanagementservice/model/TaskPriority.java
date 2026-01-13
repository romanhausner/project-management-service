package org.rhausner.projectmanagement.projectmanagementservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskPriority implements JsonEnum{
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    @JsonCreator
    public static TaskPriority fromValue(String value) {
        return EnumJsonUtils.fromValue(TaskPriority.class, value);
    }

    @JsonValue
    public String toValue() {
        return JsonEnum.super.toValue();
    }
}
