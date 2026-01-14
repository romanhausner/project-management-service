package org.rhausner.projectmanagement.projectmanagementservice.exception;

/**
 * Exception type representing a 400 Bad Request error scenario within the application.
 * This unchecked exception is intended to be thrown when the client provides invalid
 * input that prevents the request from being processed. It can be handled by a
 * global exception handler to return an appropriate HTTP 400 response to the caller.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Create a new BadRequestException with a human-readable message describing the
     * reason for the bad request.
     *
     * @param message descriptive error message
     */
    public BadRequestException(String message) {
        super(message);
    }

}
