package app.guad.feature.area;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AreaService {
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public Page<Area> search(String name, Pageable pageable) {
        var spec = Specification.allOf(
                AreaSpecifications.byName(name)
        );
        return this.areaRepository.findAll(spec, pageable);
    }

    public List<Area> findAllByUserId(UUID userId) {
        return areaRepository.findAllByUserId(userId);
    }

    public Optional<Area> findByIdAndUserId(Long id, UUID userId) {
        return areaRepository.findByIdAndUserId(id, userId);
    }

    public Optional<Area> getAreaById(long id) {
        return this.areaRepository.findById(id);
    }

    @Transactional
    public Area save(Area area) {
        if (area.getId() == null) {
            return this.areaRepository.save(area);
        }
        var found = this.areaRepository.findById(area.getId());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Area not found");
        }
        var areaFromDb = found.get();
        areaFromDb.setName(area.getName());
        areaFromDb.setDescription(area.getDescription());
        areaFromDb.setOrder(area.getOrder());
        areaFromDb.setUserId(area.getUserId());
        return this.areaRepository.save(areaFromDb);
    }

    public void deleteById(long id) {
        this.areaRepository.deleteById(id);
    }

}

