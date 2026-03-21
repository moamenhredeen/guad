package app.guad.feature.inbox.admin;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.inbox.InboxItemStatus;
import app.guad.feature.inbox.InboxService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.core.PaginationUtils.addPaginationData;

@Controller
@RequestMapping("/admin/inbox")
class InboxAdminController {

    private final InboxService inboxService;

    public InboxAdminController(InboxService inboxService) {
        this.inboxService = inboxService;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) InboxItemStatus status
    ) {
        var paginatedData = this.inboxService.search(search, status, pageable);
        var inboxItems = paginatedData.stream()
                .map(InboxItemMapper::toGetInboxItemViewModel)
                .toList();

        model.addAttribute("inboxItems", inboxItems);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("statuses", InboxItemStatus.values());
        addPaginationData(model, paginatedData);
        return "admin/inbox/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var inboxItem = this.inboxService.getInboxItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InboxItem", id));
        model.addAttribute("inboxItem", InboxItemMapper.toGetInboxItemViewModel(inboxItem));
        return "admin/inbox/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteInboxItemForm(@PathVariable Long id, Model model) {
        var inboxItem = this.inboxService.getInboxItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InboxItem", id));
        model.addAttribute("inboxItem", InboxItemMapper.toDeleteInboxItemViewModel(inboxItem));
        return "admin/inbox/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteInboxItem(@PathVariable Long id) {
        this.inboxService.deleteById(id);
        return "redirect:/admin/inbox";
    }
}
