package org.rhausner.projectmanagement.projectmanagementservice.service;

import org.rhausner.projectmanagement.projectmanagementservice.exception.BadRequestException;
import org.rhausner.projectmanagement.projectmanagementservice.exception.ImmutableFieldException;
import org.rhausner.projectmanagement.projectmanagementservice.exception.TaskNotFoundException;
import org.rhausner.projectmanagement.projectmanagementservice.dto.command.TaskPatchCommand;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.model.Task;
import org.rhausner.projectmanagement.projectmanagementservice.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer that encapsulates business logic and persistence operations for Tasks.
 *
 * This class mediates between controllers and the {@link TaskRepository}, providing
 * methods for common CRUD operations and patch/update logic. Transactional annotations
 * are applied where operations modify the entity state.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Construct a TaskService with the required repository dependency.
     *
     * @param taskRepository repository used for persistence operations
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieve all tasks.
     *
     * @return list of all persisted {@link Task} entities
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Load a task by its identifier.
     *
     * @param id the task id
     * @return the found {@link Task}
     * @throws TaskNotFoundException if no task with the given id exists
     */
    public Task getTaskById(Integer id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Create and persist a new task entity.
     *
     * The provided entity is saved through the repository and the managed instance
     * with generated identifiers is returned.
     *
     * @param task the task entity to create
     * @return the saved {@link Task} with any generated fields populated
     */
    public Task createTask(Task task) {
        Project project = task.getProject();
        project.addTask(task);
        return taskRepository.save(task);
    }

    /**
     * Update an existing task with new values (full replace semantics).
     *
     * This method runs in a transactional context and updates the managed entity
     * with the values from the provided {@code update} object. The returned instance
     * is the managed entity.
     *
     * @param id the id of the task to update
     * @param update an entity carrying the new state
     * @return the updated managed {@link Task}
     * @throws TaskNotFoundException if the task does not exist
     */
    @Transactional
    public Task updateTask(Integer id, Task update) {
        Task existing = getTaskById(id);
        existing.setTitle(update.getTitle());
        existing.setDescription(update.getDescription());
        existing.setDueDate(update.getDueDate());
        existing.setAssignee(update.getAssignee());
        // Use domain methods for stateful transitions
        existing.changeStatus(update.getStatus());
        existing.setPriority(update.getPriority());
        return existing;
    }

    /**
     * Delete a task by id.
     *
     * @param id the id of the task to delete
     * @throws TaskNotFoundException if no task with the given id exists
     */
    @Transactional
    public void deleteTaskById(Integer id) {
        Task task = getTaskById(id); // Ensure existence
        Project project = task.getProject();
        if (project != null) {
            project.removeTask(task);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Apply a partial update (PATCH semantics) to an existing task.
     *
     * The {@link TaskPatchCommand} encodes presence/absence semantics for individual
     * fields; this method applies those changes inside a transaction. Validation errors
     * result in a {@link BadRequestException}.
     *
     * @param id the id of the task to patch
     * @param cmd the patch command describing requested updates
     * @return the patched managed {@link Task}
     * @throws TaskNotFoundException if the task does not exist
     * @throws BadRequestException for invalid patch values
     */
    @Transactional
    public Task patchTask(Integer id, TaskPatchCommand cmd) {
        Task task = getTaskById(id);

        if(cmd.isProjectIdPresent()) {
            cmd.getProjectId().ifPresent(projectId -> {
                if (projectId != task.getProject().getId()) {
                    throw new ImmutableFieldException("Project ID");
                }
            });
        }

        if(cmd.isIdPresent()) {
            cmd.getId().ifPresent(taskId -> {
                if (!taskId.equals(task.getId())) {
                    throw new ImmutableFieldException("ID");
                }
            });
        }

        cmd.getTitle().ifPresent(title -> {
            if (title.isBlank()) {
                throw new BadRequestException("title must not be blank");
            }
            task.setTitle(title);
        });

        if (cmd.isDescriptionPresent()) {
            cmd.getDescription().ifPresentOrElse(task::setDescription, task::clearDescription);
        }

        if (cmd.isDueDatePresent()) {
            cmd.getDueDate().ifPresentOrElse(task::setDueDate, task::clearDueDate);
        }

        if (cmd.isAssigneePresent()) {
            cmd.getAssignee().ifPresentOrElse(task::setAssignee, task::clearAssignee);
        }

        // Use domain methods for state transitions when status is provided
        cmd.getStatus().ifPresent(task::changeStatus);

        cmd.getPriority().ifPresent(task::setPriority);

        return task;
    }
}
