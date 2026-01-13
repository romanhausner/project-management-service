package org.rhausner.projectmanagement.projectmanagementservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskCreateDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskGetDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskMapper;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskUpdateDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.command.TaskPatchCommand;
import org.rhausner.projectmanagement.projectmanagementservice.model.Task;
import org.rhausner.projectmanagement.projectmanagementservice.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing tasks in the Project Management Service.
 * Provides CRUD operations for tasks.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    /**
     * Return all tasks.
     * Response: JSON array of TaskGetDto
     */
    @GetMapping
    public List<TaskGetDto> getTasks() {
        return taskService.getAllTasks().stream()
                .map(taskMapper::toGetDto)
                .collect(Collectors.toList());
    }

    /**
     * Return a single task by id.
     * Response: TaskGetDto, implicitly 404 if not found.
     */
    @GetMapping("/{id}")
    public TaskGetDto getTaskById(@PathVariable Integer id) {
        return taskMapper.toGetDto(taskService.getTaskById(id));
    }

    /**
     * Create a new task.
     * Request: TaskCreateDto (validated)
     * Response: created TaskGetDto with generated id
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskGetDto createTask(@Valid @RequestBody TaskCreateDto taskDto) {
        Task toSave = taskMapper.fromCreateDto(taskDto);
        Task saved = taskService.createTask(toSave);
        return taskMapper.toGetDto(saved);
    }

    /**
     * Replace an existing task with the provided DTO (full update / PUT semantics).
     */
    @PutMapping("/{id}")
    public TaskGetDto updateTask(@PathVariable Integer id, @Valid @RequestBody TaskUpdateDto taskDto) {
        Task update = taskMapper.fromUpdateDto(taskDto);
        Task updated = taskService.updateTask(id, update);
        return taskMapper.toGetDto(updated);
    }

    /**
     * Apply a partial update (PATCH) to an existing task.
     */
    @PatchMapping("/{id}")
    public TaskGetDto patchTask(@PathVariable Integer id, @RequestBody JsonNode patch) {
        TaskPatchCommand cmd = TaskPatchCommand.from(patch);
        Task updated = taskService.patchTask(id, cmd);
        return taskMapper.toGetDto(updated);
    }

    /**
     * Delete a task by id.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable Integer id) {
        taskService.deleteTaskById(id);
    }

}
