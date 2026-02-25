package app.guad.feature.project;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.feature.project.ProjectSpecifications.byName;
import static app.guad.feature.project.ProjectSpecifications.byStatus;
import static app.guad.feature.project.ProjectMapper.toProjectDetailsViewModel;

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
        var paginatedData = this.projectService.search(
                Specification.allOf(
                        byName(search),
                        byStatus(status)
                ),
                pageable);
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
        var project = this.projectService.getProjectById(id);
        if (project.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        model.addAttribute("project", toProjectDetailsViewModel(project.get()));
        return "admin/projects/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteProjectForm(@PathVariable Long id, Model model) {
        var project = this.projectService.getProjectById(id);
        if (project.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        model.addAttribute("viewModel",
                project.map(p -> new DeleteProjectViewModel(p.getId(), p.getName())).get());
        return "admin/projects/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        this.projectService.deleteById(id);
        return "redirect:/admin/projects";
    }
}

