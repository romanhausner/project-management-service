package org.rhausner.projectmanagement.projectmanagementservice.controller;

import jakarta.validation.Valid;
import org.rhausner.projectmanagement.projectmanagementservice.dto.*;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.dto.command.ProjectPatchCommand;
import org.rhausner.projectmanagement.projectmanagementservice.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing projects.
 * Provides CRUD operations for {@link Project}.
 */
@RestController
@RequestMapping("api/v1/projects")
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
    @GetMapping
    public List<ProjectGetDto> getProjects() {
        return projectService.getAllProjects().stream()
                .map(projectMapper::toGetDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new project.
     * Request: ProjectCreateDto (validated)
     * Response: created ProjectGetDto with generated id
     */
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
    @GetMapping("/{id}")
    public ProjectGetDto getProjectById(@PathVariable Integer id) {
        return projectMapper.toGetDto(projectService.getProjectById(id));
    }

    /**
     * Replace an existing project with the provided DTO.
     * This is a full update (PUT semantics): caller provides the new state in ProjectUpdateDto.
     * Request is validated; the mapper converts DTO -> entity, service persists and returns the updated entity.
     * Implicitly 404 if the project to update does not exist.
     */
    @PutMapping("/{id}")
    public ProjectGetDto update(@PathVariable Integer id, @Valid @RequestBody ProjectUpdateDto projectDto) {
        Project project = projectMapper.fromUpdateDto(projectDto);
        Project updated = projectService.updateProject(id, project);
        return projectMapper.toGetDto(updated);
    }

    /**
     * Delete a project by id.
     * Response: 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectById(@PathVariable Integer id) {
        projectService.deleteProjectById(id);
    }

    /**
     * Apply a partial update (PATCH) to an existing project.
     * The request body is read as a raw JsonNode to support flexible patch payloads.
     * We convert the JsonNode into a ProjectPatchCommand which encodes presence/absence semantics
     * and then delegate the patching logic to the service layer.
     */
    @PatchMapping("/{id}")
    public ProjectGetDto patchProject(@PathVariable Integer id, @RequestBody JsonNode patch) {
        ProjectPatchCommand command = ProjectPatchCommand.from(patch);
        Project updated = projectService.patchProject(id, command);
        return projectMapper.toGetDto(updated);
    }
}
