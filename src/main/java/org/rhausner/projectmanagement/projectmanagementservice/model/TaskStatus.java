package org.rhausner.projectmanagement.projectmanagementservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of possible task lifecycle states.
 * The enum includes JSON-friendly factory and serializer helpers so that
 * incoming string values such as "in-progress" or "IN_PROGRESS" are accepted
 * and correctly mapped to enum constants. The JSON representation produced by
 * {@link #toValue()} uses uppercase names.
 */
public enum TaskStatus implements JsonEnum{
    TODO,
    IN_PROGRESS,
    DONE;

    /**
     * Create a {@link TaskStatus} from a provided string value.
     * The factory is lenient: it accepts values in different case and also
     * hyphenated forms (e.g. "in-progress"). If the value does not match any
     * known status, an {@link IllegalArgumentException} is thrown.
     * This method is annotated with {@link JsonCreator} so Jackson can use it
     * during deserialization of JSON payloads.
     *
     * @param value the textual representation of a status
     * @return the corresponding TaskStatus
     * @throws IllegalArgumentException when the value is unknown
     */
    @JsonCreator
    public static TaskStatus fromValue(String value) {
        return EnumJsonUtils.fromValue(TaskStatus.class, value);
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
