package app.guad.feature.project.admin;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.project.ProjectService;
import app.guad.feature.project.ProjectStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.feature.project.admin.ProjectMapper.toProjectDetailsViewModel;

@Controller
@RequestMapping("/admin/projects")
public class ProjectAdminController {

    private final ProjectService projectService;

    public ProjectAdminController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String search
    ) {
        var paginatedData = this.projectService.search(search, status, pageable);
        var projects = paginatedData
                .getContent()
                .stream()
                .map(ProjectMapper::toGetProjectViewModel)
                .toList();
        model.addAttribute("projects", projects);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("statuses", ProjectStatus.values());
        return "admin/projects/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var project = this.projectService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        model.addAttribute("project", toProjectDetailsViewModel(project));
        return "admin/projects/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteProjectForm(@PathVariable Long id, Model model) {
        var project = this.projectService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        model.addAttribute("viewModel", new DeleteProjectViewModel(project.getId(), project.getName()));
        return "admin/projects/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        this.projectService.deleteById(id);
        return "redirect:/admin/projects";
    }
}
