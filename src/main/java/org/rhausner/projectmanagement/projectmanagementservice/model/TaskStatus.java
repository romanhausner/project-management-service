package org.rhausner.projectmanagement.projectmanagementservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus implements JsonEnum{
    TODO,
    IN_PROGRESS,
    DONE;

    @JsonCreator
    public static TaskStatus fromValue(String value) {
        return EnumJsonUtils.fromValue(TaskStatus.class, value);
    }

    @JsonValue
    public String toValue() {
        return JsonEnum.super.toValue();
    }
}
