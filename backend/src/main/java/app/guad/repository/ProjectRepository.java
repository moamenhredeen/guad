package app.guad.repository;

import app.guad.entity.Project;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectRepository extends
        CrudRepository<Project, Long>,
        PagingAndSortingRepository<Project, Long>,
        JpaSpecificationExecutor<Project> {
}

