# User Profile & Registration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a local UserProfile entity with lazy creation and a REST API for managing user settings, plus enable Keycloak self-service registration.

**Architecture:** A `HandlerInterceptor` on `/api/**` routes auto-creates a `UserProfile` on first authenticated request. Profile stores Guad-specific preferences (review day, timezone, notifications). Registration is handled entirely by Keycloak's built-in flow. No Caffeine cache — the spec mentions it but it's not a dependency and premature for now; a simple DB lookup on a unique-indexed UUID column is fast enough. The spec's JWT `iat`-based staleness check is simplified to a direct value comparison — acceptable for pre-alpha. Custom Keycloak theme is deferred to a future task (cosmetic, not blocking).

**Tech Stack:** Spring Boot 4.0.3, Java 25, Spring Data JPA, PostgreSQL, Flyway, Spring Security OAuth2/JWT

**Spec:** `docs/superpowers/specs/2026-03-21-user-profile-and-registration-design.md`

---

### Task 1: Consolidate Database Migrations

**Files:**
- Modify: `backend/src/main/resources/db/migration/V1__Schema.sql`
- Delete: `backend/src/main/resources/db/migration/V2__Gtd_enhancements.sql`

- [ ] **Step 1: Merge V2 into V1 and add user_profiles table**

Rewrite `V1__Schema.sql` to be the single consolidated migration. Changes from the current V1+V2:
- Remove `users`, `roles`, `user_roles` tables and their sequences (`users_seq`, `roles_seq`)
- Remove all FK constraints that referenced the `users` table (`FK_ACTIONS_ON_USER`, `FK_AREAS_ON_USER`, `FK_ATTACHMENTS_ON_USER`, `FK_CONTEXTS_ON_USER`, `FK_INBOX_ITEMS_ON_USER`, `FK_PROJECTS_ON_USER`)
- Change `user_id` columns from `BIGINT` to `UUID` directly in the table definitions
- Add `user_id` indexes from V2 inline
- Add `waiting_for_items` and `weekly_reviews` tables from V2 inline
- Add new `user_profiles` table and sequence

The consolidated V1 should contain (in order):
1. All sequences (including `user_profiles_seq`, excluding `users_seq` and `roles_seq`)
2. All tables with `user_id UUID NOT NULL` (no BIGINT conversion needed)
3. `user_profiles` table with the schema from the spec
4. All FK constraints (excluding user FK constraints)
5. All indexes (including `user_id` indexes)

```sql
-- At the top, add the new sequence alongside existing ones:
CREATE SEQUENCE IF NOT EXISTS user_profiles_seq START WITH 1 INCREMENT BY 50;

-- New table (add after existing tables):
CREATE TABLE user_profiles
(
    id                             BIGINT       NOT NULL DEFAULT nextval('user_profiles_seq'),
    keycloak_id                    UUID         NOT NULL,
    email                          VARCHAR(255) NOT NULL,
    display_name                   VARCHAR(255) NOT NULL,
    timezone                       VARCHAR(50)  NOT NULL DEFAULT 'Europe/Berlin',
    default_review_day             VARCHAR(10)  NOT NULL DEFAULT 'SATURDAY',
    energy_tracking_enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
    email_digests_enabled          BOOLEAN      NOT NULL DEFAULT FALSE,
    reminder_notifications_enabled BOOLEAN      NOT NULL DEFAULT TRUE,
    created_date                   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_date                   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_user_profiles PRIMARY KEY (id),
    CONSTRAINT uq_user_profiles_keycloak_id UNIQUE (keycloak_id)
);
```

- [ ] **Step 2: Delete V2 migration file**

Delete `backend/src/main/resources/db/migration/V2__Gtd_enhancements.sql`.

