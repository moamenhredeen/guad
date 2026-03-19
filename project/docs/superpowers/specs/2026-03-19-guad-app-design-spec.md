# Guad App Design Spec

A GTD (Getting Things Done) task management app with a Swabian identity, built as a Kotlin Multiplatform Compose app. This spec defines the UI design as a static interactive HTML prototype before implementation.

## Design Identity

### Principles

Inspired by the Baden-Württemberg corporate design language:

- **Kontrastreich** — Bold contrasts. Warm black meets bright accents.
- **Klar** — Clear and minimal. Focused, no clutter.
- **Guad** — It just feels good. Swabian warmth and reliability.

### Color Palette

Derived from the official BW design system (design.landbw.de).

**Primary (BW Türkis):**
- Türkis Dunkel `#094954` — App bar, headers
- Türkis Hell `#309AAF` — Primary actions, links, active tab
- Türkis Surface `#e8f4f6` — Selected states, badges

**Accent (BW Gelb):**
- BaWü Gelb `#FFFC00` — FAB, important highlights
- Orange Hell `#DD6F06` — Warnings, due soon
- Rot Hell `#FD4D4D` — Overdue, destructive actions

**Neutrals (BW warm grays):**
- BaWü Schwarz `#2A2623` — Primary text
- Grau 5 `#6D6766` — Secondary text
- Grau 3 `#BBB6B5` — Borders, dividers, placeholders
- Grau 2 `#E4E0E0` — Tab bar border, light dividers
- Grau 1 `#F4F3F3` — Backgrounds, subtle fills
- White `#FFFFFF` — Screen background

**Functional (BW palette):**
- Grün Hell `#609D0F` — Completed, success
- Blau Hell `#508CF1` — Info, Waiting For person tags
- Lila Hell `#C761EC` — Someday/Maybe

### Typography

Following BW's serif + sans-serif contrast principle:

- **Screen titles:** Serif (Georgia or similar), 28px, bold — warmth and character
- **Section headers:** Sans-serif, 13px, semibold, uppercase, teal — `letter-spacing: 0.5px`
- **Task items:** Sans-serif (system font), 15px, regular — scannable
- **Metadata/supporting:** Sans-serif, 12-13px, Grau 3 or Grau 5
- **Spacing:** 8px base unit (8, 12, 16, 20, 24, 32)
- **Border radius:** 12px cards/inputs, 16px FAB, 20px chips, 50% circles

### Swabian Personality

Empty states use Swabian dialect:
- Empty inbox: *"Alles guad! Nix zum schaffe."*
- All done: *"Des passt! Feierabend."*
- Review complete: *"Sauber gmacht!"*

## App Structure

### Navigation

Bottom tab bar with 5 tabs:

| Tab | Icon | Label |
|-----|------|-------|
| Inbox | 📥 | Inbox |
| Next Actions | ⚡ | Next |
| Projects | 📁 | Projects |
| Waiting For | ⏳ | Waiting |
| More | ••• | More |

- Active tab: Türkis Hell `#309AAF`, semibold label
- Inactive tab: Grau 3 `#BBB6B5`
- Tab bar: White background, `1px solid #E4E0E0` top border, `padding: 8px 0 24px` (safe area)

### Global FAB

- Yellow `#FFFC00`, `56×56px`, `border-radius: 16px`
- Black "+" icon, `28px`, `font-weight: 300`
- `box-shadow: 0 4px 16px rgba(0,0,0,0.18)`
- Position: `bottom: 80px, right: 20px` (above tab bar)
- Present on all tab screens
- Tapping opens Quick Capture bottom sheet

## Screens

### Screen 1: Inbox

**Header:**
- Serif title "Inbox", 28px bold
- Subtitle: "{count} items to process" in Grau 5, 13px

**Task list:**
- Each row: circular checkbox (22px, 2px border Grau 3) + title (15px) + "Added {time}" (12px, Grau 3)
- Row padding: `14px 4px`, separated by `1px solid #F4F3F3`

