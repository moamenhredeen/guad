package app.guad.feature.document;

import app.guad.core.AuditMetadata;
import app.guad.feature.attachment.Attachment;
import app.guad.feature.project.Project;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToMany
    @JoinTable(name = "document_attachments", joinColumns = @JoinColumn(name = "document_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    private Set<Attachment> attachments;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public AuditMetadata getAudit() {
        return audit;
    }

    public void setAudit(AuditMetadata audit) {
        this.audit = audit;
    }
}
