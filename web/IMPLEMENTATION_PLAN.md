# GTD Admin web - Implementation Plan

## Overview

This document outlines the views, components, and implementation order for the **GTD Admin web** - an administrative interface for managing and monitoring the GTD task management system across all users.

**Key Characteristics:**

- Admin-focused interface (not user-facing)
- Multi-user data management
- System-wide monitoring and metrics
- Bulk operations support
- Data management and troubleshooting tools

---

## 📋 Views Required

### Authentication Views (✅ Already Implemented)

- `LoginView.vue` - Admin login
- `SignUpView.vue` - Admin registration (if needed)
- `OTPVerificationView.vue` - OTP verification

### Core Admin Views

#### 1. Admin Dashboard/Overview View

- **File:** `DashboardView.vue` (or update `HomeView.vue`)
- **Purpose:** System-wide monitoring and metrics
- **Shows:**
  - Total users count
  - Active users (last 30 days)
  - Total inbox items (unprocessed/processed)
  - Total actions by status
  - Total projects by status
  - System storage usage (attachments)
  - Recent activity/events
  - System health indicators
  - Top users by activity
  - Data growth trends (charts)

#### 2. Users Management View

- **File:** `UsersView.vue`
- **Purpose:** Manage all users in the system
- **Features:**
  - List of all users (table view)
  - Filter by registration date, activity status
  - Search users by email/name
  - View user details
  - Edit user information
  - Deactivate/activate users
  - View user statistics (actions, projects, etc.)
  - Export user list

#### 3. User Detail View

- **File:** `UserDetailView.vue`
- **Purpose:** View and manage individual user's data
- **Features:**
  - User profile information
  - User statistics dashboard
  - Quick links to user's data (actions, projects, etc.)
  - Activity timeline
  - Account management actions

#### 4. Inbox Management View

- **File:** `InboxView.vue`
- **Purpose:** UC-001, UC-002, UC-003, UC-004, UC-028 - View and manage inbox items across all users
- **Features:**
  - List of all inbox items (with user filter)
  - Filter by user, status, date range
  - Search inbox items
  - View item details
  - Bulk operations (delete, mark processed)
  - Process item on behalf of user (if needed)
  - Export data

#### 5. Actions Management View

- **File:** `ActionsView.vue`
- **Purpose:** UC-005, UC-008, UC-020, UC-026 - View and manage actions across all users
- **Features:**
  - List of all actions (table view)
  - Filter by user, context(s), status, energy level, area, date range
  - Search actions
  - Bulk operations (update status, assign context, delete)
  - Export actions data
  - View action statistics by user/context/area

#### 6. Action Detail View

- **File:** `ActionDetailView.vue`
- **Purpose:** UC-007, UC-012, UC-013, UC-014, UC-027 - View/edit any user's action
- **Features:**
  - View action details (with user info)
  - Edit action details (admin override)
  - View/edit contexts
  - View waiting for information
  - View attachments
  - Activity history/audit log
  - Delete action (with confirmation)

#### 7. Projects Management View

- **File:** `ProjectsView.vue`
- **Purpose:** UC-004, UC-006, UC-015, UC-024 - View and manage projects across all users
- **Features:**
  - List of all projects (table view)
  - Filter by user, area, status, date range
  - Search projects
  - Bulk operations (update status, assign area, delete)
  - Export projects data
  - View project statistics

#### 8. Project Detail View

- **File:** `ProjectDetailView.vue`
- **Purpose:** UC-004, UC-006, UC-015 - View/edit any user's project
- **Features:**
  - View project details (with user info)
  - Edit project information (admin override)
  - View associated actions
  - View references and attachments
  - Activity history/audit log
  - Delete project (with confirmation)

#### 9. Contexts Management View

- **File:** `ContextsView.vue`
- **Purpose:** UC-009 - View and manage contexts across all users
- **Features:**
  - List of all contexts (grouped by user)
  - Filter by user
  - Search contexts
  - View context usage statistics
  - Edit/delete contexts (admin override)
  - Merge duplicate contexts (if needed)

#### 10. Areas Management View

