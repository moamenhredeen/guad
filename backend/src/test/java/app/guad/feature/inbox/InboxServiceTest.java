package app.guad.feature.inbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxServiceTest {

    @Mock
    InboxRepository inboxRepository;

    @InjectMocks
    InboxService inboxService;

    @Test
    void save_newItem_delegatesToRepository() {
        var item = new InboxItem();
        item.setTitle("New item");
        item.setStatus(InboxItemStatus.UNPROCESSED);

        var saved = new InboxItem();
        saved.setId(1L);
        saved.setTitle("New item");

        when(inboxRepository.save(item)).thenReturn(saved);

        var result = inboxService.save(item);

        assertThat(result.getId()).isEqualTo(1L);
        verify(inboxRepository).save(item);
    }

    @Test
    void save_existingItem_mergesFieldsOntoFoundEntity() {
        var existing = new InboxItem();
        existing.setId(42L);
        existing.setTitle("Old title");
        existing.setStatus(InboxItemStatus.UNPROCESSED);

        var update = new InboxItem();
        update.setId(42L);
        update.setTitle("New title");
        update.setDescription("New desc");
        update.setStatus(InboxItemStatus.PROCESSED);

        when(inboxRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(inboxRepository.save(existing)).thenReturn(existing);

        var result = inboxService.save(update);

        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getDescription()).isEqualTo("New desc");
        assertThat(result.getStatus()).isEqualTo(InboxItemStatus.PROCESSED);
        verify(inboxRepository).findById(42L);
        verify(inboxRepository).save(existing);
    }

    @Test
    void save_nonExistentId_throwsIllegalArgumentException() {
        var item = new InboxItem();
        item.setId(99L);
        item.setTitle("Ghost item");

        when(inboxRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inboxService.save(item))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("InboxItem not found");
    }

    @Test
    void getUnprocessedByUserId_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var items = List.of(new InboxItem());
        when(inboxRepository.findAllByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED)).thenReturn(items);

        var result = inboxService.getUnprocessedByUserId(userId);

        assertThat(result).isEqualTo(items);
        verify(inboxRepository).findAllByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED);
    }

    @Test
    void getByIdAndUserId_found_returnsOptionalWithItem() {
        var userId = UUID.randomUUID();
        var item = new InboxItem();
        item.setId(1L);
        when(inboxRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(item));

        var result = inboxService.getByIdAndUserId(1L, userId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getByIdAndUserId_notFound_returnsEmptyOptional() {
        var userId = UUID.randomUUID();
        when(inboxRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());

        var result = inboxService.getByIdAndUserId(1L, userId);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_delegatesToRepository() {
        inboxService.deleteById(7L);
        verify(inboxRepository).deleteById(7L);
    }

    @Test
    void countByUserIdAndStatus_delegatesToRepository() {
        var userId = UUID.randomUUID();
        when(inboxRepository.countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED)).thenReturn(5L);

        var result = inboxService.countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED);

        assertThat(result).isEqualTo(5L);
        verify(inboxRepository).countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED);
    }
}
