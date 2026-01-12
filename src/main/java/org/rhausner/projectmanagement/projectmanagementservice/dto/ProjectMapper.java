package org.rhausner.projectmanagement.projectmanagementservice.dto;

import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper component that converts between domain {@link Project} entities
 * and the corresponding DTO representations used by the API.
 *
 * Implemented as a Spring {@code @Component} so it can be injected into controllers.
 * Mapping is manual and intentionally straightforward to keep control
 * over default values and null handling.
 */
@Component
public class ProjectMapper {

    /**
     * Convert a domain {@link Project} to a {@link ProjectGetDto} used for GET responses.
     *
     * @param project the domain entity, may be {@code null}
     * @return a {@code ProjectGetDto} containing the entity's data, or {@code null} when input is {@code null}
     */
    public ProjectGetDto toGetDto(Project project) {
        if (project == null) {
            return null;
        }
        return new ProjectGetDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getProjectStatus()
        );
    }

    /**
     * Convert a {@link ProjectCreateDto} (incoming create payload) into a domain {@link Project}.
     *
     * If the DTO's {@code projectStatus} is {@code null}, a sensible default of {@link ProjectStatus#PLANNED}
     * is applied. The method returns {@code null} when the input DTO itself is {@code null}.
     *
     * @param dto the create DTO, may be {@code null}
     * @return the domain entity prepared for persistence, or {@code null} if dto was {@code null}
     */
    public Project fromCreateDto(ProjectCreateDto dto) {
        if (dto == null) {
            return null;
        }
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

    /**
     * Convert a {@link ProjectGetDto} back to a domain {@link Project}.
     * Returns {@code null} when the input is {@code null}.
     *
     * @param dto the get DTO, may be {@code null}
     * @return the reconstructed domain entity, or {@code null} if dto was {@code null}
     */
    public Project fromGetDto(ProjectGetDto dto) {
        if (dto == null) {
            return null;
        }
        Project project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setProjectStatus(dto.getProjectStatus());
        return project;
    }

    /**
     * Convert a {@link ProjectUpdateDto} (incoming full-update payload) into a domain {@link Project}.
     * The returned entity contains the values from the DTO.
     *
     * @param dto the update DTO, must not be {@code null}
     * @return the domain entity representing the new state
     */
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
