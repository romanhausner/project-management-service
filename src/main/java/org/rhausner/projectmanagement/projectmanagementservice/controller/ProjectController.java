package org.rhausner.projectmanagement.projectmanagementservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.rhausner.projectmanagement.projectmanagementservice.dto.*;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.dto.command.ProjectPatchCommand;
import org.rhausner.projectmanagement.projectmanagementservice.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing projects.
 * Provides CRUD operations for {@link Project}.
 */
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "API for managing projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    /**
     * Return all projects.
     * Response: JSON array of ProjectGetDto
     */
    @Operation(summary = "Get all projects", description = "Returns a list of all projects")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all projects",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectGetDto.class))))
    @GetMapping
    public List<ProjectGetDto> getProjects() {
        return projectService.getAllProjects().stream()
                .map(projectMapper::toGetDto)
                .toList();
    }

    /**
     * Create a new project.
     * Request: ProjectCreateDto (validated)
     * Response: created ProjectGetDto with generated id
     */
    @Operation(summary = "Create a new project", description = "Creates a new project with the provided data")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project successfully created",
                    content = @Content(schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectGetDto createProject(@Valid @RequestBody ProjectCreateDto projectDto) {
        Project toSave = projectMapper.fromCreateDto(projectDto);
        Project saved = projectService.createProject(toSave);
        return projectMapper.toGetDto(saved);
    }

    /**
     * Return a single project by id.
     * Response: ProjectGetDto, implicitly 404 if not found.
     */
    @Operation(summary = "Get project by ID", description = "Returns a single project by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project found",
                    content = @Content(schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ProjectGetDto getProjectById(
            @Parameter(description = "ID of the project to retrieve") @PathVariable Long id) {
        return projectMapper.toGetDto(projectService.getProjectById(id));
    }

    /**
     * Replace an existing project with the provided DTO.
     * This is a full update (PUT semantics): caller provides the new state in ProjectUpdateDto.
     * Request is validated; the mapper converts DTO -> entity, service persists and returns the updated entity.
     * Implicitly 404 if the project to update does not exist.
     */
    @Operation(summary = "Update a project", description = "Fully replaces an existing project with the provided data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project successfully updated",
                    content = @Content(schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ProjectGetDto update(
            @Parameter(description = "ID of the project to update") @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateDto projectDto) {
        Project project = projectMapper.fromUpdateDto(projectDto);
        Project updated = projectService.updateProject(id, project);
        return projectMapper.toGetDto(updated);
    }

    /**
     * Delete a project by id.
     * Response: 204 No Content on success.
     */
    @Operation(summary = "Delete a project", description = "Deletes a project by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Project successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectById(
            @Parameter(description = "ID of the project to delete") @PathVariable Long id) {
        projectService.deleteProjectById(id);
    }

    /**
     * Apply a partial update (PATCH) to an existing project.
     * The request body is read as a raw JsonNode to support flexible patch payloads.
     * We convert the JsonNode into a ProjectPatchCommand which encodes presence/absence semantics
     * and then delegate the patching logic to the service layer.
     */
    @Operation(summary = "Partially update a project", description = "Applies a partial update to an existing project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project successfully patched",
                    content = @Content(schema = @Schema(implementation = ProjectGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patch data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public ProjectGetDto patchProject(
            @Parameter(description = "ID of the project to patch") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON object with fields to update",
                    content = @Content(schema = @Schema(implementation = Object.class)))
            @RequestBody JsonNode patch) {
        ProjectPatchCommand command = ProjectPatchCommand.from(patch);
        Project updated = projectService.patchProject(id, command);
        return projectMapper.toGetDto(updated);
    }
}
