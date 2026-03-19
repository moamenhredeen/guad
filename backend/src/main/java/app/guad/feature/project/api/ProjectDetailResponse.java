package app.guad.feature.project.api;

import app.guad.feature.action.Action;
import app.guad.feature.action.api.ActionResponse;
import app.guad.feature.project.Project;
import app.guad.feature.waitingfor.WaitingForItem;
import app.guad.feature.waitingfor.api.WaitingForResponse;
import java.time.Instant;
import java.util.List;

public record ProjectDetailResponse(
    Long id, String name, String description, String desiredOutcome,
    String status, String areaName, Long areaId, String color,
    List<ActionResponse> nextActions,
    List<WaitingForResponse> waitingForItems,
    List<ActionResponse> completedActions,
    Instant createdDate
) {
    public static ProjectDetailResponse from(Project project, List<Action> nextActions,
                                              List<WaitingForItem> waitingFor, List<Action> completed) {
        return new ProjectDetailResponse(
            project.getId(), project.getName(), project.getDescription(),
            project.getDesired_outcome(), project.getStatus().name(),
            project.getArea() != null ? project.getArea().getName() : null,
            project.getArea() != null ? project.getArea().getId() : null,
            project.getColor(),
            nextActions.stream().map(ActionResponse::from).toList(),
            waitingFor.stream().map(WaitingForResponse::from).toList(),
            completed.stream().map(ActionResponse::from).toList(),
            project.getCreatedDate()
        );
    }
}
