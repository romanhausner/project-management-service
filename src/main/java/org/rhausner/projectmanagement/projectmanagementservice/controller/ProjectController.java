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

    @GetMapping
    public List<ProjectGetDto> getProjects() {
        return projectService.getAllProjects().stream()
                .map(projectMapper::toGetDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectGetDto createProject(@Valid @RequestBody ProjectCreateDto projectDto) {
        Project toSave = projectMapper.fromCreateDto(projectDto);
        Project saved = projectService.createProject(toSave);
        return projectMapper.toGetDto(saved);
    }

    @GetMapping("/{id}")
    public ProjectGetDto getProjectById(@PathVariable Integer id) {
        return projectMapper.toGetDto(projectService.getProjectById(id));
    }

    /**
     * Updates existing project with given id.
     *
     * @param id id of project to update
     * @param projectDto project to update
     * @return updated project as dto
     */
    @PutMapping("/{id}")
    public ProjectGetDto update(@PathVariable Integer id, @Valid @RequestBody ProjectUpdateDto projectDto) {
        Project project = projectMapper.fromUpdateDto(projectDto);
        Project updated = projectService.updateProject(id, project);
        return projectMapper.toGetDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectById(@PathVariable Integer id) {
        projectService.deleteProjectById(id);
    }

//    @PatchMapping("/{id}")
//    public ProjectGetDto patchProject(
//            @PathVariable Integer id,
//            @RequestBody JsonNode patch) {
//
//        Project project = projectService.getProjectById(id);
//
//        if (patch.has("description")) {
//            if (patch.get("description").isNull()) {
//                project.setDescription(null);
//            } else {
//                project.setDescription(patch.get("description").asString());
//            }
//        }
//
//        projectService.updateProject(id, project);
//        return projectMapper.toGetDto(project);
//    }

    @PatchMapping("/{id}")
    public ProjectGetDto patchProject(
            @PathVariable Integer id,
            @RequestBody JsonNode patch) {

        ProjectPatchCommand command = ProjectPatchCommand.from(patch);
        Project updated = projectService.patchProject(id, command);

        return projectMapper.toGetDto(updated);
    }
}
