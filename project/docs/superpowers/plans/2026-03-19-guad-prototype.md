# Guad Interactive Prototype Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a single interactive HTML prototype of the Guad GTD app with working navigation, all 10 screens, and the BW/Swabian design identity.

**Architecture:** Single `design/guad-prototype.html` file with all CSS and JS inline. Screens are `<div>` sections toggled via JS. No external dependencies. Phone-frame wrapper (375px) centered on page.

**Tech Stack:** HTML, CSS, vanilla JavaScript — no frameworks, no build tools.

---

## File Structure

- **Create:** `project/design/guad-prototype.html` — the complete interactive prototype

This is a single-file deliverable. All CSS lives in a `<style>` block, all JS in a `<script>` block, all screens as `<div>` sections within the body.

---

### Task 1: Page Shell + CSS Design Tokens

Set up the HTML document, phone frame wrapper, and all CSS custom properties from the BW color palette.

**Files:**
- Create: `project/design/guad-prototype.html`

- [ ] **Step 1: Create the HTML document with meta tags and phone frame**

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Guad — Prototype</title>
</head>
<body>
  <div class="phone-frame">
    <div class="phone-screen" id="app">
      <!-- screens go here -->
    </div>
  </div>
</body>
</html>
```

- [ ] **Step 2: Add CSS reset and design tokens as custom properties**

In a `<style>` block in `<head>`, add:

```css
:root {
  /* Primary - BW Türkis */
  --turkis-dark: #094954;
  --turkis: #309AAF;
  --turkis-surface: #e8f4f6;

  /* Accent - BW Gelb */
  --gelb: #FFFC00;
  --orange: #DD6F06;
  --rot: #FD4D4D;

  /* Neutrals - BW warm grays */
  --schwarz: #2A2623;
  --grau-5: #6D6766;
  --grau-3: #BBB6B5;
  --grau-2: #E4E0E0;
  --grau-1: #F4F3F3;
  --weiss: #FFFFFF;

  /* Functional */
  --grun: #609D0F;
  --blau: #508CF1;
  --lila: #C761EC;

  /* Typography */
  --font-serif: Georgia, 'Times New Roman', serif;
  --font-sans: -apple-system, 'Segoe UI', system-ui, sans-serif;

  /* Spacing */
  --sp-1: 4px;
  --sp-2: 8px;
  --sp-3: 12px;
  --sp-4: 16px;
  --sp-5: 20px;
  --sp-6: 24px;
  --sp-8: 32px;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  background: #E4E0E0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  font-family: var(--font-sans);
  color: var(--schwarz);
}

.phone-frame {
  width: 375px;
  height: 812px;
  background: var(--weiss);
  border-radius: 40px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  position: relative;
}

.phone-screen {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}
```

- [ ] **Step 3: Add shared component styles**

Continuing in the same `<style>` block, add styles for common elements used across screens:

```css
/* Status bar */
.status-bar {
  display: flex;
  justify-content: space-between;
  padding: var(--sp-2) var(--sp-5);
  font-size: 12px;
  font-weight: 600;
  color: var(--schwarz);
}

/* Screen header */
.screen-title {
  font-family: var(--font-serif);
  font-size: 28px;
  font-weight: 700;
  color: var(--schwarz);
}

.screen-subtitle {
  font-size: 13px;
  color: var(--grau-5);
  margin-top: 2px;
}

/* Section headers */
.section-header {
  font-size: 13px;
  font-weight: 600;
  color: var(--turkis);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: var(--sp-3) var(--sp-1);
}

/* Task row */
.task-row {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: 14px var(--sp-1);
  border-bottom: 1px solid var(--grau-1);
  cursor: pointer;
}

.task-row:active { background: var(--grau-1); }

.checkbox {
  width: 22px;
  height: 22px;
  border: 2px solid var(--grau-3);
  border-radius: 50%;
  flex-shrink: 0;
}

.checkbox--done {
  background: var(--grun);
  border-color: var(--grun);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  font-weight: 700;
}

.task-title { font-size: 15px; color: var(--schwarz); }
.task-meta { font-size: 12px; color: var(--grau-3); margin-top: 3px; }

/* Screen container */
.screen {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: var(--weiss);
  display: none;
  flex-direction: column;
  overflow-y: auto;
  padding-bottom: 72px;
}

.screen.active { display: flex; }

/* Navigation bar (back + action) */
.nav-bar {
  padding: var(--sp-3) var(--sp-5);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-back {
  color: var(--turkis);
  font-size: 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: var(--sp-2);
}

.nav-back::before { content: '‹'; font-size: 18px; }

.nav-action {
  color: var(--turkis);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}
```

- [ ] **Step 4: Add bottom tab bar and FAB styles**

```css
/* Tab bar */
.tab-bar {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  background: var(--weiss);
  border-top: 1px solid var(--grau-2);
  display: flex;
  justify-content: space-around;
  padding: var(--sp-2) 0 var(--sp-6);
  z-index: 100;
}

.tab {
  text-align: center;
  font-size: 10px;
  color: var(--grau-3);
  cursor: pointer;
}

.tab.active {
  color: var(--turkis);
  font-weight: 600;
}

.tab-icon { font-size: 22px; margin-bottom: 2px; }

/* FAB */
.fab {
  position: absolute;
  bottom: 80px;
  right: 20px;
  width: 56px;
  height: 56px;
  background: var(--gelb);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(0,0,0,0.18);
  cursor: pointer;
  z-index: 90;
  border: none;
  font-size: 28px;
  color: var(--schwarz);
  font-weight: 300;
  line-height: 1;
}

.fab:active { transform: scale(0.95); }

/* Overlay */
.overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(42,38,35,0.4);
  z-index: 200;
  display: none;
}

