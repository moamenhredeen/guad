package app.guad.feature.attachment;

import app.guad.infrastructure.storage.ObjectStorageProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    AttachmentRepository attachmentRepository;

    @Mock
    ObjectStorageProvider objectStorageProvider;

    private AttachmentService buildService() {
        var props = new AttachmentProperties();
        props.setMaxFileSizeMb(10);
        props.setAllowedMimeTypes(List.of("application/pdf", "image/png"));
        props.setPresignedUrlExpirationMinutes(30);
        return new AttachmentService(attachmentRepository, objectStorageProvider, props);
    }

    @Test
    void uploadFile_validFile_savesAndReturns() throws IOException {
        var service = buildService();
        var userId = UUID.randomUUID();

        var file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getOriginalFilename()).thenReturn("doc.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(objectStorageProvider.upload(anyString(), any(), anyString(), anyLong())).thenReturn("key");

        var saved = new Attachment();
        saved.setId(1L);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(saved);

        var result = service.uploadFile(file, userId);

        assertThat(result.getId()).isEqualTo(1L);
        verify(objectStorageProvider).upload(anyString(), any(), eq("application/pdf"), eq(1024L));
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void uploadFile_nullFile_throwsIllegalArgument() {
        var service = buildService();
        assertThatThrownBy(() -> service.uploadFile(null, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File is required");
    }

    @Test
    void uploadFile_emptyFile_throwsIllegalArgument() {
        var service = buildService();
        var file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> service.uploadFile(file, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File is required");
    }

    @Test
    void uploadFile_tooLarge_throwsIllegalArgument() {
        var service = buildService();
        var file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(11L * 1024 * 1024); // 11 MB, limit is 10

        assertThatThrownBy(() -> service.uploadFile(file, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds maximum");
    }

    @Test
    void uploadFile_disallowedMimeType_throwsIllegalArgument() {
        var service = buildService();
        var file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("text/plain");

        assertThatThrownBy(() -> service.uploadFile(file, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not allowed");
    }

    @Test
    void getAttachmentById_found_returnsOptional() {
        var service = buildService();
        var attachment = new Attachment();
        attachment.setId(1L);
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));

        var result = service.getAttachmentById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getAttachmentById_notFound_returnsEmpty() {
        var service = buildService();
        when(attachmentRepository.findById(1L)).thenReturn(Optional.empty());

        var result = service.getAttachmentById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteAttachment_deletesFromStorageAndRepo() {
        var service = buildService();
        var attachment = new Attachment();
        attachment.setFileUrl("attachments/user/file.pdf");

        service.deleteAttachment(attachment);

        verify(objectStorageProvider).delete("attachments/user/file.pdf");
        verify(attachmentRepository).delete(attachment);
    }

    @Test
    void isOwner_sameUser_returnsTrue() {
        var service = buildService();
        var userId = UUID.randomUUID();
        var attachment = new Attachment();
        attachment.setUserId(userId);

        assertThat(service.isOwner(attachment, userId)).isTrue();
    }

    @Test
    void isOwner_differentUser_returnsFalse() {
        var service = buildService();
        var attachment = new Attachment();
        attachment.setUserId(UUID.randomUUID());

        assertThat(service.isOwner(attachment, UUID.randomUUID())).isFalse();
    }

    @Test
    void search_delegatesToRepositoryWithSpec() {
        var service = buildService();
        var page = new PageImpl<>(List.of(new Attachment()));
        when(attachmentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var result = service.search("doc", "application/pdf", Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(attachmentRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getPresignedDownloadUrl_delegatesToStorageProvider() {
        var service = buildService();
        var attachment = new Attachment();
        attachment.setFileUrl("attachments/user/file.pdf");
        when(objectStorageProvider.generatePresignedUrl(anyString(), any(Duration.class)))
                .thenReturn("https://presigned-url");

        var result = service.getPresignedDownloadUrl(attachment);

        assertThat(result).isEqualTo("https://presigned-url");
        verify(objectStorageProvider).generatePresignedUrl(eq("attachments/user/file.pdf"), any(Duration.class));
    }
}
