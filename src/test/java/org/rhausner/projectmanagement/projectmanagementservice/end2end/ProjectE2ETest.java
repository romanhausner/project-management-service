package org.rhausner.projectmanagement.projectmanagementservice.end2end;

import org.junit.jupiter.api.Test;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectCreateDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectGetDto;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for Project API endpoints.
 * Tests the full stack with real database and HTTP calls using TestRestTemplate.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectE2ETest {

    @Autowired
    TestRestTemplate rest;

    /**
     * Tests creating a project via POST and fetching it via GET.
     * Verifies that all project fields are correctly persisted and retrieved.
     */
    @Test
    void testCreateAndFetchProject() {

        // 1. Create Project
        String name = "Demo Project";
        String description = "Project for E2E Test";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);
        ProjectStatus projectStatus = ProjectStatus.PLANNED;

        ProjectCreateDto request =
                new ProjectCreateDto(name, description, startDate, endDate, projectStatus);

        ResponseEntity<ProjectGetDto> createResponse =
                rest.postForEntity("/api/v1/projects", request, ProjectGetDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long projectId = createResponse.getBody().getId();

        // 2. Fetch Project
        ResponseEntity<ProjectGetDto> getResponse =
                rest.getForEntity("/api/v1/projects/" + projectId, ProjectGetDto.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo(name);
        assertThat(getResponse.getBody().getDescription()).isEqualTo(description);
        assertThat(getResponse.getBody().getStartDate()).isEqualTo(startDate);
        assertThat(getResponse.getBody().getEndDate()).isEqualTo(endDate);
    }

    /**
     * Tests that creating a project without a name results in a BAD_REQUEST (400) response.
     * Validates server-side validation for required fields.
     */
    @Test
    void testCreateProjectWithMissingNameBadRequest() {

        // Create Project with missing name
        String description = "Project without a name";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);
        ProjectStatus projectStatus = ProjectStatus.PLANNED;

        ProjectCreateDto request =
                new ProjectCreateDto(null, description, startDate, endDate, projectStatus);

        ResponseEntity<String> createResponse =
                rest.postForEntity("/api/v1/projects", request, String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Tests that fetching a non-existing project by ID returns a NOT_FOUND (404) response.
     * Validates proper error handling for missing resources.
     */
    @Test
    void testFetchNonExistingProjectNotFound() {

        // Fetch non-existing Project
        Long nonExistingProjectId = 9999L;

        ResponseEntity<String> getResponse =
                rest.getForEntity("/api/v1/projects/" + nonExistingProjectId, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Tests creating a project and verifying it appears in the project list.
     * Validates GET /api/v1/projects returns all projects including the newly created one.
     */
    @Test
    void testCreateProjectAndGetInList() {

        // Create Project
        String name = "List Test Project";
        String description = "Project for List E2E Test";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(2);
        ProjectStatus projectStatus = ProjectStatus.PLANNED;

        ProjectCreateDto request =
                new ProjectCreateDto(name, description, startDate, endDate, projectStatus);

        ResponseEntity<ProjectGetDto> createResponse =
                rest.postForEntity("/api/v1/projects", request, ProjectGetDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long projectId = createResponse.getBody().getId();

        // Fetch Project List
        ResponseEntity<ProjectGetDto[]> listResponse =
                rest.getForEntity("/api/v1/projects", ProjectGetDto[].class);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProjectGetDto[] projects = listResponse.getBody();
        assertThat(projects).isNotNull();
        boolean found = false;
        for (ProjectGetDto project : projects) {
            if (project.getId().equals(projectId)) {
                found = true;
                assertThat(project.getName()).isEqualTo(name);
                assertThat(project.getDescription()).isEqualTo(description);
                assertThat(project.getStartDate()).isEqualTo(startDate);
                assertThat(project.getEndDate()).isEqualTo(endDate);
                assertThat(project.getProjectStatus()).isEqualTo(projectStatus);
                break;
            }
        }
        assertThat(found).isTrue();
    }

}
