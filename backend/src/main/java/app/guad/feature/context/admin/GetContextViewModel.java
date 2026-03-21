package app.guad.feature.context.admin;

public record GetContextViewModel(
        Long id,
        String name,
        String description,
        String color
) {
}
