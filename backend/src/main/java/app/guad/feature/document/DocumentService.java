package app.guad.feature.document;

import app.guad.feature.attachment.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Page<Document> getDocuments(Pageable pageable) {
        return this.documentRepository.findAll(pageable);
    }

    public Page<Document> search(Specification<Document> spec, Pageable pageable) {
        return this.documentRepository.findAll(spec, pageable);
    }

    public Optional<Document> getDocumentById(long id) {
        return this.documentRepository.findById(id);
    }

    @Transactional
    public Document save(Document document) {
        if (document.getId() == null) {
            return this.documentRepository.save(document);
        }
        var found = this.documentRepository.findById(document.getId());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Document not found");
        }
        var documentFromDb = found.get();
        documentFromDb.setName(document.getName());
        documentFromDb.setContent(document.getContent());
        // Preserve attachments if they were set on the document
        if (document.getAttachments() != null) {
            documentFromDb.setAttachments(document.getAttachments());
        }
        return this.documentRepository.save(documentFromDb);
    }

    @Transactional
    public Document saveWithAttachments(Document document, Set<Attachment> attachments) {
        if (document.getAttachments() == null) {
            document.setAttachments(new HashSet<>());
        }
        document.getAttachments().addAll(attachments);
        return save(document);
    }

    public void deleteById(long id) {
        this.documentRepository.deleteById(id);
    }
}