.overlay.active { display: block; }

.bottom-sheet {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  background: var(--weiss);
  border-radius: 20px 20px 0 0;
  padding: var(--sp-3) var(--sp-5) var(--sp-8);
}

.sheet-handle {
  width: 36px;
  height: 4px;
  background: var(--grau-2);
  border-radius: 2px;
  margin: 0 auto var(--sp-5);
}
```

- [ ] **Step 5: Open in browser and verify empty phone frame renders**

Run: `xdg-open project/design/guad-prototype.html` (or open manually)
Expected: Gray page with centered white phone frame (375×812), rounded corners, shadow.

- [ ] **Step 6: Commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): prototype shell with CSS design tokens and shared styles"
```

---

### Task 2: Tab Bar + Screen Navigation JS

Add the bottom tab bar HTML, the 5 tab screen containers, the FAB, and the JS to switch between screens.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add the 5 tab screen divs inside `#app`**

Inside `<div class="phone-screen" id="app">`, add:

```html
<!-- Tab screens -->
<div class="screen active" id="screen-inbox"></div>
<div class="screen" id="screen-next"></div>
<div class="screen" id="screen-projects"></div>
<div class="screen" id="screen-waiting"></div>
<div class="screen" id="screen-more"></div>

<!-- Detail screens (hidden by default) -->
<div class="screen" id="screen-process"></div>
<div class="screen" id="screen-task-detail"></div>
<div class="screen" id="screen-project-detail"></div>
<div class="screen" id="screen-review"></div>

<!-- Overlay -->
<div class="overlay" id="overlay-capture">
  <div class="bottom-sheet">
    <div class="sheet-handle"></div>
  </div>
</div>

<!-- Tab bar -->
<div class="tab-bar">
  <div class="tab active" data-screen="screen-inbox">
    <div class="tab-icon">📥</div>
    <div>Inbox</div>
  </div>
  <div class="tab" data-screen="screen-next">
    <div class="tab-icon">⚡</div>
    <div>Next</div>
  </div>
  <div class="tab" data-screen="screen-projects">
    <div class="tab-icon">📁</div>
    <div>Projects</div>
  </div>
  <div class="tab" data-screen="screen-waiting">
    <div class="tab-icon">⏳</div>
    <div>Waiting</div>
  </div>
  <div class="tab" data-screen="screen-more">
    <div class="tab-icon">•••</div>
    <div>More</div>
  </div>
</div>

<!-- FAB -->
<button class="fab" id="fab-capture">+</button>
```

- [ ] **Step 2: Add navigation JS at end of body**

```html
<script>
const tabs = document.querySelectorAll('.tab');
const screens = document.querySelectorAll('.screen');
const fab = document.getElementById('fab-capture');
const overlay = document.getElementById('overlay-capture');

function showScreen(id) {
  screens.forEach(s => s.classList.remove('active'));
  document.getElementById(id).classList.add('active');

  // Update tab bar active state (only for tab screens)
  const tabIds = ['screen-inbox','screen-next','screen-projects','screen-waiting','screen-more'];
  if (tabIds.includes(id)) {
    tabs.forEach(t => t.classList.toggle('active', t.dataset.screen === id));
    fab.style.display = 'flex';
  }
}

function showDetail(id) {
  screens.forEach(s => s.classList.remove('active'));
  document.getElementById(id).classList.add('active');
  fab.style.display = 'none';
}

function goBack(tabId) {
  showScreen(tabId);
}

tabs.forEach(tab => {
  tab.addEventListener('click', () => showScreen(tab.dataset.screen));
});

fab.addEventListener('click', () => {
  overlay.classList.add('active');
});

overlay.addEventListener('click', (e) => {
  if (e.target === overlay) {
    overlay.classList.remove('active');
  }
});
</script>
```

- [ ] **Step 3: Verify tab switching works**

Open in browser. Click each tab — the active tab should highlight in teal, the corresponding screen div should show (empty for now). Click FAB — overlay dims. Click overlay background — it dismisses.

- [ ] **Step 4: Commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): tab bar navigation, screen containers, and FAB overlay"
```

---

### Task 3: Inbox Screen (Populated + Empty)

Build the Inbox screen with sample tasks, swipe hint bar, and the empty state.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add Inbox screen content inside `#screen-inbox`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div style="padding: var(--sp-4) var(--sp-5) var(--sp-3);">
  <div class="screen-title">Inbox</div>
  <div class="screen-subtitle">5 items to process</div>
</div>

