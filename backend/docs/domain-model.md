# GTD Domain Model

This document describes the recommended domain model for Guad as a GTD-based task management system.

## Modeling Principle

Do not model GTD as one generic `Task` table with a `type` field.

Inbox items, actions, waiting-for items, projects, and reference documents have different meanings and different invariants. A generic table usually becomes a collection of nullable fields and weak rules.

Prefer separate entities with clear responsibilities.

## Core Entities

### InboxItem

An `InboxItem` is raw captured input.

It can be vague, incomplete, and not yet actionable. It represents something the user captured so they can clarify it later.

Typical fields:

- `id`
- `title`
- `description`
- `source`
- `status`
- `processedAt`
- `processedIntoType`
- `processedIntoId`
- `userId`
- audit metadata

Valid statuses:

- `UNPROCESSED`
- `PROCESSED`
- `TRASHED`

Rules:

- An inbox item belongs to exactly one user.
- An unprocessed inbox item should not be treated as executable work.
- Processing an inbox item should create or link to one resulting domain object.
- The system should keep traceability from the inbox item to the created action, project, waiting-for item, or reference document.

### Action

An `Action` is a concrete executable unit of work.

It should represent something the user can actually do, ideally starting with a verb.

Typical fields:

- `id`
- `description`
- `notes`
- `status`
- `scheduledDate`
- `dueDate`
- `completedAt`
- `estimatedDuration`
- `energyLevel`
- `location`
- `isTimeSpecific`
- `projectId`
- `areaId`
- `contextIds`
- `userId`
- audit metadata

Valid statuses:

- `NEXT`
- `SCHEDULED`
- `IN_PROGRESS`
- `COMPLETED`
- `SOMEDAY_MAYBE`
- `CANCELLED`

Rules:

- An action belongs to exactly one user.
- An action must have a non-empty description.
- A completed action should have `completedAt`.
- A non-completed action should not have `completedAt`.
- A scheduled action should have `scheduledDate`.
- A next action should be actionable without further clarification.
- Related project, area, and context records must belong to the same user.

### Project

A `Project` is a desired outcome requiring more than one action.

It is not itself executable work. It groups and gives meaning to actions.

Typical fields:

- `id`
- `name`
- `description`
- `desiredOutcome`
- `status`
- `targetDate`
- `completedAt`
- `lastReviewedAt`
- `areaId`
- `userId`
- audit metadata

Valid statuses:

- `ACTIVE`
- `ON_HOLD`
- `COMPLETED`
- `SOMEDAY_MAYBE`
- `CANCELLED`

Rules:

- A project belongs to exactly one user.
- A project should have a clear name.
- An active project should usually have at least one next action, waiting-for item, or explicit reason it is stalled.
- A completed project should have `completedAt`.
- A project area must belong to the same user.

### WaitingForItem

A `WaitingForItem` is an external dependency or delegated item.

It is not the same thing as an action. An action is something the user can do; a waiting-for item is something the user is tracking because another person or external system must act.

Typical fields:

- `id`
- `title`
- `delegatedTo`
- `delegatedAt`
- `followUpDate`
- `resolvedAt`
- `notes`
- `status`
- `projectId`
- `originatingActionId`
- `userId`
- audit metadata

Valid statuses:

- `WAITING`
- `FOLLOWED_UP`
- `RESOLVED`
- `CANCELLED`

Rules:

- A waiting-for item belongs to exactly one user.
- A waiting-for item should identify who or what the user is waiting on.
- A resolved waiting-for item should have `resolvedAt`.
- If linked to a project or originating action, those records must belong to the same user.
- Prefer a separate `WaitingForItem` entity over `ActionStatus.WAITING_FOR`.

### ReferenceDocument

A `ReferenceDocument` is non-actionable information the user wants to keep.

It may support a project or action, but it is not work to be completed.

Typical fields:

- `id`
- `name`
- `content`
- `url`
- `projectId`
- `userId`
- audit metadata

Rules:

- A reference document belongs to exactly one user.
- Reference material should be searchable.
- If linked to a project, the project must belong to the same user.
- Processing an inbox item as reference should create a user-owned reference document.

### Area

An `Area` is an ongoing responsibility or sphere of life/work.

Areas do not complete in the same way projects do. They provide long-term organization.

Typical fields:

- `id`
- `name`
- `description`
- `position`
- `lastReviewedAt`
- `userId`
- audit metadata

Rules:

- An area belongs to exactly one user.
- Area names should be unique per user.
- Projects and standalone actions may belong to an area.

### Context

A `Context` describes the location, tool, person, or condition needed to perform an action.

Typical fields:

