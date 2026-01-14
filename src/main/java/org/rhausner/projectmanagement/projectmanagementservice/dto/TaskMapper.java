package org.rhausner.projectmanagement.projectmanagementservice.dto;

import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.model.Task;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskPriority;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper component that converts between domain {@link Task} entities
 * and the corresponding DTO representations used by the API.
 * Implemented as a Spring {@code @Component} so it can be injected into controllers.
 * Mapping is manual and intentionally straightforward to keep control
 * over default values and null handling.
 */
@Component
public class TaskMapper {

    /**
     * Convert a domain {@link Task} to a {@link TaskGetDto} used for GET responses.
     */
    public TaskGetDto toGetDto(Task task) {
        if (task == null) return null;
        Long projectId = task.getProject() != null ? task.getProject().getId() : null;
        return new TaskGetDto(
                task.getId(),
                projectId,
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getAssignee(),
                task.getCreatedAt(),
                task.getCompletedAt()
        );
    }

    /**
     * Convert a {@link TaskCreateDto} into a domain {@link Task}.
     * The returned task will have its project reference set to a Project with the provided id.
     * If {@code dto.status} is null, default to {@link TaskStatus#TODO}; if {@code dto.priority} is null, default to {@link TaskPriority#MEDIUM}.
     */
    public Task fromCreateDto(TaskCreateDto dto) {
        if (dto == null) return null;
        Task task = new Task();
        // link project by id only — service layer expected to resolve the actual project entity
        if (dto.getProjectId() != null) {
            Project p = new Project();
            p.setId(dto.getProjectId());
            task.setProject(p);
        }
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setAssignee(dto.getAssignee());
        task.setStatus(dto.getStatus() != null ? dto.getStatus() : TaskStatus.TODO);
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : TaskPriority.MEDIUM);
        return task;
    }

    /**
     * Convert a {@link TaskUpdateDto} (full update) into a domain {@link Task}.
     * The returned entity contains values from the DTO.
     */
    public Task fromUpdateDto(TaskUpdateDto dto) {
        if (dto == null) return null;
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setAssignee(dto.getAssignee());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        return task;
    }
}

