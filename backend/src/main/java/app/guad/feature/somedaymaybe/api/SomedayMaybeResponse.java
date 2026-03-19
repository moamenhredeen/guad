package app.guad.feature.somedaymaybe.api;

import app.guad.feature.action.api.ActionResponse;
import app.guad.feature.project.api.ProjectResponse;
import java.util.List;

public record SomedayMaybeResponse(
    List<ActionResponse> actions,
    List<ProjectResponse> projects
) {}
