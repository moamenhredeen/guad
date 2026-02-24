package app.guad.web.viewmodel;

import java.util.List;

public record GetAreaViewModel(
        Long id,
        String name,
        String description,
        Integer order
) {
}

