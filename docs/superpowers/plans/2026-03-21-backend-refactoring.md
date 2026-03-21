# Backend Refactoring Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor all backend features to a consistent package structure with compiler-enforced boundaries, add auditing, API response envelope, bean validation, and comprehensive tests.

**Architecture:** Each feature has `api/` and `admin/` sub-packages (package-private contents) with entity, repository, specifications, and service at the feature root. Repositories are package-private so the compiler prevents cross-feature access. Services are public and serve as feature entry points.

**Tech Stack:** Spring Boot 4.0.3, Java 25, PostgreSQL, Testcontainers, Mockito, Spring Data JPA Auditing

**Spec:** `docs/superpowers/specs/2026-03-21-backend-refactoring-design.md`

**Prerequisites:** `@EnableJpaAuditing` is already present on `GuadApplication.java`. `spring-boot-starter-validation` is already a dependency.

**Important notes for implementers:**
- **Step ordering within tasks is strict.** When making repos package-private, you MUST update controllers/services to remove direct repo usage FIRST, then change visibility LAST. Otherwise the code won't compile between steps.
- **Admin controllers use Specifications for search/filter.** After specs become package-private, admin controllers (in `admin/` sub-package) can't access them. Each service must expose `search(filterParams, Pageable)` methods that internally use Specifications. Admin controllers call the service method instead.
- **Renaming `createdDate` → `createdAt` is a breaking API change.** Since this is pre-alpha, this is acceptable but the frontend needs updating too.
- **Use `TIMESTAMPTZ` for all audit columns** in the schema to match `java.time.Instant` semantics.
- **Mappers:** Features with a single mapper at the root (Action, Area, Context, etc.) that is used by the admin controller — move the mapper to `admin/`. API controllers already use static `from()` factory methods on the response records, so no separate API mapper is needed.
- **`InboxProcessingService` uses `InboxRepository` directly** — this is fine because they share the same package.

---

## File Structure Overview

### Shared Infrastructure (new/modified)
- Create: `backend/src/main/java/app/guad/core/AuditMetadata.java`
- Create: `backend/src/main/java/app/guad/core/ApiResponse.java`
- Create: `backend/src/main/java/app/guad/core/SecurityAuditorAware.java`
- Modify: `backend/src/main/java/app/guad/core/GlobalExceptionHandler.java`
- Modify: `backend/src/main/resources/db/migration/V1__Schema.sql`

### Per-Feature Pattern (example: Inbox)
After refactoring, each feature looks like:
```
feature/inbox/
    api/
        InboxRestController.java      (package-private, exists)
        InboxItemResponse.java        (package-private, exists)
        CreateInboxItemRequest.java    (package-private, exists)
        ProcessInboxItemRequest.java   (package-private, exists)
    admin/
        InboxAdminController.java     (package-private, exists)
        InboxItemMapper.java          (package-private, exists)
        GetInboxItemViewModel.java    (package-private, exists)
        InboxItemDetailsViewModel.java (package-private, exists)
        DeleteInboxItemViewModel.java (package-private, exists)
    InboxItem.java                    (public, exists — add AuditMetadata)
    InboxItemStatus.java              (public, exists)
    InboxRepository.java              (package-private, exists — change visibility)
    InboxItemSpecifications.java      (package-private, exists — change visibility)
    InboxService.java                 (public, exists)
    InboxProcessingService.java       (public, exists)
```

---

## Task 1: Create AuditMetadata Embeddable

**Files:**
- Create: `backend/src/main/java/app/guad/core/AuditMetadata.java`
- Create: `backend/src/test/java/app/guad/core/AuditMetadataTest.java`

- [ ] **Step 1: Write the test for AuditMetadata**

```java
package app.guad.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuditMetadataTest {

    @Test
    void newInstance_hasNullFields() {
        var audit = new AuditMetadata();
        assertNull(audit.getCreatedAt());
        assertNull(audit.getUpdatedAt());
        assertNull(audit.getCreatedBy());
        assertNull(audit.getUpdatedBy());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.core.AuditMetadataTest" -Dsurefire.failIfNoSpecifiedTests=false`
Expected: FAIL — class does not exist

- [ ] **Step 3: Create AuditMetadata**

```java
package app.guad.core;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Embeddable
public class AuditMetadata {

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.core.AuditMetadataTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/app/guad/core/AuditMetadata.java backend/src/test/java/app/guad/core/AuditMetadataTest.java
git commit -m "feat: add AuditMetadata embeddable for JPA auditing"
```

---

## Task 2: Create SecurityAuditorAware

**Files:**
- Create: `backend/src/main/java/app/guad/core/SecurityAuditorAware.java`
- Create: `backend/src/test/java/app/guad/core/SecurityAuditorAwareTest.java`

- [ ] **Step 1: Write the test**

```java
package app.guad.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityAuditorAwareTest {

    private final SecurityAuditorAware auditorAware = new SecurityAuditorAware();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsUsername_whenAuthenticated() {
        var auth = new TestingAuthenticationToken("user-123", null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        var auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isPresent());
        assertEquals("user-123", auditor.get());
    }

    @Test
    void returnsEmpty_whenNoAuthentication() {
        var auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isEmpty());
    }

    @Test
    void returnsEmpty_whenAnonymous() {
        var auth = new TestingAuthenticationToken("anonymousUser", null);
        auth.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        var auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isEmpty());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.core.SecurityAuditorAwareTest"`
