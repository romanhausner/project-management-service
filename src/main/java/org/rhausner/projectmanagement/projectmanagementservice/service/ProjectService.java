package org.rhausner.projectmanagement.projectmanagementservice.service;

import org.rhausner.projectmanagement.projectmanagementservice.repository.ProjectRepository;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project getProjectById(Integer id) {
        return projectRepository.findById(id).orElse(null);
    }
}