- **File:** `AreasView.vue`
- **Purpose:** UC-010 - View and manage areas across all users
- **Features:**
  - List of all areas (grouped by user)
  - Filter by user
  - Search areas
  - View area statistics (projects, actions count)
  - Edit/delete areas (admin override)

#### 11. References Management View

- **File:** `ReferencesView.vue`
- **Purpose:** UC-002, UC-029 - View and manage references across all users
- **Features:**
  - List of all references (with user filter)
  - Filter by user, project, tags
  - Search references
  - View/edit/delete references
  - Export references

#### 12. Waiting For Management View

- **File:** `WaitingForView.vue`
- **Purpose:** UC-012, UC-023 - View and manage waiting for items across all users
- **Features:**
  - List of all waiting for items (with user filter)
  - Filter by user, follow-up date, resolved status
  - Search waiting for items
  - View/edit/delete waiting for items
  - Bulk mark as resolved

#### 13. Attachments Management View

- **File:** `AttachmentsView.vue`
- **Purpose:** UC-031, UC-032, UC-033 - Manage file attachments
- **Features:**
  - List of all attachments (with user filter)
  - Filter by user, entity type, file type, date
  - Search attachments
  - View attachment details
  - Download attachments
  - Delete attachments (with storage cleanup)
  - Storage usage statistics
  - Orphaned attachments detection

#### 14. Activity/Audit Log View

- **File:** `ActivityLogView.vue`
- **Purpose:** Track system activity and changes
- **Features:**
  - List of system events/activities
  - Filter by user, action type, date range
  - Search activity log
  - View detailed event information
  - Export audit logs
  - User action timeline

#### 15. System Health/Monitoring View

- **File:** `SystemHealthView.vue`
- **Purpose:** Monitor system status and health
- **Features:**
  - API health status
  - Database connection status
  - Storage usage (attachments)
  - Error rates
  - Performance metrics
  - Recent errors/alerts
  - System configuration

#### 16. Search View

- **File:** `SearchView.vue`
- **Purpose:** UC-018 - Global search across all entities and users
- **Features:**
  - Global search across all data types
  - Filter by entity type, user
  - Search results grouped by type and user
  - Advanced search filters

---

## 🧩 Components Required

### Shared/Reusable Components

#### Admin-Specific Components

- `UserSelector.vue` - User filter/selector (used across all views)
- `UserBadge.vue` - Display user info badge
- `BulkActionsBar.vue` - Bulk action toolbar
- `DataTable.vue` - Advanced data table with sorting, filtering, pagination
- `ExportButton.vue` - Export data functionality
- `ActivityTimeline.vue` - Activity/audit log timeline
- `SystemHealthCard.vue` - System health indicator card
- `UserStatsCard.vue` - User statistics card
- `FilterPanel.vue` - Advanced filter panel
- `DateRangeFilter.vue` - Date range picker for filtering

#### Action Components

- `ActionCard.vue` - Display action in card format (with user info)
- `ActionTable.vue` - Table view for actions (admin-focused)
- `ActionList.vue` - List of actions with filtering
- `ActionForm.vue` - Create/edit action form (with user selector)
- `ActionStatusBadge.vue` - Status indicator badge
- `ContextSelector.vue` - Multi-select context picker
- `EnergyLevelSelector.vue` - Energy level picker
- `DurationSelector.vue` - Estimated duration input

#### Project Components

- `ProjectCard.vue` - Display project in card format (with user info)
- `ProjectTable.vue` - Table view for projects (admin-focused)
- `ProjectList.vue` - List of projects
- `ProjectForm.vue` - Create/edit project form (with user selector)
- `ProjectStatusBadge.vue` - Project status indicator
- `ProjectActionsList.vue` - Actions within a project

#### Inbox Components

- `InboxItemCard.vue` - Display inbox item (with user info)
- `InboxItemTable.vue` - Table view for inbox items (admin-focused)
- `InboxItemList.vue` - List of inbox items
- `ProcessItemDialog.vue` - Modal for processing inbox item (admin override)

#### Context Components

- `ContextBadge.vue` - Context tag/badge
- `ContextList.vue` - List of contexts
- `ContextForm.vue` - Create/edit context form
- `ContextColorPicker.vue` - Color picker for contexts

#### Area Components

