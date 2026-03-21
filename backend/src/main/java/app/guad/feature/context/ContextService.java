package app.guad.feature.context;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContextService {
    private final ContextRepository contextRepository;

    public ContextService(ContextRepository contextRepository) {
        this.contextRepository = contextRepository;
    }

    public Page<Context> search(String name, Pageable pageable) {
        var spec = Specification.allOf(
            ContextSpecifications.byName(name)
        );
        return contextRepository.findAll(spec, pageable);
    }

    public List<Context> findAllByUserId(UUID userId) {
        return contextRepository.findAllByUserId(userId);
    }

    public Optional<Context> findByIdAndUserId(Long id, UUID userId) {
        return contextRepository.findByIdAndUserId(id, userId);
    }

    public Optional<Context> findById(Long id) {
        return contextRepository.findById(id);
    }

    public Optional<Context> getContextById(long id) {
        return this.contextRepository.findById(id);
    }

    @Transactional
    public Context save(Context context) {
        if (context.getId() == null) {
            return this.contextRepository.save(context);
        }
        var found = this.contextRepository.findById(context.getId());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Context not found");
        }
        var contextFromDb = found.get();
        contextFromDb.setName(context.getName());
        contextFromDb.setDescription(context.getDescription());
        contextFromDb.setColor(context.getColor());
        contextFromDb.setIconKey(context.getIconKey());
        contextFromDb.setUserId(context.getUserId());
        return this.contextRepository.save(contextFromDb);
    }

    public void deleteById(long id) {
        this.contextRepository.deleteById(id);
    }
}
