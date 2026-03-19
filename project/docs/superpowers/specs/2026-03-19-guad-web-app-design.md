# Guad Web App Design Spec

A full GTD web application built with Vue 3, shadcn-vue (reka-ui), Tailwind CSS 4, and Pinia. Connects to the existing Spring Boot backend REST API. Follows the same BW (Baden-Württemberg) design identity as the mobile app.

## Decisions

- **Scope:** Full GTD web app — same features as mobile, adapted for desktop. No admin/user management views.
- **Navigation:** Collapsible sidebar (existing shadcn-vue sidebar component), not tabs.
- **Quick Capture:** Todoist-style — inline "Add Task" form in list views + `Q` keyboard shortcut for a floating quick-capture modal.
- **Task Detail:** Side panel sliding in from the right when a task is clicked, keeping the list visible.
- **Build Approach:** Screen-by-screen, API-connected. Each page gets its own store, API service, and components. Usable after the first page ships.

## Design Identity

Identical to the mobile app prototype (see `project/design/guad-prototype.html`).

### Color Palette (BW Corporate Design)

| Token | Hex | Usage |
|-------|-----|-------|
| Türkis Dunkel | `#094954` | Sidebar active, dark accents |
| Türkis Hell | `#309AAF` | Primary actions, links, active states |
| Türkis Surface | `#e8f4f6` | Selected rows, badges, highlights |
| BaWü Gelb | `#FFFC00` | Important highlights (sparingly on web) |
| Orange Hell | `#DD6F06` | Due soon, age warnings |
| Rot Hell | `#FD4D4D` | Overdue, destructive actions |
| BaWü Schwarz | `#2A2623` | Primary text |
| Grau 5 | `#6D6766` | Secondary text |
| Grau 3 | `#BBB6B5` | Borders, placeholders |
| Grau 2 | `#E4E0E0` | Light dividers |
| Grau 1 | `#F4F3F3` | Backgrounds, subtle fills |
| White | `#FFFFFF` | Page background |
| Grün Hell | `#609D0F` | Completed, success |
| Blau Hell | `#508CF1` | Info, person tags |
| Lila Hell | `#C761EC` | Someday/Maybe |

### Typography

- **Page titles:** Georgia (serif), 24px, bold — warmth and character
- **Section headers:** System sans-serif, 12px, semibold, uppercase, türkis — `letter-spacing: 0.5px`
- **Body/task items:** System sans-serif, 14px, regular
- **Metadata:** System sans-serif, 12px, Grau 3 or Grau 5

### Swabian Personality

Empty states use Swabian dialect:
- Empty inbox: *"Alles guad! Nix zum schaffe."*
- All done: *"Des passt! Feierabend."*
- Review complete: *"Sauber gmacht!"*

## App Layout

Three-panel layout:

### Sidebar (240px, collapsible)

Uses the existing shadcn-vue `Sidebar` component. Structure top-to-bottom:

1. **Brand header:** Guad logo (türkis-dark square with "G") + "Guad" / "GTD System"
2. **Add Task button:** Türkis text, `+` icon, shows `Q` shortcut hint. Opens inline add-task in current view.
3. **Primary nav:** Inbox (with unprocessed count badge), Next Actions (count), Projects (count), Waiting For (count), Someday/Maybe
4. **Review section:** Weekly Review (shows "Due" badge when ≥7 days since last review)
5. **Manage section:** Contexts, Areas, Settings
6. **User footer:** Avatar + name + email, logout

Active item: `turkis-surface` background, `turkis-dark` text.
Badges: Grau-2 background normally, türkis background on active item.

### Main Content (flexible width)

- Serif page title (24px bold) + subtitle (13px, grau-5)
- Content area with task lists, project lists, etc.
- Inline "Add Task" row at bottom of lists

### Task Detail Side Panel (340px, conditional)

Slides in from right when a task is clicked. The selected task row highlights with `turkis-surface`.

- **Header:** Close (✕) button + Save button
- **Title:** Checkbox + task title (18px, semibold)
- **Metadata fields** (key-value rows separated by grau-1 dividers):
  - Project — name + chevron, clickable
  - Context — chip with turkis-surface background
  - Due date — orange if upcoming
  - Area — plain text
  - Energy — plain text (High/Medium/Low)
  - Time needed — plain text
- **Notes:** Label + grau-1 background container
- **Delete:** Red "Delete Task" text at bottom

## Pages

### Inbox

**Purpose:** Capture and triage incoming items.

**Content:**
- Task list: checkbox + title + "Added {time}" metadata
- Inline "Add Task" form (Todoist-style): expands to show title input, note placeholder, toolbar buttons (Project, Context, Due), "Add Task" submit button
- Triage action bar below the list: horizontal row of quick-process buttons — ⚡ Next Action, 📁 To Project, ⏳ Waiting For, 💭 Someday, 🗑️ Trash. These appear contextually when item(s) are selected.

