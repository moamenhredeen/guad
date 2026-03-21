package app.guad.feature.action.admin;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.action.Action;
import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.core.PaginationUtils.addPaginationData;
import static app.guad.feature.action.admin.ActionDetailsViewModel.toActionDetailsViewModel;

@Controller
@RequestMapping("/admin/actions")
public class ActionAdminController {

    private static final Logger logger = LoggerFactory.getLogger(ActionAdminController.class);

    private final ActionService actionService;

    public ActionAdminController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ActionStatus status
    ) {
        var paginatedData = this.actionService.search(search, status, pageable);
        var actions = paginatedData
                .stream()
                .map(ActionMapper::toGetActionViewModel)
                .toList();
        model.addAttribute("actions",  actions);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("statuses", ActionStatus.values());
        addPaginationData(model, paginatedData);
        return "admin/actions/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var action = this.actionService.getActionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Action", id));
        model.addAttribute("action", toActionDetailsViewModel(action));
        return "admin/actions/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteActionForm(@PathVariable Long id, Model model) {
        var action = this.actionService.getActionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Action", id));
        model.addAttribute("action", ActionMapper.toDeleteActionViewModel(action));
        return "admin/actions/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteAction(@PathVariable Long id) {
        this.actionService.deleteById(id);
        return "redirect:/admin/actions";
    }
}
