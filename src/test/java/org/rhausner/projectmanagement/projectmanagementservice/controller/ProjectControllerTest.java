package org.rhausner.projectmanagement.projectmanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectGetDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectCreateDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectUpdateDto;
import org.rhausner.projectmanagement.projectmanagementservice.dto.ProjectMapper;
import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.rhausner.projectmanagement.projectmanagementservice.model.ProjectStatus;
import org.rhausner.projectmanagement.projectmanagementservice.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Basic controller tests for ProjectController using MockMvc.
 * Tests use mocked ProjectService and ProjectMapper to keep them focused on controller behavior.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private ProjectMapper projectMapper;

    @Test
    void getProjects_returnsList() throws Exception {
        String name = "Test Project";
        String desc = "desc";
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        Project project = new Project(1, name, desc, startDate, endDate);
        ProjectStatus projectStatus = ProjectStatus.PLANNED;
        project.setProjectStatus(projectStatus);

        ProjectGetDto dto = new ProjectGetDto(1, name, desc, startDate, endDate, projectStatus);

        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(projectMapper.toGetDto(project)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].description").value(desc))
                .andExpect(jsonPath("$[0].startDate").value(startDate.toString()))
                .andExpect(jsonPath("$[0].endDate").value(endDate.toString()));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void createProject_returnsCreated() throws Exception {
        String name = "New Project";
        String desc = "desc";
        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 30);
        ProjectCreateDto createDto = new ProjectCreateDto(name, desc, startDate, endDate, ProjectStatus.PLANNED);
        Project toSave = new Project(null, name, desc, startDate, endDate);
        toSave.setProjectStatus(ProjectStatus.PLANNED);

        Project saved = new Project(42, name, desc, startDate, endDate);
        saved.setProjectStatus(ProjectStatus.PLANNED);

        ProjectGetDto resultDto = new ProjectGetDto(42, name, desc, startDate, endDate, ProjectStatus.PLANNED);

        when(projectMapper.fromCreateDto(any(ProjectCreateDto.class))).thenReturn(toSave);
        when(projectService.createProject(any(Project.class))).thenReturn(saved);
        when(projectMapper.toGetDto(saved)).thenReturn(resultDto);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value(name));

        verify(projectService, times(1)).createProject(any(Project.class));
    }

    @Test
    void updateProject_returnsUpdated() throws Exception {
        ProjectUpdateDto updateDto = new ProjectUpdateDto("Updated", "newdesc", LocalDate.of(2026,3,1), LocalDate.of(2026,12,31), ProjectStatus.IN_PROGRESS);

        Project incoming = new Project(null, "Updated", "newdesc", LocalDate.of(2026,3,1), LocalDate.of(2026,12,31));
        incoming.setProjectStatus(ProjectStatus.IN_PROGRESS);

        Project updated = new Project(7, "Updated", "newdesc", LocalDate.of(2026,3,1), LocalDate.of(2026,12,31));
        updated.setProjectStatus(ProjectStatus.IN_PROGRESS);

        ProjectGetDto resultDto = new ProjectGetDto(7, "Updated", "newdesc", LocalDate.of(2026,3,1), LocalDate.of(2026,12,31), ProjectStatus.IN_PROGRESS);

        when(projectMapper.fromUpdateDto(any(ProjectUpdateDto.class))).thenReturn(incoming);
        when(projectService.updateProject(eq(7), any(Project.class))).thenReturn(updated);
        when(projectMapper.toGetDto(updated)).thenReturn(resultDto);

        mockMvc.perform(put("/api/v1/projects/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.projectStatus").value("IN_PROGRESS"));

        verify(projectService, times(1)).updateProject(eq(7), any(Project.class));
    }

    @Test
    void deleteProject_returnsNoContent() throws Exception {
        doNothing().when(projectService).deleteProjectById(5);

        mockMvc.perform(delete("/api/v1/projects/5"))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProjectById(5);
    }
}
