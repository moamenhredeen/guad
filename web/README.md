# Tue (GTD-based task management software)

## GTD Domain Model - Complete Specification

### Entities

#### 1. Item (Inbox)
**Purpose:** Capture point for all incoming thoughts, tasks, emails, etc.

**Attributes:**
- `id` - Sequence
- `title` - string, required
- `description` - text, optional
- `captured_date` - timestamp, auto-generated
- `source` - string (email, voice, web, manual, etc.)
- `status` - enum: `unprocessed` | `processed`
- `user_id` - UUID, required

**Relationships:**
- Belongs to one User

#### 2. Action
**Purpose:** Single, concrete, physical action that can be done

**Attributes:**
- `id` - Sequence
- `description` - string, required (starts with verb ideally)
- `notes` - text, optional
- `status` - enum: `next_action` | `waiting_for` | `scheduled` | `someday_maybe` | `completed`
- `created_date` - timestamp, auto-generated
- `completed_date` - timestamp, nullable
- `due_date` - date, nullable (hard deadline - rare in GTD)
- `scheduled_date` - date, nullable (when you plan to do it)
- `scheduled_time` - time, nullable (specific time if applicable)
- `is_time_specific` - boolean (distinguishes appointments from flexible tasks)
- `estimated_duration` - integer (minutes), nullable
- `energy_level` - enum: `high` | `medium` | `low`, nullable
- `location` - string, nullable (for time-specific actions)
- `project_id` - UUID, nullable
- `area_id` - UUID, nullable
- `user_id` - UUID, required

**Relationships:**
- Belongs to zero or one Project
- Belongs to zero or one Area
- Has many Contexts (many-to-many through ActionContext)
- Belongs to one User
- Has zero or one WaitingFor

#### 3. Project
**Purpose:** Any desired outcome requiring more than one action step

**Attributes:**
- `id` - Sequence
- `title` - string, required
- `description` - text, optional
- `desired_outcome` - text (clear success criteria)
- `status` - enum: `active` | `on_hold` | `completed` | `someday_maybe` | `cancelled`
- `created_date` - timestamp, auto-generated
- `completed_date` - timestamp, nullable
- `target_date` - date, nullable (target completion)
- `last_reviewed_date` - date, nullable
- `area_id` - UUID, required
- `user_id` - UUID, required

**Relationships:**
- Has many Actions
- Belongs to one Area
- Belongs to one User
- Has many References

#### 4. Context
**Purpose:** Tags representing location, tool, or circumstances needed to do work

**Attributes:**
- `id` - Sequence
- `name` - string, required, unique per user (e.g., "@home", "@computer")
- `description` - text, optional
- `color` - display color
- `icon` - string, optional (for UI)
- `user_id` - UUID, required

**Relationships:**
- Has many Actions (many-to-many through ActionContext)
- Belongs to one User

#### 5. Area
**Purpose:** Ongoing areas of life/work you're responsible for maintaining

**Attributes:**
- `id` - Sequence
- `name` - string, required (e.g., "Health", "Career")
- `description` - text, optional
- `sort_order` - integer (for user-defined ordering)
- `last_reviewed_date` - date, nullable
- `user_id` - UUID, required

**Relationships:**
- Has many Projects
- Has many Actions (standalone actions without projects)
- Belongs to one User

#### 6. Reference
**Purpose:** Non-actionable information you want to keep

**Attributes:**
- `id` - Sequence
- `title` - string, required
- `content` - text, optional
- `url` - string, optional (external link)
- `file_url` - string, optional (stored file)
- `tags` - array of strings
- `created_date` - timestamp, auto-generated
- `project_id` - UUID, nullable
- `user_id` - UUID, required

**Relationships:**
- Belongs to zero or one Project
- Belongs to one User

#### 7. WaitingFor
**Purpose:** Track items delegated to others or awaiting external input

