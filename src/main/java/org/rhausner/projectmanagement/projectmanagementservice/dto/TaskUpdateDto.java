package org.rhausner.projectmanagement.projectmanagementservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskPriority;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskStatus;

import java.time.LocalDate;

/**
 * DTO used when performing a full update (PUT) of a Task.
 */
@Schema(description = "Data for fully updating an existing task (PUT)")
public class TaskUpdateDto {

    @Schema(description = "Title of the task", example = "Implement login feature", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String title;

    @Schema(description = "Detailed description of the task", example = "Create user authentication with OAuth2")
    private String description;

    @Schema(description = "Current status of the task. DONE is terminal", example = "IN_PROGRESS", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private TaskStatus status;

    @Schema(description = "Priority level of the task", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Due date for task completion", example = "2026-02-28")
    private LocalDate dueDate;

    @Schema(description = "Person assigned to this task", example = "john.doe@example.com")
    private String assignee;

    /**
     * No-args constructor required by Jackson and other frameworks.
     */
    public TaskUpdateDto() {
    }

    /**
     * All-args constructor for convenient manual instantiation in tests or internal code.
     */
    public TaskUpdateDto(String title, String description, TaskStatus status, TaskPriority priority, LocalDate dueDate, String assignee) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignee = assignee;
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
}

