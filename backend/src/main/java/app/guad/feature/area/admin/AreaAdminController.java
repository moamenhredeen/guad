package app.guad.feature.area.admin;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.area.AreaService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam(required = false) String search
    ) {
        var paginatedData = this.areaService.search(search, pageable);
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
        var area = this.areaService.getAreaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area", id));
        model.addAttribute("area", AreaMapper.toAreaDetailsViewModel(area));
        return "admin/areas/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteAreaForm(@PathVariable Long id, Model model) {
        var area = this.areaService.getAreaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area", id));
        model.addAttribute("area", AreaMapper.toDeleteAreaViewModel(area));
        return "admin/areas/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteArea(@PathVariable Long id) {
        this.areaService.deleteById(id);
        return "redirect:/admin/areas";
    }
}
