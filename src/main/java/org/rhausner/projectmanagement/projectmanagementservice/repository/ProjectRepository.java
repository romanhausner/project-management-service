package org.rhausner.projectmanagement.projectmanagementservice.repository;

import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repsository for {@link Project}.
 */
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
