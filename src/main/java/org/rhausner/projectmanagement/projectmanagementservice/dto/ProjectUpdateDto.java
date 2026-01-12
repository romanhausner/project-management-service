package org.rhausner.projectmanagement.projectmanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;

import java.time.LocalDate;

/**
 * Data Transfer Object used when updating an existing Project via PUT.
 * This DTO represents the complete new state of a project (PUT semantics).
 * Validation annotations enforce required fields for update operations.
 */
public class ProjectUpdateDto {

    @NotBlank
    private String name;
    private String description;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull
    private ProjectStatus projectStatus;

    /**
     * No-args constructor required by Jackson and other frameworks.
     */
    public ProjectUpdateDto() {
    }

    /**
     * All-args constructor for convenient manual instantiation in tests or internal code.
     */
    public ProjectUpdateDto(String name, String description, LocalDate startDate, LocalDate endDate, ProjectStatus projectStatus) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectStatus = projectStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }
}
