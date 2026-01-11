package org.rhausner.projectmanagement.projectmanagementservice.dto;

import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectGetDto toGetDto(Project project) {
        if (project == null) return null;
        return new ProjectGetDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getProjectStatus()
        );
    }

    public Project fromCreateDto(ProjectCreateDto dto) {
        if (dto == null) return null;
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        if (dto.getProjectStatus() != null) {
            project.setProjectStatus(dto.getProjectStatus());
        } else {
            project.setProjectStatus(ProjectStatus.PLANNED);
        }
        return project;
    }

    public Project fromGetDto(ProjectGetDto dto) {
        if (dto == null) return null;
        Project project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setProjectStatus(dto.getProjectStatus());
        return project;
    }

    public Project fromUpdateDto(ProjectUpdateDto dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setProjectStatus(dto.getProjectStatus());
        return project;
    }
}