- `AreaCard.vue` - Display area
- `AreaList.vue` - List of areas
- `AreaForm.vue` - Create/edit area form
- `AreaStats.vue` - Statistics for an area

#### Reference Components

- `ReferenceCard.vue` - Display reference
- `ReferenceList.vue` - List of references
- `ReferenceForm.vue` - Create/edit reference form
- `TagInput.vue` - Tag input component

#### Waiting For Components

- `WaitingForCard.vue` - Display waiting for item
- `WaitingForList.vue` - List of waiting for items
- `WaitingForForm.vue` - Create/edit waiting for form

#### Attachment Components

- `AttachmentList.vue` - List of attachments
- `AttachmentUpload.vue` - File upload component
- `AttachmentPreview.vue` - Preview/download attachment
- `AttachmentCard.vue` - Display attachment

#### Dashboard Components

- `SystemStatsCard.vue` - System-wide statistics cards
- `UserActivityChart.vue` - User activity charts/graphs
- `DataGrowthChart.vue` - Data growth over time
- `TopUsersWidget.vue` - Top active users widget
- `StorageUsageWidget.vue` - Storage usage widget
- `RecentActivityWidget.vue` - Recent system activity
- `SystemHealthWidget.vue` - System health indicators

#### Activity/Audit Components

- `ActivityLogTable.vue` - Activity log table
- `ActivityEventCard.vue` - Individual activity event card
- `UserActivityTimeline.vue` - User activity timeline
- `AuditTrailView.vue` - Detailed audit trail view

#### Search Components

- `SearchBar.vue` - Global search input
- `SearchResults.vue` - Search results display
- `SearchFilters.vue` - Search filter panel

#### Form Components (May need additional shadcn-vue components)

- `DatePicker.vue` - Date picker (may need to add shadcn-vue calendar/popover)
- `TimePicker.vue` - Time picker
- `Select.vue` - Select dropdown (may need to add shadcn-vue select)
- `Textarea.vue` - Textarea (may need to add shadcn-vue textarea)
- `Checkbox.vue` - Checkbox (may need to add shadcn-vue checkbox)
- `RadioGroup.vue` - Radio group (may need to add shadcn-vue radio-group)
- `MultiSelect.vue` - Multi-select dropdown (for bulk operations)

#### Utility Components

- `EmptyState.vue` - Empty state placeholder
- Use shadcn-vue `Spinner` component (already installed)
- `ErrorDisplay.vue` - Error message display
- `ConfirmDialog.vue` - Confirmation dialog (may need shadcn-vue dialog)
- `StatusFilter.vue` - Status filter dropdown
- `DateRangePicker.vue` - Date range selector
- Use shadcn-vue `Pagination` component (already installed)
- `BulkSelectCheckbox.vue` - Bulk selection checkbox
- `ExportDialog.vue` - Export options dialog

---

## 🚀 Implementation Order

### Phase 1: Foundation & Core Setup (Week 1-2)

**Goal:** Set up basic infrastructure for admin web

1. **Install Additional shadcn-vue Components**

   - [ ] Dialog (for modals/confirmations)
   - [ ] Select (for dropdowns)
   - [ ] Textarea (for multi-line input)
   - [ ] Checkbox (for checkboxes)
   - [ ] Radio Group (for radio buttons)
   - [ ] Calendar (for date picking)
   - [ ] Popover (for date picker)
   - [ ] Table (for data tables - **CRITICAL for admin web**)
   - [ ] Tabs (for tabbed interfaces)
   - [ ] Badge (if not already available)

2. **Set Up API Client/Store**

   - [ ] Create API service layer (with admin endpoints)
   - [ ] Set up Pinia stores for:
     - [ ] Auth store (admin authentication)
     - [ ] Users store (user management)
     - [ ] Actions store (with user filtering)
     - [ ] Projects store (with user filtering)
     - [ ] Inbox store (with user filtering)
     - [ ] Contexts store (with user filtering)
     - [ ] Areas store (with user filtering)
     - [ ] References store (with user filtering)
     - [ ] WaitingFor store (with user filtering)
     - [ ] Attachments store
     - [ ] Activity/Audit store
     - [ ] System health store

