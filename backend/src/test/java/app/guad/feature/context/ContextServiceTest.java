package app.guad.feature.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextServiceTest {

    @Mock
    ContextRepository contextRepository;

    @InjectMocks
    ContextService contextService;

    @Test
    void save_newContext_delegatesToRepository() {
        var context = new Context();
        context.setName("Home");
        when(contextRepository.save(any())).thenReturn(context);

        var result = contextService.save(context);
        assertEquals("Home", result.getName());
        verify(contextRepository).save(context);
    }

    @Test
    void save_existingContext_mergesFields() {
        var existing = new Context();
        existing.setId(1L);
        existing.setName("Old");
        when(contextRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(contextRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var updated = new Context();
        updated.setId(1L);
        updated.setName("New");
        updated.setDescription("Updated desc");

        var result = contextService.save(updated);
        assertEquals("New", result.getName());
        assertEquals("Updated desc", result.getDescription());
    }

    @Test
    void save_nonExistentId_throwsException() {
        var context = new Context();
        context.setId(999L);
        when(contextRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> contextService.save(context));
    }

    @Test
    void findAllByUserId_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var context = new Context();
        context.setName("Office");
        when(contextRepository.findAllByUserId(userId)).thenReturn(List.of(context));

        var result = contextService.findAllByUserId(userId);
        assertEquals(1, result.size());
        assertEquals("Office", result.getFirst().getName());
    }

    @Test
    void findByIdAndUserId_found_returnsContext() {
        var userId = UUID.randomUUID();
        var context = new Context();
        context.setId(1L);
        when(contextRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(context));

        var result = contextService.findByIdAndUserId(1L, userId);
        assertTrue(result.isPresent());
    }

    @Test
    void findByIdAndUserId_notFound_returnsEmpty() {
        var userId = UUID.randomUUID();
        when(contextRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());

        var result = contextService.findByIdAndUserId(1L, userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_found_returnsContext() {
        var context = new Context();
        context.setId(1L);
        when(contextRepository.findById(1L)).thenReturn(Optional.of(context));

        var result = contextService.findById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(contextRepository.findById(1L)).thenReturn(Optional.empty());

        var result = contextService.findById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_delegatesToRepository() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var context = new Context();
        context.setName("Office");
        var page = new org.springframework.data.domain.PageImpl<>(List.of(context), pageable, 1);
        when(contextRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
            .thenReturn(page);

        var result = contextService.search("Off", pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("Office", result.getContent().getFirst().getName());
    }

    @Test
    void deleteById_delegatesToRepository() {
        contextService.deleteById(1L);
        verify(contextRepository).deleteById(1L);
    }
}
