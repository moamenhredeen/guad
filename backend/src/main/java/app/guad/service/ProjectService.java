package app.guad.service;

import app.guad.entity.Attachment;
import app.guad.entity.Project;
import app.guad.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Page<Project> getProjects(Pageable pageable) {
        return this.projectRepository.findAll(pageable);
    }


    public Page<Project> search(Specification<Project> spec, Pageable pageable) {
        return this.projectRepository.findAll(spec, pageable);
    }

    public Optional<Project> getProjectById(long id) {
        return this.projectRepository.findById(id);
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