<div style="padding: 0 var(--sp-4);">
  <div class="task-row" onclick="showDetail('screen-process')">
    <div class="checkbox"></div>
    <div><div class="task-title">Call Hans about project deadline</div><div class="task-meta">Added today</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-process')">
    <div class="checkbox"></div>
    <div><div class="task-title">Buy new running shoes</div><div class="task-meta">Added today</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-process')">
    <div class="checkbox"></div>
    <div><div class="task-title">Research vacation spots for summer</div><div class="task-meta">Added yesterday</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-process')">
    <div class="checkbox"></div>
    <div><div class="task-title">Fix leaking kitchen faucet</div><div class="task-meta">Added 2 days ago</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-process')">
    <div class="checkbox"></div>
    <div><div class="task-title">Review quarterly budget report</div><div class="task-meta">Added 3 days ago</div></div>
  </div>

  <div style="margin-top: var(--sp-4); padding: var(--sp-3) var(--sp-4); background: var(--grau-1); border-radius: 10px; display: flex; gap: var(--sp-4); justify-content: center; font-size: 12px; color: var(--grau-5);">
    <span>← Someday</span>
    <span style="color: var(--grau-3);">|</span>
    <span>Tap to triage</span>
    <span style="color: var(--grau-3);">|</span>
    <span>Next Action →</span>
  </div>
</div>
```

- [ ] **Step 2: Verify Inbox renders correctly**

Open in browser. Inbox should show 5 task rows with circular checkboxes, a swipe hint bar, serif title, warm colors. Tapping a task should navigate to the (empty) process screen.

- [ ] **Step 3: Commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): inbox screen with task list and swipe hints"
```

---

### Task 4: Next Actions Screen

Build the Next Actions screen with context filter chips and tasks grouped by context.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add Next Actions content inside `#screen-next`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div style="padding: var(--sp-4) var(--sp-5) var(--sp-3);">
  <div class="screen-title">Next Actions</div>
  <div class="screen-subtitle">12 actions across 4 contexts</div>
</div>

<!-- Filter chips -->
<div style="padding: 0 var(--sp-5) var(--sp-3); display: flex; gap: var(--sp-2); overflow-x: auto;">
  <div style="padding: 6px 14px; background: var(--turkis-dark); color: white; border-radius: 20px; font-size: 13px; font-weight: 500; white-space: nowrap; cursor: pointer;">All</div>
  <div style="padding: 6px 14px; background: var(--grau-1); color: var(--schwarz); border-radius: 20px; font-size: 13px; white-space: nowrap; cursor: pointer;">@Office</div>
  <div style="padding: 6px 14px; background: var(--grau-1); color: var(--schwarz); border-radius: 20px; font-size: 13px; white-space: nowrap; cursor: pointer;">@Home</div>
  <div style="padding: 6px 14px; background: var(--grau-1); color: var(--schwarz); border-radius: 20px; font-size: 13px; white-space: nowrap; cursor: pointer;">@Calls</div>
  <div style="padding: 6px 14px; background: var(--grau-1); color: var(--schwarz); border-radius: 20px; font-size: 13px; white-space: nowrap; cursor: pointer;">@Errands</div>
</div>

<div style="padding: 0 var(--sp-4);">
  <div class="section-header">@Office</div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Draft proposal for client meeting</div><div class="task-meta">Website Relaunch · Due Fri</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Review PR from Sarah</div><div class="task-meta">Mobile App v2</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Update team wiki with new process</div><div class="task-meta">Team Ops</div></div>
  </div>

  <div class="section-header" style="padding-top: 18px;">@Home</div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Fix leaking kitchen faucet</div><div class="task-meta">Home Maintenance</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Organize garage shelves</div><div class="task-meta">Home Maintenance</div></div>
  </div>

  <div class="section-header" style="padding-top: 18px;">@Calls</div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Call Hans about project deadline</div><div class="task-meta">Website Relaunch</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Schedule dentist appointment</div><div class="task-meta">Health</div></div>
  </div>
</div>
```

- [ ] **Step 2: Verify and commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): next actions screen with context chips and grouped tasks"
```

---

### Task 5: Projects Screen

Build the Projects screen with clean rows grouped by area.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add Projects content inside `#screen-projects`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div style="padding: var(--sp-4) var(--sp-5) var(--sp-4);">
  <div class="screen-title">Projects</div>
  <div class="screen-subtitle">8 active projects</div>
</div>

