package app.guad.feature.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionServiceTest {

    @Mock
    ActionRepository actionRepository;

    @InjectMocks
    ActionService actionService;

    @Test
    void save_newAction_delegatesToRepository() {
        var action = new Action();
        action.setDescription("Buy milk");
        action.setStatus(ActionStatus.NEXT);

        var saved = new Action();
        saved.setId(1L);
        saved.setDescription("Buy milk");

        when(actionRepository.save(action)).thenReturn(saved);

        var result = actionService.save(action);

        assertThat(result.getId()).isEqualTo(1L);
        verify(actionRepository).save(action);
    }

    @Test
    void save_existingAction_mergesFieldsOntoFoundEntity() {
        var existing = new Action();
        existing.setId(10L);
        existing.setDescription("Old description");
        existing.setStatus(ActionStatus.NEXT);

        var update = new Action();
        update.setId(10L);
        update.setDescription("New description");
        update.setNotes("Some notes");
        update.setStatus(ActionStatus.IN_PROGRESS);

        when(actionRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(actionRepository.save(existing)).thenReturn(existing);

        var result = actionService.save(update);

        assertThat(result.getDescription()).isEqualTo("New description");
        assertThat(result.getNotes()).isEqualTo("Some notes");
        assertThat(result.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
        verify(actionRepository).findById(10L);
        verify(actionRepository).save(existing);
    }

    @Test
    void save_nonExistentId_throwsIllegalArgumentException() {
        var action = new Action();
        action.setId(99L);
        action.setDescription("Ghost action");

        when(actionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actionService.save(action))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Action not found");
    }

    @Test
    void deleteById_delegatesToRepository() {
        actionService.deleteById(5L);
        verify(actionRepository).deleteById(5L);
    }

    @Test
    void getActionById_found_returnsOptionalWithAction() {
        var action = new Action();
        action.setId(1L);
        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));

        var result = actionService.getActionById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getActionById_notFound_returnsEmptyOptional() {
        when(actionRepository.findById(1L)).thenReturn(Optional.empty());

        var result = actionService.getActionById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void countByUserIdAndStatus_delegatesToRepository() {
        var userId = UUID.randomUUID();
        when(actionRepository.countByUserIdAndStatus(userId, ActionStatus.NEXT)).thenReturn(3L);

        var result = actionService.countByUserIdAndStatus(userId, ActionStatus.NEXT);

        assertThat(result).isEqualTo(3L);
        verify(actionRepository).countByUserIdAndStatus(userId, ActionStatus.NEXT);
    }

    @Test
    void findAllByUserIdAndStatus_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var actions = List.of(new Action());
        when(actionRepository.findAllByUserIdAndStatus(userId, ActionStatus.NEXT)).thenReturn(actions);

        var result = actionService.findAllByUserIdAndStatus(userId, ActionStatus.NEXT);

        assertThat(result).isEqualTo(actions);
        verify(actionRepository).findAllByUserIdAndStatus(userId, ActionStatus.NEXT);
    }

    @Test
    void findAllByUserId_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var actions = List.of(new Action());
        when(actionRepository.findAllByUserId(userId)).thenReturn(actions);

        var result = actionService.findAllByUserId(userId);

        assertThat(result).isEqualTo(actions);
        verify(actionRepository).findAllByUserId(userId);
    }

    @Test
    void findByIdAndUserId_found_returnsOptional() {
        var userId = UUID.randomUUID();
        var action = new Action();
        action.setId(1L);
        when(actionRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(action));

        var result = actionService.findByIdAndUserId(1L, userId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void findByIdAndUserId_notFound_returnsEmptyOptional() {
        var userId = UUID.randomUUID();
        when(actionRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());

        var result = actionService.findByIdAndUserId(1L, userId);

        assertThat(result).isEmpty();
    }

    @Test
    void completeAction_setsStatusAndCompletedDate() {
        var userId = UUID.randomUUID();
        var action = new Action();
        action.setId(1L);
        action.setStatus(ActionStatus.NEXT);

        when(actionRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(action));
        when(actionRepository.save(action)).thenReturn(action);

        var result = actionService.completeAction(1L, userId);

        assertThat(result.getStatus()).isEqualTo(ActionStatus.COMPLETED);
        assertThat(result.getCompletedDate()).isNotNull();
        verify(actionRepository).save(action);
    }

    @Test
    void completeAction_notFound_throwsIllegalArgumentException() {
        var userId = UUID.randomUUID();
        when(actionRepository.findByIdAndUserId(99L, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actionService.completeAction(99L, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Action not found");
    }

    @Test
    void updateStatus_setsNewStatus() {
        var userId = UUID.randomUUID();
        var action = new Action();
        action.setId(1L);
        action.setStatus(ActionStatus.NEXT);

        when(actionRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(action));
        when(actionRepository.save(action)).thenReturn(action);

        var result = actionService.updateStatus(1L, userId, ActionStatus.IN_PROGRESS);

        assertThat(result.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
        assertThat(result.getCompletedDate()).isNull();
    }

    @Test
    void updateStatus_toCompleted_setsCompletedDate() {
        var userId = UUID.randomUUID();
        var action = new Action();
        action.setId(1L);
        action.setStatus(ActionStatus.NEXT);

        when(actionRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(action));
        when(actionRepository.save(action)).thenReturn(action);

        var result = actionService.updateStatus(1L, userId, ActionStatus.COMPLETED);

        assertThat(result.getStatus()).isEqualTo(ActionStatus.COMPLETED);
        assertThat(result.getCompletedDate()).isNotNull();
    }

    @Test
    void search_callsRepositoryWithBuiltSpec() {
        var page = new PageImpl<>(List.of(new Action()));
        when(actionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var result = actionService.search("Buy", ActionStatus.NEXT, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(actionRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllActions_delegatesToRepository() {
        var spec = Specification.<Action>unrestricted();
        var page = new PageImpl<>(List.of(new Action()));
        when(actionRepository.findAll(spec, Pageable.unpaged())).thenReturn(page);

        var result = actionService.getAllActions(spec, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(actionRepository).findAll(spec, Pageable.unpaged());
    }
}
