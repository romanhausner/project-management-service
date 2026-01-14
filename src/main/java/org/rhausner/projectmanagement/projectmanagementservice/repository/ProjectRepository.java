package org.rhausner.projectmanagement.projectmanagementservice.repository;

import org.rhausner.projectmanagement.projectmanagementservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link Project} entities.
 * This interface inherits standard CRUD and pagination operations from {@link JpaRepository}.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
