package app.guad.feature.area.api;

import app.guad.feature.area.Area;

public record AreaResponse(Long id, String name, String description) {
    public static AreaResponse from(Area area) {
        return new AreaResponse(area.getId(), area.getName(), area.getDescription());
    }
}
