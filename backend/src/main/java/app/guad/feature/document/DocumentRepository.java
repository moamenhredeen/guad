package app.guad.feature.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

interface DocumentRepository extends
        JpaRepository<Document, Long>,
        PagingAndSortingRepository<Document, Long>,
        JpaSpecificationExecutor<Document> {
}
