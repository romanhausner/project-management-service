package org.rhausner.projectmanagement.projectmanagementservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.rhausner.projectmanagement.projectmanagementservice.controller.ProjectController;
import org.rhausner.projectmanagement.projectmanagementservice.controller.TaskController;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectMapper;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskMapper;
import org.rhausner.projectmanagement.projectmanagementservice.dto.command.TaskPatchCommand;
import org.rhausner.projectmanagement.projectmanagementservice.exception.ImmutableFieldException;
import org.rhausner.projectmanagement.projectmanagementservice.exception.InvalidTaskStateException;
import org.rhausner.projectmanagement.projectmanagementservice.exception.TaskNotFoundException;
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
 * <p>
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

    @Autowired
    private EntityManager entityManager;

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
     * Test that PATCH on a project preserves unchanged properties.
     */
    @Test
    void patchProject_preservesUnchangedProperties() {
        // Create a project with all properties set
        Project project = new Project();
        project.setName("Original Name");
        project.setDescription("Original Description");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 12, 31));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);
        Integer projectId = project.getId();

        // Patch only the name
        var patchCommand = org.rhausner.projectmanagement.projectmanagementservice.dto.command.ProjectPatchCommand.from(
                new ObjectMapper().createObjectNode().put("name", "Updated Name")
        );

        Project patched = projectService.patchProject(projectId, patchCommand);

        // Verify name was updated
        assertEquals("Updated Name", patched.getName());

        // Verify all other properties remain unchanged
        assertEquals("Original Description", patched.getDescription());
        assertEquals(LocalDate.of(2026, 1, 1), patched.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 31), patched.getEndDate());
        assertEquals(ProjectStatus.PLANNED, patched.getProjectStatus());
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

    /**
     * Test that deleting a non-existent project throws ProjectNotFoundException.
     */
    @Test
    void deleteNonExistentProject_throwsProjectNotFoundException() {
        Integer nonExistentId = 99999;

        // Verify the project does not exist
        assertFalse(projectRepository.findById(nonExistentId).isPresent());

        // Attempt to delete non-existent project should throw ProjectNotFoundException
        assertThrows(
                org.rhausner.projectmanagement.projectmanagementservice.exception.ProjectNotFoundException.class,
                () -> projectService.deleteProjectById(nonExistentId),
                "Deleting non-existent project should throw ProjectNotFoundException"
        );
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
        project.addTask(task);
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
        project.addTask(task);
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
     * Test that PATCH on a task preserves unchanged properties.
     */
    @Test
    void patchTask_preservesUnchangedProperties() {
        // Create a project
        Project project = new Project();
        project.setName("Task Project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);

        // Create a task with all properties set
        Task task = new Task();
        project.addTask(task);
        task.setTitle("Original Title");
        task.setDescription("Original Description");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setDueDate(LocalDate.of(2026, 6, 15));
        task.setAssignee("john.doe");
        Task saved = taskRepository.save(task);
        Integer taskId = saved.getId();

        // Patch only the priority
        var patchCommand = TaskPatchCommand.from(
                new ObjectMapper().createObjectNode().put("priority", "HIGH")
        );

        Task patched = taskService.patchTask(taskId, patchCommand);

        // Verify priority was updated
        assertEquals(TaskPriority.HIGH, patched.getPriority());

        // Verify all other properties remain unchanged
        assertEquals("Original Title", patched.getTitle());
        assertEquals("Original Description", patched.getDescription());
        assertEquals(TaskStatus.TODO, patched.getStatus());
        assertEquals(LocalDate.of(2026, 6, 15), patched.getDueDate());
        assertEquals("john.doe", patched.getAssignee());
        assertEquals(project.getId(), patched.getProject().getId());
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
        project.addTask(task);
        task.setTitle("To Delete");
        task.setStatus(TaskStatus.TODO);

        Task saved = taskRepository.save(task);
        Integer id = saved.getId();

        assertTrue(taskRepository.findById(id).isPresent());

        taskRepository.deleteById(id);

        assertFalse(taskRepository.findById(id).isPresent(), "Task should be deleted");
    }

    /**
     * Test that changing the project ID of an existing task throws ImmutableFieldException.
     */
    @Test
    void changeTaskProjectId_throwsImmutableFieldException() {
        // Create two projects
        Project project1 = new Project();
        project1.setName("Project 1");
        project1.setStartDate(LocalDate.of(2026, 1, 1));
        project1.setProjectStatus(ProjectStatus.PLANNED);
        project1 = projectRepository.save(project1);

        Project project2 = new Project();
        project2.setName("Project 2");
        project2.setStartDate(LocalDate.of(2026, 2, 1));
        project2.setProjectStatus(ProjectStatus.PLANNED);
        project2 = projectRepository.save(project2);

        // Create a task for project1
        Task task = new Task();
        project1.addTask(task);
        task.setTitle("Task for Project 1");
        task.setStatus(TaskStatus.TODO);
        Task saved = taskRepository.save(task);

        // Attempt to change project via patch should throw ImmutableFieldException
        var patchCommand = TaskPatchCommand.from(
                new ObjectMapper().createObjectNode().put("projectId", project2.getId())
        );

        assertThrows(
                ImmutableFieldException.class,
                () -> taskService.patchTask(saved.getId(), patchCommand),
                "Changing project ID should throw ImmutableFieldException"
        );
    }

    /**
     * Test that changing the status of a DONE task to IN_PROGRESS throws InvalidTaskStateException.
     */
    @Test
    void changeTaskStatusFromDoneToInProgress_throwsInvalidTaskStateException() {
        // Create a project
        Project project = new Project();
        project.setName("Test Project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);

        // Create a task and mark it as done
        Task task = new Task();
        project.addTask(task);
        task.setTitle("Completed Task");
        task.setStatus(TaskStatus.DONE);
        Task saved = taskRepository.save(task);

        // Attempt to change status from DONE to IN_PROGRESS via patch should throw InvalidTaskStateException
        var patchCommand = TaskPatchCommand.from(
                new ObjectMapper().createObjectNode().put("status", "IN_PROGRESS")
        );

        assertThrows(
                InvalidTaskStateException.class,
                () -> taskService.patchTask(saved.getId(), patchCommand),
                "Changing status from DONE to IN_PROGRESS should throw InvalidTaskStateException"
        );
    }

    /**
     * Test that changing a task from IN_PROGRESS to DONE sets completedAt.
     */
    @Test
    void changeTaskFromInProgressToDone_setsCompletedAt() {
        // Create a project
        Project project = new Project();
        project.setName("Test Project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);

        // Create a task with status IN_PROGRESS
        Task task = new Task();
        project.addTask(task);
        task.setTitle("In Progress Task");
        task.setStatus(TaskStatus.IN_PROGRESS);
        Task saved = taskRepository.save(task);
        Integer taskId = saved.getId();

        // Verify completedAt is not set
        assertNull(saved.getCompletedAt(), "completedAt should be null before marking done");

        // Patch status to DONE
        var patchCommand = TaskPatchCommand.from(
                new ObjectMapper().createObjectNode().put("status", "DONE")
        );

        Task patched = taskService.patchTask(taskId, patchCommand);

        // Verify status is DONE
        assertEquals(TaskStatus.DONE, patched.getStatus());

        // Verify completedAt is now set
        assertNotNull(patched.getCompletedAt(), "completedAt should be set after marking done");
    }

    /**
     * Test that setting a DONE task to DONE again is idempotent (no exception thrown).
     */
    @Test
    void setDoneTaskToDoneAgain_isIdempotent() {
        // Create a project
        Project project = new Project();
        project.setName("Test Project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);

        // Create a task and mark it as done
        Task task = new Task();
        project.addTask(task);
        task.setTitle("Completed Task");
        task.setStatus(TaskStatus.DONE);
        Task saved = taskRepository.save(task);

        // Attempt to set status to DONE again should succeed (idempotent)
        var patchCommand = org.rhausner.projectmanagement.projectmanagementservice.dto.command.TaskPatchCommand.from(
                new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode().put("status", "DONE")
        );

        // Should not throw any exception
        Task patched = taskService.patchTask(saved.getId(), patchCommand);

        assertEquals(TaskStatus.DONE, patched.getStatus(), "Status should remain DONE");
    }

    /**
     * Test that deleting a non-existent task throws TaskNotFoundException.
     */
    @Test
    void deleteNonExistentTask_throwsTaskNotFoundException() {
        Integer nonExistentId = 99999;

        // Verify the task does not exist
        assertFalse(taskRepository.findById(nonExistentId).isPresent());

        // Attempt to delete non-existent task should throw TaskNotFoundException
        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTaskById(nonExistentId),
                "Deleting non-existent task should throw TaskNotFoundException"
        );
    }

    /**
     * Test that deleting a project with existing tasks cascades the delete (or fails depending on configuration).
     */
    @Test
    void deleteProjectWithTasks_cascadesDelete() {
        // Create a project
        Project project = new Project();
        project.setName("Project with Tasks");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setProjectStatus(ProjectStatus.PLANNED);
        project = projectRepository.save(project);
        Integer projectId = project.getId();

        // Create tasks for the project
        Task task1 = new Task();
        project.addTask(task1);
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.TODO);
        task1 = taskRepository.save(task1);
        Integer task1Id = task1.getId();

        Task task2 = new Task();
        project.addTask(task2);
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2 = taskRepository.save(task2);
        Integer task2Id = task2.getId();

        // Verify project and tasks exist
        assertTrue(projectRepository.findById(projectId).isPresent());
        assertTrue(taskRepository.findById(task1Id).isPresent());
        assertTrue(taskRepository.findById(task2Id).isPresent());

        // Delete the project
        projectService.deleteProjectById(projectId);

        entityManager.flush();
        entityManager.clear();

        // Verify project is deleted
        assertFalse(projectRepository.findById(projectId).isPresent(), "Project should be deleted");

        // Verify tasks are also deleted (cascading delete)
        assertFalse(taskRepository.findById(task1Id).isPresent(), "Task 1 should be deleted with project");
        assertFalse(taskRepository.findById(task2Id).isPresent(), "Task 2 should be deleted with project");
    }

    /**
     * Test that a task cannot be moved to a different project.
     */
    @Test
    void addTaskToAnotherProject_throwsException() {
        // Create two projects
        Project project1 = new Project();
        project1.setName("Project 1");
        project1.setStartDate(LocalDate.of(2026, 1, 1));
        project1.setProjectStatus(ProjectStatus.PLANNED);
        project1 = projectRepository.save(project1);

        Project project2 = new Project();
        project2.setName("Project 2");
        project2.setStartDate(LocalDate.of(2026, 2, 1));
        project2.setProjectStatus(ProjectStatus.PLANNED);
        project2 = projectRepository.save(project2);

        // Create a task for project1
        Task task = new Task();
        project1.addTask(task);
        task.setTitle("Task for Project 1");
        task.setStatus(TaskStatus.TODO);
        Task savedTask = taskRepository.save(task);

        // Verify task is assigned to project1
        assertEquals(project1.getId(), savedTask.getProject().getId());

        // Attempt to add the same task to project2 should throw an exception
        Project finalProject2 = project2;
        Task finalTask = savedTask;
        assertThrows(
                IllegalStateException.class,
                () -> finalProject2.addTask(finalTask),
                "Adding a task that already belongs to another project should throw IllegalStateException"
        );
    }

}
