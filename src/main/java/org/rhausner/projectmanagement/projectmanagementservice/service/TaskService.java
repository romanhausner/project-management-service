package org.rhausner.projectmanagement.projectmanagementservice.service;

import org.rhausner.projectmanagement.projectmanagementservice.exception.TaskNotFoundException;
import org.rhausner.projectmanagement.projectmanagementservice.model.Task;
import org.rhausner.projectmanagement.projectmanagementservice.repository.TaskRepository;
import org.springframework.stereotype.Service;

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
        return taskRepository.save(task);
    }
}
