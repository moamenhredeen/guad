package app.guad.feature.inbox;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface InboxRepository extends
        JpaRepository<InboxItem, Long>,
        PagingAndSortingRepository<InboxItem, Long>,
        JpaSpecificationExecutor<InboxItem> {
    
    @EntityGraph(attributePaths = {"attachments", "user"})
    Optional<InboxItem> findById(Long id);
}

