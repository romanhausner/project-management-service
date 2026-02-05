package org.rhausner.projectmanagement.projectmanagementservice.dto.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.rhausner.projectmanagement.projectmanagementservice.exception.BadRequestException;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskPriority;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Command object for PATCH-style updates to a Task.
 * <p>
 * This class mirrors the semantics of {@code ProjectPatchCommand} and represents
 * a partial update where each field is modelled as an {@link Optional} to
 * explicitly express presence/absence. For fields where "explicitly set to null"
 * must be distinguished from "not provided", an additional presence flag is used
 * (e.g. {@code descriptionPresent}, {@code dueDatePresent}, {@code assigneePresent}).
 */
@SuppressWarnings("OptionalUsedAsField")
public class TaskPatchCommand {

    private Optional<Long> projectId;

    private Optional<Long> id;

    private Optional<String> title;

    private Optional<String> description;

    private Optional<LocalDate> dueDate;

    private Optional<TaskStatus> status;

    private Optional<TaskPriority> priority;

    private Optional<String> assignee;

    /**
     * Parse a JsonNode into a TaskPatchCommand.
     * <p>
     * Behavior notes:
     * - Missing fields leave the corresponding Optional empty (caller should not change that property).
     * - Fields with presence flags (description, dueDate, assignee) allow explicit clearing via JSON null.
     * - Fields like title must not be provided as JSON null; providing null for required fields will cause a BadRequestException.
     */
    public static TaskPatchCommand from(JsonNode node) {
        TaskPatchCommand cmd = new TaskPatchCommand();

        // PROJECT ID: if provided, must not be JSON null
        if(node.has("projectId")) {
            if (node.get("projectId").isNull()) {
                throw new BadRequestException("projectId must not be null");
            }
            cmd.projectId = Optional.of(node.get("projectId").asLong());
        }

        // ID: if provided, must not be JSON null
        if(node.has("id")) {
            if (node.get("id").isNull()) {
                throw new BadRequestException("id must not be null");
            }
            cmd.id = Optional.of(node.get("id").asLong());
        }

        // TITLE: if provided it must not be JSON null
        if (node.has("title")) {
            if (node.get("title").isNull()) {
                throw new BadRequestException("title must not be null");
            }
            cmd.title = Optional.of(node.get("title").asText());
        }

        // DESCRIPTION: presence flag and allow explicit clearing with null
        if (node.has("description")) {
            cmd.description = node.get("description").isNull()
                    ? Optional.empty()
                    : Optional.of(node.get("description").asText());
        }

        // DUE DATE: presence flag and allow clearing with null; validate ISO date if present and not null
        if (node.has("dueDate")) {
            try {
                cmd.dueDate = node.get("dueDate").isNull()
                        ? Optional.empty()
                        : Optional.of(node.get("dueDate").asText()).map(LocalDate::parse);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("dueDate must be a valid date in ISO format (yyyy-MM-dd)");
            }
        }

        // STATUS: allow setting or clearing the enum value
        if (node.has("status")) {
            cmd.status = node.get("status").isNull()
                    ? Optional.empty()
                    : Optional.of(TaskStatus.fromValue(node.get("status").asText()));
        }

        // PRIORITY: allow setting or clearing the enum value
        if (node.has("priority")) {
            cmd.priority = node.get("priority").isNull()
                    ? Optional.empty()
                    : Optional.of(TaskPriority.fromValue(node.get("priority").asText()));
        }

        // ASSIGNEE: presence flag and allow explicit clearing
        if (node.has("assignee")) {
            cmd.assignee = node.get("assignee").isNull()
                    ? Optional.empty()
                    : Optional.of(node.get("assignee").asText());
        }

        return cmd;
    }

    public Optional<Long> getProjectId() {
        return projectId;
    }

    public  Optional<Long> getId() {
        return id;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<LocalDate> getDueDate() {
        return dueDate;
    }

    public Optional<TaskStatus> getStatus() {
        return status;
    }

    public Optional<TaskPriority> getPriority() {
        return priority;
    }

    public Optional<String> getAssignee() {
        return assignee;
    }

}

