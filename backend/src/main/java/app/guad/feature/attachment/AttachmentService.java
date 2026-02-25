package app.guad.feature.attachment;

import app.guad.infrastructure.storage.ObjectStorageProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ObjectStorageProvider objectStorageProvider;
    private final long maxFileSizeBytes;
    private final List<String> allowedMimeTypes;
    private final int presignedUrlExpirationMinutes;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            ObjectStorageProvider objectStorageProvider,
            AttachmentProperties attachmentProperties
    ) {
        this.attachmentRepository = attachmentRepository;
        this.objectStorageProvider = objectStorageProvider;
        this.maxFileSizeBytes = attachmentProperties.getMaxFileSizeMb() * 1024 * 1024;
        this.allowedMimeTypes = attachmentProperties.getAllowedMimeTypes();
        this.presignedUrlExpirationMinutes = attachmentProperties.getPresignedUrlExpirationMinutes();
    }

    public Attachment uploadFile(MultipartFile file, UUID userId) throws IOException {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        // Generate unique S3 key
        String s3Key = generateS3Key(userId, originalFilename);

        // Upload to object storage
        objectStorageProvider.upload(s3Key, file.getInputStream(), contentType, fileSize);

        // Save metadata to database
        Attachment attachment = new Attachment();
        attachment.setFilename(originalFilename);
        attachment.setFileSize(fileSize);
        attachment.setMimeType(contentType);
        attachment.setFileUrl(s3Key); // Store S3 key, not full URL
        attachment.setUploadedDate(Instant.now());
        attachment.setUserId(userId);

        return attachmentRepository.save(attachment);
    }

    public String getPresignedDownloadUrl(Attachment attachment) {
        return objectStorageProvider.generatePresignedUrl(
                attachment.getFileUrl(),
                Duration.ofMinutes(presignedUrlExpirationMinutes));
    }

    public Page<Attachment> getAttachments(Pageable pageable) {
        return attachmentRepository.findAll(pageable);
    }

    public Page<Attachment> search(Specification<Attachment> spec, Pageable pageable) {
        return this.attachmentRepository.findAll(spec, pageable);
    }

    public Optional<Attachment> getAttachmentById(Long id) {
        return attachmentRepository.findById(id);
    }

    public void deleteAttachment(Attachment attachment) {
        // Delete from object storage
        objectStorageProvider.delete(attachment.getFileUrl());
        // Delete from database
        attachmentRepository.delete(attachment);
    }

    public boolean isOwner(Attachment attachment, UUID userId) {
        return attachment.getUserId().equals(userId);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d MB", maxFileSizeBytes / (1024 * 1024)));
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedMimeTypes.contains(contentType)) {
            throw new IllegalArgumentException(
                    String.format("File type '%s' is not allowed. Allowed types: %s", contentType, allowedMimeTypes));
        }
    }

    private String generateS3Key(UUID userId, String originalFilename) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String sanitizedFilename = sanitizeFilename(originalFilename);
        return String.format("attachments/%s/%s-%s-%s", userId, timestamp, uuid, sanitizedFilename);
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        // Remove path separators and other potentially dangerous characters
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