<div style="padding: 0 var(--sp-4);">
  <div class="section-header">Work</div>
  <div class="task-row" onclick="showDetail('screen-project-detail')" style="justify-content: space-between;">
    <div><div style="font-size: 15px; font-weight: 500;">Website Relaunch</div><div class="task-meta">3 next actions</div></div>
    <div style="color: var(--grau-3); font-size: 16px;">›</div>
  </div>
  <div class="task-row" onclick="showDetail('screen-project-detail')" style="justify-content: space-between;">
    <div><div style="font-size: 15px; font-weight: 500;">Mobile App v2</div><div class="task-meta">2 next actions</div></div>
    <div style="color: var(--grau-3); font-size: 16px;">›</div>
  </div>
  <div class="task-row" onclick="showDetail('screen-project-detail')" style="justify-content: space-between;">
    <div><div style="font-size: 15px; font-weight: 500;">Team Ops</div><div class="task-meta">1 next action</div></div>
    <div style="color: var(--grau-3); font-size: 16px;">›</div>
  </div>

  <div class="section-header" style="padding-top: 18px;">Personal</div>
  <div class="task-row" onclick="showDetail('screen-project-detail')" style="justify-content: space-between;">
    <div><div style="font-size: 15px; font-weight: 500;">Summer Vacation</div><div class="task-meta">2 next actions</div></div>
    <div style="color: var(--grau-3); font-size: 16px;">›</div>
  </div>
  <div class="task-row" onclick="showDetail('screen-project-detail')" style="justify-content: space-between;">
    <div><div style="font-size: 15px; font-weight: 500;">Home Maintenance</div><div class="task-meta">2 next actions</div></div>
    <div style="color: var(--grau-3); font-size: 16px;">›</div>
  </div>

  <div class="section-header" style="padding-top: 18px;">Health</div>
  <div class="task-row" onclick="showDetail('screen-project-detail')" style="justify-content: space-between;">
    <div><div style="font-size: 15px; font-weight: 500;">Marathon Training</div><div class="task-meta">1 next action</div></div>
    <div style="color: var(--grau-3); font-size: 16px;">›</div>
  </div>
  <div class="task-row" onclick="showDetail('screen-project-detail')" style="justify-content: space-between;">
    <div><div style="font-size: 15px; font-weight: 500;">Nutrition Plan</div><div class="task-meta">1 next action</div></div>
    <div style="color: var(--grau-3); font-size: 16px;">›</div>
  </div>
</div>
```

- [ ] **Step 2: Verify and commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): projects screen with area-grouped clean rows"
```

---

### Task 6: Waiting For Screen

Build the Waiting For screen with delegated items showing person tags and age.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add Waiting For content inside `#screen-waiting`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div style="padding: var(--sp-4) var(--sp-5) var(--sp-4);">
  <div class="screen-title">Waiting For</div>
  <div class="screen-subtitle">4 items delegated</div>
</div>

<div style="padding: 0 var(--sp-4);">
  <div style="padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="font-size: 15px;">Design mockups from Lisa</div>
    <div style="font-size: 12px; color: var(--grau-5); margin-top: 3px;">Website Relaunch</div>
    <div style="display: flex; gap: var(--sp-3); margin-top: var(--sp-2); font-size: 12px;">
      <span style="color: var(--blau);">👤 Lisa</span>
      <span style="color: var(--grau-3);">Delegated Mar 12</span>
    </div>
  </div>

  <div style="padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="font-size: 15px;">Budget approval from Thomas</div>
    <div style="font-size: 12px; color: var(--grau-5); margin-top: 3px;">Team Ops</div>
    <div style="display: flex; gap: var(--sp-3); margin-top: var(--sp-2); font-size: 12px;">
      <span style="color: var(--blau);">👤 Thomas</span>
      <span style="color: var(--orange);">Delegated Mar 5 · 14 days</span>
    </div>
  </div>

  <div style="padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="font-size: 15px;">Electrician quote for garage</div>
    <div style="font-size: 12px; color: var(--grau-5); margin-top: 3px;">Home Maintenance</div>
    <div style="display: flex; gap: var(--sp-3); margin-top: var(--sp-2); font-size: 12px;">
      <span style="color: var(--blau);">👤 Müller Elektro</span>
      <span style="color: var(--grau-3);">Delegated Mar 15</span>
    </div>
  </div>

  <div style="padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="font-size: 15px;">API docs review from Sarah</div>
    <div style="font-size: 12px; color: var(--grau-5); margin-top: 3px;">Mobile App v2</div>
    <div style="display: flex; gap: var(--sp-3); margin-top: var(--sp-2); font-size: 12px;">
      <span style="color: var(--blau);">👤 Sarah</span>
      <span style="color: var(--grau-3);">Delegated Mar 17</span>
    </div>
  </div>
</div>
```

- [ ] **Step 2: Verify and commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): waiting for screen with person tags and age indicators"
```

---

### Task 7: More Screen

Build the More menu with sections for GTD Lists, Review, and App settings.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add More content inside `#screen-more`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div style="padding: var(--sp-4) var(--sp-5) var(--sp-4);">
  <div class="screen-title">More</div>
</div>

