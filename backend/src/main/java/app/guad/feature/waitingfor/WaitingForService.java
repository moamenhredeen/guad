package app.guad.feature.waitingfor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WaitingForService {
    private final WaitingForRepository waitingForRepository;

    public WaitingForService(WaitingForRepository waitingForRepository) {
        this.waitingForRepository = waitingForRepository;
    }

    public List<WaitingForItem> findAllByUserIdAndStatus(UUID userId, WaitingForItemStatus status) {
        return waitingForRepository.findAllByUserIdAndStatus(userId, status);
    }

    public Optional<WaitingForItem> findByIdAndUserId(Long id, UUID userId) {
        return waitingForRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public WaitingForItem save(WaitingForItem item) {
        if (item.getId() == null) {
            return waitingForRepository.save(item);
        }
        var found = waitingForRepository.findById(item.getId());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("WaitingForItem not found");
        }
        var fromDb = found.get();
        fromDb.setTitle(item.getTitle());
        fromDb.setDelegatedTo(item.getDelegatedTo());
        fromDb.setDelegatedAt(item.getDelegatedAt());
        fromDb.setFollowUpDate(item.getFollowUpDate());
        fromDb.setNotes(item.getNotes());
        fromDb.setStatus(item.getStatus());
        fromDb.setAction(item.getAction());
        fromDb.setProject(item.getProject());
        return waitingForRepository.save(fromDb);
    }

    public void deleteById(long id) {
        waitingForRepository.deleteById(id);
    }

    public long countByUserIdAndStatus(UUID userId, WaitingForItemStatus status) {
        return waitingForRepository.countByUserIdAndStatus(userId, status);
    }
}