**Attributes:**
- `id` - Sequence
- `description` - string, required
- `waiting_on_person` - string, nullable
- `waiting_on_organization` - string, nullable
- `waiting_since_date` - date, auto-generated
- `follow_up_date` - date, nullable
- `resolved_date` - date, nullable
- `notes` - text, optional
- `action_id` - UUID, nullable
- `project_id` - UUID, nullable
- `user_id` - UUID, required

**Relationships:**
- Belongs to zero or one Action
- Belongs to zero or one Project
- Belongs to one User

#### 8. Review
**Purpose:** Track review cycles

**Attributes:**
- `id` - Sequence
- `review_type` - enum: `daily` | `weekly` | `monthly` | `quarterly`
- `review_date` - date, auto-generated
- `notes` - text, optional
- `duration_minutes` - integer, optional
- `user_id` - UUID, required

**Relationships:**
- Belongs to one User

#### 9. User
**Purpose:** Person using the GTD system

**Attributes:**
- `id` - Sequence
- `email` - string, unique, required
- `name` - string, required
- `preferences` - JSON (system settings)
- `created_date` - timestamp

#### Attachment

**Purpose:** Store files associated with various entities

**Attributes:**
- `id` - UUID
- `filename` - string, required (original filename)
- `file_size` - integer (bytes)
- `mime_type` - string (e.g., "application/pdf", "image/jpeg")
- `file_url` - string, required (S3/cloud storage URL)
- `thumbnail_url` - string, nullable (for images)
- `uploaded_date` - timestamp, auto-generated
- `user_id` - UUID, required

**Relationships:**
- Belongs to Action OR Project OR Reference OR Item OR WaitingFor (polymorphic)
- Belongs to one User

### Junction Tables

#### ActionContext
**Attributes:**
- `action_id` - UUID
- `context_id` - UUID
- Primary key: (action_id, context_id)

#### ActionAttachment
**Attributes:**
- `action_id` - UUID, FK to Action, ON DELETE CASCADE
- `attachment_id` - UUID, FK to Attachment, ON DELETE CASCADE
- `attached_date` - timestamp, auto-generated
- `attached_by_user_id` - UUID, FK to User
- **PK:** (action_id, attachment_id)

#### ProjectAttachment
**Attributes:**
- `project_id` - UUID, FK to Project, ON DELETE CASCADE
- `attachment_id` - UUID, FK to Attachment, ON DELETE CASCADE
- `attached_date` - timestamp
- `attached_by_user_id` - UUID, FK to User
- **PK:** (project_id, attachment_id)

#### ReferenceAttachment
**Attributes:**
- `reference_id` - UUID, FK to Reference, ON DELETE CASCADE
- `attachment_id` - UUID, FK to Attachment, ON DELETE CASCADE
- `attached_date` - timestamp
- `attached_by_user_id` - UUID, FK to User
- **PK:** (reference_id, attachment_id)

#### ItemAttachment
**Attributes:**
- `item_id` - UUID, FK to Item, ON DELETE CASCADE
- `attachment_id` - UUID, FK to Attachment, ON DELETE CASCADE
- `attached_date` - timestamp
- `attached_by_user_id` - UUID, FK to User
- **PK:** (item_id, attachment_id)

#### WaitingForAttachment
**Attributes:**
- `waiting_for_id` - UUID, FK to WaitingFor, ON DELETE CASCADE
- `attachment_id` - UUID, FK to Attachment, ON DELETE CASCADE
- `attached_date` - timestamp
- `attached_by_user_id` - UUID, FK to User
- **PK:** (waiting_for_id, attachment_id)

## Use Cases with Acceptance Criteria

### UC-001: Capture Item to Inbox

**As a** user  
**I want to** quickly capture any thought, task, or idea into my inbox  
**So that** I can get it out of my head and process it later

#### Acceptance Criteria

**Given** I am an authenticated user  
**When** I submit a new item with a title  
**Then** the item is created with status "unprocessed"  
**And** the captured_date is set to current timestamp  
**And** the item is associated with my user account  
**And** I receive the created item with HTTP 201

