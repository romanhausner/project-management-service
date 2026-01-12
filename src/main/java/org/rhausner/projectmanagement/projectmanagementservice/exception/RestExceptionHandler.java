package org.rhausner.projectmanagement.projectmanagementservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 *
 * Converts application exceptions into appropriate HTTP responses with a consistent
 * JSON error structure where applicable. This class is registered via Spring's
 * {@code @ControllerAdvice} and provides handlers for common error cases.
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Handle cases where a requested project was not found.
     *
     * Returns HTTP 404 (Not Found) with a simple textual message in the response body.
     *
     * @param ex the thrown ProjectNotFoundException
     * @return a ResponseEntity with 404 status and the exception message as body
     */
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Object> handleProjectNotFound(ProjectNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    /**
     * Handle validation errors triggered by {@code @Valid} annotated request bodies.
     *
     * Returns HTTP 400 (Bad Request) and a JSON object containing a map of field names
     * to validation error messages for easier client-side handling.
     *
     * @param ex the MethodArgumentNotValidException produced by Spring validation
     * @return a ResponseEntity with 400 status and a structured error body
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "" : fe.getDefaultMessage(),
                        (a, b) -> a + "; " + b));
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle application-level bad request scenarios represented by {@link BadRequestException}.
     *
     * Returns HTTP 400 (Bad Request) with a JSON object containing a status and an error message.
     *
     * @param ex the BadRequestException thrown by the application
     * @return a ResponseEntity with 400 status and a structured error body
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback handler for all uncaught exceptions.
     *
     * Returns HTTP 500 (Internal Server Error) with a JSON object containing the status
     * and the exception message. In production you might want to hide internal messages
     * and return a more generic error description.
     *
     * @param ex the uncaught exception
     * @return a ResponseEntity with 500 status and a structured error body
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAll(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
