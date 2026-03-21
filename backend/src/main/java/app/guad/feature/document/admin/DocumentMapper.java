package app.guad.feature.document.admin;

import app.guad.feature.document.Document;

import java.util.List;

final class DocumentMapper {
    private DocumentMapper() {}

    static GetDocumentViewModel toGetDocumentViewModel(Document document) {
        return new GetDocumentViewModel(
                document.getId(),
                document.getName(),
                document.getProject() != null ? document.getProject().getName() : null
        );
    }

    static DocumentDetailsViewModel toDocumentDetailsViewModel(Document document) {
        return new DocumentDetailsViewModel(
                document.getId(),
                document.getName(),
                document.getContent(),
                List.of()
        );
    }

    static DeleteDocumentViewModel toDeleteDocumentViewModel(Document document) {
        return new DeleteDocumentViewModel(document.getId(), document.getName());
    }
}