**Given** I submit an item without a title  
**Then** I receive HTTP 400 with validation error

**Given** I submit an item with source "email"  
**Then** the source field is stored correctly

### UC-002: Process Inbox Item - Not Actionable

**As a** user  
**I want to** process an inbox item that is not actionable  
**So that** I can either delete it or store it as reference

#### Acceptance Criteria

**Given** I have an unprocessed inbox item  
**When** I decide it's not actionable and has no value  
**Then** I can delete the item  
**And** it's permanently removed from the system

**Given** I have an unprocessed inbox item  
**When** I decide it's reference material  
**Then** I can convert it to a Reference  
**And** the Reference is created with the item's title and description  
**And** the inbox item status is set to "processed"  
**And** optionally I can assign it to a Project

### UC-003: Process Inbox Item - Single Action

**As a** user  
**I want to** process an inbox item that requires a single action  
**So that** I can convert it into an actionable task

#### Acceptance Criteria

**Given** I have an unprocessed inbox item  
**When** I convert it to an Action  
**Then** an Action is created with the item's title as description  
**And** the Action status is set to "next_action"  
**And** I must assign at least one Context  
**And** I should assign an Area  
**And** the inbox item status is set to "processed"  
**And** I receive HTTP 201 with the created Action

**Given** I create an Action without any Context  
**Then** I receive HTTP 400 with validation error "Action must have at least one context"

### UC-004: Process Inbox Item - Multi-Step Project

**As a** user  
**I want to** process an inbox item that requires multiple steps  
**So that** I can create a Project with its first next action

#### Acceptance Criteria

**Given** I have an unprocessed inbox item  
**When** I convert it to a Project  
**Then** a Project is created with the item's title  
**And** the Project requires an Area assignment  
**And** the Project status is set to "active"  
**And** I must define at least one next Action for the Project  
**And** the inbox item status is set to "processed"  
**And** I receive HTTP 201 with the created Project and its first Action

**Given** I create a Project without an Area  
**Then** I receive HTTP 400 with validation error

**Given** I create a Project without a next Action  
**Then** I receive HTTP 400 with validation error "Active project must have at least one next action"

### UC-005: Create Standalone Action

**As a** user  
**I want to** create an action that doesn't belong to any project  
**So that** I can track one-off tasks

#### Acceptance Criteria

**Given** I am an authenticated user  
**When** I create an Action with description and at least one Context  
**Then** the Action is created with status "next_action"  
**And** project_id is null  
**And** I receive HTTP 201

**Given** I create an Action with multiple Contexts  
**Then** all Contexts are associated with the Action  
**And** I can query the Action and see all its Contexts

**Given** I create an Action with energy_level "low"  
**Then** the energy_level is stored correctly

### UC-006: Add Action to Existing Project

**As a** user  
**I want to** add a new action to an existing project  
**So that** I can break down my project into actionable steps

#### Acceptance Criteria

**Given** I have an active Project  
**When** I create an Action with that project_id  
**Then** the Action is created and associated with the Project  
**And** I receive HTTP 201

**Given** I add an Action to a "completed" Project  
**Then** I receive HTTP 400 with error "Cannot add actions to completed project"


### UC-007: Complete an Action

**As a** user  
**I want to** mark an action as completed  
**So that** I can track my progress

#### Acceptance Criteria

**Given** I have an Action with status "next_action"  
**When** I update its status to "completed"  
**Then** the status is updated  
**And** completed_date is set to current timestamp  
**And** I receive HTTP 200

**Given** the Action is the last remaining action in an active Project  
**When** I complete it  
**Then** I receive a notification "Project 'X' has no next actions"  
**And** the Project status remains "active" (user must explicitly complete project)

### UC-008: Get Next Actions by Context

**As a** user  
**I want to** view all available next actions filtered by context  
**So that** I can see what I can do in my current situation

#### Acceptance Criteria

**Given** I have 10 next actions with various contexts:
- 3 with @computer
- 2 with @phone
- 3 with both @home and @computer
- 2 with @errands

