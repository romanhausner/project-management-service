package org.rhausner.projectmanagement.projectmanagementservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;

import java.time.LocalDate;

/**
 * DTO returned for GET requests representing a Project's public representation.
 */
@Schema(description = "Represents a project in GET responses")
public class ProjectGetDto {

    @Schema(description = "Unique identifier of the project", example = "1")
    private Long id;

    @Schema(description = "Name of the project", example = "Website Redesign")
    private String name;

    @Schema(description = "Detailed description of the project", example = "Complete overhaul of the company website")
    private String description;

    @Schema(description = "Start date of the project", example = "2026-01-15")
    private LocalDate startDate;

    @Schema(description = "End date of the project", example = "2026-06-30")
    private LocalDate endDate;

    @Schema(description = "Current status of the project", example = "IN_PROGRESS")
    private ProjectStatus projectStatus;

    /**
     * No-args constructor required by Jackson and other frameworks.
     */
    public ProjectGetDto() {
    }

    /**
     * All-args constructor for convenient manual instantiation in tests or internal code.
     */
    public ProjectGetDto(Long id, String name, String description, LocalDate startDate, LocalDate endDate, ProjectStatus projectStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectStatus = projectStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
