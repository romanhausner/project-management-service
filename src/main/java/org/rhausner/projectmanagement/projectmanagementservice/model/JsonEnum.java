package org.rhausner.projectmanagement.projectmanagementservice.model;

/**
 * Marker interface for enums that should expose a JSON-friendly string representation.
 * <p>
 * Enums implementing this interface gain a default {@link #toValue()} implementation
 * which returns a stable, uppercase name suitable for JSON output (via
 * {@code @JsonValue} when used with Jackson). Implementing this interface signals
 * that the enum supports a canonical external representation and may be paired with
 * a shared parsing utility for {@code @JsonCreator} conversion.
 */
public interface JsonEnum {
    /**
     * Return the external JSON representation for this enum constant.
     * <p>
     * The default implementation returns the enum name in uppercase. This value is
     * intended for stable serialization (for example using Jackson's
     * {@code @JsonValue}). Implementing enums can override this method to provide
     * a different serialized form if required.
     *
     * @return the JSON-compatible string representation of this enum constant
     */
    default String toValue() {
        return ((Enum<?>) this).name().toUpperCase();
    }
}
