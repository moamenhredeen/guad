package app.guad.feature.inbox;

import app.guad.feature.attachment.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class InboxService {
    private final InboxRepository inboxRepository;

    public InboxService(InboxRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    public Page<Capture> getCaptures(Pageable pageable) {
        return this.inboxRepository.findAll(pageable);
    }

    public Page<Capture> search(String title, CaptureStatus status, Pageable pageable) {
        var spec = Specification.allOf(
            CaptureSpecifications.byTitle(title),
            CaptureSpecifications.byStatus(status)
        );
        return inboxRepository.findAll(spec, pageable);
    }

    public Optional<Capture> getCaptureById(long id) {
        return this.inboxRepository.findById(id);
    }

    public List<Capture> getUnprocessedByUserId(UUID userId) {
        return inboxRepository.findByStatus(userId, CaptureStatus.UNPROCESSED);
    }

    public Optional<Capture> getByIdAndUserId(Long id, UUID userId) {
        return inboxRepository.findById(id, userId);
    }

    public long countByUserIdAndStatus(UUID userId, CaptureStatus status) {
        return inboxRepository.countByStatus(userId, status);
    }

    @Transactional
    public Capture save(Capture capture) {
        if (capture.getId() == null) {
            return this.inboxRepository.save(capture);
        }
        var found = this.inboxRepository.findById(capture.getId());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Capture not found");
        }
        var captureFromDb = found.get();
        captureFromDb.setTitle(capture.getTitle());
        captureFromDb.setDescription(capture.getDescription());
        captureFromDb.setStatus(capture.getStatus());
        captureFromDb.setProcessedDate(capture.getProcessedDate());
        captureFromDb.setUserId(capture.getUserId());
        // Preserve attachments if they were set on the capture
        if (capture.getAttachments() != null) {
            captureFromDb.setAttachments(capture.getAttachments());
        }
        return this.inboxRepository.save(captureFromDb);
    }

    @Transactional
    public Capture saveWithAttachments(Capture capture, Set<Attachment> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            if (capture.getAttachments() == null) {
                capture.setAttachments(new HashSet<>());
            }
            capture.getAttachments().addAll(attachments);
        }
        return save(capture);
    }

    public void deleteById(long id) {
        this.inboxRepository.deleteById(id);
    }

}