<div style="padding: 0 var(--sp-4);">
  <div class="section-header">GTD Lists</div>

  <div class="task-row" style="justify-content: space-between;">
    <div style="display: flex; align-items: center; gap: var(--sp-3);">
      <div style="width: 32px; height: 32px; background: #f3e8ff; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 16px;">💭</div>
      <div><div style="font-size: 15px;">Someday / Maybe</div><div class="task-meta">Ideas for later</div></div>
    </div>
    <div style="display: flex; align-items: center; gap: var(--sp-2);">
      <div style="background: var(--grau-1); padding: 2px 8px; border-radius: 10px; font-size: 12px; color: var(--grau-5);">14</div>
      <span style="color: var(--grau-3);">›</span>
    </div>
  </div>

  <div class="task-row" style="justify-content: space-between;">
    <div style="display: flex; align-items: center; gap: var(--sp-3);">
      <div style="width: 32px; height: 32px; background: var(--turkis-surface); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 16px;">🏷️</div>
      <div><div style="font-size: 15px;">Contexts</div><div class="task-meta">@Office, @Home, @Calls...</div></div>
    </div>
    <div style="display: flex; align-items: center; gap: var(--sp-2);">
      <div style="background: var(--grau-1); padding: 2px 8px; border-radius: 10px; font-size: 12px; color: var(--grau-5);">6</div>
      <span style="color: var(--grau-3);">›</span>
    </div>
  </div>

  <div class="task-row" style="justify-content: space-between;">
    <div style="display: flex; align-items: center; gap: var(--sp-3);">
      <div style="width: 32px; height: 32px; background: #fef3c7; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 16px;">📋</div>
      <div><div style="font-size: 15px;">Reference</div><div class="task-meta">Non-actionable info</div></div>
    </div>
    <div style="display: flex; align-items: center; gap: var(--sp-2);">
      <div style="background: var(--grau-1); padding: 2px 8px; border-radius: 10px; font-size: 12px; color: var(--grau-5);">8</div>
      <span style="color: var(--grau-3);">›</span>
    </div>
  </div>

  <div class="section-header" style="padding-top: var(--sp-5);">Review</div>

  <div class="task-row" style="justify-content: space-between; cursor: pointer;" onclick="showDetail('screen-review')">
    <div style="display: flex; align-items: center; gap: var(--sp-3);">
      <div style="width: 32px; height: 32px; background: #ecfdf5; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 16px;">🔄</div>
      <div><div style="font-size: 15px;">Weekly Review</div><div class="task-meta">Last done: 5 days ago</div></div>
    </div>
    <div style="display: flex; align-items: center; gap: var(--sp-2);">
      <div style="background: #fef3c7; padding: 2px 8px; border-radius: 10px; font-size: 12px; color: #92400e;">Due</div>
      <span style="color: var(--grau-3);">›</span>
    </div>
  </div>

  <div class="section-header" style="padding-top: var(--sp-5);">App</div>

  <div class="task-row" style="justify-content: space-between;">
    <div style="display: flex; align-items: center; gap: var(--sp-3);">
      <div style="width: 32px; height: 32px; background: var(--grau-1); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 16px;">🏔️</div>
      <div><div style="font-size: 15px;">Areas of Responsibility</div><div class="task-meta">Manage life areas</div></div>
    </div>
    <span style="color: var(--grau-3);">›</span>
  </div>

  <div class="task-row" style="justify-content: space-between;">
    <div style="display: flex; align-items: center; gap: var(--sp-3);">
      <div style="width: 32px; height: 32px; background: var(--grau-1); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 16px;">⚙️</div>
      <div><div style="font-size: 15px;">Settings</div><div class="task-meta">Theme, notifications, account</div></div>
    </div>
    <span style="color: var(--grau-3);">›</span>
  </div>
</div>
```

- [ ] **Step 2: Verify and commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): more screen with GTD lists, review, and app sections"
```

---

### Task 8: Quick Capture Bottom Sheet

Fill in the Quick Capture overlay with the flat, borderless input design.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Replace the empty bottom sheet content in `#overlay-capture`**

The overlay div already exists from Task 2. Replace the bottom sheet inner content:

```html
<div class="overlay" id="overlay-capture" onclick="if(event.target===this)this.classList.remove('active')">
  <div class="bottom-sheet">
    <div class="sheet-handle"></div>
    <div style="font-family: var(--font-serif); font-size: 20px; font-weight: 700; margin-bottom: var(--sp-6);">Quick Capture</div>
    <div style="font-size: 17px; color: var(--schwarz); margin-bottom: 6px; padding-left: 2px;">Buy birthday gift for Mama</div>
    <div style="font-size: 14px; color: var(--grau-3); margin-bottom: var(--sp-8); padding-left: 2px;">Add a note...</div>
    <div style="padding: 14px; background: var(--gelb); border-radius: 12px; text-align: center; font-weight: 600; font-size: 15px; color: var(--schwarz); cursor: pointer;" onclick="document.getElementById('overlay-capture').classList.remove('active')">Save to Inbox</div>
  </div>
</div>
```

- [ ] **Step 2: Verify FAB opens sheet and clicking Save/backdrop dismisses it**

- [ ] **Step 3: Commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): quick capture bottom sheet with flat borderless inputs"
```

---

### Task 9: Process Item Screen

Build the triage screen with GTD prompt, swipe indicators, action grid, and 2-minute rule.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add Process Item content inside `#screen-process`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div class="nav-bar">
  <div class="nav-back" onclick="goBack('screen-inbox')">Inbox</div>
</div>

<div style="padding: var(--sp-5) var(--sp-5) var(--sp-4);">
  <div style="font-family: var(--font-serif); font-size: 22px; font-weight: 700; line-height: 1.3;">Buy birthday gift for Mama</div>
  <div style="font-size: 13px; color: var(--grau-3); margin-top: 6px;">Added today</div>
</div>

<div style="padding: 0 var(--sp-5); margin-bottom: var(--sp-6);">
  <div style="background: var(--turkis-surface); border-radius: 12px; padding: 14px var(--sp-4);">
    <div style="font-size: 14px; color: var(--turkis-dark); font-weight: 500;">What's the next action?</div>
  </div>
</div>

