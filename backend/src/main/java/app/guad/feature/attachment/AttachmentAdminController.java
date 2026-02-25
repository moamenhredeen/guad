package app.guad.feature.attachment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.feature.attachment.AttachmentSpecifications.byFilename;
import static app.guad.feature.attachment.AttachmentSpecifications.byMimeType;
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
        var spec = Specification.allOf(
                byFilename(search),
                byMimeType(mimetype)
        );
        var paginatedData = attachmentService.search(spec, pageable);
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
        var attachment = this.attachmentService.getAttachmentById(id);
        if (attachment.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        model.addAttribute("attachment", attachment.map(AttachmentMapper::toAttachmentDetailsViewModel).get());
        return "admin/attachments/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteAttachmentForm(@PathVariable Long id, Model model) {
        var attachment = this.attachmentService.getAttachmentById(id);
        if (attachment.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        model.addAttribute("attachment",
                attachment.map(AttachmentMapper::toDeleteAttachmentViewModel).get());
        return "/admin/attachments/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteAttachment(@PathVariable Long id) {
        var attachment = this.attachmentService.getAttachmentById(id);
        if (attachment.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        this.attachmentService.deleteAttachment(attachment.get());
        return "redirect:/admin/attachments";
    }
}
