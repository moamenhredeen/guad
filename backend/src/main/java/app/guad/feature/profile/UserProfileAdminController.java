package app.guad.feature.profile;

import app.guad.core.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.core.PaginationUtils.addPaginationData;
import static app.guad.feature.profile.UserProfileSpecifications.byDisplayName;
import static app.guad.feature.profile.UserProfileSpecifications.byEmail;

@Controller
@RequestMapping("/admin/profiles")
public class UserProfileAdminController {

    private final UserProfileRepository userProfileRepository;

    public UserProfileAdminController(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        var spec = Specification.allOf(
                Specification.anyOf(byEmail(search), byDisplayName(search))
        );
        var paginatedData = userProfileRepository.findAll(spec, pageable);
        var profiles = paginatedData
                .stream()
                .map(UserProfileMapper::toGetUserProfileViewModel)
                .toList();
        model.addAttribute("profiles", profiles);
        model.addAttribute("search", search);
        addPaginationData(model, paginatedData);
        return "admin/profiles/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", id));
        model.addAttribute("profile", UserProfileMapper.toUserProfileDetailsViewModel(profile));
        return "admin/profiles/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteProfileForm(@PathVariable Long id, Model model) {
        var profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", id));
        model.addAttribute("profile", UserProfileMapper.toDeleteUserProfileViewModel(profile));
        return "admin/profiles/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteProfile(@PathVariable Long id) {
        userProfileRepository.deleteById(id);
        return "redirect:/admin/profiles";
    }
}
