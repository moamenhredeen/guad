package app.guad.web.mapper;

import app.guad.entity.Context;
import app.guad.web.viewmodel.ContextDetailsViewModel;
import app.guad.web.viewmodel.DeleteContextViewModel;
import app.guad.web.viewmodel.GetContextViewModel;

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

