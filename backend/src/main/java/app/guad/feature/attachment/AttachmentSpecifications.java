package app.guad.feature.attachment;

import org.springframework.data.jpa.domain.Specification;

public class AttachmentSpecifications {

    public static Specification<Attachment> byFilename(String filename) {
        if (filename == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.like(root.get("filename"), filename + "%");
    }

    public static Specification<Attachment> byMimeType(String mimeType) {
        if (mimeType == null) return Specification.unrestricted();
        return (root, _, cb) ->  cb.equal(root.get("mimeType"), mimeType);
    }
}
