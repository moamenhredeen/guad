package app.guad.feature.context;

public final class ContextMapper {
    private ContextMapper(){}

    public static GetContextViewModel toGetContextViewModel(Context context) {
        return new GetContextViewModel(
                context.getId(),
                context.getName(),
                context.getDescription(),
                context.getColor()
        );
    }

    public static ContextDetailsViewModel toContextDetailsViewModel(Context context) {
        return new ContextDetailsViewModel(
                context.getId(),
                context.getName(),
                context.getDescription(),
                context.getColor(),
                context.getIconKey()
        );
    }

    public static DeleteContextViewModel toDeleteContextViewModel(Context context) {
        return new DeleteContextViewModel(context.getId(), context.getName());
    }
}