**Swipe hint bar:**
- Below task list, rounded container (`background: #F4F3F3, border-radius: 10px`)
- Text: "← Someday | Tap to triage | Next Action →" in 12px Grau 5

**Empty state:**
- Centered vertically
- Teal circle (80px) with checkmark
- Serif title: "Alles guad!" (22px bold)
- Subtitle: "Nix zum schaffe. Tap + to capture something new." (15px, Grau 5)

### Screen 2: Next Actions

**Header:**
- Serif title "Next Actions", 28px bold
- Subtitle: "{count} actions across {n} contexts"

**Context filter chips:**
- Horizontal scrollable row below header
- Active chip: `background: #094954, color: white, border-radius: 20px`
- Inactive chip: `background: #F4F3F3, color: #2A2623, border-radius: 20px`
- Chip padding: `6px 14px`, font 13px

**Task list grouped by context:**
- Context header: 13px semibold, Türkis Hell, uppercase, `letter-spacing: 0.5px`
- Task rows identical to Inbox (checkbox + title + project name and optional due date as metadata)

### Screen 3: Projects

**Header:**
- Serif title "Projects", 28px bold
- Subtitle: "{count} active projects"

**Project list grouped by area:**
- Area header: 13px semibold, Türkis Hell, uppercase, `letter-spacing: 0.5px`
- Clean rows style (no cards, no background):
  - Project name: 15px, `font-weight: 500`
  - Metadata: "{n} next actions" in 12px Grau 3
  - Chevron "›" on right in Grau 3
  - Row padding: `14px 4px`, separated by `1px solid #F4F3F3`

### Screen 4: Waiting For

**Header:**
- Serif title "Waiting For", 28px bold
- Subtitle: "{count} items delegated"

**Item rows:**
- Title: 15px, primary text
- Project name: 12px, Grau 5
- Metadata row below (8px margin-top): person tag (blue `#508CF1`, "👤 {name}") + delegation date
- Older items show age in orange: "Delegated Mar 5 · 14 days" in `#DD6F06`

### Screen 5: More

**Header:**
- Serif title "More", 28px bold

**Three sections with headers (GTD Lists, Review, App):**

Each row:
- Icon badge: 32px rounded square with tinted background + emoji
- Title (15px) + description (12px, Grau 3)
- Optional count badge: `background: #F4F3F3, border-radius: 10px, padding: 2px 8px`
- Chevron on right

**Items:**
- GTD Lists: Someday/Maybe (💭, purple bg), Contexts (🏷️, teal bg), Reference (📋, yellow bg)
- Review: Weekly Review (🔄, green bg) — shows "Due" badge in yellow when ≥7 days since last review
- App: Areas of Responsibility (🏔️, gray bg), Settings (⚙️, gray bg)

### Screen 6: Quick Capture (Bottom Sheet)

**Overlay:** Current screen dimmed (`rgba(42,38,35,0.4)`)

**Bottom sheet:**
- White, `border-radius: 20px 20px 0 0`
- Drag handle: `36×4px`, Grau 2, centered
- Serif title "Quick Capture", 20px bold

**Input fields — completely flat, no borders or backgrounds:**
- Title: 17px, primary text color, cursor only indicator
- Note placeholder: 14px, Grau 3, "Add a note..."

**Action:**
- "Save to Inbox" button: `background: #FFFC00`, 15px semibold, `border-radius: 12px`

### Screen 7: Process Item (Triage)

**Navigation:** Back arrow + "Inbox" link in Türkis Hell

**Item display:**
- Title: Serif, 22px bold
- "Added {time}" in Grau 3

**GTD prompt:**
- Teal container (`background: #e8f4f6, border-radius: 12px`)
- "What's the next action?" in `#094954`, `font-weight: 500`

