package org.rhausner.projectmanagement.projectmanagementservice.exception;

/**
 * Exception thrown when a project with given id is not found.
 * This unchecked exception indicates that a requested Project resource does not exist.
 * It is intended to be translated to an HTTP 404 (Not Found) response by a global
 * exception handler in the web layer.
 */
public class ProjectNotFoundException extends RuntimeException {

    /**
     * Create a new ProjectNotFoundException for the given project id.
     *
     * @param id the id of the project that could not be found
     */
    public ProjectNotFoundException(Long id) {
        super("Project with id " + id + " not found");
    }

}
