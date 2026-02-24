package app.guad.web.controller;

import app.guad.service.ContextService;
import app.guad.web.mapper.ContextMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.specification.ContextSpecifications.byName;
import static app.guad.web.util.PaginationUtils.addPaginationData;

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
        var paginatedData = this.contextService.search(byName(search), pageable);
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
        var context = this.contextService.getContextById(id);
        if (context.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        var viewModel = context.map(ContextMapper::toContextDetailsViewModel).get();
        model.addAttribute("context", viewModel);
        return "admin/contexts/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteContextForm(@PathVariable Long id, Model model) {
        var context = this.contextService.getContextById(id);
        if (context.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        var viewModel = context.map(ContextMapper::toDeleteContextViewModel).get();
        model.addAttribute("context", viewModel);
        return "/admin/contexts/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteContext(@PathVariable Long id) {
        this.contextService.deleteById(id);
        return "redirect:/admin/contexts";
    }
}
