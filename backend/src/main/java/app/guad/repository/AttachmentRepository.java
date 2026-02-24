package app.guad.repository;

import app.guad.entity.Attachment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttachmentRepository extends
        CrudRepository<Attachment, Long>,
        PagingAndSortingRepository<Attachment, Long>,
        JpaSpecificationExecutor<Attachment> {
}