- `id`
- `name`
- `description`
- `color`
- `iconKey`
- `userId`
- audit metadata

Rules:

- A context belongs to exactly one user.
- Context names should be unique per user.
- Contexts attach to actions, not projects.

### WeeklyReview

A `WeeklyReview` tracks the user's review workflow.

Typical fields:

- `id`
- `startedAt`
- `completedAt`
- `currentStep`
- `notes`
- `userId`
- audit metadata

Example steps:

- `CLEAR_INBOX`
- `REVIEW_ACTIONS`
- `REVIEW_PROJECTS`
- `REVIEW_WAITING_FOR`
- `REVIEW_SOMEDAY_MAYBE`
- `DONE`

Rules:

- A weekly review belongs to exactly one user.
- A completed review should have `completedAt`.
- Only one active review per user should exist unless parallel reviews are intentional.

### Attachment

An `Attachment` is a user-owned file that can be linked to other domain objects.

Typical fields:

- `id`
- `filename`
- `fileSize`
- `mimeType`
- `fileUrl`
- `thumbnailUrl`
- `userId`
- audit metadata

Rules:

- An attachment belongs to exactly one user.
- An attachment may be linked to inbox items, actions, projects, waiting-for items, or reference documents.
- Linked records must belong to the same user as the attachment.

## Recommended Relationships

```text
User
  has many InboxItems
  has many Actions
  has many Projects
  has many WaitingForItems
  has many ReferenceDocuments
  has many Areas
  has many Contexts
  has many WeeklyReviews
  has many Attachments

Project
  belongs to User
  belongs to optional Area
  has many Actions
  has many WaitingForItems
  has many ReferenceDocuments

Action
  belongs to User
  belongs to optional Project
  belongs to optional Area
  has many Contexts
  has many Attachments

WaitingForItem
  belongs to User
  belongs to optional Project
  belongs to optional originating Action

InboxItem
  belongs to User
  may process into Action, Project, WaitingForItem, ReferenceDocument, SomedayMaybe, or Trash
```

## Inbox Processing Outcomes

When processing an inbox item, the user should choose one outcome:

- Create next action
- Create scheduled action
- Create project
- Create waiting-for item
- Create reference document
- Move to someday/maybe
- Trash

Processing should:

- mark the inbox item as processed
- record `processedAt`
- record the resulting object type and ID
- copy or link attachments as appropriate
- keep all created records under the same `userId`

## Someday/Maybe

For an MVP, someday/maybe can be represented as `Action.status = SOMEDAY_MAYBE` and `Project.status = SOMEDAY_MAYBE`.

If the product later needs broader someday/maybe capture, such as ideas, books, trips, someday projects, or non-actionable possibilities, introduce a separate `SomedayMaybeItem`.

## Important Design Decisions

### Keep WaitingForItem Separate From Action

Prefer this:

```text
WaitingForItem
  title
  delegatedTo
  followUpDate
  projectId
  originatingActionId
```

Over this:

```text
Action
  status = WAITING_FOR
  delegatedTo
  followUpDate
```

Reason: waiting-for is an external dependency, while action is executable work. Keeping them separate avoids polluting `Action` with fields that only apply to delegated or blocked work.

### Do Not Trust Raw IDs Across Users

Every relationship assignment must verify ownership.

For example, creating an action with `projectId`, `areaId`, or `contextIds` should only succeed if those records belong to the authenticated user.

### Put Domain Rules In Services Or Domain Methods

Avoid free-form status mutation from controllers.

Prefer explicit operations:

- `completeAction(actionId, userId)`
- `scheduleAction(actionId, scheduledDate, userId)`
- `moveActionToSomeday(actionId, userId)`
- `resolveWaitingFor(itemId, userId)`
- `completeProject(projectId, userId)`

These operations should enforce required fields and state transitions.

## User Stories

These stories describe product behavior from the user's perspective. They should guide API design, UI flows, and acceptance tests.

### Capture

#### US-001: Capture an inbox item

As a user, I want to quickly capture a thought, task, idea, or note into my inbox so that I can get it out of my head and clarify it later.

Acceptance criteria:

- The user can create an inbox item with a title.
- The user may add an optional description.
- The created item is marked `UNPROCESSED`.
- The item is owned by the authenticated user.
- The item appears in the user's inbox list.

#### US-002: Capture with source context

As a user, I want captured items to record where they came from so that I can understand their origin when processing them.

Acceptance criteria:

- The user or client can provide a source such as `manual`, `email`, `voice`, `web`, or `share`.
- If no source is provided, the system defaults to `manual`.
- The source is visible when viewing or processing the inbox item.

#### US-003: Attach files to captured input

As a user, I want to attach files to an inbox item so that supporting material is not lost before I clarify it.

