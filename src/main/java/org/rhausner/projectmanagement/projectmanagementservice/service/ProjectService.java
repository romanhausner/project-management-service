package org.rhausner.projectmanagement.projectmanagementservice.service;

import org.rhausner.projectmanagement.projectmanagementservice.exception.BadRequestException;
import org.rhausner.projectmanagement.projectmanagementservice.exception.ProjectNotFoundException;
import org.rhausner.projectmanagement.projectmanagementservice.dto.command.ProjectPatchCommand;
import org.rhausner.projectmanagement.projectmanagementservice.repository.ProjectRepository;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer that encapsulates business logic and persistence operations for Projects.
 *
 * This class mediates between controllers and the {@link ProjectRepository}, providing
 * methods for common CRUD operations and patch/update logic. Transactional annotations
 * are applied where operations modify the entity state.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * Construct a ProjectService with the required repository dependency.
     *
     * @param projectRepository repository used for persistence operations
     */
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Retrieve all projects.
     *
     * @return list of all persisted {@link Project} entities
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Create and persist a new project entity.
     *
     * The provided entity is saved through the repository and the managed instance
     * with generated identifiers is returned.
     *
     * @param project the project entity to create
     * @return the saved {@link Project} with any generated fields populated
     */
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    /**
     * Load a project by its identifier.
     *
     * @param id the project id
     * @return the found {@link Project}
     * @throws ProjectNotFoundException if no project with the given id exists
     */
    public Project getProjectById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    /**
     * Update an existing project with new values (full replace semantics).
     *
     * This method runs in a transactional context and updates the managed entity
     * with the values from the provided {@code update} object. The returned instance
     * is the managed entity (no explicit save required due to transactional write-through).
     *
     * @param id the id of the project to update
     * @param update an entity carrying the new state
     * @return the updated managed {@link Project}
     * @throws ProjectNotFoundException if the project does not exist
     */
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

    /**
     * Delete a project by id.
     *
     * @param id the id of the project to delete
     */
    public void deleteProjectById(Integer id) {
        getProjectById(id); // ensure existence
        projectRepository.deleteById(id);
    }

    /**
     * Apply a partial update (PATCH semantics) to an existing project.
     *
     * The {@link ProjectPatchCommand} encodes presence/absence semantics for individual
     * fields; this method applies those changes inside a transaction. Validation errors
     * (e.g. blank name) result in a {@link BadRequestException}.
     *
     * @param id the id of the project to patch
     * @param cmd the patch command describing requested updates
     * @return the patched managed {@link Project}
     * @throws ProjectNotFoundException if the project does not exist
     * @throws BadRequestException for invalid patch values
     */
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
