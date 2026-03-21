package app.guad.feature.waitingfor;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WaitingForServiceTest {

    @Mock
    WaitingForRepository waitingForRepository;

    @InjectMocks
    WaitingForService waitingForService;

    @Test
    void save_newItem_delegatesToRepository() {
        var item = new WaitingForItem();
        item.setTitle("Wait for reply");
        item.setStatus(WaitingForItemStatus.WAITING);

        var saved = new WaitingForItem();
        saved.setId(1L);
        saved.setTitle("Wait for reply");

        when(waitingForRepository.save(item)).thenReturn(saved);

        var result = waitingForService.save(item);

        assertThat(result.getId()).isEqualTo(1L);
        verify(waitingForRepository).save(item);
    }

    @Test
    void save_existingItem_mergesFieldsOntoFoundEntity() {
        var existing = new WaitingForItem();
        existing.setId(10L);
        existing.setTitle("Old title");
        existing.setStatus(WaitingForItemStatus.WAITING);

        var update = new WaitingForItem();
        update.setId(10L);
        update.setTitle("New title");
        update.setNotes("Some notes");
        update.setStatus(WaitingForItemStatus.RESOLVED);

        when(waitingForRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(waitingForRepository.save(existing)).thenReturn(existing);

        var result = waitingForService.save(update);

        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getNotes()).isEqualTo("Some notes");
        assertThat(result.getStatus()).isEqualTo(WaitingForItemStatus.RESOLVED);
        verify(waitingForRepository).findById(10L);
        verify(waitingForRepository).save(existing);
    }

    @Test
    void save_nonExistentId_throwsIllegalArgumentException() {
        var item = new WaitingForItem();
        item.setId(99L);
        item.setTitle("Ghost item");

        when(waitingForRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> waitingForService.save(item))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("WaitingForItem not found");
    }

    @Test
    void findAllByUserIdAndStatus_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var items = List.of(new WaitingForItem());
        when(waitingForRepository.findAllByUserIdAndStatus(userId, WaitingForItemStatus.WAITING))
            .thenReturn(items);

        var result = waitingForService.findAllByUserIdAndStatus(userId, WaitingForItemStatus.WAITING);

        assertThat(result).isEqualTo(items);
        verify(waitingForRepository).findAllByUserIdAndStatus(userId, WaitingForItemStatus.WAITING);
    }

    @Test
    void findByIdAndUserId_found_returnsOptional() {
        var userId = UUID.randomUUID();
        var item = new WaitingForItem();
        item.setId(1L);
        when(waitingForRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(item));

        var result = waitingForService.findByIdAndUserId(1L, userId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void findByIdAndUserId_notFound_returnsEmptyOptional() {
        var userId = UUID.randomUUID();
        when(waitingForRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());

        var result = waitingForService.findByIdAndUserId(1L, userId);

        assertThat(result).isEmpty();
    }

    @Test
    void countByUserIdAndStatus_delegatesToRepository() {
        var userId = UUID.randomUUID();
        when(waitingForRepository.countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING))
            .thenReturn(5L);

        var result = waitingForService.countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING);

        assertThat(result).isEqualTo(5L);
        verify(waitingForRepository).countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING);
    }

    @Test
    void deleteById_delegatesToRepository() {
        waitingForService.deleteById(5L);
        verify(waitingForRepository).deleteById(5L);
    }
}
