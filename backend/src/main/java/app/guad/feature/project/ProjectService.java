package app.guad.feature.project;

import app.guad.feature.attachment.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Project> findById(Long id) {
        return this.projectRepository.findById(id);
    }

    public List<Project> findAllByUserId(UUID userId) {
        return this.projectRepository.findAllByUserId(userId);
    }

    public Optional<Project> findByIdAndUserId(Long id, UUID userId) {
        return this.projectRepository.findByIdAndUserId(id, userId);
    }

    public List<Project> findAllByUserIdAndStatus(UUID userId, ProjectStatus status) {
        return this.projectRepository.findAllByUserIdAndStatus(userId, status);
    }

    public long countByUserIdAndStatus(UUID userId, ProjectStatus status) {
        return this.projectRepository.countByUserIdAndStatus(userId, status);
    }

    public Page<Project> search(String name, ProjectStatus status, Pageable pageable) {
        var spec = Specification.allOf(
            ProjectSpecifications.byName(name),
            ProjectSpecifications.byStatus(status)
        );
        return this.projectRepository.findAll(spec, pageable);
    }

    @Transactional
    public Project save(Project project) {
        if (project.getId() == null) {
            return this.projectRepository.save(project);
        }
        var found = this.projectRepository.findById(project.getId());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Project not found");
        }
        var projectFromDb = found.get();
        projectFromDb.setName(project.getName());
        projectFromDb.setDescription(project.getDescription());
        projectFromDb.setDesired_outcome(project.getDesired_outcome());
        projectFromDb.setStatus(project.getStatus());
        projectFromDb.setColor(project.getColor());
        projectFromDb.setArea(project.getArea());
        projectFromDb.setUserId(project.getUserId());
        // Preserve attachments if they were set on the project
        if (project.getAttachments() != null) {
            projectFromDb.setAttachments(project.getAttachments());
        }
        return this.projectRepository.save(projectFromDb);
    }

    @Transactional
    public Project saveWithAttachments(Project project, Set<Attachment> attachments) {
        if (project.getAttachments() == null) {
            project.setAttachments(new HashSet<>());
        }
        project.getAttachments().addAll(attachments);
        return save(project);
    }

    public void deleteById(long id) {
        this.projectRepository.deleteById(id);
    }
}