**Empty state:** Teal circle with checkmark, "Alles guad!", "Nix zum schaffe. Press Q to capture something new."

**API:** `GET /api/inbox`, `POST /api/inbox`, `DELETE /api/inbox/{id}`, `POST /api/inbox/{id}/process`

### Next Actions

**Purpose:** View and work on actionable tasks grouped by context.

**Content:**
- Filter chips: All (active by default), then one chip per context. Active chip: turkis-dark bg, white text. Inactive: grau-1 bg.
- Tasks grouped by context section headers (12px, uppercase, türkis)
- Each row: checkbox + title + project name + optional due date (orange)
- Inline "Add Task" at bottom

**API:** `GET /api/actions?status=NEXT`, `GET /api/contexts`, `PATCH /api/actions/{id}/complete`

### Projects

**Purpose:** Overview of all active projects grouped by area.

**Content:**
- Project rows grouped by area section headers
- Each row: project name (14px, medium weight) + "{n} next actions" metadata + chevron
- Clicking navigates to Project Detail sub-page

**API:** `GET /api/projects`

### Project Detail (sub-page of Projects)

**Purpose:** Full view of a single project.

**Content:**
- Back link "← Projects" + Edit button
- Project name (serif, 24px bold) + area name + outcome statement (italic, grau-5)
- Three sections:
  1. **Next Actions** (türkis header) — task rows, clickable to open side panel
  2. **Waiting For** (blau header) — person tag + delegation date
  3. **Completed** (grün header) — green checkmarks, strikethrough titles, reduced opacity
- Dashed "+ Add next action" button at bottom

**API:** `GET /api/projects/{id}` (returns ProjectDetailResponse with actions, waitingFor, completed)

### Waiting For

**Purpose:** Track delegated items.

**Content:**
- Item rows: title + project name + person tag (blue 👤) + delegation date
- Older items show age in orange: "Delegated Mar 5 · 14 days"
- "Add waiting for item" button at bottom

**API:** `GET /api/waiting-for`, `POST /api/waiting-for`, `PATCH /api/waiting-for/{id}/resolve`

### Someday/Maybe

**Purpose:** Ideas and deferred items.

**Content:**
- Two sections: Actions and Projects (matching the API response)
- Action rows similar to Next Actions
- Project rows similar to Projects list

**API:** `GET /api/someday-maybe`

### Weekly Review (wizard)

**Purpose:** Guided 6-step weekly review process.

**Content:**
- Top bar: "Exit Review" link + "Step {n} of 6" counter
- Progress bar: 4px track, türkis fill proportional to step
- Step title (serif, 24px bold) + guiding question (14px, grau-5)
- Checklist: reviewed items (green check, grayed text), current item (yellow highlight, bold), pending items (empty checkbox)
- Bottom: step breadcrumbs (completed green ✓, current bold, pending gray) + "Next: {step name} →" button (turkis-dark bg)

**Steps:** 1. Clear Inbox, 2. Review Next Actions, 3. Review Projects, 4. Review Waiting For, 5. Review Someday/Maybe, 6. Done — "Sauber gmacht!"

**API:** `POST /api/reviews` (start), `GET /api/reviews/current`, `PATCH /api/reviews/{id}/step`, `POST /api/reviews/{id}/complete`

### Contexts (manage page)

**Content:** Simple list of contexts with name, description, color. Add/edit/delete.

**API:** `GET /api/contexts`, `POST /api/contexts`, `PUT /api/contexts/{id}`, `DELETE /api/contexts/{id}`

### Areas (manage page)

**Content:** Simple list of areas with name and description. Add/edit/delete.

**API:** `GET /api/areas`, `POST /api/areas`, `PUT /api/areas/{id}`, `DELETE /api/areas/{id}`

## Quick Capture

Two entry points:

### Inline Add Task
- Appears at the bottom of task lists (Inbox, Next Actions, Project Detail)
- Collapsed: "+ Add task" row with türkis color
- Expanded: bordered container with title input (15px), note placeholder (13px, grau-3), toolbar row (Project, Context, Due buttons) + "Add Task" submit button

### Quick Capture Modal (`Q` shortcut)
- Global keyboard shortcut `Q` opens a centered modal dialog
- Dimmed backdrop (`rgba(42,38,35,0.15)`)
- Modal (480px wide): serif title "Quick Capture", same form as inline add-task but with türkis border
- Keyboard shortcuts: `Esc` to close, `Cmd/Ctrl+Enter` to save
- Saves to Inbox by default

## Technical Architecture