Acceptance criteria:

- The user can attach one or more files to an inbox item.
- Attachments belong to the authenticated user.
- Attachments are visible when viewing the inbox item.
- When the inbox item is processed, attachments can be copied or linked to the resulting object.

### Clarify And Process

#### US-004: Process an inbox item into a next action

As a user, I want to turn an inbox item into a concrete next action so that I know exactly what to do.

Acceptance criteria:

- The user can choose `Create next action` while processing an inbox item.
- The action must have a non-empty description.
- If the user does not provide a description, the inbox item title is used.
- The action is created with status `NEXT`.
- The inbox item is marked `PROCESSED`.
- The inbox item records the created action as its processing result.

#### US-005: Process an inbox item into a scheduled action

As a user, I want to turn an inbox item into a scheduled action so that date-specific work appears on my calendar or scheduled list.

Acceptance criteria:

- The user can choose `Create scheduled action`.
- The user must provide a scheduled date or date-time.
- The created action has status `SCHEDULED`.
- The inbox item is marked `PROCESSED`.
- The scheduled action belongs to the authenticated user.

#### US-006: Process an inbox item into a project

As a user, I want to turn an inbox item into a project when it requires more than one action so that I can track the desired outcome.

Acceptance criteria:

- The user can choose `Create project`.
- The project must have a name.
- The user may define a desired outcome.
- The project is created with status `ACTIVE`.
- The inbox item is marked `PROCESSED`.
- The inbox item records the created project as its processing result.

#### US-007: Process an inbox item into a waiting-for item

As a user, I want to turn an inbox item into a waiting-for item when someone else needs to respond or act.

Acceptance criteria:

- The user can choose `Create waiting-for item`.
- The waiting-for item must have a title.
- The user can identify who or what they are waiting on.
- The user may set a follow-up date.
- The waiting-for item is created with status `WAITING`.
- The inbox item is marked `PROCESSED`.

#### US-008: Process an inbox item into reference material

As a user, I want to turn an inbox item into reference material when it is useful but not actionable.

Acceptance criteria:

- The user can choose `Create reference document`.
- The reference document is owned by the authenticated user.
- The inbox title becomes the reference name unless the user changes it.
- The inbox description becomes the reference content unless the user changes it.
- The inbox item is marked `PROCESSED`.

#### US-009: Move an inbox item to someday/maybe

As a user, I want to move an inbox item to someday/maybe when it is not actionable now but may be useful later.

Acceptance criteria:

- The user can choose `Move to someday/maybe`.
- The resulting item is visible in the someday/maybe list.
- The inbox item is marked `PROCESSED`.
- The inbox item records the someday/maybe result.

#### US-010: Trash an inbox item

As a user, I want to trash an inbox item when it has no value so that my inbox stays clear.

Acceptance criteria:

- The user can choose `Trash`.
- The inbox item is no longer shown in the unprocessed inbox.
- The item is marked `TRASHED` or `PROCESSED` with a trash result.
- Trash does not create an action, project, waiting-for item, or reference document.

### Organize

#### US-011: Create a project

As a user, I want to create a project directly so that I can track an outcome that requires multiple steps.

Acceptance criteria:

- The user can create a project with a name.
- The user may add a description and desired outcome.
- The project starts as `ACTIVE`.
- The project belongs to the authenticated user.
- The user may assign the project to one of their areas.

#### US-012: Add an action to a project

As a user, I want to add a next action to a project so that the project can keep moving.

Acceptance criteria:

- The user can create an action from a project.
- The action is linked to that project.
- The action starts as `NEXT` unless scheduled.
- The action and project must belong to the same user.

#### US-013: Assign contexts to an action

As a user, I want to assign contexts to an action so that I can filter actions by where, how, or with whom they can be done.

Acceptance criteria:

- The user can assign zero or more contexts to an action.
- Each context must belong to the authenticated user.
- The user can filter actions by context.
- Contexts attach to actions, not projects.

#### US-014: Assign an area to a project or standalone action

As a user, I want to assign projects and standalone actions to areas so that I can organize work by responsibility.

Acceptance criteria:

- The user can assign one of their areas to a project.
- The user can assign one of their areas to a standalone action.
- The system rejects areas owned by another user.
- The user can filter or review work by area.

### Engage

#### US-015: View next actions

As a user, I want to view my next actions so that I can choose what to do now.

Acceptance criteria:

- The user can view actions with status `NEXT`.
- Completed, cancelled, scheduled, and someday/maybe actions are excluded by default.
- The user can filter by context, project, area, energy level, or estimated duration.
- Only the authenticated user's actions are shown.

#### US-016: Complete an action