Expected: FAIL — class does not exist

- [ ] **Step 3: Create SecurityAuditorAware**

```java
package app.guad.core;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.core.SecurityAuditorAwareTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/app/guad/core/SecurityAuditorAware.java backend/src/test/java/app/guad/core/SecurityAuditorAwareTest.java
git commit -m "feat: add SecurityAuditorAware for JPA auditing"
```

---

## Task 3: Create ApiResponse Envelope

**Files:**
- Create: `backend/src/main/java/app/guad/core/ApiResponse.java`
- Create: `backend/src/test/java/app/guad/core/ApiResponseTest.java`

- [ ] **Step 1: Write the test**

```java
package app.guad.core;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void of_singleItem_hasNullMeta() {
        var response = ApiResponse.of("hello");
        assertEquals("hello", response.data());
        assertNull(response.meta());
    }

    @Test
    void of_page_includesPageMeta() {
        var page = new PageImpl<>(List.of("a", "b"), PageRequest.of(0, 10), 25);
        var response = ApiResponse.of(List.of("a", "b"), page);

        assertEquals(List.of("a", "b"), response.data());
        assertNotNull(response.meta());
        assertEquals(0, response.meta().page());
        assertEquals(10, response.meta().size());
        assertEquals(25, response.meta().totalElements());
        assertEquals(3, response.meta().totalPages());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.core.ApiResponseTest"`
Expected: FAIL — class does not exist

- [ ] **Step 3: Create ApiResponse**

```java
package app.guad.core;

import org.springframework.data.domain.Page;

public record ApiResponse<T>(T data, PageMeta meta) {

    public record PageMeta(int page, int size, long totalElements, int totalPages) {}

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> of(T data, Page<?> page) {
        return new ApiResponse<>(data, new PageMeta(
            page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages()
        ));
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.core.ApiResponseTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/app/guad/core/ApiResponse.java backend/src/test/java/app/guad/core/ApiResponseTest.java
git commit -m "feat: add ApiResponse envelope for consistent REST responses"
```

---

## Task 4: Update V1 Schema with Audit Columns

**Files:**
- Modify: `backend/src/main/resources/db/migration/V1__Schema.sql`

- [ ] **Step 1: Add audit columns to all entity tables**

Add `created_at`, `updated_at`, `created_by`, `updated_by` columns to all tables. Replace existing `created_date`/`updated_date` columns. Tables to update:

- `actions`: replace `created_date`/`updated_date` with `created_at`/`updated_at`, add `created_by`/`updated_by`
- `inbox_items`: replace `created_date`/`updated_date` with `created_at`/`updated_at`, add `created_by`/`updated_by`
- `projects`: replace `created_date`/`updated_date` with `created_at`/`updated_at`, add `created_by`/`updated_by`
- `waiting_for_items`: replace `created_date`/`updated_date` with `created_at`/`updated_at`, add `created_by`/`updated_by`
- `attachments`: replace `uploaded_date` with `created_at`/`updated_at`, add `created_by`/`updated_by`
- `user_profiles`: replace `created_date`/`updated_date` with `created_at`/`updated_at`, add `created_by`/`updated_by`
- `areas`: add `created_at`/`updated_at`/`created_by`/`updated_by` (currently has none)
- `contexts`: add `created_at`/`updated_at`/`created_by`/`updated_by` (currently has none)
- `documents`: add `created_at`/`updated_at`/`created_by`/`updated_by` (currently has none)
- `weekly_reviews`: add `created_at`/`updated_at`/`created_by`/`updated_by` (currently has none)

For each table, the pattern is:
```sql
created_at   TIMESTAMPTZ,
updated_at   TIMESTAMPTZ,
created_by   VARCHAR(255),
updated_by   VARCHAR(255),
```

- [ ] **Step 2: Verify app starts with updated schema**

Run: `cd backend && ./mvnw spring-boot:run` (or run existing tests)
Expected: App starts or tests pass with new schema

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/db/migration/V1__Schema.sql
git commit -m "refactor: update V1 schema with audit columns (created_at, updated_at, created_by, updated_by)"
```

---

## Task 5: Refactor Inbox Feature — Package Structure & Visibility

The Inbox feature is already closest to the target structure (has `api/` and `admin/` sub-packages). Main changes: make repository and specifications package-private, add AuditMetadata to entity, remove direct repository usage from controller.

**Files:**
- Modify: `backend/src/main/java/app/guad/feature/inbox/InboxItem.java`
- Modify: `backend/src/main/java/app/guad/feature/inbox/InboxRepository.java`
- Modify: `backend/src/main/java/app/guad/feature/inbox/InboxItemSpecifications.java`
- Modify: `backend/src/main/java/app/guad/feature/inbox/InboxService.java`
- Modify: `backend/src/main/java/app/guad/feature/inbox/InboxProcessingService.java`
- Modify: `backend/src/main/java/app/guad/feature/inbox/api/InboxRestController.java`
- Modify: `backend/src/main/java/app/guad/feature/inbox/api/InboxItemResponse.java`
- Modify: `backend/src/main/java/app/guad/feature/inbox/api/CreateInboxItemRequest.java`
- Modify: `backend/src/main/java/app/guad/feature/inbox/admin/InboxItemMapper.java`

- [ ] **Step 1: Add AuditMetadata to InboxItem entity**

In `InboxItem.java`:
- Add `@EntityListeners(AuditingEntityListener.class)` to the class (NOTE: this also fixes a bug — the existing `@CreatedDate`/`@LastModifiedDate` annotations were never working without the entity listener)
- Remove `createdDate` and `updatedDate` fields and their getters/setters
- Remove `@CreatedDate` and `@LastModifiedDate` imports
- Add `@Embedded private AuditMetadata audit = new AuditMetadata();` with getter/setter

- [ ] **Step 2: Add missing methods to InboxService (MUST happen before making repo package-private)**

The `InboxRestController` currently uses `inboxRepository` directly for `findAllByUserIdAndStatus` and `findByIdAndUserId`. Add these methods to `InboxService`:

```java
public List<InboxItem> getUnprocessedByUserId(UUID userId) {
    return inboxRepository.findAllByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED);
}

