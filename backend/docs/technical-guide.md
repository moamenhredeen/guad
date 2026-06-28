# Spring Boot, Keycloak, and Spring Data JPA Technical Guide

This guide defines project conventions for the Guad backend. It is written for a Spring Boot 4, Java 25, PostgreSQL, Flyway, Spring Security OAuth2, Keycloak, and Spring Data JPA stack.

## Architectural Principles

Keep the domain model explicit. GTD concepts such as inbox item, action, project, waiting-for item, reference document, area, and context should remain separate domain types when they have different lifecycle rules.

Use the authenticated user as a hard boundary. User-owned data must always be queried, mutated, linked, and deleted through user-scoped operations.

Prefer explicit business operations over generic field updates. Operations such as `completeAction`, `scheduleAction`, `resolveWaitingFor`, and `processInboxItem` should enforce domain rules.

Keep controllers thin. Controllers should handle HTTP concerns, authentication extraction, request validation, and response mapping. Business rules belong in application services or domain methods.

Use database constraints as a second line of defense. Validation annotations help API clients, but the database must still protect required fields, uniqueness, relationships, and ownership-sensitive data shape.

## Package Structure

Use feature packages for product areas:

```text
app.guad.feature.action
app.guad.feature.project
app.guad.feature.inbox
app.guad.feature.waitingfor
app.guad.feature.review
```

Within each feature, keep responsibilities clear:

```text
feature/action/Action.java
feature/action/ActionRepository.java
feature/action/ActionService.java
feature/action/api/ActionRestController.java
feature/action/api/ActionResponse.java
feature/action/api/CreateActionRequest.java
feature/action/admin/ActionAdminController.java
```

REST DTOs, admin view models, JPA entities, and domain services should not be treated as interchangeable.

## Spring Security And Keycloak

Use Keycloak as the identity provider, not as the domain user store. Store application profile data in `UserProfile`, linked by the Keycloak subject UUID.

For API endpoints:

- Use Spring Security OAuth2 Resource Server with JWT validation.
- Validate tokens through the configured Keycloak issuer.
- Derive the user ID from the JWT subject, not from request bodies.
- Never accept `userId` from API clients for user-owned operations.
- Return `404` or `403` for records outside the caller's ownership boundary without leaking whether the record exists.

For admin endpoints:

- Keep a separate security filter chain for browser-based OAuth2 login.
- Restrict admin routes by roles from Keycloak.
- Keep admin authorization independent from normal app user authorization.

For role and authority mapping:

- Centralize Keycloak role mapping in one mapper.
- Prefer application roles such as `ROLE_ADMIN` over scattering Keycloak-specific claim parsing across controllers.
- Test authority mapping with realistic JWT claims.

For CORS:

- Configure allowed origins from environment-specific properties.
- Do not use wildcard origins with credentials.
- Keep API CORS separate from admin browser login behavior.

For logging:

- Do not leave Spring Security logs at `TRACE` outside local debugging.
- Never log access tokens, refresh tokens, client secrets, authorization headers, or raw JWTs.

## Authentication User Handling

Create a small authenticated-user adapter for JWT extraction.

Good pattern:

```java
var userId = AuthenticatedUser.from(jwt).id();
```

Then pass `userId` into application services:

```java
actionService.completeAction(actionId, userId);
```

Avoid passing `Jwt` into domain services. Services should not know about HTTP, OAuth2, or Keycloak token structure.

## Ownership And Multi-Tenancy

Every user-owned entity should include `userId`.

User-owned entities include:

- `InboxItem`
- `Action`
- `Project`
- `WaitingForItem`
- `ReferenceDocument`
- `Area`
- `Context`
- `Attachment`
- `WeeklyReview`

Repository methods for user-facing APIs should be user-scoped:

```java
Optional<Action> findByIdAndUserId(Long id, UUID userId);
List<Action> findAllByUserIdAndStatus(UUID userId, ActionStatus status);
```

Relationship assignment must also be user-scoped:

```java
projectService.findByIdAndUserId(projectId, userId)
    .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
```

Do not load related records with plain `findById` from user-facing flows.

## REST API Design

Use request DTOs for input and response DTOs for output.

Do not return JPA entities directly from controllers. Entities often include lazy relationships, internal fields, and persistence-specific structure.

Use validation annotations on request DTOs:

```java
public record CreateActionRequest(
    @NotBlank String description,
    Long projectId,
    List<Long> contextIds
) {}
```

Handle invalid enum values, missing records, and validation failures consistently through global exception handlers.

Prefer resource-oriented routes:

```text
POST   /api/inbox
POST   /api/inbox/{id}/process
GET    /api/actions
PATCH  /api/actions/{id}/complete
PATCH  /api/actions/{id}/schedule
GET    /api/projects/{id}
POST   /api/projects/{id}/actions
```

Use `PATCH` for domain operations and partial state transitions. Use `PUT` when replacing a full resource representation.

## Service Layer

Services should own use-case logic.

Good service methods:

```java
Action createNextAction(CreateActionCommand command, UUID userId);
Action completeAction(Long actionId, UUID userId);
InboxProcessingResult processInboxItem(Long inboxItemId, ProcessInboxItemCommand command, UUID userId);
WaitingForItem resolveWaitingFor(Long waitingForId, UUID userId);
```

Avoid exposing generic `save(entity)` as the primary application API for user-facing behavior. Generic save methods make it easy to bypass invariants.

Use transactions around operations that change multiple records:

```java
@Transactional
public InboxProcessingResult processInboxItem(...) {
    ...
}
```

