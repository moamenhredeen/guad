package app.guad.feature.document;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.feature.document.DocumentSpecifications.byName;
import static app.guad.feature.document.DocumentMapper.toDocumentDetailsViewModel;
import static app.guad.core.PaginationUtils.addPaginationData;

@Controller
@RequestMapping("/admin/documents")
public class DocumentAdminController {

    private final DocumentService documentService;

    public DocumentAdminController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        var spec = Specification.allOf(byName(search));
        var paginatedData = this.documentService.search(spec, pageable);
        var documents = paginatedData.stream()
                .map(DocumentMapper::toGetDocumentViewModel)
                .toList();

        model.addAttribute("documents", documents);
        model.addAttribute("search", search);
        addPaginationData(model, paginatedData);
        return "admin/documents/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var document = this.documentService.getDocumentById(id);
        if (document.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        model.addAttribute("document", toDocumentDetailsViewModel(document.get()));
        return "admin/documents/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteDocumentForm(@PathVariable Long id, Model model) {
        var document = this.documentService.getDocumentById(id);
        if (document.isEmpty()) {
            return "redirect:/admin/not-found";
        }
        var viewModel = document.map(DocumentMapper::toDeleteDocumentViewModel).get();
        model.addAttribute("document", viewModel);
        return "admin/documents/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteDocument(@PathVariable Long id) {
        this.documentService.deleteById(id);
        return "redirect:/admin/documents";
    }
}

