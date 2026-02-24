package app.guad.web.mapper;

import app.guad.entity.Document;
import app.guad.service.AttachmentService;
import app.guad.web.viewmodel.*;

import java.util.List;
import java.util.stream.Collectors;

public final class DocumentMapper {
    private DocumentMapper(){}

    public static GetDocumentViewModel toGetDocumentViewModel(Document document) {
        return new GetDocumentViewModel(
                document.getId(),
                document.getName(),
                document.getProject() != null ? document.getProject().getName() : null
        );
    }

    public static DocumentDetailsViewModel toDocumentDetailsViewModel(Document document) {
        return new DocumentDetailsViewModel(
                document.getId(),
                document.getName(),
                document.getContent(),
                List.of()
        );
    }

    public static DeleteDocumentViewModel toDeleteDocumentViewModel(Document document) {
        return new DeleteDocumentViewModel(document.getId(), document.getName());
    }
}

