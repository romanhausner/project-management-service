package org.rhausner.projectmanagement.projectmanagementservice;

import org.junit.jupiter.api.Test;
import org.rhausner.projectmanagement.projectmanagementservice.controller.ProjectController;
import org.rhausner.projectmanagement.projectmanagementservice.controller.TaskController;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectMapper;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskMapper;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import org.rhausner.projectmanagement.projectmanagementservice.model.Task;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskStatus;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskPriority;
import org.rhausner.projectmanagement.projectmanagementservice.repository.ProjectRepository;
import org.rhausner.projectmanagement.projectmanagementservice.repository.TaskRepository;
import org.rhausner.projectmanagement.projectmanagementservice.service.ProjectService;
import org.rhausner.projectmanagement.projectmanagementservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Project Management Service application.
 *
 * This test class verifies:
 * <ul>
 *   <li>Spring application context loads correctly</li>
 *   <li>Key beans (controllers, services, repositories, mappers) are properly injected</li>
 *   <li>Basic CRUD operations for Project and Task entities</li>
 *   <li>Mapper logic for converting between entities and DTOs</li>
 * </ul>
 *
 * Tests are transactional and will be rolled back after each test method.
 */
@SpringBootTest
@Transactional
class ProjectManagementServiceApplicationTests {

    @Autowired
    private ProjectController projectController;

    @Autowired
    private TaskController taskController;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskMapper taskMapper;

    /**
     * Verify that the Spring application context loads without errors.
     */
    @Test
    void contextLoads() {
        // Test passes if Spring context loads without throwing an exception
    }

    /**
     * Verify that the ProjectController bean is created and injected.
     */
    @Test
    void projectControllerBeanExists() {
        assertNotNull(projectController, "ProjectController should be injected");
    }

    /**
     * Verify that the TaskController bean is created and injected.
     */
    @Test
    void taskControllerBeanExists() {
        assertNotNull(taskController, "TaskController should be injected");
    }

    /**
     * Verify that the ProjectService bean is created and injected.
     */
    @Test
    void projectServiceBeanExists() {
        assertNotNull(projectService, "ProjectService should be injected");
    }

    /**
     * Verify that the TaskService bean is created and injected.
     */
    @Test
    void taskServiceBeanExists() {
        assertNotNull(taskService, "TaskService should be injected");
    }

    /**
     * Verify that the ProjectRepository bean is created and injected.
     */
    @Test
    void projectRepositoryBeanExists() {
        assertNotNull(projectRepository, "ProjectRepository should be injected");
    }

    /**
     * Verify that the TaskRepository bean is created and injected.
     */
    @Test
    void taskRepositoryBeanExists() {
        assertNotNull(taskRepository, "TaskRepository should be injected");
    }

    /**
     * Verify that the ProjectMapper bean is created and injected.
     */
    @Test
    void projectMapperBeanExists() {
        assertNotNull(projectMapper, "ProjectMapper should be injected");
    }

    /**
     * Verify that the TaskMapper bean is created and injected.
     */
    @Test
    void taskMapperBeanExists() {
        assertNotNull(taskMapper, "TaskMapper should be injected");
    }

    // ==================== Project CRUD Tests ====================

