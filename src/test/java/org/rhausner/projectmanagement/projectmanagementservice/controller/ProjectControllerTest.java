package org.rhausner.projectmanagement.projectmanagementservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectMapper projectMapper;

    @Test
    void getProjects_returnsList() throws Exception {
        Long projectId = 1L;
        String name = "Test Project";
        String desc = "desc";
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        Project project = new Project(projectId, name, desc, startDate, endDate);
        ProjectStatus projectStatus = ProjectStatus.PLANNED;
        project.setProjectStatus(projectStatus);

        ProjectGetDto dto = new ProjectGetDto(projectId, name, desc, startDate, endDate, projectStatus);

        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(projectMapper.toGetDto(project)).thenReturn(dto);

        var mvcResult = mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        List<ProjectGetDto> response = objectMapper.readValue(json, new TypeReference<>() {
        });

        Assertions.assertEquals(projectId, response.get(0).getId());
        Assertions.assertEquals(name, response.get(0).getName());
        Assertions.assertEquals(desc, response.get(0).getDescription());
        Assertions.assertEquals(startDate, response.get(0).getStartDate());
        Assertions.assertEquals(endDate, response.get(0).getEndDate());
        Assertions.assertEquals(projectStatus, response.get(0).getProjectStatus());

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void createProject_returnsCreated() throws Exception {
        Long projectId = 42L;
        String name = "New Project";
        String desc = "desc";
        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 30);
        ProjectStatus projectStatus = ProjectStatus.PLANNED;
        ProjectCreateDto createDto = new ProjectCreateDto(name, desc, startDate, endDate, projectStatus);
        Project toSave = new Project(null, name, desc, startDate, endDate);
        toSave.setProjectStatus(projectStatus);

        Project saved = new Project(projectId, name, desc, startDate, endDate);
        saved.setProjectStatus(projectStatus);

        ProjectGetDto resultDto = new ProjectGetDto(projectId, name, desc, startDate, endDate, projectStatus);

        when(projectMapper.fromCreateDto(any(ProjectCreateDto.class))).thenReturn(toSave);
        when(projectService.createProject(any(Project.class))).thenReturn(saved);
        when(projectMapper.toGetDto(saved)).thenReturn(resultDto);

        var mvcResult = mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ProjectGetDto response = objectMapper.readValue(json, ProjectGetDto.class);

        Assertions.assertEquals(projectId, response.getId());
        Assertions.assertEquals(name, response.getName());
        Assertions.assertEquals(desc, response.getDescription());
        Assertions.assertEquals(startDate, response.getStartDate());
        Assertions.assertEquals(endDate, response.getEndDate());
        Assertions.assertEquals(projectStatus, response.getProjectStatus());

        verify(projectService, times(1)).createProject(any(Project.class));
    }

    @Test
    void updateProject_returnsUpdated() throws Exception {
        Long projectId = 7L;
        String name = "Updated";
        String desc = "newdesc";
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        ProjectStatus projectStatus = ProjectStatus.IN_PROGRESS;

        ProjectUpdateDto updateDto = new ProjectUpdateDto(name, desc, startDate, endDate, projectStatus);

        Project incoming = new Project(null, name, desc, startDate, endDate);
        incoming.setProjectStatus(projectStatus);

        Project updated = new Project(projectId, name, desc, startDate, endDate);
        updated.setProjectStatus(projectStatus);

        ProjectGetDto resultDto = new ProjectGetDto(projectId, name, desc, startDate, endDate, projectStatus);

        when(projectMapper.fromUpdateDto(any(ProjectUpdateDto.class))).thenReturn(incoming);
        when(projectService.updateProject(eq(projectId), any(Project.class))).thenReturn(updated);
        when(projectMapper.toGetDto(updated)).thenReturn(resultDto);

        var mvcResult = mockMvc.perform(put("/api/v1/projects/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ProjectGetDto response = objectMapper.readValue(json, ProjectGetDto.class);

        Assertions.assertEquals(projectId, response.getId());
        Assertions.assertEquals(name, response.getName());
        Assertions.assertEquals(startDate, response.getStartDate());
        Assertions.assertEquals(endDate, response.getEndDate());
        Assertions.assertEquals(projectStatus, response.getProjectStatus());

        verify(projectService, times(1)).updateProject(eq(projectId), any(Project.class));
    }

    @Test
    void patchProject_returnsPatched() throws Exception {
        Long projectId = 99L;
        String name = "Patched Project";
        String desc = "patched desc";
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 1);
        ProjectStatus projectStatus = ProjectStatus.COMPLETED;

        // payload only includes fields to patch
        Map<String, Object> patchPayload = new HashMap<>();
        patchPayload.put("name", name);
        patchPayload.put("description", desc);
        patchPayload.put("startDate", startDate.toString());
        patchPayload.put("endDate", endDate.toString());
        patchPayload.put("projectStatus", projectStatus.name());

        Project patched = new Project(projectId, name, desc, startDate, endDate);
        patched.setProjectStatus(projectStatus);

        ProjectGetDto resultDto = new ProjectGetDto(projectId, name, desc, startDate, endDate, projectStatus);

        when(projectService.patchProject(eq(projectId), any())).thenReturn(patched);
        when(projectMapper.toGetDto(patched)).thenReturn(resultDto);

        var mvcResult = mockMvc.perform(patch("/api/v1/projects/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchPayload)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ProjectGetDto response = objectMapper.readValue(json, ProjectGetDto.class);

        Assertions.assertEquals(projectId, response.getId());
        Assertions.assertEquals(name, response.getName());
        Assertions.assertEquals(desc, response.getDescription());
        Assertions.assertEquals(startDate, response.getStartDate());
        Assertions.assertEquals(endDate, response.getEndDate());
        Assertions.assertEquals(projectStatus, response.getProjectStatus());

        verify(projectService, times(1)).patchProject(eq(projectId), any());
    }

    @Test
    void deleteProject_returnsNoContent() throws Exception {
        Long projectId = 5L;
        doNothing().when(projectService).deleteProjectById(projectId);

        mockMvc.perform(delete("/api/v1/projects/" + projectId))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProjectById(projectId);
    }
}