3. **Create Base Admin Components**
   - [ ] `EmptyState.vue`
   - [ ] `LoadingSpinner.vue`
   - [ ] `ErrorDisplay.vue`
   - [ ] `ConfirmDialog.vue`
   - [ ] `UserSelector.vue` (critical - used everywhere)
   - [ ] `DataTable.vue` (critical - for admin views)
   - [ ] `BulkActionsBar.vue`
   - [ ] `FilterPanel.vue`
   - [ ] `Pagination.vue`

### Phase 2: User Management (Week 2-3)

**Goal:** Build user management foundation (CRITICAL for admin web)

**Why First:** All other views need user filtering/selection

4. **User Management System**
   - [ ] `UsersView.vue` (table view with search/filter)
   - [ ] `UserDetailView.vue`
   - [ ] `UserTable.vue` (data table component)
   - [ ] `UserStatsCard.vue`
   - [ ] `UserSelector.vue` (reusable component)
   - [ ] `UserBadge.vue`
   - [ ] User API integration
   - [ ] User store implementation

### Phase 3: Admin Dashboard (Week 3-4)

**Goal:** Create system-wide monitoring dashboard

5. **Admin Dashboard**
   - [ ] `DashboardView.vue` (system-wide metrics)
   - [ ] `SystemStatsCard.vue`
   - [ ] `UserActivityChart.vue`
   - [ ] `DataGrowthChart.vue`
   - [ ] `TopUsersWidget.vue`
   - [ ] `StorageUsageWidget.vue`
   - [ ] `RecentActivityWidget.vue`
   - [ ] `SystemHealthWidget.vue`
   - [ ] Dashboard API integration

### Phase 4: Actions Management (Week 4-5)

**Goal:** Build action management with user filtering

6. **Action Management System**
   - [ ] `ActionsView.vue` (table view with user filter)
   - [ ] `ActionTable.vue` (data table)
   - [ ] `ActionDetailView.vue` (with user context)
   - [ ] `ActionCard.vue` (with user info)
   - [ ] `ActionForm.vue` (with user selector)
   - [ ] `ActionStatusBadge.vue`
   - [ ] Bulk operations for actions
   - [ ] Export functionality

### Phase 5: Projects Management (Week 5-6)

**Goal:** Build project management with user filtering

7. **Project Management System**
   - [ ] `ProjectsView.vue` (table view with user filter)
   - [ ] `ProjectTable.vue` (data table)
   - [ ] `ProjectDetailView.vue` (with user context)
   - [ ] `ProjectCard.vue` (with user info)
   - [ ] `ProjectForm.vue` (with user selector)
   - [ ] `ProjectStatusBadge.vue`
   - [ ] Bulk operations for projects
   - [ ] Export functionality

### Phase 6: Inbox & Other Entities (Week 6-7)

**Goal:** Complete remaining entity management views

8. **Inbox Management**

   - [ ] `InboxView.vue` (table view with user filter)
   - [ ] `InboxItemTable.vue`
   - [ ] `InboxItemCard.vue` (with user info)
   - [ ] Bulk operations

9. **Contexts & Areas Management**

   - [ ] `ContextsView.vue` (with user filter)
   - [ ] `AreasView.vue` (with user filter)
   - [ ] Context/Area statistics

10. **References & Waiting For**
    - [ ] `ReferencesView.vue` (with user filter)
    - [ ] `WaitingForView.vue` (with user filter)
    - [ ] Bulk operations

### Phase 7: Attachments Management (Week 7-8)

**Goal:** File management and storage monitoring

11. **Attachment System**
    - [ ] `AttachmentsView.vue` (with user filter)
    - [ ] `AttachmentTable.vue`
    - [ ] `AttachmentPreview.vue`
    - [ ] Storage usage monitoring
    - [ ] Orphaned attachments detection
    - [ ] Bulk delete with storage cleanup

### Phase 8: Activity & Audit Logs (Week 8-9)

**Goal:** Track system activity and changes

12. **Activity/Audit System**
    - [ ] `ActivityLogView.vue`
    - [ ] `ActivityLogTable.vue`
    - [ ] `ActivityEventCard.vue`
    - [ ] `UserActivityTimeline.vue`
    - [ ] `AuditTrailView.vue`
    - [ ] Activity filtering and search
    - [ ] Export audit logs

