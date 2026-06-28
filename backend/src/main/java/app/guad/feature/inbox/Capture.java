package app.guad.feature.inbox;

import app.guad.core.AuditMetadata;
import app.guad.feature.attachment.Attachment;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/// Capture point for all incoming thoughts, tasks, emails, etc.
@Entity
@Table(name = "captures")
@EntityListeners(AuditingEntityListener.class)
public class Capture {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "capture_sequence")
    @SequenceGenerator(name = "capture_sequence", sequenceName = "captures_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaptureStatus status;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    @Column
    private Instant processedDate;

    @Column(nullable = false)
    private UUID userId;

    @ManyToMany
    @JoinTable(
            name = "capture_attachments",
            joinColumns = @JoinColumn(name = "capture_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private Set<Attachment> attachments;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CaptureStatus getStatus() {
        return status;
    }

    public void setStatus(CaptureStatus status) {
        this.status = status;
    }

    public AuditMetadata getAudit() {
        return audit;
    }

    public void setAudit(AuditMetadata audit) {
        this.audit = audit;
    }

    public Instant getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Instant processedDate) {
        this.processedDate = processedDate;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
