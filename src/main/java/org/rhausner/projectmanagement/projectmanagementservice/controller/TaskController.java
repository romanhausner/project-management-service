package org.rhausner.projectmanagement.projectmanagementservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tasks", description = "API for managing tasks")
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
    @Operation(summary = "Get all tasks", description = "Returns a list of all tasks")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all tasks",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskGetDto.class))))
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
    @Operation(summary = "Get task by ID", description = "Returns a single task by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskGetDto.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @GetMapping("/{id}")
    public TaskGetDto getTaskById(
            @Parameter(description = "ID of the task to retrieve") @PathVariable Long id) {
        return taskMapper.toGetDto(taskService.getTaskById(id));
    }

    /**
     * Create a new task.
     * Request: TaskCreateDto (validated)
     * Response: created TaskGetDto with generated id
     */
    @Operation(summary = "Create a new task", description = "Creates a new task with the provided data")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task successfully created",
                    content = @Content(schema = @Schema(implementation = TaskGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
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
    @Operation(summary = "Update a task", description = "Fully replaces an existing task with the provided data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully updated",
                    content = @Content(schema = @Schema(implementation = TaskGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @PutMapping("/{id}")
    public TaskGetDto updateTask(
            @Parameter(description = "ID of the task to update") @PathVariable Long id,
            @Valid @RequestBody TaskUpdateDto taskDto) {
        Task update = taskMapper.fromUpdateDto(taskDto);
        Task updated = taskService.updateTask(id, update);
        return taskMapper.toGetDto(updated);
    }

    /**
     * Apply a partial update (PATCH) to an existing task.
     */
    @Operation(summary = "Partially update a task", description = "Applies a partial update to an existing task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task successfully patched",
                    content = @Content(schema = @Schema(implementation = TaskGetDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patch data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public TaskGetDto patchTask(
            @Parameter(description = "ID of the task to patch") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON object with fields to update",
                    content = @Content(schema = @Schema(implementation = Object.class)))
            @RequestBody JsonNode patch) {
        TaskPatchCommand cmd = TaskPatchCommand.from(patch);
        Task updated = taskService.patchTask(id, cmd);
        return taskMapper.toGetDto(updated);
    }

    /**
     * Delete a task by id.
     */
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(
            @Parameter(description = "ID of the task to delete") @PathVariable Long id) {
        taskService.deleteTaskById(id);
    }

}
