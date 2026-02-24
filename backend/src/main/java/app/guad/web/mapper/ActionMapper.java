package app.guad.web.mapper;

import app.guad.entity.*;
import app.guad.service.AttachmentService;
import app.guad.web.viewmodel.ActionDetailsViewModel;
import app.guad.web.viewmodel.DeleteActionViewModel;
import app.guad.web.viewmodel.GetActionViewModel;

import java.util.List;
import java.util.stream.Collectors;

public final class ActionMapper {
    private ActionMapper() {
    }

    public static GetActionViewModel toGetActionViewModel(Action action) {
        return new GetActionViewModel(
                action.getId(),
                action.getDescription(),
                action.getStatus() != null ? action.getStatus().name() : null,
                action.getArea() != null ? action.getArea().getName() : null,
                action.getProject() != null ? action.getProject().getName() : null
        );
    }

    public static ActionDetailsViewModel toActionDetailsViewModel(Action action, AttachmentService attachmentService) {
        List<String> contextNames = action.getContexts() != null
                ? action.getContexts().stream()
                        .map(Context::getName)
                        .collect(Collectors.toList())
                : List.of();

        return new ActionDetailsViewModel(
                action.getId(),
                action.getDescription(),
                action.getNotes(),
                action.getStatus() != null ? action.getStatus().name() : null,
                action.isTimeSpecific(),
                action.getEstimatedDuration(),
                action.getEnergyLevel(),
                action.getLocation(),
                action.getCreatedDate(),
                action.getUpdatedDate(),
                action.getCompletedDate(),
                action.getScheduledDate(),
                action.getDueDate(),
                action.getProject() != null ? action.getProject().getId() : null,
                action.getProject() != null ? action.getProject().getName() : null,
                action.getArea() != null ? action.getArea().getId() : null,
                action.getArea() != null ? action.getArea().getName() : null,
                contextNames,
                null);
    }

    private static ActionStatus parseActionStatus(String status) {
        if (status == null) {
            return ActionStatus.NEXT;
        }
        try {
            return ActionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ActionStatus.NEXT;
        }
    }

    public static DeleteActionViewModel toDeleteActionViewModel(Action action) {
        return new DeleteActionViewModel(action.getId(), action.getDescription());
    }
}
