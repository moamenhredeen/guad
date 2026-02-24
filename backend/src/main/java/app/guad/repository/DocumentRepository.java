package app.guad.repository;

import app.guad.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DocumentRepository extends
                JpaRepository<Document, Long>,
                PagingAndSortingRepository<Document, Long>,
                JpaSpecificationExecutor<Document> {

        @Query("SELECT d FROM Document d WHERE d.project.id = :projectId")
        List<Document> findByProjectId(Long projectId);

}

