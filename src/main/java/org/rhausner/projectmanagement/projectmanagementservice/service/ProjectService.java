package org.rhausner.projectmanagement.projectmanagementservice.service;

import org.rhausner.projectmanagement.projectmanagementservice.exception.BadRequestException;
import org.rhausner.projectmanagement.projectmanagementservice.exception.ProjectNotFoundException;
import org.rhausner.projectmanagement.projectmanagementservice.dto.command.ProjectPatchCommand;
import org.rhausner.projectmanagement.projectmanagementservice.repository.ProjectRepository;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

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
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @Transactional
    public Project updateProject(Integer id, Project update) {
        Project existing = getProjectById(id);
        existing.setName(update.getName());
        existing.setDescription(update.getDescription());
        existing.setStartDate(update.getStartDate());
        existing.setEndDate(update.getEndDate());
        existing.setProjectStatus(update.getProjectStatus());
        return existing;
    }

    public void deleteProjectById(Integer id) {
        projectRepository.deleteById(id);
    }

    @Transactional
    public Project patchProject(Integer id, ProjectPatchCommand cmd) {
        Project project = getProjectById(id);
        cmd.getName().ifPresent(name -> {
            if (name.isBlank()) {
                throw new BadRequestException("name must not be blank");
            }
            project.setName(name);
        });
        if (cmd.isDescriptionPresent()) {
            cmd.getDescription().ifPresentOrElse(project::setDescription, project::clearDescription);
        }
        cmd.getStartDate().ifPresent(project::setStartDate);
        if (cmd.isEndDatePresent()) {
            cmd.getEndDate().ifPresentOrElse(project::setEndDate, project::clearEndDate);
        }
        cmd.getProjectStatus().ifPresent(project::setProjectStatus);
        return project;
    }
}
