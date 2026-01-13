package org.rhausner.projectmanagement.projectmanagementservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskCreateDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskGetDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskMapper;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskUpdateDto;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.model.Task;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskPriority;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskStatus;
import org.rhausner.projectmanagement.projectmanagementservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Basic controller tests for TaskController using MockMvc.
 * Tests use mocked TaskService and TaskMapper to keep them focused on controller behavior.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;

    @Test
    void getTasks_returnsList() throws Exception {
        int taskId = 1;
        int projectId = 10;
        String title = "Test Task";
        String desc = "Task description";
        TaskStatus status = TaskStatus.TODO;
        TaskPriority priority = TaskPriority.MEDIUM;
        LocalDate dueDate = LocalDate.of(2026, 6, 15);
        String assignee = "john.doe";
        Instant createdAt = Instant.now();

        Project project = new Project();
        project.setId(projectId);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        task.setAssignee(assignee);

        TaskGetDto dto = new TaskGetDto(taskId, projectId, title, desc, status, priority, dueDate, assignee, createdAt, null);

        when(taskService.getAllTasks()).thenReturn(List.of(task));
        when(taskMapper.toGetDto(task)).thenReturn(dto);

        var mvcResult = mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        List<TaskGetDto> response = objectMapper.readValue(json, new TypeReference<>() {});

        Assertions.assertEquals(taskId, response.get(0).getId());
        Assertions.assertEquals(projectId, response.get(0).getProjectId());
        Assertions.assertEquals(title, response.get(0).getTitle());
        Assertions.assertEquals(desc, response.get(0).getDescription());
        Assertions.assertEquals(status, response.get(0).getStatus());
        Assertions.assertEquals(priority, response.get(0).getPriority());
        Assertions.assertEquals(dueDate, response.get(0).getDueDate());
        Assertions.assertEquals(assignee, response.get(0).getAssignee());

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void getTaskById_returnsTask() throws Exception {
        int taskId = 5;
        int projectId = 20;
        String title = "Single Task";
        String desc = "Single task desc";
        TaskStatus status = TaskStatus.IN_PROGRESS;
        TaskPriority priority = TaskPriority.HIGH;
        LocalDate dueDate = LocalDate.of(2026, 7, 20);
        String assignee = "jane.doe";
        Instant createdAt = Instant.now();

        Project project = new Project();
        project.setId(projectId);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        task.setAssignee(assignee);

        TaskGetDto dto = new TaskGetDto(taskId, projectId, title, desc, status, priority, dueDate, assignee, createdAt, null);

        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(taskMapper.toGetDto(task)).thenReturn(dto);

        var mvcResult = mockMvc.perform(get("/api/v1/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        TaskGetDto response = objectMapper.readValue(json, TaskGetDto.class);

        Assertions.assertEquals(taskId, response.getId());
        Assertions.assertEquals(projectId, response.getProjectId());
        Assertions.assertEquals(title, response.getTitle());
        Assertions.assertEquals(status, response.getStatus());
        Assertions.assertEquals(priority, response.getPriority());

        verify(taskService, times(1)).getTaskById(taskId);
    }

    @Test
    void createTask_returnsCreated() throws Exception {
        int taskId = 42;
        int projectId = 10;
        String title = "New Task";
        String desc = "New task description";
        TaskStatus status = TaskStatus.TODO;
        TaskPriority priority = TaskPriority.LOW;
        LocalDate dueDate = LocalDate.of(2026, 8, 1);
        String assignee = "bob";
        Instant createdAt = Instant.now();

        TaskCreateDto createDto = new TaskCreateDto(projectId, title, desc, status, priority, dueDate, assignee);

        Project project = new Project();
        project.setId(projectId);

        Task toSave = new Task();
        toSave.setProject(project);
        toSave.setTitle(title);
        toSave.setDescription(desc);
        toSave.setStatus(status);
        toSave.setPriority(priority);
        toSave.setDueDate(dueDate);
        toSave.setAssignee(assignee);

        Task saved = new Task();
        saved.setId(taskId);
        saved.setProject(project);
        saved.setTitle(title);
        saved.setDescription(desc);
        saved.setStatus(status);
        saved.setPriority(priority);
        saved.setDueDate(dueDate);
        saved.setAssignee(assignee);

        TaskGetDto resultDto = new TaskGetDto(taskId, projectId, title, desc, status, priority, dueDate, assignee, createdAt, null);

        when(taskMapper.fromCreateDto(any(TaskCreateDto.class))).thenReturn(toSave);
        when(taskService.createTask(any(Task.class))).thenReturn(saved);
        when(taskMapper.toGetDto(saved)).thenReturn(resultDto);

        var mvcResult = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        TaskGetDto response = objectMapper.readValue(json, TaskGetDto.class);

        Assertions.assertEquals(taskId, response.getId());
        Assertions.assertEquals(projectId, response.getProjectId());
        Assertions.assertEquals(title, response.getTitle());
        Assertions.assertEquals(desc, response.getDescription());
        Assertions.assertEquals(status, response.getStatus());
        Assertions.assertEquals(priority, response.getPriority());
        Assertions.assertEquals(dueDate, response.getDueDate());
        Assertions.assertEquals(assignee, response.getAssignee());

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void updateTask_returnsUpdated() throws Exception {
        int taskId = 7;
        int projectId = 15;
        String title = "Updated Task";
        String desc = "Updated description";
        TaskStatus status = TaskStatus.IN_PROGRESS;
        TaskPriority priority = TaskPriority.HIGH;
        LocalDate dueDate = LocalDate.of(2026, 9, 15);
        String assignee = "alice";
        Instant createdAt = Instant.now();

        TaskUpdateDto updateDto = new TaskUpdateDto(title, desc, status, priority, dueDate, assignee);

        Project project = new Project();
        project.setId(projectId);

        Task incoming = new Task();
        incoming.setTitle(title);
        incoming.setDescription(desc);
        incoming.setStatus(status);
        incoming.setPriority(priority);
        incoming.setDueDate(dueDate);
        incoming.setAssignee(assignee);

        Task updated = new Task();
        updated.setId(taskId);
        updated.setProject(project);
        updated.setTitle(title);
        updated.setDescription(desc);
        updated.setStatus(status);
        updated.setPriority(priority);
        updated.setDueDate(dueDate);
        updated.setAssignee(assignee);

        TaskGetDto resultDto = new TaskGetDto(taskId, projectId, title, desc, status, priority, dueDate, assignee, createdAt, null);

        when(taskMapper.fromUpdateDto(any(TaskUpdateDto.class))).thenReturn(incoming);
        when(taskService.updateTask(eq(taskId), any(Task.class))).thenReturn(updated);
        when(taskMapper.toGetDto(updated)).thenReturn(resultDto);

        var mvcResult = mockMvc.perform(put("/api/v1/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        TaskGetDto response = objectMapper.readValue(json, TaskGetDto.class);

        Assertions.assertEquals(taskId, response.getId());
        Assertions.assertEquals(title, response.getTitle());
        Assertions.assertEquals(desc, response.getDescription());
        Assertions.assertEquals(status, response.getStatus());
        Assertions.assertEquals(priority, response.getPriority());
        Assertions.assertEquals(dueDate, response.getDueDate());
        Assertions.assertEquals(assignee, response.getAssignee());

        verify(taskService, times(1)).updateTask(eq(taskId), any(Task.class));
    }

    @Test
    void patchTask_returnsPatched() throws Exception {
        int taskId = 99;
        int projectId = 25;
        String title = "Patched Task";
        String desc = "Patched description";
        TaskStatus status = TaskStatus.DONE;
        TaskPriority priority = TaskPriority.CRITICAL;
        LocalDate dueDate = LocalDate.of(2026, 10, 1);
        String assignee = "charlie";
        Instant createdAt = Instant.now();
        Instant completedAt = Instant.now();

        Map<String, Object> patchPayload = new HashMap<>();
        patchPayload.put("title", title);
        patchPayload.put("description", desc);
        patchPayload.put("status", status.name());
        patchPayload.put("priority", priority.name());
        patchPayload.put("dueDate", dueDate.toString());
        patchPayload.put("assignee", assignee);

        Project project = new Project();
        project.setId(projectId);

        Task patched = new Task();
        patched.setId(taskId);
        patched.setProject(project);
        patched.setTitle(title);
        patched.setDescription(desc);
        patched.setStatus(status);
        patched.setPriority(priority);
        patched.setDueDate(dueDate);
        patched.setAssignee(assignee);

        TaskGetDto resultDto = new TaskGetDto(taskId, projectId, title, desc, status, priority, dueDate, assignee, createdAt, completedAt);

        when(taskService.patchTask(eq(taskId), any())).thenReturn(patched);
        when(taskMapper.toGetDto(patched)).thenReturn(resultDto);

        var mvcResult = mockMvc.perform(patch("/api/v1/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchPayload)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        TaskGetDto response = objectMapper.readValue(json, TaskGetDto.class);

        Assertions.assertEquals(taskId, response.getId());
        Assertions.assertEquals(projectId, response.getProjectId());
        Assertions.assertEquals(title, response.getTitle());
        Assertions.assertEquals(desc, response.getDescription());
        Assertions.assertEquals(status, response.getStatus());
        Assertions.assertEquals(priority, response.getPriority());
        Assertions.assertEquals(dueDate, response.getDueDate());
        Assertions.assertEquals(assignee, response.getAssignee());
        Assertions.assertEquals(completedAt, response.getCompletedAt());

        verify(taskService, times(1)).patchTask(eq(taskId), any());
    }

    @Test
    void deleteTask_returnsNoContent() throws Exception {
        int taskId = 5;
        doNothing().when(taskService).deleteTaskById(taskId);

        mockMvc.perform(delete("/api/v1/tasks/" + taskId))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTaskById(taskId);
    }
}