### Stack
- **Vue 3** with Composition API + `<script setup>`
- **Vue Router** for navigation
- **Pinia** for state management (one store per feature)
- **Tailwind CSS 4** for styling
- **shadcn-vue (reka-ui)** for UI components (sidebar, sheet, dialog, button, input, etc.)
- **Lucide Vue Next** for icons
- **VueUse** for composables (keyboard shortcuts, etc.)

### Project Structure
```
web/src/
├── components/
│   ├── app/              # App shell components
│   │   ├── AppSidebar.vue
│   │   ├── QuickCaptureModal.vue
│   │   └── TaskDetailPanel.vue
│   ├── task/             # Shared task components
│   │   ├── TaskRow.vue
│   │   ├── TaskCheckbox.vue
│   │   ├── InlineAddTask.vue
│   │   └── TriageBar.vue
│   └── ui/               # shadcn-vue components (existing)
├── views/
│   ├── InboxView.vue
│   ├── NextActionsView.vue
│   ├── ProjectsView.vue
│   ├── ProjectDetailView.vue
│   ├── WaitingForView.vue
│   ├── SomedayMaybeView.vue
│   ├── WeeklyReviewView.vue
│   ├── ContextsView.vue
│   ├── AreasView.vue
│   ├── SettingsView.vue
│   ├── LoginView.vue       # existing
│   ├── SignUpView.vue       # existing
│   └── OTPVerificationView.vue  # existing
├── stores/
│   ├── inbox.ts
│   ├── actions.ts
│   ├── projects.ts
│   ├── waitingFor.ts
│   ├── somedayMaybe.ts
│   ├── contexts.ts
│   ├── areas.ts
│   ├── review.ts
│   └── auth.ts
├── api/
│   ├── client.ts          # Axios/fetch wrapper with JWT interceptor
│   ├── inbox.ts
│   ├── actions.ts
│   ├── projects.ts
│   ├── waitingFor.ts
│   ├── somedayMaybe.ts
│   ├── contexts.ts
│   ├── areas.ts
│   ├── review.ts
│   └── auth.ts
├── types/
│   └── index.ts           # TypeScript interfaces matching backend DTOs
├── composables/
│   ├── useQuickCapture.ts  # Q shortcut + modal state
│   └── useTaskDetail.ts    # Side panel state
├── router.ts
├── main.ts
├── App.vue
└── styles.css
```

### Auth Flow
- Login/SignUp/OTP pages are unauthenticated routes
- JWT token stored in memory (Pinia auth store) + localStorage for persistence
- API client attaches `Authorization: Bearer {token}` header
- Router guard redirects to `/login` if no token
- Token refresh handled by interceptor

### Routing
```
/login              → LoginView (no sidebar)
/signup             → SignUpView (no sidebar)
/otp                → OTPVerificationView (no sidebar)

/ (SidebarLayout)
  /inbox            → InboxView
  /next-actions     → NextActionsView
  /projects         → ProjectsView
  /projects/:id     → ProjectDetailView
  /waiting-for      → WaitingForView
  /someday-maybe    → SomedayMaybeView
  /weekly-review    → WeeklyReviewView
  /contexts         → ContextsView
  /areas            → AreasView
  /settings         → SettingsView
```

Default route `/` redirects to `/inbox`.

### Cleanup from Scaffolding
Remove existing placeholder content:
- Delete `views/DashboardView.vue`
- Delete `views/users/` directory
- Delete `views/InboxItem.vue`
- Delete `components/HelloWorld.vue`, `TheWelcome.vue`, `WelcomeItem.vue`
- Delete `components/icons/IconCommunity.vue`, `IconDocumentation.vue`, `IconEcosystem.vue`, `IconSupport.vue`, `IconTooling.vue`
- Delete `stores/counter.ts`
- Delete `components/__tests__/HelloWorld.spec.ts`
- Update router to remove dashboard and users routes

## Build Order

### Phase 1 — Core Shell
1. Auth (login, signup, OTP, JWT token management, route guards)
2. App shell (sidebar navigation with all items, routing, SidebarLayout update)
3. API client with auth interceptor

### Phase 2 — GTD Core
4. Inbox (list, inline add task, triage processing)
5. Next Actions (context-grouped list, filter chips, complete action)
6. Task detail side panel (shared component, metadata editing)
7. Quick capture modal (`Q` shortcut)

### Phase 3 — Expand
8. Projects (list + detail sub-page)
9. Waiting For (list, add, resolve)
10. Someday/Maybe (list view)
11. Weekly Review wizard (6-step flow)

### Phase 4 — Manage
12. Contexts management (CRUD)
13. Areas management (CRUD)
14. Settings page

## Visual Mockups

Interactive mockups are available in:
- `project/.superpowers/brainstorm/54945-1773938866/design-layout.html` — full 3-panel layout mockup
- `project/.superpowers/brainstorm/54945-1773938866/design-pages.html` — all page designs
- `project/design/guad-prototype.html` — original mobile prototype (design reference)
