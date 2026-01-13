package org.rhausner.projectmanagement.projectmanagementservice.model;

import jakarta.persistence.*;
import org.rhausner.projectmanagement.projectmanagementservice.exception.InvalidTaskStateException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain JPA entity representing a Task within a Project.
 *
 * This entity stores task-related attributes such as title, description, status,
 * priority, due date and assignee. It contains small pieces of domain logic
 * (e.g. {@link #start()} and {@link #markDone()}) to ensure state transitions are
 * performed consistently and any side-effects (like setting {@code completedAt})
 * are applied in a single place.
 */
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

    /**
     * Convenience constructor for creating a new Task with its required associations.
     *
     * The constructor sets the required {@code project} reference and {@code title};
     * other fields can be filled in later by the service or mapper.
     *
     * @param project the associated project (must not be null)
     * @param title the task title (must not be null)
     */
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

    public void clearDescription() {
        this.description = null;
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

    public void clearDueDate() {
        this.dueDate = null;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public  void clearAssignee() {
        this.assignee = null;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    /**
     * Equality is based on id, project, title and description. Note that the id may be
     * {@code null} for transient instances; the equality logic mirrors the previous
     * implementation and intentionally includes multiple fields for identity comparison.
     *
     * @param o the other object to compare
     * @return {@code true} if the objects are considered equal
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(project, task.project) && Objects.equals(title, task.title) && Objects.equals(description, task.description);
    }

    /**
     * Hash code implementation matching {@link #equals(Object)}.
     *
     * @return hash code value
     */
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

    /**
     * Change the task status to the specified new status.
     *
     * Valid transitions are:
     * - TODO -> IN_PROGRESS
     * - IN_PROGRESS -> DONE
     * - TODO -> DONE
     *
     * Attempting to change to the same status has no effect.
     * Any other transition will result in an InvalidTaskStateException.
     *
     * @param newStatus the desired new status
     */
    public void changeStatus(TaskStatus newStatus) {
        if (newStatus == this.status) {
            return; // idempotent
        }

        switch (newStatus) {
            case IN_PROGRESS -> start();
            case DONE -> markDone();
            default -> throw new InvalidTaskStateException(
                    "Illegal transition from " + status + " to " + newStatus
            );
        }
    }


}
