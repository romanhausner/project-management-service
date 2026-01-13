package org.rhausner.projectmanagement.projectmanagementservice.exception;

/**
 * Exception thrown when an operation is attempted on a task that is not
 * valid in the current state of that task.
 * <p>
 * This unchecked exception is intended for cases where business rules prevent
 * a state transition (for example: starting a task that is already completed).
 */
public class InvalidTaskStateException extends RuntimeException {

    /**
     * Create a new InvalidTaskStateException with a descriptive message.
     *
     * @param message a human-readable description of the invalid state transition
     */
    public InvalidTaskStateException(String message) {
        super(message);
    }
}
