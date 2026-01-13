package org.rhausner.projectmanagement.projectmanagementservice.model;

import java.util.Objects;

/**
 * Utility class to convert external string values into enum constants.
 * <p>
 * This helper normalizes incoming values (it replaces '-' with '_' and performs a
 * case-insensitive match against enum names) to increase tolerance for common
 * variants in JSON payloads or external systems. It throws an
 * {@link IllegalArgumentException} when the input value is null or no matching
 * enum constant can be found.
 * <p>
 * The class is final and non-instantiable; use the static {@link #fromValue(Class, String)}
 * method to perform conversions.
 */
public final class EnumJsonUtils {
    private EnumJsonUtils() {}

    /**
     * Convert the provided string value to the corresponding enum constant of the
     * given enum type.
     * <p>
     * Normalization rules applied to the input value before matching:
     * <ul>
     *   <li>All '-' characters are replaced with '_'</li>
     *   <li>Comparison is case-insensitive against the enum constant names</li>
     * </ul>
     * <p>
     * Example: the input values "in-progress" or "IN_PROGRESS" will match an
     * enum constant named {@code IN_PROGRESS}.
     *
     * @param enumType the enum class to search for a matching constant (must not be null)
     * @param value the input string value to convert (must not be null)
     * @param <E> the enum type
     * @return the matching enum constant
     * @throws IllegalArgumentException if {@code enumType} or {@code value} is null,
     *                                  or if no matching enum constant is found
     */
    public static <E extends Enum<E>> E fromValue(Class<E> enumType, String value) {
        Objects.requireNonNull(enumType, "enumType");
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
        String normalized = value.replace("-", "_");
        for (E constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(normalized)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
