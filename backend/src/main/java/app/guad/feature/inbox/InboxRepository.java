package app.guad.feature.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface InboxRepository extends
        JpaRepository<InboxItem, Long>,
        PagingAndSortingRepository<InboxItem, Long>,
        JpaSpecificationExecutor<InboxItem> {

    List<InboxItem> findAllByUserIdAndStatus(UUID userId, InboxItemStatus status);
    Optional<InboxItem> findByIdAndUserId(Long id, UUID userId);
    long countByUserIdAndStatus(UUID userId, InboxItemStatus status);
}
