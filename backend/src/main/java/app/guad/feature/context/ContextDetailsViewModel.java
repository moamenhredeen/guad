package app.guad.feature.context;

public record ContextDetailsViewModel(
        Long id,
        String name,
        String description,
        String color,
        String iconKey
) {
}

