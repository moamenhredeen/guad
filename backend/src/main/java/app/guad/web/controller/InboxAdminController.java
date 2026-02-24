package app.guad.web.controller;

import app.guad.entity.InboxItemStatus;
import app.guad.service.InboxService;
import app.guad.web.mapper.InboxItemMapper;
import app.guad.web.viewmodel.DeleteInboxItemViewModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.specification.InboxItemSpecifications.byStatus;
import static app.guad.specification.InboxItemSpecifications.byTitle;
import static app.guad.web.util.PaginationUtils.addPaginationData;

@Controller
@RequestMapping("/admin/inbox")
public class InboxAdminController {

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
        var spec = Specification.allOf(
                byTitle(search),
                byStatus(status)
        );
        var paginatedData = this.inboxService.search(spec, pageable);
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
        var inboxItem = this.inboxService.getInboxItemById(id);
        if (inboxItem.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        model.addAttribute("inboxItem", InboxItemMapper.toGetInboxItemViewModel(inboxItem.get()));
        return "admin/inbox/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteInboxItemForm(@PathVariable Long id, Model model) {
        var inboxItem = this.inboxService.getInboxItemById(id);
        if (inboxItem.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        var viewModel = inboxItem.map(InboxItemMapper::toDeleteInboxItemViewModel).get();
        model.addAttribute("inboxItem", viewModel);
        return "/admin/inbox/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteInboxItem(@PathVariable Long id) {
        this.inboxService.deleteById(id);
        return "redirect:/admin/inbox";
    }
}
