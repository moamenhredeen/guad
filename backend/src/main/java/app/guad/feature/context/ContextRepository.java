package app.guad.feature.context;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContextRepository extends
        CrudRepository<Context, Long>,
        PagingAndSortingRepository<Context, Long>,
        JpaSpecificationExecutor<Context> {
    List<Context> findAllByUserId(UUID userId);
    Optional<Context> findByIdAndUserId(Long id, UUID userId);
}
