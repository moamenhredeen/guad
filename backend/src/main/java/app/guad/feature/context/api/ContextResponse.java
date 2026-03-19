package app.guad.feature.context.api;

import app.guad.feature.context.Context;

public record ContextResponse(Long id, String name, String description, String color, String iconKey) {
    public static ContextResponse from(Context context) {
        return new ContextResponse(context.getId(), context.getName(), context.getDescription(),
            context.getColor(), context.getIconKey());
    }
}
