package org.rhausner.projectmanagement.projectmanagementservice.dto.command;

import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.Optional;

public class ProjectPatchCommand {

    private Optional<String> name;
    private Optional<String> description;
    private Optional<LocalDate> startDate;
    private Optional<LocalDate> endDate;
    private Optional<ProjectStatus> projectStatus;

    public static ProjectPatchCommand from(JsonNode node) {
        ProjectPatchCommand cmd = new ProjectPatchCommand();

        if (node.has("name")) {
            cmd.name = node.get("name").isNull()
                    ? Optional.empty()
                    : Optional.of(node.get("name").asString());
        }

        if (node.has("description")) {
            cmd.description = node.get("description").isNull()
                    ? Optional.empty()
                    : Optional.of(node.get("description").asString());
        }

        if (node.has("startDate")) {
            cmd.startDate = node.get("startDate").isNull()
                    ? Optional.empty()
                    : Optional.of(node.get("startDate").asString()).map(LocalDate::parse);
        }

        if (node.has("endDate")) {
            cmd.endDate = node.get("endDate").isNull()
                    ? Optional.empty()
                    : Optional.of(node.get("endDate").asString()).map(LocalDate::parse);
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
