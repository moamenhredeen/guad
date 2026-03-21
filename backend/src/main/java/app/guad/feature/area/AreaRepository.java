package app.guad.feature.area;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface AreaRepository extends
        CrudRepository<Area, Long>,
        PagingAndSortingRepository<Area, Long>,
        JpaSpecificationExecutor<Area> {
    List<Area> findAllByUserId(UUID userId);
    Optional<Area> findByIdAndUserId(Long id, UUID userId);
}
