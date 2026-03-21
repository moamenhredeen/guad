package app.guad.feature.area.admin;

public record AreaDetailsViewModel(
        Long id,
        String name,
        String description,
        Integer order
) {
}