Use `@Transactional(readOnly = true)` for read-only query methods that load lazy associations or perform multiple reads.

## Domain Rules

Encode important GTD rules in one place.

Examples:

- A next action must have a non-empty description.
- A scheduled action must have a scheduled date.
- A completed action must have `completedAt`.
- A non-completed action should not retain `completedAt`.
- A waiting-for item should identify who or what is being waited on.
- A completed project should have `completedAt`.
- Cross-user links are forbidden.

Prefer domain operations:

```java
action.complete(clock.instant());
action.schedule(scheduledDate);
waitingFor.resolve(clock.instant());
```

Use `Clock` injection for time-sensitive behavior so tests are deterministic.

## Spring Data JPA

Keep repositories small and intention-revealing.

Good repository methods:

```java
Optional<Project> findByIdAndUserId(Long id, UUID userId);
List<Project> findAllByUserIdAndStatus(UUID userId, ProjectStatus status);
long countByUserIdAndStatus(UUID userId, ProjectStatus status);
```

Use specifications for admin search and flexible filtering. For core product queries, prefer explicit repository methods or carefully named query methods.

Use pagination for lists that can grow:

```java
Page<Action> findAllByUserId(UUID userId, Pageable pageable);
```

Avoid loading all user records and filtering in memory when the filter can be expressed in SQL.

Use fetch joins, entity graphs, DTO projections, or explicit query methods when returning relationship-heavy views. Do not rely on accidental lazy loading from controllers.

## JPA Entity Mapping

Use `@Enumerated(EnumType.STRING)` for enums unless there is a deliberate reason to store ordinals.

Good pattern:

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private ActionStatus status;
```

Use Java-style field names and map database names explicitly when needed:

```java
@Column(name = "desired_outcome")
private String desiredOutcome;
```

Avoid snake_case Java fields such as `desired_outcome`.

Use `Instant` for persisted timestamps. Use `LocalDate` for date-only GTD concepts such as a due date or review day. Use `LocalDateTime` only when timezone interpretation is handled intentionally.

Be careful with bidirectional relationships. Add them only when the code needs navigation in both directions. Otherwise, unidirectional mappings are simpler.

Use cascade rules deliberately. Do not cascade deletes across user-owned aggregates unless the product behavior is explicit.

## Database And Flyway

Flyway migrations are the source of truth for schema changes.

Recommended database practices:

- Keep `spring.jpa.hibernate.ddl-auto=none`.
- Make required fields `NOT NULL`.
- Add foreign keys for all persistent relationships.
- Add indexes for common query patterns.
- Add unique constraints for per-user unique names where needed.
- Prefer additive migrations after the first release.
- Do not edit applied migrations in shared environments.

Useful constraints:

```sql
UNIQUE (user_id, name)
```

Useful indexes:

```sql
CREATE INDEX idx_actions_user_status ON actions (user_id, status);
CREATE INDEX idx_actions_user_project ON actions (user_id, project_id);
CREATE INDEX idx_waiting_user_status ON waiting_for_items (user_id, status);
```

## Validation

Use validation at multiple layers:

- Request DTO validation for API feedback.
- Service/domain validation for business invariants.
- Database constraints for data integrity.

Do not rely on DTO validation alone. Data can enter through admin screens, tests, migrations, or future integrations.

## Error Handling

Use a consistent error response shape.

Recommended behavior:

- `400` for malformed input or validation errors.
- `401` for unauthenticated requests.
- `403` for authenticated users lacking permission.
- `404` for missing or inaccessible user-owned resources.
- `409` for domain conflicts such as duplicate names or invalid state transitions.

Do not expose stack traces or internal exception messages to clients.

## Testing

Use focused unit tests for domain behavior and service rules.

Use Spring MVC tests for controllers:

- authentication required
- ownership enforced
- request validation
- response shape
- status codes

Use Testcontainers for repository and integration tests that depend on PostgreSQL behavior.

Use realistic JWTs or Spring Security test support for resource-server tests.

Avoid tests that only verify repository delegation. Prefer tests that prove business rules and security boundaries.

Keep test runtime reliable:

- Document Docker/Testcontainers requirements.
- Configure Mockito for the supported Java version.
- Inject `Clock` where time matters.

## File Storage And Attachments

Keep storage provider concerns behind an interface.

Good pattern:

```java
interface ObjectStorageProvider {
    StoredObject upload(...);
    URI createPresignedDownloadUrl(...);
    void delete(...);
}
```

Validate files before upload:

- maximum file size
- allowed MIME type
- empty file rejection
- user ownership

Store object keys, not only public URLs, so files can be moved, re-signed, or deleted safely.

## Configuration

Use environment variables for secrets:

- Keycloak client secret
- database password
- AWS credentials

Use safe defaults only for local development.

Keep environment-specific behavior in properties or profiles:

```text
application.properties
application-dev.properties
application-test.properties
application-prod.properties
```

Do not commit production secrets.

## Admin UI

Keep admin controllers separate from API controllers.

Admin screens can use broader search and operational views, but they must still respect intentional authorization rules.

Do not let admin convenience methods become the default application service API if they bypass ownership or domain invariants.

## References

- Spring Security OAuth2 Resource Server JWT documentation: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
- Spring Data JPA reference documentation: https://docs.spring.io/spring-data/jpa/reference/
- Spring Boot reference documentation: https://docs.spring.io/spring-boot/
- Keycloak server administration and securing apps documentation: https://www.keycloak.org/documentation