public Optional<InboxItem> getByIdAndUserId(Long id, UUID userId) {
    return inboxRepository.findByIdAndUserId(id, userId);
}

public long countByUserIdAndStatus(UUID userId, InboxItemStatus status) {
    return inboxRepository.countByUserIdAndStatus(userId, status);
}
```

Also add a search method for the admin controller (which currently uses `InboxItemSpecifications` directly — after specs become package-private, admin can't access them):

```java
public Page<InboxItem> search(String title, InboxItemStatus status, Pageable pageable) {
    var spec = Specification.allOf(
        InboxItemSpecifications.byTitle(title),
        InboxItemSpecifications.byStatus(status)
    );
    return inboxRepository.findAll(spec, pageable);
}
```

- [ ] **Step 3: Update InboxRestController to use service instead of repository**

Remove `InboxRepository` injection. Replace all direct repository calls with service calls:

```java
@RestController
@RequestMapping("/api/inbox")
class InboxRestController {

    private final InboxService inboxService;
    private final InboxProcessingService inboxProcessingService;

    InboxRestController(InboxService inboxService, InboxProcessingService inboxProcessingService) {
        this.inboxService = inboxService;
        this.inboxProcessingService = inboxProcessingService;
    }

    @PostMapping
    ResponseEntity<InboxItemResponse> create(@Valid @RequestBody CreateInboxItemRequest request,
                                             @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var item = new InboxItem();
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setStatus(InboxItemStatus.UNPROCESSED);
        item.setUserId(userId);
        var saved = inboxService.save(item);
        return ResponseEntity.created(URI.create("/api/inbox/" + saved.getId()))
            .body(InboxItemResponse.from(saved));
    }