**When** I request next actions with context "@computer"  
**Then** I receive 6 actions (3 + 3 that have both @home and @computer)  
**And** all returned actions have status "next_action"  
**And** completed actions are not included

**When** I request next actions with contexts "@computer" AND "@home"  
**Then** I receive only 3 actions that have both contexts

**When** I request next actions with energy_level "low"  
**Then** I receive only actions marked with low energy level

**When** I request next actions with estimated_duration <= 30 minutes  
**Then** I receive only actions that can be done in 30 minutes or less

### UC-009: Create and Manage Contexts

**As a** user  
**I want to** create custom contexts  
**So that** I can organize my work by my unique circumstances

#### Acceptance Criteria

**Given** I am an authenticated user  
**When** I create a Context with name "@deep-work"  
**Then** the Context is created  
**And** I receive HTTP 201

**Given** I try to create a Context with a name that already exists for me  
**Then** I receive HTTP 409 with error "Context already exists"

**Given** another user has a Context "@deep-work"  
**When** I create a Context with the same name  
**Then** it succeeds (contexts are user-specific)

### UC-010: Create and Manage Areas

**As a** user  
**I want to** define my areas of responsibility  
**So that** I can organize my projects and actions by life domains

#### Acceptance Criteria

**Given** I am a new user  
**When** I create Areas: "Health", "Career", "Finance", "Family"  
**Then** all Areas are created with sort_order based on creation sequence  
**And** I receive HTTP 201 for each

**Given** I have multiple Areas  
**When** I request all my Areas  
**Then** they are returned sorted by sort_order  
**And** each Area shows count of active Projects and next Actions

### UC-011: Move Action Between Projects

**As a** user  
**I want to** move an action from one project to another  
**So that** I can reorganize when I realize an action belongs elsewhere

#### Acceptance Criteria

**Given** I have an Action in Project A  
**When** I update the Action's project_id to Project B  
**Then** the Action is now associated with Project B  
**And** I receive HTTP 200

**Given** I have an Action in a Project  
**When** I set project_id to null  
**Then** the Action becomes a standalone action  
**And** I receive HTTP 200

### UC-012: Mark Action as Waiting For

**As a** user  
**I want to** mark an action as waiting for someone else  
**So that** I can track delegated items and follow up appropriately

#### Acceptance Criteria

**Given** I have a next action "Review contract draft"  
**When** I change its status to "waiting_for"  
**Then** I must create a WaitingFor record  
**And** the WaitingFor record requires either waiting_on_person or waiting_on_organization  
**And** waiting_since_date is set to current date  
**And** I can optionally set a follow_up_date  
**And** I receive HTTP 200

**Given** I have an Action with status "waiting_for"  
**When** I query it  
**Then** I see the associated WaitingFor details

**Given** I have a WaitingFor record  
**When** I mark it resolved  
**Then** resolved_date is set to current date  
**And** I should update the Action status (usually to "next_action" or "completed")

### UC-013: Schedule Action for Specific Date

**As a** user  
**I want to** schedule an action for a specific date or date/time  
**So that** I can plan when to work on things

#### Acceptance Criteria

**Given** I have a next action  
**When** I set scheduled_date to "2025-01-15"  
**Then** the date is stored  
**And** the Action still appears in my next actions list (with a date indicator)

**Given** I have an action "Team meeting"  
**When** I set scheduled_date to "2025-01-15", scheduled_time to "14:00", and is_time_specific to true  
**Then** the Action represents a time-specific commitment  
**And** I can set location "Conference Room B"

**Given** I query my actions for a specific date  
**Then** I see all actions scheduled for that date  
**And** time-specific actions are sorted by time

### UC-014: Move Action/Project to Someday/Maybe

**As a** user  
**I want to** move actions or projects to someday/maybe  
**So that** I can defer things I might do later without losing track of them

#### Acceptance Criteria

