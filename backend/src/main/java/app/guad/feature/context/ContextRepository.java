package app.guad.feature.context;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContextRepository extends
        CrudRepository<Context, Long>,
        PagingAndSortingRepository<Context, Long>,
        JpaSpecificationExecutor<Context> {
}

