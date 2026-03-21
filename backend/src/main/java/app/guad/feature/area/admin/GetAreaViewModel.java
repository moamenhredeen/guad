package app.guad.feature.area.admin;

public record GetAreaViewModel(
        Long id,
        String name,
        String description,
        Integer order
) {
}
