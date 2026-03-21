package app.guad.feature.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ProjectService projectService;

    @Test
    void save_newProject_delegatesToRepository() {
        var project = new Project();
        project.setName("Home renovation");
        when(projectRepository.save(any())).thenReturn(project);

        var result = projectService.save(project);
        assertEquals("Home renovation", result.getName());
        verify(projectRepository).save(project);
    }

    @Test
    void save_existingProject_mergesFields() {
        var existing = new Project();
        existing.setId(1L);
        existing.setName("Old name");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var updated = new Project();
        updated.setId(1L);
        updated.setName("New name");
        updated.setDescription("Updated desc");
        updated.setStatus(ProjectStatus.ACTIVE);
        updated.setColor("#FF0000");

        var result = projectService.save(updated);
        assertEquals("New name", result.getName());
        assertEquals("Updated desc", result.getDescription());
        assertEquals(ProjectStatus.ACTIVE, result.getStatus());
        assertEquals("#FF0000", result.getColor());
    }

    @Test
    void save_nonExistentId_throwsException() {
        var project = new Project();
        project.setId(999L);
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> projectService.save(project));
    }

    @Test
    void findAllByUserId_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var project = new Project();
        project.setName("My project");
        when(projectRepository.findAllByUserId(userId)).thenReturn(List.of(project));

        var result = projectService.findAllByUserId(userId);
        assertEquals(1, result.size());
        assertEquals("My project", result.getFirst().getName());
    }

    @Test
    void findByIdAndUserId_found_returnsProject() {
        var userId = UUID.randomUUID();
        var project = new Project();
        project.setId(1L);
        when(projectRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(project));

        var result = projectService.findByIdAndUserId(1L, userId);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findByIdAndUserId_notFound_returnsEmpty() {
        var userId = UUID.randomUUID();
        when(projectRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());

        var result = projectService.findByIdAndUserId(1L, userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_found_returnsProject() {
        var project = new Project();
        project.setId(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        var result = projectService.findById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        var result = projectService.findById(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void countByUserIdAndStatus_delegatesToRepository() {
        var userId = UUID.randomUUID();
        when(projectRepository.countByUserIdAndStatus(userId, ProjectStatus.ACTIVE)).thenReturn(5L);

        var result = projectService.countByUserIdAndStatus(userId, ProjectStatus.ACTIVE);
        assertEquals(5L, result);
    }

    @Test
    void findAllByUserIdAndStatus_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var project = new Project();
        project.setName("Active project");
        when(projectRepository.findAllByUserIdAndStatus(userId, ProjectStatus.ACTIVE))
            .thenReturn(List.of(project));

        var result = projectService.findAllByUserIdAndStatus(userId, ProjectStatus.ACTIVE);
        assertEquals(1, result.size());
        assertEquals("Active project", result.getFirst().getName());
    }

    @Test
    void deleteById_delegatesToRepository() {
        projectService.deleteById(1L);
        verify(projectRepository).deleteById(1L);
    }

    @SuppressWarnings("unchecked")
    @Test
    void search_delegatesToRepository() {
        var pageable = PageRequest.of(0, 10);
        var project = new Project();
        project.setName("Found");
        when(projectRepository.findAll(any(Specification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(project)));

        var result = projectService.search("Found", ProjectStatus.ACTIVE, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("Found", result.getContent().getFirst().getName());
    }
}
