package app.guad.feature.attachment.admin;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.attachment.AttachmentService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.core.PaginationUtils.addPaginationData;

@Controller
@RequestMapping("/admin/attachments")
public class AttachmentAdminController {

    private final AttachmentService attachmentService;

    public AttachmentAdminController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String mimetype
    ) {
        var paginatedData = attachmentService.search(search, mimetype, pageable);
        var attachments = paginatedData
                .stream()
                .map(AttachmentMapper::toGetAttachmentViewModel)
                .toList();

        model.addAttribute("attachments", attachments);
        model.addAttribute("search", search);
        model.addAttribute("mimetype", mimetype);
        addPaginationData(model, paginatedData);
        return "admin/attachments/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var attachment = this.attachmentService.getAttachmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", id));
        model.addAttribute("attachment", AttachmentMapper.toAttachmentDetailsViewModel(attachment));
        return "admin/attachments/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteAttachmentForm(@PathVariable Long id, Model model) {
        var attachment = this.attachmentService.getAttachmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", id));
        model.addAttribute("attachment", AttachmentMapper.toDeleteAttachmentViewModel(attachment));
        return "admin/attachments/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteAttachment(@PathVariable Long id) {
        var attachment = this.attachmentService.getAttachmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", id));
        this.attachmentService.deleteAttachment(attachment);
        return "redirect:/admin/attachments";
    }
}