### Phase 9: System Health & Monitoring (Week 9-10)

**Goal:** System monitoring and health checks

13. **System Health**
    - [ ] `SystemHealthView.vue`
    - [ ] `SystemHealthCard.vue`
    - [ ] API health monitoring
    - [ ] Database status
    - [ ] Error tracking
    - [ ] Performance metrics

### Phase 10: Search & Advanced Features (Week 10-11)

**Goal:** Global search and advanced admin features

14. **Global Search**

    - [ ] `SearchView.vue` (cross-entity, cross-user search)
    - [ ] `SearchBar.vue` (add to header/sidebar)
    - [ ] `SearchResults.vue` (grouped by entity and user)
    - [ ] `SearchFilters.vue` (advanced filters)

15. **Bulk Operations & Export**

    - [ ] Enhance bulk operations across all views
    - [ ] `ExportDialog.vue`
    - [ ] Export functionality (CSV, JSON)
    - [ ] Bulk update capabilities

16. **Navigation & Routing**
    - [ ] Update `router.ts` with all admin routes
    - [ ] Update `AppSidebar.vue` with admin navigation
    - [ ] Add breadcrumbs where needed
    - [ ] User context indicator in header

### Phase 11: Testing & Refinement (Week 11-12)

**Goal:** Polish and test admin web

17. **Testing**

    - [ ] Unit tests for admin components
    - [ ] E2E tests for critical admin flows
    - [ ] Accessibility audit
    - [ ] Performance testing (large datasets)

18. **Polish & Optimization**
    - [ ] Error handling improvements
    - [ ] Loading states (skeleton loaders for tables)
    - [ ] Pagination optimization
    - [ ] Data table performance (virtual scrolling if needed)
    - [ ] Responsive design
    - [ ] Dark mode (if applicable)
    - [ ] Keyboard shortcuts for power users

---

## 📦 Additional shadcn-vue Components to Install

Based on the plan, you'll need to add these shadcn-vue components:

1. **Dialog** - For modals and confirmations
2. **Select** - For dropdown selects
3. **Textarea** - For multi-line text input
4. **Checkbox** - For checkboxes
5. **Radio Group** - For radio button groups
6. **Calendar** - For date picking
7. **Popover** - For date picker overlay
8. **Table** - For data tables (optional, can use cards)
9. **Tabs** - For tabbed interfaces
10. **Badge** - For status indicators (if not already available)
11. **Command** - For command palette/search (optional)

---

## 🎯 Key Dependencies

- **All views depend on:** User Management (for filtering/selection)
- **Actions depend on:** Contexts, Areas (optional)
- **Projects depend on:** Areas (required)
- **Dashboard depends on:** All entities and user data
- **Activity logs depend on:** All entities (for tracking)
- **Bulk operations depend on:** Data tables and selection components

---

## 📝 Admin web Specific Notes

- **User Context:** All views should support user filtering/selection
- **Data Tables:** Primary UI pattern for admin views (not cards)
- **Bulk Operations:** Critical feature - admins need to manage multiple items
- **Export Functionality:** Important for reporting and data analysis
- **Audit Trails:** Track all admin actions for accountability
- **Performance:** Optimize for large datasets (pagination, virtual scrolling)
- **API Integration:** Admin endpoints will differ from user endpoints
- **Permissions:** Consider role-based access control (RBAC) if needed
- **Real-time Updates:** Consider WebSocket for system health monitoring
- **Error Handling:** Robust error handling for admin operations
- **Data Validation:** Server-side validation critical for admin operations
- **Storage Management:** Monitor and manage attachment storage

---

## 🔄 Iterative Approach

This plan can be adjusted based on:

- Admin user feedback
- API availability
- Priority features
- Team capacity

**Recommended MVP Path:**

- Phases 1-3: Foundation, User Management, Dashboard (critical for admin web)
- Phases 4-6: Core entity management (Actions, Projects, Inbox)
- Phases 7-11: Advanced features and polish

**Key Differences from User App:**

- Focus on data tables over cards
- User filtering on every view
- Bulk operations are essential
- System monitoring is critical
- Export functionality is important
- Less focus on "productivity workflows" and more on "data management"
