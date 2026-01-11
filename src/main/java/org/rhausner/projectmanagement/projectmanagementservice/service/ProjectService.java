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
        return projectRepository.findById(id).orElse(null);
    }

//    public Project updateProject(Integer id, Project project) {
//        projectRepository.findById(id)
//                .orElseThrow(() -> new ProjectNotFoundException(id));
//        project.setId(id);
//        return projectRepository.save(project);
//    }

    @Transactional
    public Project updateProject(Integer id, Project update) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

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
    public Project patchProject(Integer id, ProjectPatchCommand cmd){
        Project project = getProjectById(id);
        if (project == null) {
            throw new ProjectNotFoundException(id);
        }
        if (cmd.getName().isPresent()) {
            if (cmd.getName().get().isBlank()) {
                throw new BadRequestException("name must not be blank");
            }
            project.setName(cmd.getName().get());
        }

        cmd.getDescription().ifPresent(
                project::setDescription
        );
        cmd.getStartDate().ifPresent(
                project::setStartDate
        );
        cmd.getEndDate().ifPresent(
                project::setEndDate
        );
       cmd.getProjectStatus().ifPresent(project::setProjectStatus);
        return project;
    }
}
