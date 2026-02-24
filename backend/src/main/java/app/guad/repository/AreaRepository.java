package app.guad.repository;

import app.guad.entity.Area;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AreaRepository extends
        CrudRepository<Area, Long>,
        PagingAndSortingRepository<Area, Long>,
        JpaSpecificationExecutor<Area> {
}

