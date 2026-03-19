package app.guad.feature.project;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends
        CrudRepository<Project, Long>,
        PagingAndSortingRepository<Project, Long>,
        JpaSpecificationExecutor<Project> {
    List<Project> findAllByUserIdAndStatus(UUID userId, ProjectStatus status);
    Optional<Project> findByIdAndUserId(Long id, UUID userId);
    long countByUserIdAndStatus(UUID userId, ProjectStatus status);
}
