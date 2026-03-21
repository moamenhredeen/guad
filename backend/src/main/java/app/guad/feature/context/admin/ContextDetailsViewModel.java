package app.guad.feature.context.admin;

public record ContextDetailsViewModel(
        Long id,
        String name,
        String description,
        String color,
        String iconKey
) {
}
