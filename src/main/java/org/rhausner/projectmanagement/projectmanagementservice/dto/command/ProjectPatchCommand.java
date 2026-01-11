package org.rhausner.projectmanagement.projectmanagementservice.dto.command;

import org.rhausner.projectmanagement.projectmanagementservice.exception.BadRequestException;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ProjectPatchCommand {

    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<LocalDate> startDate = Optional.empty();
    private Optional<LocalDate> endDate = Optional.empty();
    private Optional<ProjectStatus> projectStatus = Optional.empty();

    public static ProjectPatchCommand from(JsonNode node) {
        ProjectPatchCommand cmd = new ProjectPatchCommand();

        if (node.has("name")) {
            if (node.get("name").isNull()) {
                throw new BadRequestException("name must not be null");
            }
            cmd.name = Optional.of(node.get("name").asString());
        }

        if (node.has("description")) {
            cmd.description = node.get("description").isNull()
                    ? Optional.empty()
                    : Optional.of(node.get("description").asString());
        }

        if (node.has("startDate")) {
            if (node.get("startDate").isNull()) {
                throw new BadRequestException("startDate must not be null");
            }
            try {
                cmd.startDate = Optional.of(node.get("startDate").asString()).map(LocalDate::parse);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("startDate must be a valid date in ISO format (yyyy-MM-dd)");
            }
        }

        try {
            if (node.has("endDate")) {
                cmd.endDate = node.get("endDate").isNull()
                        ? Optional.empty()
                        : Optional.of(node.get("endDate").asString()).map(LocalDate::parse);
            }
        } catch (DateTimeParseException e) {
            throw new BadRequestException("endDate must be a valid date in ISO format (yyyy-MM-dd)");
        }

        if (node.has("projectStatus")) {
            cmd.projectStatus = node.get("projectStatus").isNull()
                    ? Optional.empty()
                    : Optional.of(ProjectStatus.fromValue(node.get("projectStatus").asString()));
        }

        return cmd;
    }

    public Optional<String> getName() {
        return this.name;
    }

    public Optional<String> getDescription() {
        return this.description;
    }

    public Optional<LocalDate> getEndDate() {
        return this.endDate;
    }

    public Optional<ProjectStatus> getProjectStatus() {
        return this.projectStatus;
    }

    public Optional<LocalDate> getStartDate() {
        return this.startDate;
    }
}