<div style="padding: 0 var(--sp-5); margin-bottom: var(--sp-6);">
  <div style="display: flex; justify-content: space-between; align-items: center; padding: var(--sp-4) 0;">
    <div style="text-align: center; flex: 1;">
      <div style="font-size: 24px; margin-bottom: var(--sp-1);">💭</div>
      <div style="font-size: 11px; color: var(--lila); font-weight: 500;">← Someday</div>
    </div>
    <div style="width: 1px; height: 40px; background: var(--grau-2);"></div>
    <div style="text-align: center; flex: 1;">
      <div style="font-size: 24px; margin-bottom: var(--sp-1);">⚡</div>
      <div style="font-size: 11px; color: var(--turkis); font-weight: 500;">Next Action →</div>
    </div>
  </div>
</div>

<div style="padding: 0 var(--sp-5);">
  <div style="font-size: 12px; font-weight: 600; color: var(--grau-5); text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: var(--sp-3);">Or choose:</div>
  <div style="display: grid; grid-template-columns: 1fr 1fr; gap: var(--sp-2);">
    <div style="padding: 14px; border: 1px solid var(--grau-2); border-radius: 12px; text-align: center; cursor: pointer;">
      <div style="font-size: 18px; margin-bottom: var(--sp-1);">📁</div>
      <div style="font-size: 13px; font-weight: 500;">Add to Project</div>
    </div>
    <div style="padding: 14px; border: 1px solid var(--grau-2); border-radius: 12px; text-align: center; cursor: pointer;">
      <div style="font-size: 18px; margin-bottom: var(--sp-1);">⏳</div>
      <div style="font-size: 13px; font-weight: 500;">Waiting For</div>
    </div>
    <div style="padding: 14px; border: 1px solid var(--grau-2); border-radius: 12px; text-align: center; cursor: pointer;">
      <div style="font-size: 18px; margin-bottom: var(--sp-1);">📋</div>
      <div style="font-size: 13px; font-weight: 500;">Reference</div>
    </div>
    <div style="padding: 14px; border: 1px solid var(--grau-2); border-radius: 12px; text-align: center; cursor: pointer;">
      <div style="font-size: 18px; margin-bottom: var(--sp-1);">🗑️</div>
      <div style="font-size: 13px; font-weight: 500;">Trash</div>
    </div>
  </div>

  <div style="margin-top: var(--sp-4); padding: var(--sp-3) var(--sp-4); background: #fef3c7; border-radius: 10px; display: flex; align-items: center; gap: 10px;">
    <span style="font-size: 16px;">⚡</span>
    <div style="font-size: 13px; color: #92400e;">Takes less than 2 minutes? <strong>Just do it now.</strong></div>
  </div>
</div>
```

- [ ] **Step 2: Verify clicking an inbox item opens process screen, back button returns to inbox**

- [ ] **Step 3: Commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): process item triage screen with GTD prompt and action grid"
```

---

### Task 10: Task Detail Screen

Build the full task editing view with metadata rows and notes.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add Task Detail content inside `#screen-task-detail`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div class="nav-bar">
  <div class="nav-back" onclick="goBack('screen-next')">Next Actions</div>
  <div class="nav-action">Save</div>
</div>

<div style="padding: var(--sp-4) var(--sp-5); display: flex; gap: 14px; align-items: flex-start;">
  <div class="checkbox" style="width: 24px; height: 24px; margin-top: 2px;"></div>
  <div style="font-size: 20px; font-weight: 600; line-height: 1.3;">Draft proposal for client meeting</div>
</div>

<div style="padding: 0 var(--sp-5);">
  <div style="padding: 14px 0; border-bottom: 1px solid var(--grau-1); display: flex; justify-content: space-between; align-items: center;">
    <div style="font-size: 14px; color: var(--grau-5);">Project</div>
    <div style="font-size: 14px; font-weight: 500;">Website Relaunch ›</div>
  </div>
  <div style="padding: 14px 0; border-bottom: 1px solid var(--grau-1); display: flex; justify-content: space-between; align-items: center;">
    <div style="font-size: 14px; color: var(--grau-5);">Context</div>
    <div style="padding: 4px 12px; background: var(--turkis-surface); border-radius: 16px; font-size: 13px; color: var(--turkis-dark); font-weight: 500;">@Office</div>
  </div>
  <div style="padding: 14px 0; border-bottom: 1px solid var(--grau-1); display: flex; justify-content: space-between; align-items: center;">
    <div style="font-size: 14px; color: var(--grau-5);">Due date</div>
    <div style="font-size: 14px; color: var(--orange); font-weight: 500;">Friday, Mar 21</div>
  </div>
  <div style="padding: 14px 0; border-bottom: 1px solid var(--grau-1); display: flex; justify-content: space-between; align-items: center;">
    <div style="font-size: 14px; color: var(--grau-5);">Area</div>
    <div style="font-size: 14px;">Work</div>
  </div>
  <div style="padding: 14px 0; border-bottom: 1px solid var(--grau-1); display: flex; justify-content: space-between; align-items: center;">
    <div style="font-size: 14px; color: var(--grau-5);">Energy</div>
    <div style="font-size: 14px;">High</div>
  </div>
  <div style="padding: 14px 0; border-bottom: 1px solid var(--grau-1); display: flex; justify-content: space-between; align-items: center;">
    <div style="font-size: 14px; color: var(--grau-5);">Time needed</div>
    <div style="font-size: 14px;">30 min</div>
  </div>
