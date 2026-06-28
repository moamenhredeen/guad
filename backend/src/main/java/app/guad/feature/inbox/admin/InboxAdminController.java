package app.guad.feature.inbox.admin;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.inbox.CaptureStatus;
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
            @RequestParam(required = false) CaptureStatus status
    ) {
        var paginatedData = this.inboxService.search(search, status, pageable);
        var captures = paginatedData.stream()
                .map(CaptureMapper::toGetCaptureViewModel)
                .toList();

        model.addAttribute("captures", captures);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("statuses", CaptureStatus.values());
        addPaginationData(model, paginatedData);
        return "admin/inbox/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var capture = this.inboxService.getCaptureById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Capture", id));
        model.addAttribute("capture", CaptureMapper.toGetCaptureViewModel(capture));
        return "admin/inbox/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteCaptureForm(@PathVariable Long id, Model model) {
        var capture = this.inboxService.getCaptureById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Capture", id));
        model.addAttribute("capture", CaptureMapper.toDeleteCaptureViewModel(capture));
        return "admin/inbox/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteCapture(@PathVariable Long id) {
        this.inboxService.deleteById(id);
        return "redirect:/admin/inbox";
    }
}
