# Source Code Anti-Patterns To Avoid

This document lists anti-patterns that are risky in the Guad backend codebase and should be avoided when adding or refactoring features.

## 1. Loading Related User-Owned Records With Plain `findById`

Anti-pattern:

```java
projectService.findById(request.projectId()).ifPresent(action::setProject);
contextService.findById(ctxId).ifPresent(contexts::add);
areaService.getAreaById(request.areaId()).ifPresent(action::setArea);
```

Why it is bad:

- It allows cross-user links if a caller guesses another user's ID.
- It weakens the application's privacy boundary.
- It makes ownership depend on controller discipline instead of service rules.

Preferred pattern:

```java
var project = projectService.findByIdAndUserId(projectId, userId)
    .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
action.setProject(project);
```

Apply this rule to projects, areas, contexts, attachments, documents, waiting-for items, and any future user-owned entity.

## 2. Generic `save(entity)` As The Main Use-Case API

Anti-pattern:

```java
action.setStatus(ActionStatus.COMPLETED);
actionService.save(action);
```

Why it is bad:

- Callers can bypass required domain rules.
- State transitions become scattered across controllers.
- Security and ownership checks are easy to forget.

Preferred pattern:

```java
actionService.completeAction(actionId, userId);
actionService.scheduleAction(actionId, scheduledDate, userId);
actionService.moveToSomedayMaybe(actionId, userId);
```

Keep generic persistence helpers private or package-private when possible.

## 3. Free-Form Status Mutation

Anti-pattern:

```java
action.setStatus(ActionStatus.valueOf(body.get("status")));
```

Why it is bad:

- Any status can jump to any other status.
- Required side effects can be missed.
- Invalid client input creates runtime exceptions instead of useful errors.

Preferred pattern:

```java
actionService.completeAction(id, userId);
actionService.cancelAction(id, userId);
actionService.scheduleAction(id, command, userId);
```

Each transition should enforce its own invariants.

## 4. Entity Fields That Are Required By The Domain But Nullable In The Database

Anti-pattern:

```java
@Column
private String description;

@Column
private ActionStatus status;
```

Why it is bad:

- Invalid records can be inserted outside the happy-path API.
- Later code must defensively handle states that should not exist.
- Data quality decays over time.

Preferred pattern:

```java
@Column(nullable = false)
private String description;

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private ActionStatus status;
```

Also add `NOT NULL` constraints in Flyway migrations.

## 5. Enum Ordinal Persistence

Anti-pattern:

```java
@Column
private ActionStatus status;
```

Without `@Enumerated(EnumType.STRING)`, JPA may persist enum ordinals.

Why it is bad:

- Reordering enum constants changes the meaning of stored data.
- Inserted rows are harder to inspect manually.
- Migrations become risky.

Preferred pattern:

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private ActionStatus status;
```

## 6. Snake Case Java Fields

Anti-pattern:

```java
private String desired_outcome;
```

Why it is bad:

- It breaks normal Java naming conventions.
- It leaks database naming into the domain model.
- It creates awkward getters such as `getDesired_outcome()`.

Preferred pattern:

```java
@Column(name = "desired_outcome")
private String desiredOutcome;
```

## 7. User-Owned Entity Without `userId`

Anti-pattern:

```java
class Document {
    private Long id;
    private String name;
    private String content;
}
```

Why it is bad:

- The record cannot be safely queried from user-facing APIs.
- Search results cannot be scoped by user.
- Attachments and project links can leak across users.

Preferred pattern:

```java
@Column(nullable = false)
private UUID userId;
```

Every personal GTD object should be user-owned unless it is truly global configuration.

## 8. In-Memory Filtering For Database Queries

Anti-pattern:

```java
var actions = actionService.findAllByUserId(userId);
actions = actions.stream()
    .filter(action -> hasContext(action, contextId))
    .toList();
```

Why it is bad:

- It loads more rows than needed.
- It can trigger lazy-loading problems.
- It becomes slow as user data grows.

Preferred pattern:

```java
List<Action> findAllByUserIdAndContextsId(UUID userId, Long contextId);
```

Or use a `Specification<Action>` that includes the context join.

## 9. Returning Or Depending On JPA Entities At API Boundaries

Anti-pattern:

```java
return ApiResponse.of(action);
```

Why it is bad:

- Lazy relationships can break serialization.
- Internal fields leak into API responses.
- API shape becomes coupled to persistence mapping.

Preferred pattern:

```java
return ApiResponse.of(ActionResponse.from(action));
```

Keep request and response DTOs explicit.

## 10. `Map<String, String>` For Command Bodies

Anti-pattern:

```java
@PatchMapping("/{id}/status")
ApiResponse<ActionResponse> changeStatus(@RequestBody Map<String, String> body)
```

Why it is bad:

- There is no validation.
- Missing keys produce unclear errors.
- Refactoring is harder.
- API documentation is weaker.

Preferred pattern:

```java
public record ChangeActionStatusRequest(@NotNull ActionStatus status) {}
```

For domain operations, prefer command-specific requests:

```java
public record ScheduleActionRequest(@NotNull LocalDate scheduledDate) {}
```

## 11. Time Calls Hidden Inside Domain Logic

Anti-pattern:

```java
action.setCompletedDate(Instant.now());
```

Why it is bad:

- Tests become less deterministic.
- Timezone and clock behavior are harder to control.

Preferred pattern:

```java
private final Clock clock;

