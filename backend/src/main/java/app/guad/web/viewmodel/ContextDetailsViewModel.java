package app.guad.web.viewmodel;

public record ContextDetailsViewModel(
        Long id,
        String name,
        String description,
        String color,
        String iconKey
) {
}