    @GetMapping
    List<InboxItemResponse> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return inboxService.getUnprocessedByUserId(userId).stream()
            .map(InboxItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    InboxItemResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        return inboxService.getByIdAndUserId(id, userId)
            .map(InboxItemResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("InboxItem", id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        inboxService.getByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("InboxItem", id));
        inboxService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/process")
    ResponseEntity<Void> process(@PathVariable Long id,
                                 @Valid @RequestBody ProcessInboxItemRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        inboxProcessingService.process(id, request, userId);
        return ResponseEntity.ok().build();
    }
}
```

- [ ] **Step 4: Update InboxAdminController to use service search method**

Replace direct `InboxItemSpecifications` usage with the new service `search()` method:

```java
@GetMapping
public String list(Model model, Pageable pageable,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) InboxItemStatus status) {
    var paginatedData = this.inboxService.search(search, status, pageable);
    var inboxItems = paginatedData.stream()
            .map(InboxItemMapper::toGetInboxItemViewModel).toList();
    // ... rest unchanged
}
```

- [ ] **Step 5: NOW make InboxRepository package-private**

In `InboxRepository.java`, change `public interface` to `interface`. Safe now because no code outside the package uses it.

- [ ] **Step 6: Make InboxItemSpecifications package-private**

Change `public class InboxItemSpecifications` to `class InboxItemSpecifications`.

- [ ] **Step 7: Update InboxProcessingService — replace ContextRepository with ContextService**

In `InboxProcessingService.java`, replace `ContextRepository` injection with `ContextService`. This requires `ContextService` to have a `findById` method. If it doesn't exist yet, add a temporary `public` accessor or handle this in Task 12 (Context feature refactoring). For now, keep `ContextRepository` usage and note it as a cross-feature violation to fix when Context is refactored.

- [ ] **Step 8: Update InboxItemResponse to use audit fields**

```java
public record InboxItemResponse(Long id, String title, String description, String status, Instant createdAt) {
    public static InboxItemResponse from(InboxItem item) {
        return new InboxItemResponse(item.getId(), item.getTitle(), item.getDescription(),
            item.getStatus().name(), item.getAudit().getCreatedAt());
    }
}
```

- [ ] **Step 9: Update admin InboxItemMapper to use audit fields**

Update `InboxItemMapper.toInboxItemDetailsViewModel` to use `item.getAudit().getCreatedAt()` instead of `item.getCreatedDate()`, etc.

- [ ] **Step 10: Add bean validation to CreateInboxItemRequest**

Already has `@NotBlank` on title. Verify `@Valid` is on the controller parameter (it already is). No changes needed.

- [ ] **Step 11: Verify compilation**

Run: `cd backend && ./mvnw compile`
Expected: SUCCESS — no compilation errors

- [ ] **Step 12: Commit**

```bash
cd backend && git add -A && git commit -m "refactor(inbox): restructure to target package pattern with package-private repos"
```

---

## Task 6: Inbox Feature — Unit Tests for InboxService

**Files:**
- Create: `backend/src/test/java/app/guad/feature/inbox/InboxServiceTest.java`

- [ ] **Step 1: Write unit tests for InboxService**

```java
package app.guad.feature.inbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxServiceTest {

    @Mock
    InboxRepository inboxRepository;

    @InjectMocks
    InboxService inboxService;

    @Test
    void save_newItem_delegatesToRepository() {
        var item = new InboxItem();
        item.setTitle("Test");
        when(inboxRepository.save(any())).thenReturn(item);

        var result = inboxService.save(item);
        assertEquals("Test", result.getTitle());
        verify(inboxRepository).save(item);
    }

    @Test
    void save_existingItem_mergesFields() {
        var existing = new InboxItem();
        existing.setId(1L);
        existing.setTitle("Old");
        when(inboxRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(inboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var updated = new InboxItem();
        updated.setId(1L);
        updated.setTitle("New");
        updated.setStatus(InboxItemStatus.PROCESSED);

        var result = inboxService.save(updated);
        assertEquals("New", result.getTitle());
        assertEquals(InboxItemStatus.PROCESSED, result.getStatus());
    }

    @Test
    void save_nonExistentId_throwsException() {
        var item = new InboxItem();
        item.setId(999L);
        when(inboxRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> inboxService.save(item));
    }

    @Test
    void getUnprocessedByUserId_delegatesToRepository() {
        var userId = UUID.randomUUID();
        var item = new InboxItem();
        item.setTitle("Unprocessed");
        when(inboxRepository.findAllByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED))
            .thenReturn(List.of(item));

        var result = inboxService.getUnprocessedByUserId(userId);
        assertEquals(1, result.size());
        assertEquals("Unprocessed", result.getFirst().getTitle());
    }

    @Test
    void getByIdAndUserId_found_returnsItem() {
        var userId = UUID.randomUUID();
        var item = new InboxItem();
        item.setId(1L);
        when(inboxRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(item));

        var result = inboxService.getByIdAndUserId(1L, userId);
        assertTrue(result.isPresent());
    }

    @Test
    void getByIdAndUserId_notFound_returnsEmpty() {
        var userId = UUID.randomUUID();
        when(inboxRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());

        var result = inboxService.getByIdAndUserId(1L, userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_delegatesToRepository() {
        inboxService.deleteById(1L);
        verify(inboxRepository).deleteById(1L);
    }
}
```

- [ ] **Step 2: Run tests to verify they pass**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.feature.inbox.InboxServiceTest"`
Expected: PASS — all 7 tests

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/app/guad/feature/inbox/InboxServiceTest.java
git commit -m "test(inbox): add unit tests for InboxService"
```

---

## Task 7: Inbox Feature — Integration Tests for Controllers

**Files:**
- Modify: `backend/src/test/java/app/guad/feature/inbox/api/InboxRestControllerTest.java`

- [ ] **Step 1: Update existing integration tests**

The existing `InboxRestControllerTest` already covers the main flows. Update assertions to match new response format if `ApiResponse` wrapping was applied. If wrapping is not yet applied (deferred to per-feature wrap step), verify existing tests still pass.

- [ ] **Step 2: Run all Inbox tests**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.feature.inbox.**"`
Expected: PASS — all unit and integration tests

- [ ] **Step 3: Run full test suite to check nothing broke**

Run: `cd backend && ./mvnw test`
Expected: PASS — all tests across the project

- [ ] **Step 4: Commit if any test changes were needed**

```bash
git add backend/src/test/java/app/guad/feature/inbox/
git commit -m "test(inbox): update integration tests after refactoring"
```

---

## Task 8: Refactor Action Feature

The most complex feature. Has `api/` already. Needs: move admin classes to `admin/`, make repo/specs package-private, add AuditMetadata, add service methods to replace direct repo usage, add bean validation.

**Files:**
- Modify: `backend/src/main/java/app/guad/feature/action/Action.java`
- Modify: `backend/src/main/java/app/guad/feature/action/ActionRepository.java`
- Modify: `backend/src/main/java/app/guad/feature/action/ActionSpecifications.java`
- Modify: `backend/src/main/java/app/guad/feature/action/ActionService.java`
- Move to `admin/`: `ActionAdminController.java`, `ActionMapper.java`, `ActionDetailsViewModel.java`, `GetActionViewModel.java`, `DeleteActionViewModel.java`
- Modify: `backend/src/main/java/app/guad/feature/action/api/CreateActionRequest.java`
- Modify: `backend/src/main/java/app/guad/feature/action/api/ActionResponse.java`

- [ ] **Step 1: Create `admin/` sub-package and move admin classes**

Move the following files from `feature/action/` to `feature/action/admin/`:
- `ActionAdminController.java` → update package declaration to `app.guad.feature.action.admin`
- `ActionMapper.java` → update package declaration
- `ActionDetailsViewModel.java` → update package declaration
- `GetActionViewModel.java` → update package declaration
- `DeleteActionViewModel.java` → update package declaration

Update imports in `ActionAdminController` to reference classes from parent package (`Action`, `ActionService`, `ActionSpecifications`).

- [ ] **Step 2: Add AuditMetadata to Action entity**

In `Action.java`:
- Add `@EntityListeners(AuditingEntityListener.class)`
- Remove `createdDate`/`updatedDate` fields and their getters/setters
- Add `@Embedded private AuditMetadata audit = new AuditMetadata();` with getter/setter

- [ ] **Step 3: Add service methods BEFORE making repo package-private**

Add methods to `ActionService` that other features and the admin controller need:

```java
public long countByUserIdAndStatus(UUID userId, ActionStatus status) {
    return actionRepository.countByUserIdAndStatus(userId, status);
}

public List<Action> findAllByUserIdAndStatus(UUID userId, ActionStatus status) {
    return actionRepository.findAllByUserIdAndStatus(userId, status);
}

public Page<Action> search(String description, ActionStatus status, Pageable pageable) {
    var spec = Specification.allOf(
        ActionSpecifications.byDescription(description),
        ActionSpecifications.byStatus(status)
    );
    return actionRepository.findAll(spec, pageable);
}
```

- [ ] **Step 4: Update ActionAdminController (now in `admin/`) to use service search method instead of Specifications directly**

Replace any direct `ActionSpecifications` and `ActionRepository` usage with `actionService.search(...)` calls.

- [ ] **Step 5: NOW make ActionRepository and ActionSpecifications package-private**

Safe because all external access has been replaced with service calls.

- [ ] **Step 6: Update ActionResponse to use audit fields**

Update `ActionResponse.from()` to use `action.getAudit().getCreatedAt()` instead of `action.getCreatedDate()`.

- [ ] **Step 7: Add bean validation to CreateActionRequest**

Add `@NotBlank` to description, `@NotNull` to status (if not already present).

- [ ] **Step 8: Verify compilation**

Run: `cd backend && ./mvnw compile`
Expected: SUCCESS

- [ ] **Step 9: Commit**

```bash
cd backend && git add -A && git commit -m "refactor(action): restructure to target package pattern with admin/ sub-package"
```

---

## Task 9: Action Feature — Tests

**Files:**
- Create: `backend/src/test/java/app/guad/feature/action/ActionServiceTest.java`
- Modify: `backend/src/test/java/app/guad/feature/action/api/ActionRestControllerTest.java`

- [ ] **Step 1: Write unit tests for ActionService**

Follow the same pattern as InboxServiceTest. Cover: save new, save existing (merge), save non-existent (throws), findAll, findById, delete, countByUserIdAndStatus, findAllByUserIdAndStatus.

- [ ] **Step 2: Run unit tests**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.feature.action.ActionServiceTest"`
Expected: PASS

- [ ] **Step 3: Update and run integration tests**

Run: `cd backend && ./mvnw test -pl . -Dtest="app.guad.feature.action.**"`
Expected: PASS

- [ ] **Step 4: Run full test suite**

Run: `cd backend && ./mvnw test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add backend/src/test/java/app/guad/feature/action/
git commit -m "test(action): add unit tests for ActionService, update integration tests"
```

---

## Task 10: Refactor Area Feature

**Files:**
- Modify: `backend/src/main/java/app/guad/feature/area/Area.java`
- Modify: `backend/src/main/java/app/guad/feature/area/AreaRepository.java`
- Modify: `backend/src/main/java/app/guad/feature/area/AreaSpecifications.java`
- Modify: `backend/src/main/java/app/guad/feature/area/AreaService.java`
- Move to `admin/`: `AreaAdminController.java`, `AreaMapper.java`, `AreaDetailsViewModel.java`, `GetAreaViewModel.java`, `DeleteAreaViewModel.java`
- Modify: `backend/src/main/java/app/guad/feature/area/api/CreateAreaRequest.java`

- [ ] **Step 1: Create `admin/` sub-package and move admin classes**

Same pattern as Action: move `AreaAdminController`, `AreaMapper`, `AreaDetailsViewModel`, `GetAreaViewModel`, `DeleteAreaViewModel` to `feature/area/admin/`. Update package declarations and imports.

- [ ] **Step 2: Add AuditMetadata to Area entity**

Add `@EntityListeners`, `@Embedded AuditMetadata`, remove any existing timestamp fields.

- [ ] **Step 3: Add service search method and update admin controller**

Add `search(name, Pageable)` to `AreaService`. Update `AreaAdminController` to use service method instead of `AreaSpecifications` directly.

- [ ] **Step 4: Make AreaRepository and AreaSpecifications package-private (AFTER admin controller is updated)**

- [ ] **Step 5: Add bean validation to CreateAreaRequest**

- [ ] **Step 6: Verify compilation, run tests**

Run: `cd backend && ./mvnw test`

- [ ] **Step 7: Commit**

```bash
cd backend && git add -A && git commit -m "refactor(area): restructure to target package pattern"
```

---

## Task 11: Area Feature — Tests

- [ ] **Step 1: Write unit tests for AreaService**
- [ ] **Step 2: Run unit and integration tests**
- [ ] **Step 3: Run full test suite**
- [ ] **Step 4: Commit**

---

## Task 12: Refactor Context Feature

Same pattern as Area. Move admin classes to `admin/`, make repo/specs package-private, add AuditMetadata, add bean validation.

**Important:** Add a `findById(Long id)` method to `ContextService` so `InboxProcessingService` can use it instead of `ContextRepository` directly. Then update `InboxProcessingService` to replace `ContextRepository` with `ContextService`.

- [ ] **Step 1: Create `admin/`, move admin classes**
- [ ] **Step 2: Add AuditMetadata**
- [ ] **Step 3: Add `findById` to ContextService, add service search method, update admin controller**
- [ ] **Step 4: Update InboxProcessingService to use ContextService instead of ContextRepository**
- [ ] **Step 5: Make repo/specs package-private (AFTER all external usage is removed)**
- [ ] **Step 6: Add bean validation to CreateContextRequest**
- [ ] **Step 7: Verify compilation, run tests**
- [ ] **Step 8: Commit**

---

## Task 13: Context Feature — Tests

- [ ] **Step 1: Write unit tests for ContextService**
- [ ] **Step 2: Run all tests**
- [ ] **Step 3: Commit**

---

## Task 14: Refactor Project Feature

Same pattern. Move admin classes to `admin/`, make repo/specs package-private, add AuditMetadata.

**Important:** Add service methods needed by Dashboard and SomedayMaybe:
```java
public long countByUserIdAndStatus(UUID userId, ProjectStatus status)
public List<Project> findAllByUserIdAndStatus(UUID userId, ProjectStatus status)
```

- [ ] **Step 1: Create `admin/`, move admin classes**
- [ ] **Step 2: Add AuditMetadata**
- [ ] **Step 3: Add service methods for cross-feature access (`countByUserIdAndStatus`, `findAllByUserIdAndStatus`) + search method for admin controller**
- [ ] **Step 4: Update admin controller to use service search method instead of Specifications**
- [ ] **Step 5: Make repo/specs package-private (AFTER admin controller is updated)**
- [ ] **Step 6: Add bean validation to CreateProjectRequest**
- [ ] **Step 7: Verify compilation, run tests**
- [ ] **Step 8: Commit**

---

## Task 15: Project Feature — Tests

- [ ] **Step 1: Write unit tests for ProjectService**
- [ ] **Step 2: Run all tests**
- [ ] **Step 3: Commit**

---

## Task 16: Refactor Attachment Feature

No `api/` package (admin only). Move admin classes to `admin/`, make repo/specs package-private, add AuditMetadata.

- [ ] **Step 1: Create `admin/`, move admin classes** (`AttachmentAdminController`, `AttachmentMapper`, `AttachmentDetailsViewModel`, `AttachmentListItemViewModel`, `GetAttachmentViewModel`, `DeleteAttachmentViewModel`)
- [ ] **Step 2: Add AuditMetadata (replaces existing `uploadedDate` field)**
- [ ] **Step 3: Add service search method, update admin controller**
- [ ] **Step 4: Make repo/specs package-private (AFTER admin controller is updated)**
- [ ] **Step 5: Verify compilation, run tests**
- [ ] **Step 6: Commit**

---

## Task 17: Attachment Feature — Tests

- [ ] **Step 1: Write unit tests for AttachmentService**
- [ ] **Step 2: Run all tests**
- [ ] **Step 3: Commit**

---

## Task 18: Refactor Document Feature

No `api/` package (admin only). Move admin classes to `admin/`, make repo/specs package-private, add AuditMetadata.

- [ ] **Step 1: Create `admin/`, move admin classes** (`DocumentAdminController`, `DocumentMapper`, `DocumentDetailsViewModel`, `GetDocumentViewModel`, `DeleteDocumentViewModel`)
- [ ] **Step 2: Add AuditMetadata**
- [ ] **Step 3: Add service search method, update admin controller**
- [ ] **Step 4: Make repo/specs package-private (AFTER admin controller is updated)**
- [ ] **Step 5: Verify compilation, run tests**
- [ ] **Step 6: Commit**

---

## Task 19: Document Feature — Tests

- [ ] **Step 1: Write unit tests for DocumentService**
- [ ] **Step 2: Run all tests**
- [ ] **Step 3: Commit**

---

## Task 20: Refactor Profile Feature

Has `api/` already. Move admin classes to `admin/`. Keep `ProfileWebConfig` and `UserProfileInterceptor` at feature root. Make repo/specs package-private. Rename `ProfileController` to `ProfileRestController`.

**Important:** Add service methods for cross-feature access:
```java
public Optional<UserProfile> findByKeycloakId(UUID keycloakId)
```

- [ ] **Step 1: Create `admin/`, move admin classes** (`UserProfileAdminController`, `UserProfileMapper`, `UserProfileDetailsViewModel`, `GetUserProfileViewModel`, `DeleteUserProfileViewModel`)
- [ ] **Step 2: Rename `ProfileController` to `ProfileRestController`**
- [ ] **Step 3: Add AuditMetadata (replaces existing `createdDate`/`updatedDate`)**
- [ ] **Step 4: Add service search method, update admin controller**
- [ ] **Step 5: Make repo/specs package-private (AFTER admin controller is updated)**
- [ ] **Step 6: Add bean validation to UpdateSettingsRequest**
- [ ] **Step 7: Verify compilation, run tests**
- [ ] **Step 8: Commit**

---

## Task 21: Profile Feature — Tests

- [ ] **Step 1: Update existing unit tests (ProfileServiceTest, UserProfileInterceptorTest)**
- [ ] **Step 2: Update existing integration test (ProfileControllerTest)**
- [ ] **Step 3: Run all tests**
- [ ] **Step 4: Commit**

---

## Task 22: Refactor WaitingFor Feature

Has `api/` already. No admin. Make repo package-private, add AuditMetadata.

**Important:** Add service methods for Dashboard:
```java
public long countByUserIdAndStatus(UUID userId, WaitingForItemStatus status)
```

- [ ] **Step 1: Add AuditMetadata (replaces existing `createdDate`/`updatedDate`)**
- [ ] **Step 2: Add service methods for cross-feature access (`countByUserIdAndStatus`)**
- [ ] **Step 3: Make WaitingForRepository package-private (AFTER service methods are added)**
- [ ] **Step 4: Add bean validation to CreateWaitingForRequest**
- [ ] **Step 5: Verify compilation, run tests**
- [ ] **Step 6: Commit**

---

## Task 23: WaitingFor Feature — Tests

- [ ] **Step 1: Write unit tests for WaitingForService**
- [ ] **Step 2: Run all tests**
- [ ] **Step 3: Commit**

---

## Task 24: Refactor Review Feature

Has `api/` already. No admin. Make repo package-private, add AuditMetadata.

- [ ] **Step 1: Add AuditMetadata**
- [ ] **Step 2: Verify all methods needed by DashboardService (`getLastCompletedReview`, etc.) are already public service methods**
- [ ] **Step 3: Make WeeklyReviewRepository package-private**
- [ ] **Step 4: Verify compilation, run tests**
- [ ] **Step 5: Commit**

---

## Task 25: Review Feature — Tests

- [ ] **Step 1: Write unit tests for WeeklyReviewService**
- [ ] **Step 2: Run all tests**
- [ ] **Step 3: Commit**

---

## Task 26: Refactor SomedayMaybe Feature — Use Services Instead of Repositories

**Files:**
- Modify: `backend/src/main/java/app/guad/feature/somedaymaybe/api/SomedayMaybeRestController.java`

- [ ] **Step 1: Replace repository usage with service calls**

```java
package app.guad.feature.somedaymaybe.api;

import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.action.api.ActionResponse;
import app.guad.feature.project.ProjectService;
import app.guad.feature.project.ProjectStatus;
import app.guad.feature.project.api.ProjectResponse;
import app.guad.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/someday-maybe")
class SomedayMaybeRestController {

    private final ActionService actionService;
    private final ProjectService projectService;

    SomedayMaybeRestController(ActionService actionService, ProjectService projectService) {
        this.actionService = actionService;
        this.projectService = projectService;
    }

    @GetMapping
    SomedayMaybeResponse list(@AuthenticationPrincipal Jwt jwt) {
        var userId = AuthenticatedUser.from(jwt).id();
        var actions = actionService.findAllByUserIdAndStatus(userId, ActionStatus.SOMEDAY_MAYBE)
            .stream().map(ActionResponse::from).toList();
        var projects = projectService.findAllByUserIdAndStatus(userId, ProjectStatus.SOMEDAY_MAYBE)
            .stream().map(p -> ProjectResponse.from(p, 0)).toList();
        return new SomedayMaybeResponse(actions, projects);
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `cd backend && ./mvnw compile`
Expected: SUCCESS — SomedayMaybeRestController now uses services, not repositories

- [ ] **Step 3: Run all tests**

Run: `cd backend && ./mvnw test`

- [ ] **Step 4: Commit**

```bash
cd backend && git add -A && git commit -m "refactor(somedaymaybe): use services instead of repositories"
```

---

## Task 27: Refactor Dashboard Feature — Use Services Instead of Repositories

**Files:**
- Modify: `backend/src/main/java/app/guad/feature/dashboard/DashboardService.java`

- [ ] **Step 1: Replace all repository injections with service calls**

```java
package app.guad.feature.dashboard;

import app.guad.feature.action.ActionService;
import app.guad.feature.action.ActionStatus;
import app.guad.feature.dashboard.api.DashboardResponse;
import app.guad.feature.inbox.InboxItemStatus;
import app.guad.feature.inbox.InboxService;
import app.guad.feature.project.ProjectService;
import app.guad.feature.project.ProjectStatus;
import app.guad.feature.review.WeeklyReviewService;
import app.guad.feature.waitingfor.WaitingForItemStatus;
import app.guad.feature.waitingfor.WaitingForService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class DashboardService {

    private final InboxService inboxService;
    private final ActionService actionService;
    private final ProjectService projectService;
    private final WaitingForService waitingForService;
    private final WeeklyReviewService weeklyReviewService;

    public DashboardService(InboxService inboxService, ActionService actionService,
                             ProjectService projectService, WaitingForService waitingForService,
                             WeeklyReviewService weeklyReviewService) {
        this.inboxService = inboxService;
        this.actionService = actionService;
        this.projectService = projectService;
        this.waitingForService = waitingForService;
        this.weeklyReviewService = weeklyReviewService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID userId) {
        long inboxCount = inboxService.countByUserIdAndStatus(userId, InboxItemStatus.UNPROCESSED);
        long nextActionsCount = actionService.countByUserIdAndStatus(userId, ActionStatus.NEXT);
        long activeProjectsCount = projectService.countByUserIdAndStatus(userId, ProjectStatus.ACTIVE);
        long waitingForCount = waitingForService.countByUserIdAndStatus(userId, WaitingForItemStatus.WAITING);
        long somedayMaybeCount = actionService.countByUserIdAndStatus(userId, ActionStatus.SOMEDAY_MAYBE);

        var lastReview = weeklyReviewService.getLastCompletedReview(userId).orElse(null);
        Instant lastReviewDate = lastReview != null ? lastReview.getCompletedAt() : null;
        boolean weeklyReviewDue = lastReviewDate == null ||
            lastReviewDate.isBefore(Instant.now().minus(7, ChronoUnit.DAYS));

        return new DashboardResponse(inboxCount, nextActionsCount, activeProjectsCount,
            waitingForCount, somedayMaybeCount, weeklyReviewDue, lastReviewDate);
    }
}
```

- [ ] **Step 2: Move DashboardAdminController to `admin/` sub-package**

Move `DashboardAdminController.java` to `feature/dashboard/admin/`. Update package declaration.

- [ ] **Step 3: Verify compilation, run all tests**

Run: `cd backend && ./mvnw test`

- [ ] **Step 4: Commit**

```bash
cd backend && git add -A && git commit -m "refactor(dashboard): use services instead of repositories, move admin to sub-package"
```

---

## Task 28: Dashboard Feature — Tests

- [ ] **Step 1: Write unit tests for DashboardService**

Mock all service dependencies. Test that it calls the correct service methods and assembles the response correctly.

- [ ] **Step 2: Run all tests**
- [ ] **Step 3: Commit**

---

## Task 29: Refactor Auth Feature

Minimal — just move `AuthAdminController` to `admin/` sub-package.

- [ ] **Step 1: Move AuthAdminController to `feature/auth/admin/`**
- [ ] **Step 2: Verify compilation, run all tests**
- [ ] **Step 3: Commit**

```bash
cd backend && git add -A && git commit -m "refactor(auth): move admin controller to admin/ sub-package"
```

---

## Task 30: Apply ApiResponse Wrapping Across All REST Controllers

Now that all features are refactored, wrap all REST endpoint returns in `ApiResponse<T>`.

**Files to modify (all REST controllers):**
- `feature/inbox/api/InboxRestController.java`
- `feature/action/api/ActionRestController.java`
- `feature/area/api/AreaRestController.java`
- `feature/context/api/ContextRestController.java`
- `feature/project/api/ProjectRestController.java`
- `feature/profile/api/ProfileRestController.java`
- `feature/waitingfor/api/WaitingForRestController.java`
- `feature/review/api/WeeklyReviewRestController.java`
- `feature/somedaymaybe/api/SomedayMaybeRestController.java`
- `feature/dashboard/api/DashboardRestController.java`

- [ ] **Step 1: Wrap each controller's return types in ApiResponse**

Example for Inbox:
```java
// Before:
@GetMapping
List<InboxItemResponse> list(...) { ... }

// After:
@GetMapping
ApiResponse<List<InboxItemResponse>> list(...) {
    ...
    return ApiResponse.of(responses);
}
```

For paginated endpoints, use `ApiResponse.of(data, page)`.

- [ ] **Step 2: Update all integration tests**

Update JSON path assertions from `$.field` to `$.data.field` and `$[0].field` to `$.data[0].field`. Add assertions for `$.meta` where applicable.

- [ ] **Step 3: Run full test suite**

Run: `cd backend && ./mvnw test`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
cd backend && git add -A && git commit -m "feat: wrap all REST endpoints in ApiResponse envelope"
```

---

## Task 31: Final Verification

- [ ] **Step 1: Run full test suite**

Run: `cd backend && ./mvnw test`
Expected: ALL PASS

- [ ] **Step 2: Verify app starts**

Run: `cd backend && ./mvnw spring-boot:run`
Expected: App starts without errors

- [ ] **Step 3: Verify no cross-feature repo imports remain**

Search for repository imports across feature boundaries:
```bash
# In each feature's api/admin packages, there should be NO repository imports
grep -r "import app.guad.feature.*Repository" backend/src/main/java/ --include="*.java"
```
Only allowed: repositories imported within their own feature package. No cross-feature repo imports should exist (except `InboxProcessingService` using `InboxRepository` in same feature).

- [ ] **Step 4: Commit any final fixes**

```bash
cd backend && git add -A && git commit -m "chore: final cleanup after backend refactoring"
```