- [ ] **Step 3: Verify migration compiles**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/resources/db/migration/
git commit -m "refactor: consolidate migrations into single V1, add user_profiles table"
```

---

### Task 2: UserProfile Entity and Repository

**Files:**
- Create: `backend/src/main/java/app/guad/feature/profile/UserProfile.java`
- Create: `backend/src/main/java/app/guad/feature/profile/UserProfileRepository.java`

- [ ] **Step 1: Create the UserProfile entity**

```java
package app.guad.feature.profile;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID keycloakId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String timezone = "Europe/Berlin";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek defaultReviewDay = DayOfWeek.SATURDAY;

    @Column(nullable = false)
    private boolean energyTrackingEnabled = true;

    @Column(nullable = false)
    private boolean emailDigestsEnabled = false;

    @Column(nullable = false)
    private boolean reminderNotificationsEnabled = true;

    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @Column(nullable = false)
    private Instant updatedDate;

    @PrePersist
    void onCreate() {
        createdDate = Instant.now();
        updatedDate = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedDate = Instant.now();
    }

    // Getters and setters for all fields
    // (id, keycloakId, email, displayName, timezone, defaultReviewDay,
    //  energyTrackingEnabled, emailDigestsEnabled, reminderNotificationsEnabled,
    //  createdDate, updatedDate)
}
```

- [ ] **Step 2: Create the repository**

```java
package app.guad.feature.profile;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends CrudRepository<UserProfile, Long> {
    Optional<UserProfile> findByKeycloakId(UUID keycloakId);
}
```

- [ ] **Step 3: Verify it compiles**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/app/guad/feature/profile/
git commit -m "feat: add UserProfile entity and repository"
```

---

### Task 3: ProfileService with Lazy Creation

**Files:**
- Create: `backend/src/main/java/app/guad/feature/profile/ProfileService.java`
- Create: `backend/src/test/java/app/guad/feature/profile/ProfileServiceTest.java`

- [ ] **Step 1: Write the failing test for lazy profile creation**

```java
package app.guad.feature.profile;

import app.guad.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void getOrCreateProfile_createsNewProfileOnFirstCall() {
        var keycloakId = UUID.randomUUID();
        var profile = profileService.getOrCreateProfile(keycloakId, "user@example.com", "testuser");

        assertThat(profile.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(profile.getEmail()).isEqualTo("user@example.com");
        assertThat(profile.getDisplayName()).isEqualTo("testuser");
        assertThat(profile.getTimezone()).isEqualTo("Europe/Berlin");
        assertThat(profile.getDefaultReviewDay()).isEqualTo(java.time.DayOfWeek.SATURDAY);
        assertThat(profile.isEnergyTrackingEnabled()).isTrue();
        assertThat(profile.isEmailDigestsEnabled()).isFalse();
        assertThat(profile.isReminderNotificationsEnabled()).isTrue();
        assertThat(profile.getCreatedDate()).isNotNull();
    }

    @Test
    void getOrCreateProfile_returnsExistingProfileOnSubsequentCalls() {
        var keycloakId = UUID.randomUUID();
        var first = profileService.getOrCreateProfile(keycloakId, "user@example.com", "testuser");
        var second = profileService.getOrCreateProfile(keycloakId, "user@example.com", "testuser");

        assertThat(first.getId()).isEqualTo(second.getId());
        assertThat(userProfileRepository.count()).isEqualTo(1);
    }

    @Test
    void getOrCreateProfile_updatesCachedFieldsWhenChanged() {
        var keycloakId = UUID.randomUUID();
        profileService.getOrCreateProfile(keycloakId, "old@example.com", "olduser");
        var updated = profileService.getOrCreateProfile(keycloakId, "new@example.com", "newuser");

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getDisplayName()).isEqualTo("newuser");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -pl . -Dtest="ProfileServiceTest" -q`
Expected: FAIL (ProfileService class not found)

- [ ] **Step 3: Implement ProfileService**

