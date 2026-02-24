package app.guad.web.viewmodel;

import java.util.List;

public record GetProjectViewModel(
        Long id,
        String name,
        String description,
        String status,
        String areaName
) {
}
