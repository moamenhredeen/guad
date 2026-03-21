package app.guad.feature.profile;

import org.springframework.data.jpa.domain.Specification;

class UserProfileSpecifications {
    public static Specification<UserProfile> byEmail(String email) {
        if (email == null) return Specification.unrestricted();
        return (root, _, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<UserProfile> byDisplayName(String displayName) {
        if (displayName == null) return Specification.unrestricted();
        return (root, _, cb) -> cb.like(cb.lower(root.get("displayName")), "%" + displayName.toLowerCase() + "%");
    }
}
