package org.rhausner.projectmanagement.projectmanagementservice.repository;

import org.rhausner.projectmanagement.projectmanagementservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link Task} entities.
 * This interface inherits standard CRUD and pagination operations from {@link JpaRepository}.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
