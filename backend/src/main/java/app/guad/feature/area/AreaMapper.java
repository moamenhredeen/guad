package app.guad.feature.area;

public final class AreaMapper {
    private AreaMapper(){}

    public static GetAreaViewModel toGetAreaViewModel(Area area) {
        return new GetAreaViewModel(
                area.getId(),
                area.getName(),
                area.getDescription(),
                area.getOrder()
        );
    }

    public static AreaDetailsViewModel toAreaDetailsViewModel(Area area) {
        return new AreaDetailsViewModel(
                area.getId(),
                area.getName(),
                area.getDescription(),
                area.getOrder()
        );
    }

    public static DeleteAreaViewModel toDeleteAreaViewModel(Area area) {
        return new DeleteAreaViewModel(area.getId(), area.getName());
    }
}

