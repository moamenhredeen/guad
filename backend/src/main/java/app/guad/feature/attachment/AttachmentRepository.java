package app.guad.feature.attachment;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface AttachmentRepository extends
        CrudRepository<Attachment, Long>,
        PagingAndSortingRepository<Attachment, Long>,
        JpaSpecificationExecutor<Attachment> {
}