**Given** I have a next action  
**When** I change its status to "someday_maybe"  
**Then** the status is updated  
**And** it no longer appears in my next actions list  
**And** it appears in my someday/maybe list

**Given** I have an active Project  
**When** I change its status to "someday_maybe"  
**Then** all its associated Actions should also move to "someday_maybe" status  
**And** I receive a confirmation prompt before this bulk update

### UC-015: Complete a Project

**As a** user  
**I want to** mark a project as completed  
**So that** I can archive finished projects

#### Acceptance Criteria

**Given** I have an active Project with some completed actions and some next actions  
**When** I try to mark the Project as "completed"  
**Then** I receive HTTP 400 with warning "Project has incomplete actions"  
**And** I'm prompted to complete or move those actions first

**Given** I have an active Project where all actions are completed  
**When** I mark the Project as "completed"  
**Then** the Project status updates to "completed"  
**And** completed_date is set to current timestamp  
**And** I receive HTTP 200

**Given** I forcefully complete a Project with incomplete actions  
**When** I include a force=true parameter  
**Then** all incomplete actions are automatically marked as "completed"  
**And** the Project is completed

### UC-016: Weekly Review - Review Projects by Area

**As a** user  
**I want to** review all my projects grouped by area  
**So that** I can ensure every active project has a next action

#### Acceptance Criteria

**Given** I start a weekly review  
**When** I request all Areas with their Projects and Actions  
**Then** I receive all Areas sorted by sort_order  
**And** each Area shows:
- All active Projects with their next action count
- All standalone next actions in the Area
- All someday/maybe projects

**Given** I see a Project with zero next actions  
**Then** it should be highlighted/flagged  
**And** I should create a next action or move it to on_hold/someday_maybe

**Given** I complete reviewing an Area  
**When** I update that Area  
**Then** last_reviewed_date is set to current date

**Given** I complete a weekly review  
**When** I create a Review record with type "weekly"  
**Then** the Review is stored with current date  
**And** I can add notes about insights or decisions

### UC-017: Get Dashboard / Overview

**As a** user  
**I want to** see a dashboard of my GTD system  
**So that** I can quickly understand my current state

#### Acceptance Criteria

**Given** I request my dashboard  
**Then** I receive:
- Unprocessed inbox count
- Next actions count by context
- Active projects count by area
- Waiting for items count
- Scheduled actions for next 7 days
- Someday/maybe counts
- Actions with due dates approaching
- Areas not reviewed in > 7 days

### UC-018: Search Actions and Projects

**As a** user  
**I want to** search across all my actions and projects  
**So that** I can quickly find specific items

#### Acceptance Criteria

**Given** I have actions and projects with various text  
**When** I search for "dentist"  
**Then** I receive all Actions and Projects containing "dentist" in title, description, or notes  
**And** results are grouped by type (Actions, Projects)

**Given** I search with filters  
**When** I search for "budget" in Area "Finance"  
**Then** I only receive results from that Area

### UC-019: Bulk Update Actions

**As a** user  
**I want to** update multiple actions at once  
**So that** I can efficiently reorganize my system

#### Acceptance Criteria

**Given** I select multiple actions  
**When** I add a new Context "@focus" to all of them  
**Then** all actions are updated with the new Context  
**And** their existing Contexts remain

**Given** I select multiple actions  
**When** I change their Area to "Career"  
**Then** all actions are updated  
**And** I receive HTTP 200

### UC-020: Get Time-Specific Actions (Today's Schedule)

**As a** user  
**I want to** view all time-specific actions for today  
**So that** I can see my appointments and commitments

#### Acceptance Criteria

**Given** I have actions with various scheduled dates and times  
**When** I request today's time-specific actions  
**Then** I receive only actions where:
- scheduled_date = today
- is_time_specific = true
- status != "completed"  
  **And** results are sorted by scheduled_time

**Given** I have a time-specific action at 14:00  
**When** I request actions for that specific time range  
**Then** I receive that action

### UC-021: Handle Action Without Context (Validation)

