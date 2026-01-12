package org.rhausner.projectmanagement.projectmanagementservice.dto.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.rhausner.projectmanagement.projectmanagementservice.exception.BadRequestException;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Command object for PATCH-style updates to a Project.
 *
 * This class represents a partial update (patch). Fields are modelled as Optionals
 * to explicitly express presence/absence of values from the incoming JSON. For fields
 * where a difference between "field missing" and "field explicitly set to null" is
 * important, an additional "present" flag is used (e.g. descriptionPresent / endDatePresent).
 *
 * The static `from(JsonNode)` factory parses a JSON node and performs basic validation
 * (e.g. non-null for required properties when present, ISO date parsing). It throws
 * BadRequestException for invalid input.
 */
@SuppressWarnings("OptionalUsedAsField") // intentionally allow Optionals as fields for patch semantics
public class ProjectPatchCommand {

    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private boolean descriptionPresent = false;
    private Optional<LocalDate> startDate = Optional.empty();
    private Optional<LocalDate> endDate = Optional.empty();
    private boolean endDatePresent = false;
    private Optional<ProjectStatus> projectStatus = Optional.empty();

    /**
     * Parse a JsonNode into a ProjectPatchCommand.
     *
     * Important behavior notes:
     * - If a field is missing, the corresponding Optional remains empty and consumer should not change that property.
     * - If a field is present with JSON null, the semantics differ per-field: description and endDate use presence flags
     *   so the caller can explicitly clear those values.
     * - startDate and name throw BadRequestException when present but null, because those should not be nulled by a patch.
     */
    public static ProjectPatchCommand from(JsonNode node) {
        ProjectPatchCommand cmd = new ProjectPatchCommand();

        // NAME: if provided, it must not be JSON null; we require a non-null value when name is included.
        if (node.has("name")) {
            if (node.get("name").isNull()) {
                throw new BadRequestException("name must not be null");
            }
            cmd.name = Optional.of(node.get("name").toString());
        }

        // DESCRIPTION: support explicit clearing via JSON null. Use descriptionPresent to distinguish
        // between "not provided" and "explicitly set to null".
        if (node.has("description")) {
            cmd.descriptionPresent = true;
            cmd.description = node.get("description").isNull()
                    ? Optional.empty()
                    : Optional.of(node.get("description").toString());
        }

        // START DATE: if provided it must be a valid ISO date and not null.
        if (node.has("startDate")) {
            if (node.get("startDate").isNull()) {
                throw new BadRequestException("startDate must not be null");
            }
            try {
                cmd.startDate = Optional.of(node.get("startDate").toString()).map(LocalDate::parse);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("startDate must be a valid date in ISO format (yyyy-MM-dd)");
            }
        }

        // END DATE: similar to description, we track presence and allow explicit clearing with null
        if (node.has("endDate")) {
            cmd.endDatePresent = true;
            try {
                cmd.endDate = node.get("endDate").isNull()
                        ? Optional.empty()
                        : Optional.of(node.get("endDate").toString()).map(LocalDate::parse);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("endDate must be a valid date in ISO format (yyyy-MM-dd)");
            }
        }

        // PROJECT STATUS: allow setting or clearing of the enum value. ProjectStatus.fromValue
        // should convert the incoming string to the enum or throw if invalid.
        if (node.has("projectStatus")) {
            cmd.projectStatus = node.get("projectStatus").isNull()
                    ? Optional.empty()
                    : Optional.of(ProjectStatus.fromValue(node.get("projectStatus").toString()));
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

    public boolean isDescriptionPresent() {
        return descriptionPresent;
    }

    public boolean isEndDatePresent() {
        return endDatePresent;
    }
}
