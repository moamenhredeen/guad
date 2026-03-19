package app.guad.feature.action;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActionRepository extends
                CrudRepository<Action, Long>,
                PagingAndSortingRepository<Action, Long>,
                JpaSpecificationExecutor<Action> {
        List<Action> findByProjectId(Long projectId);
        List<Action> findAllByUserId(UUID userId);
        List<Action> findAllByUserIdAndStatus(UUID userId, ActionStatus status);
        Optional<Action> findByIdAndUserId(Long id, UUID userId);
        List<Action> findAllByUserIdAndProjectId(UUID userId, Long projectId);
        long countByUserIdAndStatus(UUID userId, ActionStatus status);
}