**As a** user  
**I want to** be prevented from creating actions without contexts  
**So that** my actions remain actionable and filterable

#### Acceptance Criteria

**Given** I try to create an Action without any Context  
**Then** I receive HTTP 400  
**And** error message: "Action must have at least one context"

**Given** I try to remove all Contexts from an existing Action  
**Then** I receive HTTP 400  
**And** error message: "Action must have at least one context"

### UC-022: Handle Project Without Area (Validation)

**As a** user  
**I want to** be prevented from creating projects without an area  
**So that** my projects are properly categorized

#### Acceptance Criteria

**Given** I try to create a Project without area_id  
**Then** I receive HTTP 400  
**And** error message: "Project must be assigned to an Area"

### UC-023: Query Waiting For Items Due for Follow-up

**As a** user  
**I want to** see all waiting-for items that need follow-up  
**So that** I can take action on stalled items

#### Acceptance Criteria

**Given** I have WaitingFor items with various follow_up_dates  
**When** I request items due for follow-up  
**Then** I receive all items where:
- follow_up_date <= today
- resolved_date is null  
  **And** results are sorted by follow_up_date (oldest first)

### UC-024: Archive Completed Projects

**As a** user  
**I want to** view and manage completed projects separately  
**So that** my active project list stays focused

#### Acceptance Criteria

**Given** I have multiple completed projects  
**When** I request completed projects  
**Then** I receive all projects with status "completed"  
**And** they are sorted by completed_date (most recent first)

**Given** I want to see only active work  
**When** I request projects without specifying status  
**Then** only active projects are returned by default

### UC-025: Prevent Orphaned Actions When Deleting Project

**As a** user  
**I want to** be notified when deleting a project that has actions  
**So that** I can decide what to do with those actions

#### Acceptance Criteria

**Given** I try to delete a Project with associated Actions  
**Then** I receive HTTP 409  
**And** error message: "Project has N associated actions. Delete or reassign them first."

**Given** I force-delete a Project  
**When** I include force=true and specify action_strategy="delete"  
**Then** the Project and all its Actions are deleted

**Given** I force-delete a Project  
**When** I include force=true and specify action_strategy="make_standalone"  
**Then** the Project is deleted  
**And** all its Actions have project_id set to null

### UC-026: Get Actions by Multiple Filters

**As a** user  
**I want to** combine multiple filters when querying actions  
**So that** I can find exactly what I'm looking for

#### Acceptance Criteria

**Given** I have various actions  
**When** I query with:
- contexts: ["@computer", "@office"]
- energy_level: "high"
- area_id: <career_area_id>
- estimated_duration: <= 60
- status: "next_action"  
  **Then** I receive only actions matching ALL criteria  
  **And** results are sorted by created_date (oldest first, assuming priority)

### UC-027: Update Action Contexts

**As a** user  
**I want to** modify the contexts assigned to an action  
**So that** I can correct or refine where an action can be done

#### Acceptance Criteria

**Given** I have an Action with contexts ["@computer", "@office"]  
**When** I update it to have contexts ["@computer", "@home"]  
**Then** the Action is associated with the new contexts  
**And** "@office" is removed  
**And** I receive HTTP 200

**Given** I try to update an Action to have no contexts  
**Then** I receive HTTP 400  
**And** error message: "Action must have at least one context"

### UC-028: Get Inbox Summary

**As a** user  
**I want to** see how many unprocessed items I have  
**So that** I know when I need to do inbox processing

#### Acceptance Criteria

**Given** I have 15 unprocessed and 50 processed items  
**When** I request inbox summary  
**Then** I receive:
- unprocessed_count: 15
- processed_count: 50
- oldest_unprocessed_date: <date of oldest unprocessed item>

### UC-029: Create Reference Material

**As a** user  
**I want to** store non-actionable information  
**So that** I can reference it later

#### Acceptance Criteria

