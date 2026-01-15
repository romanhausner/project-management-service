package org.rhausner.projectmanagement.projectmanagementservice.end2end;

import org.junit.jupiter.api.Test;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectCreateDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectGetDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskCreateDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskGetDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.TaskUpdateDto;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskPriority;
import org.rhausner.projectmanagement.projectmanagementservice.model.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for Task API endpoints.
 * Tests the full stack with real database and HTTP calls using TestRestTemplate.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskE2ETest {

    @Autowired
    TestRestTemplate rest;

    /**
     * Tests creating a task via POST and fetching it via GET.
     * Verifies that all task fields are correctly persisted and retrieved.
     */
    @Test
    void testCreateAndFetchTask() {

        // 1. Create Project
        String projectName = "Demo Project";
        String projectDescription = "Project for E2E Test";
        LocalDate projectStartDate = LocalDate.now();
        LocalDate projectEndDate = projectStartDate.plusMonths(3);
        ProjectStatus projectStatus = ProjectStatus.PLANNED;

        ProjectCreateDto projectRequest =
                new ProjectCreateDto(projectName, projectDescription, projectStartDate, projectEndDate, projectStatus);

        ResponseEntity<ProjectGetDto> projectCreateResponse =
                rest.postForEntity("/api/v1/projects", projectRequest, ProjectGetDto.class);

        assertThat(projectCreateResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long projectId = projectCreateResponse.getBody().getId();

        // 2. Create Task
        String taskTitle = "Demo Task";
        String taskDescription = "Task for E2E Test";
        TaskStatus taskStatus = TaskStatus.TODO;
        TaskPriority taskPriority = TaskPriority.MEDIUM;
        LocalDate taskDueDate = LocalDate.now().plusWeeks(2);
        String taskAssignee = "Willi Wuff";

        TaskCreateDto taskRequest =
                new TaskCreateDto(projectId, taskTitle, taskDescription, taskStatus,
                        taskPriority, taskDueDate, taskAssignee);

        ResponseEntity<TaskGetDto> taskCreateResponse =
                rest.postForEntity("/api/v1/tasks", taskRequest, TaskGetDto.class);

        assertThat(taskCreateResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long taskId = taskCreateResponse.getBody().getId();

        // 3. Fetch Task
        ResponseEntity<TaskGetDto> taskGetResponse =
                rest.getForEntity("/api/v1/tasks/" + taskId, TaskGetDto.class);

        assertThat(taskGetResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskGetResponse.getBody().getTitle()).isEqualTo(taskTitle);
        assertThat(taskGetResponse.getBody().getDescription()).isEqualTo(taskDescription);
        assertThat(taskGetResponse.getBody().getStatus()).isEqualTo(taskStatus);
        assertThat(taskGetResponse.getBody().getPriority()).isEqualTo(taskPriority);
        assertThat(taskGetResponse.getBody().getDueDate()).isEqualTo(taskDueDate);
        assertThat(taskGetResponse.getBody().getAssignee()).isEqualTo(taskAssignee);
    }

    /**
     * Tests task status transitions from TODO to DONE (valid) and DONE to IN_PROGRESS (invalid).
     * Validates that the DONE status is terminal and cannot be changed to other statuses.
     */
    @Test
    void testTaskStatusTransitionFromDoneToInProgressFails() {

        // 1. Create Project
        String projectName = "Status Transition Test Project";
        String projectDescription = "Project for testing task status transitions";
        LocalDate projectStartDate = LocalDate.now();
        LocalDate projectEndDate = projectStartDate.plusMonths(2);
        ProjectStatus projectStatus = ProjectStatus.IN_PROGRESS;

        ProjectCreateDto projectRequest =
                new ProjectCreateDto(projectName, projectDescription, projectStartDate, projectEndDate, projectStatus);

        ResponseEntity<ProjectGetDto> projectCreateResponse =
                rest.postForEntity("/api/v1/projects", projectRequest, ProjectGetDto.class);

        assertThat(projectCreateResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long projectId = projectCreateResponse.getBody().getId();

        // 2. Create Task with status TODO
        String taskTitle = "Task for Status Transition Test";
        String taskDescription = "This task will transition from TODO to DONE to IN_PROGRESS (should fail)";
        TaskStatus initialStatus = TaskStatus.TODO;
        TaskPriority taskPriority = TaskPriority.HIGH;
        LocalDate taskDueDate = LocalDate.now().plusDays(5);
        String taskAssignee = "Karl Kater";

        TaskCreateDto taskRequest =
                new TaskCreateDto(projectId, taskTitle, taskDescription, initialStatus,
                        taskPriority, taskDueDate, taskAssignee);

        ResponseEntity<TaskGetDto> taskCreateResponse =
                rest.postForEntity("/api/v1/tasks", taskRequest, TaskGetDto.class);

        assertThat(taskCreateResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long taskId = taskCreateResponse.getBody().getId();

        // 3. Update task status to DONE via PUT
        TaskUpdateDto updateToDone = new TaskUpdateDto(
                taskTitle,
                taskDescription,
                TaskStatus.DONE,  // Change to DONE
                taskPriority,
                taskDueDate,
                taskAssignee
        );

        ResponseEntity<TaskGetDto> updateToDoneResponse =
                rest.exchange("/api/v1/tasks/" + taskId, HttpMethod.PUT,
                        new HttpEntity<>(updateToDone), TaskGetDto.class);

        assertThat(updateToDoneResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateToDoneResponse.getBody().getStatus()).isEqualTo(TaskStatus.DONE);

        // 4. Verify via GET that status is DONE
        ResponseEntity<TaskGetDto> getAfterDoneResponse =
                rest.getForEntity("/api/v1/tasks/" + taskId, TaskGetDto.class);

        assertThat(getAfterDoneResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAfterDoneResponse.getBody().getStatus()).isEqualTo(TaskStatus.DONE);

        // 5. Attempt to change status from DONE to IN_PROGRESS via PUT
        TaskUpdateDto updateToInProgress = new TaskUpdateDto(
                taskTitle,
                taskDescription,
                TaskStatus.IN_PROGRESS,  // Try to change from DONE to IN_PROGRESS
                taskPriority,
                taskDueDate,
                taskAssignee
        );

        ResponseEntity<String> updateToInProgressResponse =
                rest.exchange("/api/v1/tasks/" + taskId, HttpMethod.PUT,
                        new HttpEntity<>(updateToInProgress), String.class);

        // 6. Verify that the transition fails with BAD_REQUEST
        assertThat(updateToInProgressResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // 7. Verify via GET that status is still DONE
        ResponseEntity<TaskGetDto> finalGetResponse =
                rest.getForEntity("/api/v1/tasks/" + taskId, TaskGetDto.class);

        assertThat(finalGetResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(finalGetResponse.getBody().getStatus()).isEqualTo(TaskStatus.DONE);
    }

}