```java
package app.guad.feature.profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService {

    private final UserProfileRepository userProfileRepository;

    public ProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfile getOrCreateProfile(UUID keycloakId, String email, String displayName) {
        var existing = userProfileRepository.findByKeycloakId(keycloakId);
        if (existing.isPresent()) {
            var profile = existing.get();
            boolean changed = false;
            if (!profile.getEmail().equals(email)) {
                profile.setEmail(email);
                changed = true;
            }
            if (!profile.getDisplayName().equals(displayName)) {
                profile.setDisplayName(displayName);
                changed = true;
            }
            return changed ? userProfileRepository.save(profile) : profile;
        }

        var profile = new UserProfile();
        profile.setKeycloakId(keycloakId);
        profile.setEmail(email);
        profile.setDisplayName(displayName);
        return userProfileRepository.save(profile);
    }

    public UserProfile getProfileByKeycloakId(UUID keycloakId) {
        return userProfileRepository.findByKeycloakId(keycloakId).orElse(null);
    }

    @Transactional
    public UserProfile updateSettings(UUID keycloakId, String timezone, java.time.DayOfWeek defaultReviewDay,
                                       boolean energyTrackingEnabled, boolean emailDigestsEnabled,
                                       boolean reminderNotificationsEnabled) {
        var profile = userProfileRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new app.guad.core.ResourceNotFoundException("Profile"));
        profile.setTimezone(timezone);
        profile.setDefaultReviewDay(defaultReviewDay);
        profile.setEnergyTrackingEnabled(energyTrackingEnabled);
        profile.setEmailDigestsEnabled(emailDigestsEnabled);
        profile.setReminderNotificationsEnabled(reminderNotificationsEnabled);
        return userProfileRepository.save(profile);
    }
}
```

Note: `ResourceNotFoundException` currently only accepts `Long` id. Update it to also accept null:

Modify `backend/src/main/java/app/guad/core/ResourceNotFoundException.java` — add a second constructor:

```java
public ResourceNotFoundException(String resource) {
    super(resource + " not found");
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd backend && mvn test -pl . -Dtest="ProfileServiceTest" -q`
Expected: PASS (3 tests)

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/app/guad/feature/profile/ProfileService.java \
       backend/src/main/java/app/guad/core/ResourceNotFoundException.java \
       backend/src/test/java/app/guad/feature/profile/ProfileServiceTest.java
