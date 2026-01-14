package org.rhausner.projectmanagement.projectmanagementservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import org.rhausner.projectmanagement.projectmanagementservice.model.Task;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskPriority;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskStatus;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for ProjectMapper and TaskMapper.
 * These tests do not require a Spring context and test the mapper logic in isolation.
 * Also includes DTO validation tests using Jakarta Validation API.
 */
class ProjectAndTaskMapperTest {

    private ProjectMapper projectMapper;
    private TaskMapper taskMapper;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper();
        taskMapper = new TaskMapper();
    }

    // ==================== ProjectMapper Tests ====================

    /**
     * Test ProjectMapper.toGetDto() converts entity to DTO correctly.
     */
    @Test
    void projectMapper_toGetDto() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Mapper Test");
        project.setDescription("Mapper Description");
        project.setStartDate(LocalDate.of(2026, 3, 1));
        project.setEndDate(LocalDate.of(2026, 9, 30));
        project.setProjectStatus(ProjectStatus.IN_PROGRESS);

        var dto = projectMapper.toGetDto(project);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Mapper Test", dto.getName());
        assertEquals("Mapper Description", dto.getDescription());
        assertEquals(LocalDate.of(2026, 3, 1), dto.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), dto.getEndDate());
        assertEquals(ProjectStatus.IN_PROGRESS, dto.getProjectStatus());
    }

    /**
     * Test ProjectMapper.toGetDto() returns null for null input.
     */
    @Test
    void projectMapper_toGetDto_nullInput() {
        assertNull(projectMapper.toGetDto(null));
    }

    /**
     * Test ProjectMapper.fromCreateDto() converts DTO to entity correctly.
     */
    @Test
    void projectMapper_fromCreateDto() {
        var createDto = new ProjectCreateDto(
                "New Project", "New Desc", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 11, 30), ProjectStatus.PLANNED
        );

        Project project = projectMapper.fromCreateDto(createDto);

        assertNotNull(project);
        assertNull(project.getId());
        assertEquals("New Project", project.getName());
        assertEquals("New Desc", project.getDescription());
        assertEquals(LocalDate.of(2026, 5, 1), project.getStartDate());
        assertEquals(LocalDate.of(2026, 11, 30), project.getEndDate());
        assertEquals(ProjectStatus.PLANNED, project.getProjectStatus());
    }

    /**
     * Test ProjectMapper.fromCreateDto() returns null for null input.
     */
    @Test
    void projectMapper_fromCreateDto_nullInput() {
        assertNull(projectMapper.fromCreateDto(null));
    }

    /**
     * Test ProjectMapper.fromCreateDto() uses default status when null.
     */
    @Test
    void projectMapper_fromCreateDto_defaultStatus() {
        var createDto = new ProjectCreateDto(
                "Project", "Desc", LocalDate.of(2026, 1, 1), null, null
        );

        Project project = projectMapper.fromCreateDto(createDto);

        assertEquals(ProjectStatus.PLANNED, project.getProjectStatus(), "Default status should be PLANNED");
    }

    /**
     * Test ProjectMapper.fromUpdateDto() converts DTO to entity correctly.
     */
    @Test
    void projectMapper_fromUpdateDto() {
        var updateDto = new ProjectUpdateDto(
                "Updated", "Updated Desc", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 8, 31), ProjectStatus.COMPLETED
        );

        Project project = projectMapper.fromUpdateDto(updateDto);

        assertNotNull(project);
        assertEquals("Updated", project.getName());
        assertEquals("Updated Desc", project.getDescription());
        assertEquals(LocalDate.of(2026, 2, 1), project.getStartDate());
        assertEquals(LocalDate.of(2026, 8, 31), project.getEndDate());
        assertEquals(ProjectStatus.COMPLETED, project.getProjectStatus());
    }

    // ==================== TaskMapper Tests ====================

    /**
     * Test TaskMapper.toGetDto() converts entity to DTO correctly.
     */
    @Test
    void taskMapper_toGetDto() {
        Project project = new Project();
        project.setId(10L);

        Task task = new Task();
        task.setId(5L);
        task.setProject(project);
        task.setTitle("Task Title");
        task.setDescription("Task Desc");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.HIGH);
        task.setDueDate(LocalDate.of(2026, 7, 15));
        task.setAssignee("alice");

        var dto = taskMapper.toGetDto(task);

        assertNotNull(dto);
        assertEquals(5, dto.getId());
        assertEquals(10, dto.getProjectId());
        assertEquals("Task Title", dto.getTitle());
        assertEquals("Task Desc", dto.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, dto.getStatus());
        assertEquals(TaskPriority.HIGH, dto.getPriority());
        assertEquals(LocalDate.of(2026, 7, 15), dto.getDueDate());
        assertEquals("alice", dto.getAssignee());
    }

    /**
     * Test TaskMapper.toGetDto() returns null for null input.
     */
    @Test
    void taskMapper_toGetDto_nullInput() {
        assertNull(taskMapper.toGetDto(null));
    }

    /**
     * Test TaskMapper.toGetDto() handles null project gracefully.
     */
    @Test
    void taskMapper_toGetDto_nullProject() {
        Task task = new Task();
        task.setId(1L);
        task.setProject(null);
        task.setTitle("No Project Task");

        var dto = taskMapper.toGetDto(task);

        assertNotNull(dto);
        assertNull(dto.getProjectId());
    }

    /**
     * Test TaskMapper.fromCreateDto() converts DTO to entity correctly.
     */
    @Test
    void taskMapper_fromCreateDto() {
        var createDto = new TaskCreateDto(
                99L, "New Task", "Task Desc", TaskStatus.TODO, TaskPriority.LOW, LocalDate.of(2026, 8, 1), "bob"
        );

        Task task = taskMapper.fromCreateDto(createDto);

        assertNotNull(task);
        assertNotNull(task.getProject());
        assertEquals(99, task.getProject().getId());
        assertEquals("New Task", task.getTitle());
        assertEquals("Task Desc", task.getDescription());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(TaskPriority.LOW, task.getPriority());
        assertEquals(LocalDate.of(2026, 8, 1), task.getDueDate());
        assertEquals("bob", task.getAssignee());
    }

    /**
     * Test TaskMapper.fromCreateDto() returns null for null input.
     */
    @Test
    void taskMapper_fromCreateDto_nullInput() {
        assertNull(taskMapper.fromCreateDto(null));
    }

    /**
     * Test TaskMapper.fromCreateDto() uses defaults when status/priority are null.
     */
    @Test
    void taskMapper_fromCreateDto_defaults() {
        var createDto = new TaskCreateDto(
                1L, "Task", null, null, null, null, null
        );

        Task task = taskMapper.fromCreateDto(createDto);

        assertEquals(TaskStatus.TODO, task.getStatus(), "Default status should be TODO");
        assertEquals(TaskPriority.MEDIUM, task.getPriority(), "Default priority should be MEDIUM");
    }

    /**
     * Test TaskMapper.fromUpdateDto() converts DTO to entity correctly.
     */
    @Test
    void taskMapper_fromUpdateDto() {
        var updateDto = new TaskUpdateDto(
                "Updated Task", "Updated Desc", TaskStatus.DONE, TaskPriority.CRITICAL, LocalDate.of(2026, 12, 1), "charlie"
        );

        Task task = taskMapper.fromUpdateDto(updateDto);

        assertNotNull(task);
        assertEquals("Updated Task", task.getTitle());
        assertEquals("Updated Desc", task.getDescription());
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals(TaskPriority.CRITICAL, task.getPriority());
        assertEquals(LocalDate.of(2026, 12, 1), task.getDueDate());
        assertEquals("charlie", task.getAssignee());
    }

    /**
     * Test TaskMapper.fromUpdateDto() returns null for null input.
     */
    @Test
    void taskMapper_fromUpdateDto_nullInput() {
        assertNull(taskMapper.fromUpdateDto(null));
    }

    // ==================== ProjectCreateDto Validation Tests ====================

    /**
     * Test that ProjectCreateDto with valid data has no validation errors.
     */
    @Test
    void projectCreateDto_validData_noErrors() {
        var dto = new ProjectCreateDto(
                "Valid Name", "Description", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), ProjectStatus.PLANNED
        );

        Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
    }

    /**
     * Test that ProjectCreateDto with blank name fails validation.
     */
    @Test
    void projectCreateDto_blankName_hasError() {
        var dto = new ProjectCreateDto(
                "", "Description", LocalDate.of(2026, 1, 1), null, ProjectStatus.PLANNED
        );

        Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Blank name should cause validation error");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    /**
     * Test that ProjectCreateDto with null name fails validation.
     */
    @Test
    void projectCreateDto_nullName_hasError() {
        var dto = new ProjectCreateDto(
                null, "Description", LocalDate.of(2026, 1, 1), null, ProjectStatus.PLANNED
        );

        Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Null name should cause validation error");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    /**
     * Test that ProjectCreateDto with null startDate fails validation.
     */
    @Test
    void projectCreateDto_nullStartDate_hasError() {
        var dto = new ProjectCreateDto(
                "Name", "Description", null, null, ProjectStatus.PLANNED
        );

        Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Null startDate should cause validation error");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("startDate")));
    }

    /**
     * Test that ProjectCreateDto with null projectStatus fails validation.
     */
    @Test
    void projectCreateDto_nullProjectStatus_hasError() {
        var dto = new ProjectCreateDto(
                "Name", "Description", LocalDate.of(2026, 1, 1), null, null
        );

        Set<ConstraintViolation<ProjectCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Null projectStatus should cause validation error");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("projectStatus")));
    }

    // ==================== TaskCreateDto Validation Tests ====================

    /**
     * Test that TaskCreateDto with valid data has no validation errors.
     */
    @Test
    void taskCreateDto_validData_noErrors() {
        var dto = new TaskCreateDto(
                1L, "Valid Title", "Description", TaskStatus.TODO, TaskPriority.MEDIUM, LocalDate.of(2026, 6, 1), "john"
        );

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
    }

    /**
     * Test that TaskCreateDto with null projectId fails validation.
     */
    @Test
    void taskCreateDto_nullProjectId_hasError() {
        var dto = new TaskCreateDto(
                null, "Title", "Description", TaskStatus.TODO, TaskPriority.MEDIUM, null, null
        );

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Null projectId should cause validation error");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("projectId")));
    }

    /**
     * Test that TaskCreateDto with blank title fails validation.
     */
    @Test
    void taskCreateDto_blankTitle_hasError() {
        var dto = new TaskCreateDto(
                1L, "", "Description", TaskStatus.TODO, TaskPriority.MEDIUM, null, null
        );

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Blank title should cause validation error");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    /**
     * Test that TaskCreateDto with null title fails validation.
     */
    @Test
    void taskCreateDto_nullTitle_hasError() {
        var dto = new TaskCreateDto(
                1L, null, "Description", TaskStatus.TODO, TaskPriority.MEDIUM, null, null
        );

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Null title should cause validation error");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    /**
     * Test that TaskCreateDto allows null for optional fields (description, priority, dueDate, assignee).
     */
    @Test
    void taskCreateDto_nullOptionalFields_noErrors() {
        var dto = new TaskCreateDto(
                1L, "Title", null, TaskStatus.TODO, null, null, null
        );

        Set<ConstraintViolation<TaskCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Null optional fields should not cause validation errors");
    }
}