**Swipe directions:**
- Visual indicator: 💭 "← Someday" (purple) | ⚡ "Next Action →" (teal)
- Vertical divider between them

**Triage grid (2×2):**
- Outlined cards (`border: 1px solid #E4E0E0, border-radius: 12px`)
- Each: emoji + label (13px semibold)
- Options: Add to Project (📁), Waiting For (⏳), Reference (📋), Trash (🗑️)

**2-minute rule reminder:**
- Yellow banner (`background: #fef3c7, border-radius: 10px`)
- "⚡ Takes less than 2 minutes? **Just do it now.**"

### Screen 8: Task Detail

**Navigation:** Back link + "Save" in Türkis Hell (semibold)

**Title area:**
- Checkbox (24px) + task title (20px, `font-weight: 600`)

**Metadata fields (key-value rows):**
Each row: label (14px, Grau 5) on left, value on right, separated by `1px solid #F4F3F3`

| Field | Value style |
|-------|------------|
| Project | Name + chevron, tappable |
| Context | Chip: `background: #e8f4f6, color: #094954, border-radius: 16px` |
| Due date | Orange `#DD6F06` if upcoming |
| Area | Plain text |
| Energy | Plain text (High/Medium/Low) |
| Time needed | Plain text |

**Notes:**
- Label "Notes" in Grau 5
- Content in gray container (`background: #F4F3F3, border-radius: 12px, padding: 12px 16px`)

**Delete:** Centered red text "Delete Task" at bottom (`color: #FD4D4D`)

### Screen 9: Project Detail

**Navigation:** Back "Projects" link + "Edit" in Türkis Hell

**Header:**
- Project name: Serif, 24px bold
- Area: 13px, Grau 5
- Outcome statement: 14px italic, Grau 5 — the GTD "what does done look like?"

**Three sections:**

1. **Next Actions** (teal header) — same task rows as other screens
2. **Waiting For** (blue `#508CF1` header) — person tag + delegation date
3. **Completed** (green `#609D0F` header) — green filled checkmarks, strikethrough titles in Grau 3

**Footer:** Dashed "+ Add next action" button (`border: 1px dashed #BBB6B5, border-radius: 12px`)

### Screen 10: Weekly Review Wizard

**Header:** "Exit" link (Türkis Hell) + "Step {n} of 6" counter

**Progress bar:**
- Track: 4px, `#F4F3F3`, rounded
- Fill: `#309AAF`, width proportional to step

**Review steps (6 total):**
1. Clear Inbox
2. Review Next Actions
3. Review Projects
4. Review Waiting For
5. Review Someday/Maybe
6. Done — "Sauber gmacht!"

**Step content:**
- Serif title (24px bold) for current step
- Guiding question in Grau 5 (14px, `line-height: 1.5`)
- Checklist of items to review:
  - Reviewed: Green filled square checkbox (6px radius) + grayed-out text
  - Current: Highlighted row (`background: #fefce8`) + empty checkbox + bold title
  - Pending: Light border checkbox + normal text

**Step breadcrumbs:** Bottom of screen, horizontal list of step names
- Completed: green "✓ {name}"
- Current: black bold
- Pending: Grau 3

**Navigation:** Dark teal button at bottom: "Next: {step name} →" (`background: #094954, color: white`)

## Prototype Structure

The prototype is a single interactive HTML file with:
- Working bottom tab bar navigation between all 5 tab screens
- CSS transitions between screens
- Tappable FAB that shows the Quick Capture bottom sheet
- Tappable inbox items that navigate to the Process screen
- Tappable project rows that navigate to Project Detail
- Tappable "Weekly Review" in More that launches the wizard
- All screens rendered inside a phone-sized frame (375px wide)
- Uses the BW color palette and serif/sans-serif typography contrast

## Deliverable

A single `design.html` file placed at `project/design/guad-prototype.html` containing the complete interactive prototype. No external dependencies — all CSS and JS inline.
