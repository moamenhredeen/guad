package app.guad.feature.document;

import app.guad.feature.attachment.Attachment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    DocumentRepository documentRepository;

    @InjectMocks
    DocumentService documentService;

    @Test
    void save_newDocument_delegatesToRepository() {
        var document = new Document();
        document.setName("My Document");

        var saved = new Document();
        saved.setId(1L);
        saved.setName("My Document");

        when(documentRepository.save(document)).thenReturn(saved);

        var result = documentService.save(document);

        assertThat(result.getId()).isEqualTo(1L);
        verify(documentRepository).save(document);
    }

    @Test
    void save_existingDocument_mergesFieldsOntoFoundEntity() {
        var existing = new Document();
        existing.setId(10L);
        existing.setName("Old Name");
        existing.setContent("Old content");

        var update = new Document();
        update.setId(10L);
        update.setName("New Name");
        update.setContent("New content");

        when(documentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(documentRepository.save(existing)).thenReturn(existing);

        var result = documentService.save(update);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getContent()).isEqualTo("New content");
        verify(documentRepository).findById(10L);
        verify(documentRepository).save(existing);
    }

    @Test
    void save_nonExistentId_throwsIllegalArgumentException() {
        var document = new Document();
        document.setId(99L);
        document.setName("Ghost document");

        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.save(document))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Document not found");
    }

    @Test
    void save_existingDocument_preservesAttachmentsWhenSet() {
        var attachment = new Attachment();
        attachment.setId(1L);

        var existing = new Document();
        existing.setId(10L);
        existing.setName("Doc");

        var update = new Document();
        update.setId(10L);
        update.setName("Doc");
        update.setAttachments(Set.of(attachment));

        when(documentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(documentRepository.save(existing)).thenReturn(existing);

        documentService.save(update);

        assertThat(existing.getAttachments()).containsExactly(attachment);
    }

    @Test
    void getDocumentById_found_returnsOptionalWithDocument() {
        var document = new Document();
        document.setId(1L);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        var result = documentService.getDocumentById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getDocumentById_notFound_returnsEmptyOptional() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        var result = documentService.getDocumentById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_delegatesToRepository() {
        documentService.deleteById(5L);
        verify(documentRepository).deleteById(5L);
    }

    @Test
    void search_delegatesToRepositoryWithSpec() {
        var page = new PageImpl<>(List.of(new Document()));
        when(documentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var result = documentService.search("My Doc", Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(documentRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void saveWithAttachments_addsAttachmentsAndSaves() {
        var attachment = new Attachment();
        attachment.setId(1L);

        var document = new Document();
        document.setName("Doc with attachments");

        var saved = new Document();
        saved.setId(1L);
        saved.setName("Doc with attachments");
        saved.setAttachments(Set.of(attachment));

        when(documentRepository.save(any(Document.class))).thenReturn(saved);

        var result = documentService.saveWithAttachments(document, Set.of(attachment));

        assertThat(result.getAttachments()).containsExactly(attachment);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void saveWithAttachments_initializesAttachmentSetIfNull() {
        var attachment = new Attachment();
        attachment.setId(1L);

        var document = new Document();
        document.setName("Doc");
        // attachments is null by default

        when(documentRepository.save(any(Document.class))).thenReturn(document);

        documentService.saveWithAttachments(document, Set.of(attachment));

        assertThat(document.getAttachments()).isNotNull();
        assertThat(document.getAttachments()).contains(attachment);
    }
}