</div>

<div style="padding: var(--sp-5);">
  <div style="font-size: 14px; color: var(--grau-5); margin-bottom: var(--sp-2);">Notes</div>
  <div style="font-size: 14px; line-height: 1.6; padding: var(--sp-3) var(--sp-4); background: var(--grau-1); border-radius: 12px; min-height: 60px;">
    Include Q2 projections and the updated timeline. Check with Sarah for the latest figures.
  </div>
</div>

<div style="padding: 0 var(--sp-5);">
  <div style="text-align: center; padding: 14px; color: var(--rot); font-size: 15px; cursor: pointer;">Delete Task</div>
</div>
```

- [ ] **Step 2: Verify clicking a Next Actions task opens detail, back returns to Next Actions**

- [ ] **Step 3: Commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): task detail screen with metadata rows and notes"
```

---

### Task 11: Project Detail Screen

Build the project drill-down with outcome, next actions, waiting for, and completed sections.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add Project Detail content inside `#screen-project-detail`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div class="nav-bar">
  <div class="nav-back" onclick="goBack('screen-projects')">Projects</div>
  <div class="nav-action">Edit</div>
</div>

<div style="padding: var(--sp-2) var(--sp-5) var(--sp-1);">
  <div style="font-family: var(--font-serif); font-size: 24px; font-weight: 700;">Website Relaunch</div>
  <div style="font-size: 13px; color: var(--grau-5); margin-top: var(--sp-1);">Work</div>
</div>

<div style="padding: var(--sp-3) var(--sp-5) var(--sp-4);">
  <div style="font-size: 14px; line-height: 1.5; font-style: italic; color: var(--grau-5);">
    "New company website live with updated branding and portfolio section."
  </div>
</div>

<div style="padding: 0 var(--sp-4);">
  <div class="section-header">Next Actions</div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Draft proposal for client meeting</div><div class="task-meta">@Office · Due Fri</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Review PR from Sarah</div><div class="task-meta">@Office</div></div>
  </div>
  <div class="task-row" onclick="showDetail('screen-task-detail')">
    <div class="checkbox"></div>
    <div><div class="task-title">Call Hans about project deadline</div><div class="task-meta">@Calls</div></div>
  </div>

  <div class="section-header" style="padding-top: 18px; color: var(--blau);">Waiting For</div>
  <div style="padding: var(--sp-3) var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="font-size: 15px;">Design mockups from Lisa</div>
    <div style="display: flex; gap: 10px; margin-top: var(--sp-1); font-size: 12px;">
      <span style="color: var(--blau);">👤 Lisa</span>
      <span style="color: var(--grau-3);">Delegated Mar 12</span>
    </div>
  </div>

  <div class="section-header" style="padding-top: 18px; color: var(--grun);">Completed</div>
  <div class="task-row">
    <div class="checkbox checkbox--done">✓</div>
    <div><div class="task-title" style="color: var(--grau-3); text-decoration: line-through;">Set up staging environment</div><div class="task-meta">Completed Mar 10</div></div>
  </div>
  <div class="task-row">
    <div class="checkbox checkbox--done">✓</div>
    <div><div class="task-title" style="color: var(--grau-3); text-decoration: line-through;">Gather brand assets</div><div class="task-meta">Completed Mar 8</div></div>
  </div>
</div>

<div style="padding: var(--sp-5);">
  <div style="padding: var(--sp-3); border: 1px dashed var(--grau-3); border-radius: 12px; text-align: center; font-size: 14px; color: var(--grau-5); cursor: pointer;">
    + Add next action
  </div>
</div>
```

- [ ] **Step 2: Verify clicking a project row opens detail, back returns to Projects**

- [ ] **Step 3: Commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): project detail screen with outcome, actions, and completed"
```

---

### Task 12: Weekly Review Wizard

Build the guided review wizard with progress bar, step content, breadcrumbs, and navigation.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Add Review Wizard content inside `#screen-review`**

