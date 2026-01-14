package org.rhausner.projectmanagement.projectmanagementservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskPriority;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO returned for GET requests representing a Task's public representation.
 */
@Schema(description = "Represents a task in GET responses")
public class TaskGetDto {

    @Schema(description = "Unique identifier of the task", example = "1")
    private Integer id;

    @Schema(description = "ID of the project this task belongs to", example = "1")
    private Integer projectId;

    @Schema(description = "Title of the task", example = "Implement login feature")
    private String title;

    @Schema(description = "Detailed description of the task", example = "Create user authentication with OAuth2")
    private String description;

    @Schema(description = "Current status of the task", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "Priority level of the task", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Due date for task completion", example = "2026-02-28")
    private LocalDate dueDate;

    @Schema(description = "Person assigned to this task", example = "john.doe@example.com")
    private String assignee;

    @Schema(description = "Timestamp when the task was created", example = "2026-01-15T10:30:00Z")
    private Instant createdAt;

    @Schema(description = "Timestamp when the task was completed", example = "2026-02-15T14:45:00Z")
    private Instant completedAt;

    /**
     * No-args constructor required by Jackson and other frameworks.
     */
    public TaskGetDto() {
    }

    /**
     * All-args constructor for convenient manual instantiation in tests or internal code.
     */
    public TaskGetDto(Integer id, Integer projectId, String title, String description, TaskStatus status, TaskPriority priority, LocalDate dueDate, String assignee, Instant createdAt, Instant completedAt) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignee = assignee;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}