    /**
     * Test creating and reading a project.
     */
    @Test
    void createAndReadProject() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 12, 31));
        project.setProjectStatus(ProjectStatus.PLANNED);

        Project saved = projectRepository.save(project);

        assertNotNull(saved.getId(), "Saved project should have an ID");

        Optional<Project> found = projectRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "Project should be found by ID");
        assertEquals("Test Project", found.get().getName());
        assertEquals("Test Description", found.get().getDescription());
        assertEquals(ProjectStatus.PLANNED, found.get().getProjectStatus());
    }

    /**
     * Test updating a project.
     */
    @Test
    void updateProject() {
        Project project = new Project();
        project.setName("Original Name");
        project.setDescription("Original Description");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);

        Project saved = projectRepository.save(project);

        saved.setName("Updated Name");
        saved.setDescription("Updated Description");
        saved.setProjectStatus(ProjectStatus.IN_PROGRESS);
        projectRepository.save(saved);

        Optional<Project> found = projectRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Name", found.get().getName());
        assertEquals("Updated Description", found.get().getDescription());
        assertEquals(ProjectStatus.IN_PROGRESS, found.get().getProjectStatus());
    }

    /**
     * Test deleting a project.
     */
    @Test
    void deleteProject() {
        Project project = new Project();
        project.setName("To Delete");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);

        Project saved = projectRepository.save(project);
        Integer id = saved.getId();

        assertTrue(projectRepository.findById(id).isPresent());

        projectRepository.deleteById(id);

        assertFalse(projectRepository.findById(id).isPresent(), "Project should be deleted");
    }

    // ==================== Task CRUD Tests ====================

    /**
     * Test creating and reading a task.
     */
    @Test
    void createAndReadTask() {
        // First create a project for the task
        Project project = new Project();
        project.setName("Task Project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);

        Task task = new Task();
        task.setProject(project);
        task.setTitle("Test Task");
        task.setDescription("Task Description");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setDueDate(LocalDate.of(2026, 6, 15));
        task.setAssignee("john.doe");

        Task saved = taskRepository.save(task);

        assertNotNull(saved.getId(), "Saved task should have an ID");

        Optional<Task> found = taskRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "Task should be found by ID");
        assertEquals("Test Task", found.get().getTitle());
        assertEquals("Task Description", found.get().getDescription());
        assertEquals(TaskStatus.TODO, found.get().getStatus());
        assertEquals(TaskPriority.MEDIUM, found.get().getPriority());
        assertEquals("john.doe", found.get().getAssignee());
    }

    /**
     * Test updating a task.
     */
    @Test
    void updateTask() {
        Project project = new Project();
        project.setName("Task Project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);

        Task task = new Task();
        task.setProject(project);
        task.setTitle("Original Title");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.LOW);

        Task saved = taskRepository.save(task);

        saved.setTitle("Updated Title");
        saved.setStatus(TaskStatus.IN_PROGRESS);
        saved.setPriority(TaskPriority.HIGH);
        saved.setAssignee("jane.doe");
        taskRepository.save(saved);

        Optional<Task> found = taskRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Title", found.get().getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, found.get().getStatus());
        assertEquals(TaskPriority.HIGH, found.get().getPriority());
        assertEquals("jane.doe", found.get().getAssignee());
    }

    /**
     * Test deleting a task.
     */
    @Test
    void deleteTask() {
        Project project = new Project();
        project.setName("Task Project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);

        Task task = new Task();
        task.setProject(project);
        task.setTitle("To Delete");
        task.setStatus(TaskStatus.TODO);

        Task saved = taskRepository.save(task);
        Integer id = saved.getId();

        assertTrue(taskRepository.findById(id).isPresent());

        taskRepository.deleteById(id);

        assertFalse(taskRepository.findById(id).isPresent(), "Task should be deleted");
    }

    // ==================== ProjectMapper Tests ====================

    /**
     * Test ProjectMapper.toGetDto() converts entity to DTO correctly.
     */
    @Test
    void projectMapper_toGetDto() {
        Project project = new Project();
        project.setId(1);
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
        var createDto = new org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectCreateDto(
                "New Project", "New Desc", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 11, 30), ProjectStatus.PLANNED
        );

        Project project = projectMapper.fromCreateDto(createDto);

        assertNotNull(project);
        assertNull(project.getId()); // ID should not be set
        assertEquals("New Project", project.getName());
        assertEquals("New Desc", project.getDescription());
        assertEquals(LocalDate.of(2026, 5, 1), project.getStartDate());
        assertEquals(LocalDate.of(2026, 11, 30), project.getEndDate());
        assertEquals(ProjectStatus.PLANNED, project.getProjectStatus());
    }

    /**
     * Test ProjectMapper.fromCreateDto() uses default status when null.
     */
    @Test
    void projectMapper_fromCreateDto_defaultStatus() {
        var createDto = new org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectCreateDto(
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
        var updateDto = new org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectUpdateDto(
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
        project.setId(10);

        Task task = new Task();
        task.setId(5);
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
     * Test TaskMapper.fromCreateDto() converts DTO to entity correctly.
     */
    @Test
    void taskMapper_fromCreateDto() {
        var createDto = new org.rhausner.projectmanagement.projectmanagementservice.dto.TaskCreateDto(
                99, "New Task", "Task Desc", TaskStatus.TODO, TaskPriority.LOW, LocalDate.of(2026, 8, 1), "bob"
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
     * Test TaskMapper.fromCreateDto() uses defaults when status/priority are null.
     */
    @Test
    void taskMapper_fromCreateDto_defaults() {
        var createDto = new org.rhausner.projectmanagement.projectmanagementservice.dto.TaskCreateDto(
                1, "Task", null, null, null, null, null
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
        var updateDto = new org.rhausner.projectmanagement.projectmanagementservice.dto.TaskUpdateDto(
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
}