```html
<div class="status-bar"><span>9:41</span><span>●●● ▐▌ 🔋</span></div>
<div style="padding: var(--sp-3) var(--sp-5); display: flex; justify-content: space-between; align-items: center;">
  <div style="font-size: 15px; color: var(--turkis); cursor: pointer;" onclick="goBack('screen-more')">Exit</div>
  <div style="font-size: 13px; color: var(--grau-5);">Step 3 of 6</div>
</div>

<!-- Progress bar -->
<div style="padding: 0 var(--sp-5) var(--sp-5);">
  <div style="height: 4px; background: var(--grau-1); border-radius: 2px; overflow: hidden;">
    <div style="width: 50%; height: 100%; background: var(--turkis); border-radius: 2px;"></div>
  </div>
</div>

<div style="padding: 0 var(--sp-5) var(--sp-2);">
  <div style="font-family: var(--font-serif); font-size: 24px; font-weight: 700;">Review Projects</div>
  <div style="font-size: 14px; color: var(--grau-5); margin-top: 6px; line-height: 1.5;">Is each project still relevant? Does every active project have a next action?</div>
</div>

<div style="padding: var(--sp-4) var(--sp-4) 0;">
  <!-- Reviewed -->
  <div style="display: flex; align-items: center; gap: var(--sp-3); padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="width: 22px; height: 22px; background: var(--grun); border-radius: 6px; display: flex; align-items: center; justify-content: center; color: white; font-size: 12px; font-weight: 700; flex-shrink: 0;">✓</div>
    <div><div style="font-size: 15px; color: var(--grau-3);">Website Relaunch</div><div class="task-meta">3 next actions · Work</div></div>
  </div>

  <!-- Reviewed -->
  <div style="display: flex; align-items: center; gap: var(--sp-3); padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="width: 22px; height: 22px; background: var(--grun); border-radius: 6px; display: flex; align-items: center; justify-content: center; color: white; font-size: 12px; font-weight: 700; flex-shrink: 0;">✓</div>
    <div><div style="font-size: 15px; color: var(--grau-3);">Mobile App v2</div><div class="task-meta">2 next actions · Work</div></div>
  </div>

  <!-- Current -->
  <div style="display: flex; align-items: center; gap: var(--sp-3); padding: 14px var(--sp-2); border-bottom: 1px solid var(--grau-1); background: #fefce8; border-radius: 8px; margin: 0 -4px;">
    <div style="width: 22px; height: 22px; border: 2px solid var(--grau-3); border-radius: 6px; flex-shrink: 0;"></div>
    <div style="flex: 1;"><div style="font-size: 15px; font-weight: 500;">Team Ops</div><div style="font-size: 12px; color: var(--grau-5); margin-top: 2px;">1 next action · Work</div></div>
    <span style="color: var(--grau-3); font-size: 16px;">›</span>
  </div>

  <!-- Pending -->
  <div style="display: flex; align-items: center; gap: var(--sp-3); padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="width: 22px; height: 22px; border: 2px solid var(--grau-2); border-radius: 6px; flex-shrink: 0;"></div>
    <div><div style="font-size: 15px;">Summer Vacation</div><div class="task-meta">2 next actions · Personal</div></div>
  </div>

  <div style="display: flex; align-items: center; gap: var(--sp-3); padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="width: 22px; height: 22px; border: 2px solid var(--grau-2); border-radius: 6px; flex-shrink: 0;"></div>
    <div><div style="font-size: 15px;">Home Maintenance</div><div class="task-meta">2 next actions · Personal</div></div>
  </div>

  <div style="display: flex; align-items: center; gap: var(--sp-3); padding: 14px var(--sp-1); border-bottom: 1px solid var(--grau-1);">
    <div style="width: 22px; height: 22px; border: 2px solid var(--grau-2); border-radius: 6px; flex-shrink: 0;"></div>
    <div><div style="font-size: 15px;">Marathon Training</div><div class="task-meta">1 next action · Health</div></div>
  </div>
</div>

<!-- Breadcrumbs -->
<div style="position: absolute; bottom: 80px; left: var(--sp-5); right: var(--sp-5);">
  <div style="display: flex; justify-content: center; gap: var(--sp-4); font-size: 11px; color: var(--grau-3);">
    <span style="color: var(--grun);">✓ Inbox</span>
    <span style="color: var(--grun);">✓ Next</span>
    <span style="color: var(--schwarz); font-weight: 600;">Projects</span>
    <span>Waiting</span>
    <span>Someday</span>
    <span>Done</span>
  </div>
</div>

<!-- Next button -->
<div style="position: absolute; bottom: var(--sp-6); left: var(--sp-5); right: var(--sp-5);">
  <div style="padding: 14px; background: var(--turkis-dark); border-radius: 12px; text-align: center; font-weight: 600; font-size: 15px; color: white; cursor: pointer;">
    Next: Review Waiting For →
  </div>
</div>
```

- [ ] **Step 2: Verify clicking Weekly Review in More opens the wizard, Exit returns to More**

- [ ] **Step 3: Commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): weekly review wizard with progress bar and breadcrumbs"
```

---

### Task 13: Final Polish + Verification

Verify all navigation paths work, do a visual pass, and make the final commit.

**Files:**
- Modify: `project/design/guad-prototype.html`

- [ ] **Step 1: Test all navigation paths**

Verify each of these works:
1. Tab bar: Inbox ↔ Next ↔ Projects ↔ Waiting ↔ More
2. FAB → Quick Capture overlay → Save/backdrop dismisses
3. Inbox task row → Process Item → Back to Inbox
4. Next Actions task row → Task Detail → Back to Next Actions
5. Projects row → Project Detail → Back to Projects
6. Project Detail task row → Task Detail → Back to Next Actions (acceptable — detail always goes back to Next)
7. More → Weekly Review → Exit back to More

- [ ] **Step 2: Visual verification**

Check each screen matches the spec:
- Serif titles, sans-serif body text
- BW color palette (teal primary, yellow FAB, warm grays)
- Consistent spacing and row heights
- Phone frame centered with rounded corners

- [ ] **Step 3: Fix any issues found**

Address any visual or navigation bugs.

- [ ] **Step 4: Final commit**

```bash
git add project/design/guad-prototype.html
git commit -m "feat(design): complete Guad interactive prototype with all 10 screens"
```
