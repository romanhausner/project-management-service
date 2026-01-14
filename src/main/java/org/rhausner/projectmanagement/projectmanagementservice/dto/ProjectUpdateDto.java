package org.rhausner.projectmanagement.projectmanagementservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;

import java.time.LocalDate;

/**
 * Data Transfer Object used when updating an existing Project via PUT.
 * This DTO represents the complete new state of a project (PUT semantics).
 * Validation annotations enforce required fields for update operations.
 */
@Schema(description = "Data for fully updating an existing project (PUT)")
public class ProjectUpdateDto {

    @Schema(description = "Name of the project", example = "Website Redesign", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "Detailed description of the project", example = "Complete overhaul of the company website")
    private String description;

    @Schema(description = "Start date of the project", example = "2026-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate startDate;

    @Schema(description = "End date of the project", example = "2026-06-30")
    private LocalDate endDate;

    @Schema(description = "Current status of the project", example = "IN_PROGRESS", requiredMode = Schema.RequiredMode.REQUIRED)
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
