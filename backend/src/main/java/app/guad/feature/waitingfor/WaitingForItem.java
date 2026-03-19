package app.guad.feature.waitingfor;

import app.guad.feature.action.Action;
import app.guad.feature.project.Project;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "waiting_for_items")
public class WaitingForItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String delegatedTo;

    @Column
    private LocalDateTime delegatedAt;

    @Column
    private LocalDateTime followUpDate;

    @Column
    private String notes;

    @Column(nullable = false)
    private WaitingForItemStatus status;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdDate;

    @Column(insertable = false)
    @LastModifiedDate
    private Instant updatedDate;

    @Column
    private Instant completedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDelegatedTo() {
        return delegatedTo;
    }

    public void setDelegatedTo(String delegatedTo) {
        this.delegatedTo = delegatedTo;
    }

    public LocalDateTime getDelegatedAt() {
        return delegatedAt;
    }

    public void setDelegatedAt(LocalDateTime delegatedAt) {
        this.delegatedAt = delegatedAt;
    }

    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public WaitingForItemStatus getStatus() {
        return status;
    }

    public void setStatus(WaitingForItemStatus status) {
        this.status = status;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Instant getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Instant completedDate) {
        this.completedDate = completedDate;
    }
}