**Given** I have information I want to keep  
**When** I create a Reference with title and content  
**Then** the Reference is created  
**And** I can optionally assign it to a Project  
**And** I can add tags for searchability  
**And** I receive HTTP 201

**Given** I create a Reference with a URL  
**When** I later retrieve it  
**Then** I see the URL for easy access

### UC-030: Query Actions by Due Date

**As a** user  
**I want to** see actions with approaching due dates  
**So that** I don't miss hard deadlines

#### Acceptance Criteria

**Given** I have actions with various due dates  
**When** I request actions with due_date <= 7 days from now  
**Then** I receive all actions due within a week  
**And** they are sorted by due_date (soonest first)  
**And** only non-completed actions are included

**Given** I have an action with due_date = today  
**When** I request overdue and due-today actions  
**Then** I see that action flagged appropriately


### UC-031: Add Attachment to Action

**As a** user  
**I want to** attach files to an action  
**So that** I have all necessary context when working on it

**Acceptance Criteria:**

**Given** I have a next action "Review contract"  
**When** I upload a PDF file  
**Then** an Attachment record is created  
**And** the file is stored in cloud storage  
**And** file_url points to the stored file  
**And** attachable_type = "action"  
**And** attachable_id = action.id  
**And** I receive HTTP 201

**Given** I upload a file larger than 25MB  
**Then** I receive HTTP 413 with error "File too large"

**Given** I upload a file with malicious content  
**Then** the file is scanned and rejected if dangerous  
**And** I receive HTTP 400

---

### UC-032: View Attachments for an Entity

**As a** user  
**I want to** see all attachments for an action/project/reference  
**So that** I can access relevant files

**Acceptance Criteria:**

**Given** I have an Action with 3 attachments  
**When** I request GET /actions/{id}/attachments  
**Then** I receive all 3 attachments  
**And** each includes filename, file_size, mime_type, file_url, uploaded_date

**Given** an attachment is an image  
**Then** thumbnail_url is also provided for preview

---

### UC-033: Delete Attachment

**As a** user  
**I want to** remove attachments I no longer need  
**So that** I can keep my system clean

**Acceptance Criteria:**

**Given** I have an attachment  
**When** I delete it  
**Then** the Attachment record is deleted  
**And** the file is deleted from cloud storage  
**And** I receive HTTP 204

---

### UC-034: Move Attachment Between Entities

**As a** user  
**I want to** move an attachment from an Inbox Item to an Action  
**So that** context is preserved during processing

**Acceptance Criteria:**

**Given** I have an Inbox Item with an attachment  
**When** I convert it to an Action  
**Then** I can optionally copy the attachment to the new Action  
**And** attachable_type updates to "action"  
**And** attachable_id updates to the new action's id

---

### UC-035: Download Attachment

**As a** user  
**I want to** download attachments  
**So that** I can work with the files locally

**Acceptance Criteria:**

**Given** I have an attachment  
**When** I request to download it  
**Then** I receive a signed URL (if using S3) OR direct file download  
**And** the response includes proper Content-Disposition headers  
**And** filename matches the original filename

--- 

## Implementation Considerations

### Storage Strategy

**Cloud Storage (Recommended):**
- Use S3, Google Cloud Storage, Azure Blob Storage
- Store `file_url` as the cloud URL
- Generate signed URLs for secure access
- Easier to scale
- Lower server load

### Security


**Access Control:**
- Users can only access their own attachments
- Validate attachable_id belongs to the requesting user
- Use signed/expiring URLs for downloads

**Virus Scanning:**
- Scan uploads for malware
- Reject dangerous file types (.exe, .bat, etc.)

**File Size Limits:**
- Per-file limit: 25MB recommended
- Per-user storage quota: 1GB free tier, more for paid

**Allowed MIME Types:**
- Documents: pdf, doc, docx, txt, md
- Images: jpeg, jpg, png, gif, webp
- Spreadsheets: xls, xlsx, csv
- Archives: zip (be cautious)
- Block: exe, bat, sh, js, html (code execution risk)