package app.guad.feature.area;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static app.guad.feature.area.AreaSpecifications.byName;
import static app.guad.feature.area.AreaSpecifications.byUser;
import static app.guad.core.PaginationUtils.addPaginationData;

@Controller
@RequestMapping("/admin/areas")
public class AreaAdminController {

    private final AreaService areaService;

    public AreaAdminController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long userId
    ) {
        var spec = Specification.allOf(byName(search), byUser(userId));
        var paginatedData = this.areaService.search(spec, pageable);
        var areas = paginatedData
                .stream()
                .map(AreaMapper::toGetAreaViewModel)
                .toList();
        model.addAttribute("areas", areas);
        model.addAttribute("search", search);
        addPaginationData(model, paginatedData);
        return "admin/areas/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var area = this.areaService.getAreaById(id);
        if (area.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        var viewModel = area.map(AreaMapper::toAreaDetailsViewModel).get();
        model.addAttribute("area", viewModel);
        return "admin/areas/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteAreaForm(@PathVariable Long id, Model model) {
        var area = this.areaService.getAreaById(id);
        if (area.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        model.addAttribute("area", area.map(AreaMapper::toDeleteAreaViewModel).get());
        return "/admin/areas/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteArea(@PathVariable Long id) {
        this.areaService.deleteById(id);
        return "redirect:/admin/areas";
    }
}
