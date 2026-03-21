package app.guad.feature.context.admin;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.context.ContextService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.core.PaginationUtils.addPaginationData;

@Controller
@RequestMapping("/admin/contexts")
public class ContextAdminController {

    private final ContextService contextService;

    public ContextAdminController(ContextService contextService) {
        this.contextService = contextService;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        var paginatedData = this.contextService.search(search, pageable);
        var contexts = paginatedData
                .stream()
                .map(ContextMapper::toGetContextViewModel)
                .toList();

        model.addAttribute("contexts", contexts);
        model.addAttribute("search", search);
        addPaginationData(model, paginatedData);
        return "admin/contexts/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var context = this.contextService.getContextById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Context", id));
        model.addAttribute("context", ContextMapper.toContextDetailsViewModel(context));
        return "admin/contexts/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteContextForm(@PathVariable Long id, Model model) {
        var context = this.contextService.getContextById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Context", id));
        model.addAttribute("context", ContextMapper.toDeleteContextViewModel(context));
        return "admin/contexts/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteContext(@PathVariable Long id) {
        this.contextService.deleteById(id);
        return "redirect:/admin/contexts";
    }
}
