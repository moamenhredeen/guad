package app.guad.feature.context;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContextService {
    private final ContextRepository contextRepository;

    public ContextService(ContextRepository contextRepository) {
        this.contextRepository = contextRepository;
    }

    public Page<Context> getContexts(Pageable pageable) {
        return this.contextRepository.findAll(pageable);
    }

    public Page<Context> search(Specification<Context> spec, Pageable pageable) {
        return this.contextRepository.findAll(spec, pageable);
    }

    public Optional<Context> getContextById(long id) {
        return this.contextRepository.findById(id);
    }

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

