package org.rhausner.projectmanagement.projectmanagementservice.dto;

import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
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
                project.getEndDate()
        );
    }

    public Project fromCreateDto(ProjectCreateDto dto) {
        if (dto == null) return null;
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        return project;
    }

    public void updateFromUpdateDto(Project project, ProjectUpdateDto dto) {
        if (dto == null || project == null) return;
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
    }

    public Project fromGetDto(ProjectGetDto dto) {
        if (dto == null) return null;
        Project project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        return project;
    }
}
