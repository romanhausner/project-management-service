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
}
