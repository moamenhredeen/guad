package app.guad.service;

import app.guad.entity.Attachment;
import app.guad.entity.InboxItem;
import app.guad.repository.InboxRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class InboxService {
    private final InboxRepository inboxRepository;

    public InboxService(InboxRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    public Page<InboxItem> getInboxItems(Pageable pageable) {
        return this.inboxRepository.findAll(pageable);
    }

    public Page<InboxItem> search(Specification<InboxItem> spec, Pageable pageable) {
        return this.inboxRepository.findAll(spec, pageable);
    }

    public Optional<InboxItem> getInboxItemById(long id) {
        return this.inboxRepository.findById(id);
    }

    @Transactional
    public InboxItem save(InboxItem inboxItem) {
        if (inboxItem.getId() == null) {
            return this.inboxRepository.save(inboxItem);
        }
        var found = this.inboxRepository.findById(inboxItem.getId());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("InboxItem not found");
        }
        var inboxItemFromDb = found.get();
        inboxItemFromDb.setTitle(inboxItem.getTitle());
        inboxItemFromDb.setDescription(inboxItem.getDescription());
        inboxItemFromDb.setStatus(inboxItem.getStatus());
        inboxItemFromDb.setUserId(inboxItem.getUserId());
        // Preserve attachments if they were set on the inboxItem
        if (inboxItem.getAttachments() != null) {
            inboxItemFromDb.setAttachments(inboxItem.getAttachments());
        }
        return this.inboxRepository.save(inboxItemFromDb);
    }

    @Transactional
    public InboxItem saveWithAttachments(InboxItem inboxItem, Set<Attachment> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            if (inboxItem.getAttachments() == null) {
                inboxItem.setAttachments(new HashSet<>());
            }
            inboxItem.getAttachments().addAll(attachments);
        }
        return save(inboxItem);
    }

    public void deleteById(long id) {
        this.inboxRepository.deleteById(id);
    }

}

