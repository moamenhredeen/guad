package app.guad.feature.profile.admin;

import app.guad.core.ResourceNotFoundException;
import app.guad.feature.profile.ProfileService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static app.guad.core.PaginationUtils.addPaginationData;

@Controller
@RequestMapping("/admin/profiles")
public class UserProfileAdminController {

    private final ProfileService profileService;

    public UserProfileAdminController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public String list(
            Model model,
            Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        var paginatedData = profileService.search(search, pageable);
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
        var profile = profileService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", id));
        model.addAttribute("profile", UserProfileMapper.toUserProfileDetailsViewModel(profile));
        return "admin/profiles/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteProfileForm(@PathVariable Long id, Model model) {
        var profile = profileService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", id));
        model.addAttribute("profile", UserProfileMapper.toDeleteUserProfileViewModel(profile));
        return "admin/profiles/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteProfile(@PathVariable Long id) {
        profileService.deleteById(id);
        return "redirect:/admin/profiles";
    }
}
