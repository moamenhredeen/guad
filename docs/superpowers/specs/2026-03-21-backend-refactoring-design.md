# Backend Refactoring Design — Pragmatic DDD

**Date:** 2026-03-21
**Status:** Draft

## Problem

The backend has grown organically with inconsistent package structure across features:
- Admin controllers are sometimes at the feature root, sometimes in an `admin/` sub-package (only Inbox)
- Some features have `api/` sub-packages, some don't
- SomedayMaybe controller accesses repositories directly (no service layer)
- Cross-feature dependencies go through repositories instead of services (Dashboard, SomedayMaybe)
- No standardized testing, API response format, validation, or auditing

## Goals

1. **Consistency** — every feature follows the same package structure and layering
2. **Scalability** — introduce pragmatic DDD concepts to support growing complexity
3. **Testability** — comprehensive unit and integration tests per feature
4. **Maintainability** — clear boundaries, standard patterns, reduced duplication

## Non-Goals

- Full hexagonal / ports-and-adapters architecture
- Domain events or event sourcing
- CQRS (separate read/write models)
- API versioning

---

## Target Package Structure

Each feature follows this template (sub-packages created only when needed):

```
feature/
  <feature>/
    api/                            → REST controllers, request/response DTOs, API mapper
    admin/                          → Admin controllers, view models, admin mapper
    <Feature>.java                  → Entity (public)
    <Feature>Repository.java        → Repository (package-private)
    <Feature>Specifications.java    → Specifications (package-private)
    <Feature>Status.java            → Enums (public)
    <Feature>Service.java           → Service (public)
```

### Rules

- **Controllers never touch repositories directly** — always go through services
- **Cross-feature dependencies use services**, never repositories — enforced by the compiler since repositories are package-private
- **Entities, enums, and services are `public`** — they form the feature's public API
- **Repositories and specifications are package-private** — only accessible by the service in the same package, invisible to other features
- **DTOs stay with their consumer**: API DTOs in `api/`, view models in `admin/`
- **Mappers stay with their consumer**: API mapper in `api/`, admin mapper in `admin/`. Existing single mappers are split into two: one per consumer package. Shared mapping logic (if any) is extracted into a private method on the entity or a small utility.
- **`api/` and `admin/` classes are package-private** — controllers, DTOs, view models, and mappers cannot be accessed from outside their sub-package
- **Sub-packages are only created when needed** — no empty placeholder packages

### Feature Mapping

| Feature | `api/` | `admin/` | Root (package-private) | Root (public) |
|---------|--------|----------|-----------------------|---------------|
| Action | Yes | Yes | Repo, specs | Entity, enums, Service |
| Area | Yes | Yes | Repo, specs | Entity, Service |
| Context | Yes | Yes | Repo, specs | Entity, Service |
| Project | Yes | Yes | Repo, specs | Entity, enums, Service |
| Inbox | Yes | Yes | Repo, specs | Entity, enums, Service, InboxProcessingService |
| Attachment | No | Yes | Repo, specs | Entity, Service |
| Document | No | Yes | Repo, specs | Entity, Service |
| Profile | Yes | Yes | Repo, specs | Entity, Service, Interceptor, WebConfig |
| WaitingFor | Yes | No | Repo | Entity, enums, Service |
| Review | Yes | No | Repo | Entity, enums, Service |
| SomedayMaybe | Yes | No | — | No own service; controller delegates to ActionService/ProjectService (currently uses repos directly — must be refactored) |
| Dashboard | Yes | Yes | — | DashboardService (currently uses repos directly — refactored to use InboxService, ActionService, ProjectService, WaitingForService) |
| Auth | No | Yes | — | — |

---

## Pattern: Embeddable Audit Metadata

Replace manual timestamp handling with Spring Data JPA Auditing.

```java
@Embeddable
public class AuditMetadata {
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
```

