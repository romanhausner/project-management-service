package org.rhausner.projectmanagement.projectmanagementservice.exception;

/**
 * Exception thrown when a client attempts to modify a field that is considered immutable.
 *
 * This unchecked exception is used to signal that a request tried to change an attribute
 * which must remain constant for the lifetime of the entity (for example: changing the
 * associated project id of an existing task).
 */
public class ImmutableFieldException extends RuntimeException {

    /**
     * Create a new ImmutableFieldException for the given field name.
     *
     * @param field the name of the field that was attempted to be changed
     */
    public ImmutableFieldException(String field) {
        super(field + " is immutable");
    }
}
