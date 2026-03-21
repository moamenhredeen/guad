package app.guad.core;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void of_singleItem_hasNullMeta() {
        var response = ApiResponse.of("hello");
        assertEquals("hello", response.data());
        assertNull(response.meta());
    }

    @Test
    void of_page_includesPageMeta() {
        var page = new PageImpl<>(List.of("a", "b"), PageRequest.of(0, 10), 25);
        var response = ApiResponse.of(List.of("a", "b"), page);

        assertEquals(List.of("a", "b"), response.data());
        assertNotNull(response.meta());
        assertEquals(0, response.meta().page());
        assertEquals(10, response.meta().size());
        assertEquals(25, response.meta().totalElements());
        assertEquals(3, response.meta().totalPages());
    }
}
