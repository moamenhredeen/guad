package app.guad.feature.waitingfor;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaitingForRepository extends
        CrudRepository<WaitingForItem, Long>,
        PagingAndSortingRepository<WaitingForItem, Long> {
    List<WaitingForItem> findAllByUserIdAndStatus(UUID userId, WaitingForItemStatus status);
    Optional<WaitingForItem> findByIdAndUserId(Long id, UUID userId);
    long countByUserIdAndStatus(UUID userId, WaitingForItemStatus status);
}
