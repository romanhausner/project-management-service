package org.rhausner.projectmanagement.projectmanagementservice.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain entity representing a project in the system.
 *
 * This JPA entity stores the minimal attributes for a project such as name,
 * description, start/end dates and the current {@link ProjectStatus}. It is
 * persisted using JPA and mapped to a database table by the framework.
 */
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus projectStatus;

    /**
     * No-args constructor required by JPA.
     */
    public Project() {
    }

    /**
     * Convenience constructor to create a project instance with its core fields.
     * The {@code projectStatus} is initialised to {@link ProjectStatus#PLANNED} by default.
     *
     * @param id          the project id (may be {@code null} for new entities)
     * @param name        the project name
     * @param description the project description
     * @param startDate   the planned start date
     * @param endDate     the planned end date
     */
    public Project(Integer id, String name, String description, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectStatus = ProjectStatus.PLANNED;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    /**
     * Clear the description (set it to {@code null}).
     *
     * This method is useful to explicitly remove an optional description value
     * (for example when applying a PATCH that clears the field).
     */
    public void clearDescription(){
        this.description = null;
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

    /**
     * Clear the end date (set it to {@code null}).
     *
     * Use this to explicitly remove an end date value when the business logic
     * requires clearing the field.
     */
    public void clearEndDate() {
        this.endDate = null;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    /**
     * Equality is based on id, name, description and dates. Note that the id may be
     * {@code null} for transient instances; the equality logic mirrors the previous
     * implementation and intentionally includes multiple fields for identity comparison.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) && Objects.equals(name, project.name) && Objects.equals(description, project.description) && Objects.equals(startDate, project.startDate) && Objects.equals(endDate, project.endDate);
    }

    /**
     * Hash code implementation matching {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, startDate, endDate);
    }
}
