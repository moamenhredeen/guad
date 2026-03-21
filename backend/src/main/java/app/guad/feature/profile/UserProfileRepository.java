package app.guad.feature.profile;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

interface UserProfileRepository extends
        CrudRepository<UserProfile, Long>,
        PagingAndSortingRepository<UserProfile, Long>,
        JpaSpecificationExecutor<UserProfile> {
    Optional<UserProfile> findByKeycloakId(UUID keycloakId);
}
