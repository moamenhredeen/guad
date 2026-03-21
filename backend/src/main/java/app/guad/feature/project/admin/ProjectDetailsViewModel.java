package app.guad.feature.project.admin;

import app.guad.feature.attachment.AttachmentListItemViewModel;

import java.time.Instant;
import java.util.List;

public record ProjectDetailsViewModel(
        Long id,
        String name,
        String description,
        String desiredOutcome,
        String status,
        Instant createdDate,
        Instant updatedDate,
        Instant completedDate,
        String color,
        Long areaId,
        String areaName,
        List<AttachmentListItemViewModel> attachments) {
    public record ActionListItem(
            Long id,
            String description,
            String status) {
    }
}