git commit -m "feat: add ProfileService with lazy creation and update logic"
```

---

### Task 4: UserProfile Interceptor

**Files:**
- Create: `backend/src/main/java/app/guad/feature/profile/UserProfileInterceptor.java`
- Create: `backend/src/main/java/app/guad/feature/profile/ProfileWebConfig.java`
- Create: `backend/src/test/java/app/guad/feature/profile/UserProfileInterceptorTest.java`

- [ ] **Step 1: Write the failing test for the interceptor**

The interceptor should auto-create a profile when an authenticated JWT request hits `/api/**`. We test this indirectly: after making any authenticated API call, a profile should exist.

```java
package app.guad.feature.profile;

import app.guad.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserProfileInterceptorTest extends BaseIntegrationTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void authenticatedApiRequest_createsProfileAutomatically() throws Exception {
        // Hit any existing API endpoint — interceptor should create profile
        mockMvc.perform(get("/api/areas").with(userJwt()))
            .andExpect(status().isOk());

        var profile = userProfileRepository.findByKeycloakId(testUserId);
        assertThat(profile).isPresent();
        assertThat(profile.get().getEmail()).isEqualTo("test@example.com");
        assertThat(profile.get().getDisplayName()).isEqualTo("testuser");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && mvn test -pl . -Dtest="UserProfileInterceptorTest" -q`
Expected: FAIL (no interceptor registered yet)

- [ ] **Step 3: Create the interceptor**

```java
package app.guad.feature.profile;

import app.guad.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserProfileInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserProfileInterceptor.class);
    private final ProfileService profileService;

    public UserProfileInterceptor(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return true;
        }

        try {
            var user = AuthenticatedUser.from((Jwt) jwtAuth.getPrincipal());
            var profile = profileService.getOrCreateProfile(
                user.id(), user.email(), user.preferredUsername());
            request.setAttribute("userProfile", profile);
        } catch (Exception e) {
            log.warn("Failed to create/load user profile: {}", e.getMessage());
        }

        return true;
    }
}
```

- [ ] **Step 4: Register the interceptor for /api/** only**

```java
package app.guad.feature.profile;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProfileWebConfig implements WebMvcConfigurer {

    private final UserProfileInterceptor userProfileInterceptor;

    public ProfileWebConfig(UserProfileInterceptor userProfileInterceptor) {
        this.userProfileInterceptor = userProfileInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userProfileInterceptor)
            .addPathPatterns("/api/**");
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `cd backend && mvn test -pl . -Dtest="UserProfileInterceptorTest" -q`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/app/guad/feature/profile/UserProfileInterceptor.java \
       backend/src/main/java/app/guad/feature/profile/ProfileWebConfig.java \
       backend/src/test/java/app/guad/feature/profile/UserProfileInterceptorTest.java
git commit -m "feat: add UserProfile interceptor for lazy profile creation on /api/ routes"
```

---

### Task 5: Profile REST API

**Files:**
- Create: `backend/src/main/java/app/guad/feature/profile/api/ProfileResponse.java`
- Create: `backend/src/main/java/app/guad/feature/profile/api/UpdateSettingsRequest.java`
- Create: `backend/src/main/java/app/guad/feature/profile/api/ProfileController.java`
- Create: `backend/src/test/java/app/guad/feature/profile/api/ProfileControllerTest.java`

- [ ] **Step 1: Write the failing tests**

```java
package app.guad.feature.profile.api;

import app.guad.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileControllerTest extends BaseIntegrationTest {

    @Test
    void getProfile_returnsProfileWithDefaults() throws Exception {
        // Interceptor creates profile automatically on first request
        mockMvc.perform(get("/api/profile").with(userJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.displayName").value("testuser"))
            .andExpect(jsonPath("$.timezone").value("Europe/Berlin"))
            .andExpect(jsonPath("$.defaultReviewDay").value("SATURDAY"))
            .andExpect(jsonPath("$.energyTrackingEnabled").value(true))
            .andExpect(jsonPath("$.emailDigestsEnabled").value(false))
            .andExpect(jsonPath("$.reminderNotificationsEnabled").value(true))
            .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void updateSettings_updatesPreferences() throws Exception {
        // First call creates profile via interceptor
        mockMvc.perform(get("/api/profile").with(userJwt()))
            .andExpect(status().isOk());

        mockMvc.perform(put("/api/profile/settings")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "timezone": "America/New_York",
                        "defaultReviewDay": "SUNDAY",
                        "energyTrackingEnabled": false,
                        "emailDigestsEnabled": true,
                        "reminderNotificationsEnabled": false
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.timezone").value("America/New_York"))
            .andExpect(jsonPath("$.defaultReviewDay").value("SUNDAY"))
            .andExpect(jsonPath("$.energyTrackingEnabled").value(false))
            .andExpect(jsonPath("$.emailDigestsEnabled").value(true))
            .andExpect(jsonPath("$.reminderNotificationsEnabled").value(false));
    }

    @Test
    void updateSettings_invalidTimezone_returns400() throws Exception {
        mockMvc.perform(get("/api/profile").with(userJwt()))
            .andExpect(status().isOk());

        mockMvc.perform(put("/api/profile/settings")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "timezone": "Not/A/Timezone",
                        "defaultReviewDay": "SATURDAY",
                        "energyTrackingEnabled": true,
                        "emailDigestsEnabled": false,
                        "reminderNotificationsEnabled": true
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/profile"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void settingsAreIsolatedPerUser() throws Exception {
        // User A updates settings
        mockMvc.perform(get("/api/profile").with(userJwt()))
            .andExpect(status().isOk());
        mockMvc.perform(put("/api/profile/settings")
                .with(userJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "timezone": "Asia/Tokyo",
                        "defaultReviewDay": "MONDAY",
                        "energyTrackingEnabled": false,
                        "emailDigestsEnabled": true,
                        "reminderNotificationsEnabled": false
                    }
                    """))
            .andExpect(status().isOk());

        // User B should still have defaults
        var otherUserId = UUID.randomUUID();
        mockMvc.perform(get("/api/profile").with(userJwt(otherUserId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.timezone").value("Europe/Berlin"))
            .andExpect(jsonPath("$.defaultReviewDay").value("SATURDAY"))
            .andExpect(jsonPath("$.energyTrackingEnabled").value(true));
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd backend && mvn test -pl . -Dtest="ProfileControllerTest" -q`
Expected: FAIL (classes not found)

- [ ] **Step 3: Create ProfileResponse DTO**

```java
package app.guad.feature.profile.api;

import app.guad.feature.profile.UserProfile;
import java.time.DayOfWeek;
import java.time.Instant;

public record ProfileResponse(
    String email,
    String displayName,
    String timezone,
    DayOfWeek defaultReviewDay,
    boolean energyTrackingEnabled,
    boolean emailDigestsEnabled,
    boolean reminderNotificationsEnabled,
    Instant createdDate,
    Instant updatedDate
) {
    public static ProfileResponse from(UserProfile profile) {
        return new ProfileResponse(
            profile.getEmail(),
            profile.getDisplayName(),
            profile.getTimezone(),
            profile.getDefaultReviewDay(),
            profile.isEnergyTrackingEnabled(),
            profile.isEmailDigestsEnabled(),
            profile.isReminderNotificationsEnabled(),
            profile.getCreatedDate(),
            profile.getUpdatedDate()
        );
    }
}
```

- [ ] **Step 4: Create UpdateSettingsRequest DTO with validation**

```java
package app.guad.feature.profile.api;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;

public record UpdateSettingsRequest(
    @NotNull String timezone,
    @NotNull DayOfWeek defaultReviewDay,
    @NotNull Boolean energyTrackingEnabled,
    @NotNull Boolean emailDigestsEnabled,
    @NotNull Boolean reminderNotificationsEnabled
) {}
```

- [ ] **Step 5: Create ProfileController**

```java
package app.guad.feature.profile.api;

import app.guad.feature.profile.ProfileService;
import app.guad.feature.profile.UserProfile;
import app.guad.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

@RestController
@RequestMapping("/api/profile")
class ProfileController {

    private final ProfileService profileService;

    ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        var user = AuthenticatedUser.from(jwt);
        var profile = profileService.getProfileByKeycloakId(user.id());
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PutMapping("/settings")
    ResponseEntity<ProfileResponse> updateSettings(
            @Valid @RequestBody UpdateSettingsRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        // Validate timezone
        try {
            ZoneId.of(request.timezone());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        var user = AuthenticatedUser.from(jwt);
        var profile = profileService.updateSettings(
            user.id(),
            request.timezone(),
            request.defaultReviewDay(),
            request.energyTrackingEnabled(),
            request.emailDigestsEnabled(),
            request.reminderNotificationsEnabled()
        );
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }
}
```

- [ ] **Step 6: Run all profile tests**

Run: `cd backend && mvn test -pl . -Dtest="ProfileControllerTest,ProfileServiceTest,UserProfileInterceptorTest" -q`
Expected: PASS (all tests)

- [ ] **Step 7: Run full test suite to check for regressions**

Run: `cd backend && mvn test -q`
Expected: PASS (all existing tests still pass)

- [ ] **Step 8: Commit**

```bash
git add backend/src/main/java/app/guad/feature/profile/api/ \
       backend/src/test/java/app/guad/feature/profile/api/
git commit -m "feat: add profile REST API with GET and PUT settings endpoints"
```

---

### Task 6: Keycloak Registration Configuration

This task is manual Keycloak admin configuration, not code. Document the steps as a checklist.

- [ ] **Step 1: Enable self-service registration in Keycloak**

In Keycloak Admin Console (http://localhost:8081/admin):
1. Go to **Realm Settings → Login**
2. Enable **User registration** toggle
3. Enable **Verify email** toggle
4. Click **Save**

- [ ] **Step 2: Configure SMTP for email verification**

In Keycloak Admin Console:
1. Go to **Realm Settings → Email**
2. Configure SMTP settings (host, port, from address, auth credentials)
3. Click **Test connection** to verify
4. Click **Save**

- [ ] **Step 3: Verify registration flow works**

1. Open Guad's login page (which redirects to Keycloak)
2. Verify a "Register" link appears on the login page
3. Click Register → fill in email + password → submit
4. Check for verification email (if SMTP configured) or verify in Keycloak admin
5. After verification, user should be redirected back to Guad
6. Verify the `user_profiles` table has a new row for the registered user

- [ ] **Step 4: Commit a note about Keycloak configuration**

No code to commit — this is runtime configuration. Optionally add a note to the project's README or a `docs/keycloak-setup.md` if one exists.
