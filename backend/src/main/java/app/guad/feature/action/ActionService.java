package app.guad.feature.action;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActionService {
    private final ActionRepository actionRepository;

    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    public Page<Action> getAllActions(Specification<Action> spec, Pageable pageable) {
        return this.actionRepository.findAll(spec, pageable);
    }

    public Optional<Action> getActionById(long id) {
        return this.actionRepository.findById(id);
    }

    @Transactional
    public Action save(Action action) {
        if (action.getId() == null) {
            return this.actionRepository.save(action);
        }
        var found = this.actionRepository.findById(action.getId());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Action not found");
        }
        var actionFromDb = found.get();
        actionFromDb.setDescription(action.getDescription());
        actionFromDb.setNotes(action.getNotes());
        actionFromDb.setStatus(action.getStatus());
        actionFromDb.setTimeSpecific(action.isTimeSpecific());
        actionFromDb.setEstimatedDuration(action.getEstimatedDuration());
        actionFromDb.setEnergyLevel(action.getEnergyLevel());
        actionFromDb.setLocation(action.getLocation());
        actionFromDb.setScheduledDate(action.getScheduledDate());
        actionFromDb.setDueDate(action.getDueDate());
        actionFromDb.setProject(action.getProject());
        actionFromDb.setArea(action.getArea());
        actionFromDb.setUserId(action.getUserId());
        actionFromDb.setContexts(action.getContexts());
        // Preserve attachments if they were set on the action
        if (action.getAttachments() != null) {
            actionFromDb.setAttachments(action.getAttachments());
        }
        return this.actionRepository.save(actionFromDb);
    }

    public void deleteById(long id) {
        this.actionRepository.deleteById(id);
    }

    public List<Action> findAllByUserIdAndStatus(UUID userId, ActionStatus status) {
        return actionRepository.findAllByUserIdAndStatus(userId, status);
    }

    public Optional<Action> findByIdAndUserId(Long id, UUID userId) {
        return actionRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public Action completeAction(Long id, UUID userId) {
        var action = actionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Action not found"));
        action.setStatus(ActionStatus.COMPLETED);
        action.setCompletedDate(Instant.now());
        return actionRepository.save(action);
    }

    @Transactional
    public Action updateStatus(Long id, UUID userId, ActionStatus status) {
        var action = actionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Action not found"));
        action.setStatus(status);
        if (status == ActionStatus.COMPLETED) {
            action.setCompletedDate(Instant.now());
        }
        return actionRepository.save(action);
    }
}

