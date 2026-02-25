package app.guad.feature.action;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ActionRepository extends
                CrudRepository<Action, Long>,
                PagingAndSortingRepository<Action, Long>,
                JpaSpecificationExecutor<Action> {
        List<Action> findByProjectId(Long projectId);
}
