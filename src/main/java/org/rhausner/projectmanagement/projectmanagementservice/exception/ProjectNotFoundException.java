package org.rhausner.projectmanagement.projectmanagementservice.exception;

/**
 * Exception thrown when a project with given id is not found.
 */
public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Integer id) {
        super("Project with id " + id + " not found");
    }

}
