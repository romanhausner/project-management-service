package org.rhausner.projectmanagement.projectmanagementservice.controller;

import jakarta.validation.Valid;
import org.rhausner.projectmanagement.projectmanagementservice.dto.*;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for {@link Project}.
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

    @PutMapping("/{id}")
    public ProjectGetDto update(@PathVariable Integer id, @Valid @RequestBody ProjectUpdateDto projectDto) {
        Project existing = projectService.getProjectById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        projectMapper.updateFromUpdateDto(existing, projectDto);
        Project updated = projectService.updateProject(id, existing);
        return projectMapper.toGetDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectById(@PathVariable Integer id) {
        projectService.deleteProjectById(id);
    }
}
