package org.rhausner.projectmanagement.projectmanagementservice.exception;

/**
 * Exception thrown when a task with given id is not found.
 * This unchecked exception indicates that a requested task does not exist.
 * It is intended to be translated to an HTTP 404 (Not Found) response by a global
 * exception handler in the web layer.
 */
public class TaskNotFoundException extends RuntimeException{

    /**
     * Create a new TaskNotFoundException for the given task id.
     *
     * @param id the id of the task that could not be found
     */
    public TaskNotFoundException(Long id) {
        super("Task with id " + id + " not found");
    }
}