- **`userId`** remains as a business field on entities — represents data ownership ("this action belongs to this user")
- **`AuditMetadata`** is a technical concern — tracks who created/modified the record (user or admin)
- `AuditorAware<String>` bean extracts the current principal from the security context automatically
- `createdBy`/`updatedBy` tracks all users (not admin-only), enabling full audit trail
- Distinction: `updatedBy == userId` means self-edit; `updatedBy != userId` means admin edit
- Requires `@EnableJpaAuditing` on a configuration class (added during shared infrastructure step)

The `AuditorAware` implementation must handle both authentication paths:

```java
@Component
public class SecurityAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
        // Returns empty during unauthenticated contexts (Flyway migrations, scheduled tasks)
        // Spring Data treats empty Optional as "no auditor" — columns remain null
    }
}
```

Existing `createdDate`/`updatedDate` fields on entities will be removed and replaced by the embedded `AuditMetadata`. Since the project is pre-alpha with a consolidated V1 migration, the database schema is regenerated — no column rename needed.

Usage in entities:

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;  // business ownership

    @Embedded
    private AuditMetadata audit = new AuditMetadata();  // technical auditing

    // ... feature fields
}
```

---

## Pattern: API Response Envelope

All REST API endpoints return a consistent wrapper. Admin (Thymeleaf) endpoints are excluded — they return view models for server-side rendering.

```java
public record ApiResponse<T>(
    T data,
    PageMeta meta
) {
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

- Single items: `{ "data": { ... }, "meta": null }`
- Paginated lists: `{ "data": [ ... ], "meta": { "page": 0, "size": 20, "totalElements": 42, "totalPages": 3 } }`

Location: `core/` package.

---

## Pattern: Bean Validation on Request DTOs

Move validation into request DTOs using Jakarta Bean Validation:

```java
public record CreateActionRequest(
    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Status is required")
    ActionStatus status,

    List<Long> contextIds,
    Long projectId,
    Long areaId
) {}
```

Controllers use `@Valid`:

```java
@PostMapping
public ApiResponse<ActionResponse> create(@Valid @RequestBody CreateActionRequest request) {
    // Spring returns 400 automatically on validation failure
}
```

The existing `GlobalExceptionHandler` in `core/` already catches `MethodArgumentNotValidException`. It will be enhanced to return error responses in a consistent format. Error responses use the existing `ApiError` record (not the `ApiResponse` envelope) — success and error shapes are intentionally separate. `spring-boot-starter-validation` is already a project dependency.

---

## Pattern: Package Visibility

Use Java access modifiers to enforce feature boundaries at compile time:

**Feature root package:**
- **Services** — `public` (they are the feature's entry point, used by other features)
- **Entities and enums** — `public` (needed by `api/` and `admin/` sub-packages for mapping)
- **Repositories** — `package-private` (only used by the service in the same package; **compiler prevents** cross-feature access)
- **Specifications** — `package-private` (only used by repos/service in the same package)

**Sub-packages (`api/`, `admin/`):**
- **Controllers** — `package-private` (Spring discovers them via annotation scanning regardless of visibility)
- **DTOs and view models** — `package-private` where possible, `public` when needed by the service layer
- **Mappers** — `package-private`

---

## Testing Strategy

Each feature gets two levels of tests before moving to the next feature.

### Unit Tests (Service Layer)

- `@ExtendWith(MockitoExtension.class)` — no Spring context
- Mock repositories and cross-feature service dependencies
- Cover: CRUD happy paths, merge-on-update logic, validation, error cases, cross-feature service calls

```java
@ExtendWith(MockitoExtension.class)
class ActionServiceTest {
    @Mock ActionRepository actionRepository;
    @Mock AttachmentService attachmentService;
    @InjectMocks ActionService actionService;

    @Test void create_shouldSaveAndReturnAction() { ... }
    @Test void update_shouldMergeFields() { ... }
    @Test void delete_nonExistent_shouldThrow() { ... }
}
```

### Integration Tests (Controller Layer)

- `@SpringBootTest` + `@AutoConfigureMockMvc` with Testcontainers PostgreSQL
- Test full HTTP flow: request → controller → service → repository → database
- Cover: HTTP verbs + status codes, request validation, auth/authorization, response shape, pagination

```java
@SpringBootTest
@AutoConfigureMockMvc
class ActionRestControllerIT {
    @Autowired MockMvc mockMvc;

    @Test void getActions_authenticated_returns200() { ... }
    @Test void createAction_invalidBody_returns400() { ... }
    @Test void getAction_wrongUser_returns404() { ... }
}
```

### Shared Test Infrastructure

A `BaseIntegrationTest` class already exists with Testcontainers PostgreSQL, `MockMvc`, and JWT helpers. It will be extended as needed:

- **Testcontainers** — PostgreSQL container for integration tests (no H2), already configured
- **Security test helpers** — existing JWT mock utilities; add OAuth2 login helpers for admin controller tests
- **Test fixtures/factories** — new reusable builders for creating test entities (e.g., `ActionFixture.anAction().withStatus(NEXT).build()`)

---

## Migration Process

### Approach: Feature-by-Feature

Each feature is refactored, tested, and verified as a single unit of work before moving to the next.

### Per-Feature Steps

```
1. Restructure packages
   ├── Create admin/              → move admin controller, view models, admin mapper
   ├── Keep api/                  → already exists for most features
   ├── Keep entity, enums, repo, specs, service at feature root
   └── Set visibility: repos/specs → package-private, entity/service → public

2. Apply patterns
   ├── Add @Embedded AuditMetadata to entity
   ├── Add Bean Validation to request DTOs
   ├── Wrap REST responses in ApiResponse<T>
   ├── Replace direct repo usage with service calls (cross-feature)
   └── Fix naming inconsistencies (e.g., ProfileController → ProfileRestController)

3. Write tests
   ├── Unit tests for service
   └── Integration tests for controllers

4. Verify
   ├── All tests pass
   ├── App compiles and starts
   └── Commit
```

### Migration Order

1. **Shared infrastructure first** — `AuditMetadata`, `ApiResponse`, `AuditorAware` bean, Testcontainers config, security test helpers, test fixtures
2. **Inbox** — closest to target structure (already has `admin/` sub-package), validates the template
3. **Action** — most complex feature, proves the pattern scales
4. **Remaining features** (suggested order):
   - Area, Context (simple, no cross-feature deps)
   - Project (depends on Area)
   - Attachment, Document (used by Action/Project via service)
   - Profile (has interceptor — see note below)
   - WaitingFor (depends on Action/Project services)
   - Review
5. **SomedayMaybe & Dashboard last** — depend on other features' services being refactored first
6. **Auth** — minimal, can be done at any point

### Special Cases

- **Profile feature:** `ProfileWebConfig` and `UserProfileInterceptor` are framework integration classes. They stay at the feature root alongside the service — they don't fit `api/`, `admin/`, `domain/`, or `infrastructure/`.
- **Shared infrastructure:** `infrastructure/storage/` (`ObjectStorageProvider`, `S3ObjectStorageProvider`) remains at the app root, outside the `feature/` hierarchy. Same for `core/` and `security/`.
- **Existing `core/` classes:** `ApiError`, `ResourceNotFoundException`, `GlobalExceptionHandler`, `AdminExceptionHandler`, `AdminErrorController`, `AdminModelAdvice`, `PaginationUtils` — all survive. `GlobalExceptionHandler` is enhanced for validation errors. `ApiResponse` is added alongside `ApiError`.
- **`GenerationType`:** Code examples use `IDENTITY` for illustration. Existing entities preserve their current generation strategy (`SEQUENCE` or otherwise).

### Database Migration

Since the project is pre-alpha, all Flyway migrations are consolidated into a single V1. The AuditMetadata columns (`created_at`, `updated_at`, `created_by`, `updated_by`) will be added to all entity tables in this single migration.
