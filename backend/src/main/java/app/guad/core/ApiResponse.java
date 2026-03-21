package app.guad.core;

import org.springframework.data.domain.Page;

public record ApiResponse<T>(T data, PageMeta meta) {

    public record PageMeta(int page, int size, long totalElements, int totalPages) {}

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> of(T data, Page<?> page) {
        return new ApiResponse<>(data, new PageMeta(
            page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages()
        ));
    }
}
