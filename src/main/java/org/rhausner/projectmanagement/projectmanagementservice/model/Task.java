package org.rhausner.projectmanagement.projectmanagementservice.model;

import jakarta.persistence.*;
import org.rhausner.projectmanagement.projectmanagementservice.exception.InvalidTaskStateException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;

    private String assignee;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant completedAt;

    /**
     * No-args constructor required by JPA.
     */
    public Task() {
    }

    public Task(Project project, String title) {
        this.project = Objects.requireNonNull(project);
        this.title = Objects.requireNonNull(title);
        this.createdAt = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(project, task.project) && Objects.equals(title, task.title) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, project, title, description);
    }

    // Domain logic methods

    /**
     * Start the task. If the task is already done, an exception is thrown.
     */
    public void start() {
        if (this.status == TaskStatus.DONE) {
            throw new InvalidTaskStateException("Completed task cannot be started again");
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    /**
     * Mark the task as done. If it is already done, this method has no effect.
     */
    public void markDone() {
        if (this.status == TaskStatus.DONE) {
            return; // idempotent
        }
        this.status = TaskStatus.DONE;
        this.completedAt = Instant.now();
    }


}