action.complete(clock.instant());
```

Inject `Clock` as a bean.

## 12. Mixing Date-Time Types Without A Rule

Anti-pattern:

```java
private Instant completedDate;
private LocalDateTime scheduledDate;
private LocalDateTime dueDate;
```

Why it is bad:

- The meaning of timezone-sensitive fields becomes unclear.
- Calendar and reminder behavior can differ by server timezone.

Preferred pattern:

- Use `Instant` for event timestamps.
- Use `LocalDate` for date-only GTD fields.
- Use `ZonedDateTime` or `OffsetDateTime` only when user timezone behavior is explicit.

## 13. Hard Deletes Without Product Semantics

Anti-pattern:

```java
repository.deleteById(id);
```

Why it is bad:

- Audit history may be lost.
- Linked records can break.
- Users may expect archive/trash behavior.

Preferred pattern:

- Use soft states such as `CANCELLED`, `TRASHED`, or `ARCHIVED` for user-facing flows when history matters.
- Use hard delete only where the product explicitly wants permanent deletion.
- Always scope deletes by user ownership.

## 14. Admin Convenience Leaking Into User-Facing Services

Anti-pattern:

```java
Page<Action> getAllActions(Specification<Action> spec, Pageable pageable)
Optional<Action> getActionById(long id)
```

Then using these methods in user-facing API flows.

Why it is bad:

- Admin queries often intentionally cross user boundaries.
- User-facing queries must enforce ownership.

Preferred pattern:

- Name admin methods clearly.
- Keep user-facing service methods user-scoped.
- Consider separate admin services when behavior diverges.

## 15. Silent Ignore Of Invalid Related IDs

Anti-pattern:

```java
projectService.findById(projectId).ifPresent(action::setProject);
```

Why it is bad:

- The client thinks a project was assigned when it was not.
- Invalid IDs are hidden.
- Cross-user access attempts may be masked instead of handled intentionally.

Preferred pattern:

```java
var project = projectService.findByIdAndUserId(projectId, userId)
    .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
```

Invalid related IDs should produce a clear error.

## 16. Security Logging At Trace Level In Normal Environments

Anti-pattern:

```properties
logging.level.org.springframework.security=TRACE
```

Why it is bad:

- Logs become noisy.
- Sensitive authentication details may be exposed.
- Operational issues become harder to inspect.

Preferred pattern:

```properties
logging.level.org.springframework.security=INFO
```

Enable `TRACE` temporarily in local development only.

## 17. Tests That Only Verify Delegation

Anti-pattern:

```java
verify(repository).save(action);
```

Why it is bad:

- It proves implementation detail, not behavior.
- Refactoring services breaks tests without changing product behavior.
- Important domain rules remain untested.

Preferred test focus:

- ownership enforcement
- invalid state transitions
- required timestamps
- cross-user relationship rejection
- inbox processing result traceability
- API validation and status codes

## 18. Integration Tests Without A Reliable Environment Contract

Anti-pattern:

- Tests require Docker but the requirement is not documented.
- Mockito depends on runtime self-attachment that fails on the selected JDK.

Why it is bad:

- Developers get noisy failures before behavior is tested.
- CI and local results diverge.

Preferred pattern:

- Document Docker/Testcontainers as a requirement.
- Configure Mockito/JDK compatibility explicitly.
- Separate fast unit tests from container-backed integration tests when useful.

## 19. Storing Only Public File URLs

Anti-pattern:

```java
private String fileUrl;
```

Why it is bad:

- It is harder to delete or re-sign objects.
- Storage provider changes become painful.
- Public URL assumptions can leak into the domain.

Preferred pattern:

```java
private String storageKey;
private String originalFilename;
private String mimeType;
private long fileSize;
```

Generate presigned URLs at the edge when needed.

## 20. README Or Documentation Drift

Anti-pattern:

- Documentation describes fields or behavior that migrations and code do not implement.
- The README mixes planned design with current behavior.

Why it is bad:

- Developers cannot tell what is real.
- API and schema decisions become inconsistent.

Preferred pattern:

- Keep domain model, user stories, and implementation notes separate.
- Mark planned behavior clearly.
- Update docs in the same change as schema or behavior changes.

