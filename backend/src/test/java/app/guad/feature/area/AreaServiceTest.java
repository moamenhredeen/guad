package app.guad.feature.area;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    @Mock
    AreaRepository areaRepository;

    @InjectMocks
    AreaService areaService;

    @Test
    void save_newArea_delegatesToRepository() {
        var area = new Area();
        area.setName("Health");
        when(areaRepository.save(any())).thenReturn(area);

        var result = areaService.save(area);
        assertEquals("Health", result.getName());
        verify(areaRepository).save(area);
    }

    @Test
    void save_existingArea_mergesFields() {
        var existing = new Area();
        existing.setId(1L);
        existing.setName("Old");
        when(areaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(areaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var updated = new Area();
        updated.setId(1L);
        updated.setName("New");
        updated.setDescription("Updated desc");

        var result = areaService.save(updated);
        assertEquals("New", result.getName());
        assertEquals("Updated desc", result.getDescription());
    }

    @Test
    void save_nonExistentId_throwsException() {
        var area = new Area();
        area.setId(999L);
        when(areaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> areaService.save(area));
    }

    @Test
    void findAllByUserId_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var area = new Area();
        area.setName("Finance");
        when(areaRepository.findAllByUserId(userId)).thenReturn(List.of(area));

        var result = areaService.findAllByUserId(userId);
        assertEquals(1, result.size());
        assertEquals("Finance", result.getFirst().getName());
    }

    @Test
    void findByIdAndUserId_found_returnsArea() {
        var userId = UUID.randomUUID();
        var area = new Area();
        area.setId(1L);
        when(areaRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(area));

        var result = areaService.findByIdAndUserId(1L, userId);
        assertTrue(result.isPresent());
    }

    @Test
    void findByIdAndUserId_notFound_returnsEmpty() {
        var userId = UUID.randomUUID();
        when(areaRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());

        var result = areaService.findByIdAndUserId(1L, userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAreaById_found_returnsArea() {
        var area = new Area();
        area.setId(1L);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));

        var result = areaService.getAreaById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void deleteById_delegatesToRepository() {
        areaService.deleteById(1L);
        verify(areaRepository).deleteById(1L);
    }
}
