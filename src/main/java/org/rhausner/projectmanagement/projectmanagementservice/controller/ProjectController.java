package org.rhausner.projectmanagement.projectmanagementservice.controller;

import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for {@link Project}.
 */
@RestController
@RequestMapping("api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable Integer id) {
        return projectService.getProjectById(id);
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable Integer id, @RequestBody Project project) {
        return projectService.updateProject(id, project);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectById(@PathVariable Integer id) {
        projectService.deleteProjectById(id);
    }
}
