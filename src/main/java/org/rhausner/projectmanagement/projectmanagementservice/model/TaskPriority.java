package org.rhausner.projectmanagement.projectmanagementservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of possible task priority levels.
 * The enum includes JSON-friendly factory and serializer helpers so that
 * incoming string values such as "high" or "HIGH" are accepted
 * and correctly mapped to enum constants. The JSON representation produced by
 * {@link #toValue()} uses uppercase names.
 */
public enum TaskPriority implements JsonEnum{
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    /**
     * Create a {@link TaskPriority} from a provided string value.
     * The factory is lenient: it accepts values in different case and also
     * hyphenated forms (e.g. "high"). If the value does not match any
     * known priority, an {@link IllegalArgumentException} is thrown.
     * This method is annotated with {@link JsonCreator} so Jackson can use it
     * during deserialization of JSON payloads.
     *
     * @param value the textual representation of a priority
     * @return the corresponding TaskPriority
     * @throws IllegalArgumentException when the value is unknown
     */
    @JsonCreator
    public static TaskPriority fromValue(String value) {
        return EnumJsonUtils.fromValue(TaskPriority.class, value);
    }

    /**
     * Return the JSON representation of this enum value.
     * The value produced is the enum name in upper case. Annotated with
     * {@link JsonValue} so Jackson will use it for serialization.
     *
     * @return the string representation used for JSON output
     */
    @JsonValue
    public String toValue() {
        return JsonEnum.super.toValue();
    }
}
