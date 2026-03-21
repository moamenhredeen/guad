# Admin UI Redesign

## Problem

The admin panel uses Pico CSS with a flat horizontal nav, bare HTML tables, and no visual relationship to the Vue frontend's Baden-Württemberg design system. Non-technical users will use the admin, so it needs to be polished and consistent with the main app.

## Decisions

- Match the Vue frontend's BW design system (Schwarz/Gelb/Grau palette)
- Sidebar navigation layout
- Custom CSS from scratch (replace Pico)
- Dashboard with summary stat cards

## Design

### 1. Layout Structure

Two-column CSS grid:

- **Left sidebar** (~240px, fixed): `BW Grau 5` background, `BW Schwarz` text. Guad logo/name at top, nav links grouped logically (GTD: Inbox, Actions, Projects; Organization: Areas, Contexts; Content: Documents, Attachments), logout at bottom. Active link highlighted with `BW Gelb` left border accent. Active state determined by matching the current request URI against nav link paths.
- **Main content area** (fluid): page title at top, then page content below.
- Desktop-only — no collapsing sidebar.

### 2. Custom CSS & Color Tokens

Single `admin.css` static resource replacing Pico CSS. Defines the full BW design token set as CSS custom properties:

```
--bw-schwarz: #2A2623
--bw-grau-90: #49413C
--bw-grau-80: #524942
--bw-grau-50: #867A69
--bw-grau-20: #CBC6BD
--bw-grau-10: #E4E1DC
--bw-grau-5: #F4F3F1
--bw-gelb: #FFFC00
--bw-rot-80: #920303
--bw-grun-80: #375A08
```

Typography: `system-ui, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif` (matches Vue frontend). Base font size 14px for density. Border radius 3px.

CSS sections: reset/base, layout (sidebar + content grid), navigation, tables, forms/inputs, buttons, cards (dashboard stats, attachment grid), detail views (definition lists), error pages.

**Migration note:** All inline `style` attributes referencing `--pico-*` CSS variables (found in `attachments/details.html`, `projects/details.html`, and `fragments/attachments.html`) must be replaced with the new `--bw-*` tokens or moved into `admin.css` classes.

### 3. Data Tables

- **Header row**: `BW Grau 5` background, uppercase small text in `BW Grau 50`, letter-spacing
- **Body rows**: alternating white / `BW Grau 5` stripes, hover highlight with `BW Grau 10`
- **Actions column**: right-aligned "View" and "Delete" text links
- **Search toolbar**: styled bar above table with inline input + filter dropdown + search button (applies to list pages that already have search forms)
- **Pagination**: "Previous / Page X of Y / Next" below table as small pill buttons. Controllers already use `Pageable` and `PaginationUtils.addPaginationData()` — pagination just needs template markup added.

### 4. Dashboard

Stat cards in a 3-column, 2-row grid using existing `DashboardService`:

- **Cards**: white background, `BW Grau 20` border, large number, label below in `BW Grau 50`. Each card links to its list page.
- **Counts**: Unprocessed Inbox, Next Actions, Active Projects, Waiting For, Someday/Maybe
- **Weekly Review card**: "Due" with `BW Rot 80` accent or "Up to date" with `BW Grun 80` accent

**Backend note:** `DashboardAdminController` needs `DashboardService` injected. `DashboardService.getDashboard()` requires a `UUID userId` — resolve from the authenticated OAuth2 principal in the security context. Add each stat as a model attribute.

### 5. Detail Views

- **Definition list layout**: two-column grid — label left in `BW Grau 50` (small, uppercase), value right in `BW Schwarz`
- **Related entity links**: subtle underlined links in `BW Grau 90`
- **Attachment grid**: updated to use new card styling (replace all `--pico-*` variable references)
- **Delete confirmation**: card with warning message and `BW Rot 80` delete button

### 6. Auth Pages

`login.html` and `logout.html` are standalone pages (no sidebar layout). They include `admin.css` directly and use only the base/form/button styles for a centered card layout.

## Files to Create

- `backend/src/main/resources/static/admin.css` — custom admin stylesheet

## Files to Modify

- `templates/admin/layout.html` — replace Pico CSS with admin.css, add sidebar + content grid structure, grouped nav with active state
- `templates/admin/dashboard.html` — add stat cards grid
- `templates/admin/*/list.html` (7 files) — add actions column, update table markup, style search toolbar, add pagination markup
- `templates/admin/*/details.html` (7 files) — switch from `<ul>` to definition list layout, replace `--pico-*` inline styles
- `templates/admin/*/delete.html` (7 files) — card-style confirmation
- `templates/admin/fragments/attachments.html` — update card styling, replace `--pico-*` variables
- `templates/admin/error/*.html` (3 files) — update to use new layout
- `templates/admin/login.html` — style with BW tokens (standalone, no sidebar)
- `templates/admin/logout.html` — style with BW tokens (standalone, no sidebar)
- `DashboardAdminController` — inject DashboardService, resolve user UUID from OAuth2 principal, pass stats to template

## Files to Delete

- `backend/src/main/resources/static/pico.min.css` — replaced by admin.css