As a user, I want to complete an action so that it no longer appears as available work.

Acceptance criteria:

- The user can mark one of their actions as complete.
- The action status becomes `COMPLETED`.
- The system records `completedAt`.
- The action no longer appears in the default next-action list.

#### US-017: Reschedule an action

As a user, I want to schedule or reschedule an action so that I can defer it to the right date.

Acceptance criteria:

- The user can set or change `scheduledDate`.
- Setting a scheduled date moves the action to `SCHEDULED`.
- Removing a scheduled date from a scheduled action requires choosing a valid new status.
- Scheduled actions appear in scheduled/calendar views.

#### US-018: View waiting-for items

As a user, I want to view what I am waiting for so that I can follow up at the right time.

Acceptance criteria:

- The user can view active waiting-for items.
- The user can filter by follow-up date, project, or delegated person.
- Resolved and cancelled waiting-for items are excluded by default.
- Only the authenticated user's waiting-for items are shown.

#### US-019: Resolve a waiting-for item

As a user, I want to resolve a waiting-for item when the external dependency is complete.

Acceptance criteria:

- The user can mark one of their waiting-for items as resolved.
- The status becomes `RESOLVED`.
- The system records `resolvedAt`.
- The item no longer appears in the default waiting-for list.

### Review

#### US-020: Start a weekly review

As a user, I want to start a weekly review so that I can regain control of my system.

Acceptance criteria:

- The user can start a weekly review.
- The review starts at the first configured review step.
- The review belongs to the authenticated user.
- The system should avoid creating multiple active reviews for the same user unless explicitly allowed.

#### US-021: Advance through weekly review steps

As a user, I want to move through weekly review steps so that I can review the system in a structured way.

Acceptance criteria:

- The user can advance from the current step to the next step.
- The review cannot advance beyond `DONE`.
- Only the owner can advance the review.
- The current step is visible to the user.

#### US-022: Complete a weekly review

As a user, I want to complete my weekly review so that the system knows my review is up to date.

Acceptance criteria:

- The user can complete an active weekly review.
- The review status or current step becomes `DONE`.
- The system records `completedAt`.
- The dashboard no longer shows the weekly review as due until the configured interval passes.

#### US-023: Identify stalled projects

As a user, I want to see active projects without a next action or waiting-for item so that I can decide the next move.

Acceptance criteria:

- The weekly review highlights active projects with no next action.
- Projects with active waiting-for items can be shown separately as waiting on others.
- Completed, cancelled, and someday/maybe projects are excluded from the active stalled list.

### Reference And Search

#### US-024: Search reference material

As a user, I want to search my reference material so that I can retrieve useful non-actionable information.

Acceptance criteria:

- The user can search reference documents by name or content.
- Results only include documents owned by the authenticated user.
- The user can open a reference document from search results.

#### US-025: Link reference material to a project

As a user, I want to link reference material to a project so that project support information is easy to find.

Acceptance criteria:

- The user can link one of their reference documents to one of their projects.
- The system rejects cross-user links.
- Project details show linked reference material.

### Setup And Personalization

#### US-026: Manage areas

As a user, I want to manage my areas of responsibility so that my projects and actions can be organized meaningfully.

Acceptance criteria:

- The user can create, rename, reorder, and delete their own areas.
- Area names are unique per user.
- Deleting an area should not delete unrelated actions or projects without explicit confirmation.

#### US-027: Manage contexts

As a user, I want to manage my contexts so that my action lists match how I actually work.

Acceptance criteria:

- The user can create, rename, and delete their own contexts.
- Context names are unique per user.
- Deleting a context removes the association from actions but does not delete the actions.

#### US-028: Configure review preferences

As a user, I want to configure review preferences so that reminders match my workflow.

Acceptance criteria:

- The user can set a default review day.
- The user can set their timezone.
- Dashboard review-due calculations use the user's preferences.

### Security And Ownership

#### US-029: Enforce user ownership

As a user, I want my tasks, projects, references, and attachments to be private so that other users cannot access or link to my data.

Acceptance criteria:

- Every user-owned entity stores `userId`.
- API reads only return records owned by the authenticated user.
- API writes only modify records owned by the authenticated user.
- Relationship assignment verifies ownership on both sides.
- Unauthorized cross-user access returns not found or forbidden without leaking data.

#### US-030: Preserve audit information

As a user, I want important changes to be auditable so that I can understand when work was created, updated, completed, or processed.

Acceptance criteria:

- Created records store creation time.
- Updated records store update time.
- Completion and processing operations store domain-specific timestamps such as `completedAt`, `processedAt`, or `resolvedAt`.
- Audit metadata is not accepted blindly from client requests.
