package app.guad.web.util;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class PaginationUtils {
    public static void addPaginationData(Model model, Page<?> page){
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
    }
}
