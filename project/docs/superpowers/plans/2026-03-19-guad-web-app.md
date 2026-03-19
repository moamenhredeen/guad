# Guad Web App Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a full GTD web application with Vue 3, shadcn-vue, and Tailwind CSS that connects to the existing Spring Boot/Keycloak backend.

**Architecture:** Three-panel layout (collapsible sidebar + main content + task detail side panel). OAuth2/PKCE auth with Keycloak. Per-feature Pinia stores backed by typed API services. Screen-by-screen delivery — each task produces a working, committable increment.

**Tech Stack:** Vue 3 (Composition API), Vue Router, Pinia, Tailwind CSS 4, shadcn-vue (reka-ui), Lucide icons, VueUse, Keycloak (OAuth2/OIDC).

**Spec:** `project/docs/superpowers/specs/2026-03-19-guad-web-app-design.md`

---

## File Structure

### New files to create

```
web/src/
├── api/
│   ├── client.ts              # Fetch wrapper with JWT interceptor (replaces services/api/client.ts)
│   ├── inbox.ts               # Inbox API calls
│   ├── actions.ts             # Actions API calls
│   ├── projects.ts            # Projects API calls
│   ├── contexts.ts            # Contexts API calls
│   ├── areas.ts               # Areas API calls
│   ├── waitingFor.ts          # Waiting For API calls
│   ├── somedayMaybe.ts        # Someday/Maybe API calls
│   ├── review.ts              # Weekly Review API calls
│   └── dashboard.ts           # Dashboard summary API calls
├── types/
│   └── index.ts               # TypeScript interfaces for all backend DTOs
├── stores/
│   ├── auth.ts                # OAuth2 token state, user info from JWT claims
│   ├── dashboard.ts           # Sidebar badge counts
│   ├── inbox.ts               # Inbox items
│   ├── actions.ts             # Next actions
│   ├── projects.ts            # Projects list + detail
│   ├── contexts.ts            # Contexts
│   ├── areas.ts               # Areas
│   ├── waitingFor.ts          # Waiting For items
│   ├── somedayMaybe.ts        # Someday/Maybe items
│   └── review.ts              # Weekly Review state
├── composables/
│   ├── useQuickCapture.ts     # Q shortcut + modal state
│   └── useTaskDetail.ts       # Side panel open/close + selected task
├── components/
│   ├── app/
│   │   ├── AppSidebar.vue     # Replaces existing AppSidebar.vue
│   │   ├── QuickCaptureModal.vue
│   │   └── TaskDetailPanel.vue
│   ├── task/
│   │   ├── TaskRow.vue        # Reusable task row (checkbox + title + meta)
│   │   ├── TaskCheckbox.vue   # Circular checkbox with done state
│   │   ├── InlineAddTask.vue  # Todoist-style expandable add form
│   │   └── TriageBar.vue      # Inbox triage action buttons
│   └── icons/
│       └── IconLogo.vue       # Keep existing
├── views/
│   ├── InboxView.vue          # Rewrite existing stub
│   ├── NextActionsView.vue    # New
│   ├── ProjectsView.vue       # New
│   ├── ProjectDetailView.vue  # New
│   ├── WaitingForView.vue     # New
│   ├── SomedayMaybeView.vue   # New
│   ├── WeeklyReviewView.vue   # New
│   ├── ContextsView.vue       # New
│   ├── AreasView.vue          # New
│   ├── SettingsView.vue       # New
│   ├── LoginView.vue          # Rewrite: OAuth2 redirect trigger
│   └── CallbackView.vue       # New: OAuth2 callback handler
├── router.ts                  # Rewrite
├── App.vue                    # Keep as-is
├── main.ts                    # Keep as-is
└── styles.css                 # Extend with BW design tokens
```

### Files to delete

```
web/src/views/DashboardView.vue
web/src/views/InboxItem.vue
web/src/views/SignUpView.vue
web/src/views/OTPVerificationView.vue
web/src/views/users/                     # entire directory
web/src/services/                        # entire directory (replaced by api/)
web/src/stores/counter.ts
web/src/components/HelloWorld.vue
web/src/components/TheWelcome.vue
web/src/components/WelcomeItem.vue
web/src/components/icons/IconCommunity.vue
web/src/components/icons/IconDocumentation.vue
web/src/components/icons/IconEcosystem.vue
web/src/components/icons/IconSupport.vue
web/src/components/icons/IconTooling.vue
web/src/components/NavMain.vue           # replaced by AppSidebar
web/src/components/NavProjects.vue       # replaced by AppSidebar
web/src/components/NavUser.vue           # replaced by AppSidebar
web/src/components/__tests__/HelloWorld.spec.ts
web/src/lib/breadcrumber.ts              # replaced by simpler routing
```

### Files to modify

```
web/src/styles.css                       # Add BW design tokens
web/src/components/SidebarLayout.vue     # Simplify, add TaskDetailPanel
```

---

### Task 1: Scaffolding Cleanup + BW Design Tokens

Remove placeholder content and add the BW color palette to Tailwind CSS.

**Files:**
- Delete: all files listed in "Files to delete" above
- Modify: `web/src/styles.css`

- [ ] **Step 1: Delete all placeholder files**

```bash
cd /home/moamen/git-repos/guad/web
rm -f src/views/DashboardView.vue src/views/InboxItem.vue src/views/SignUpView.vue src/views/OTPVerificationView.vue
rm -rf src/views/users/
rm -rf src/services/
rm -f src/stores/counter.ts
rm -f src/components/HelloWorld.vue src/components/TheWelcome.vue src/components/WelcomeItem.vue
rm -f src/components/icons/IconCommunity.vue src/components/icons/IconDocumentation.vue src/components/icons/IconEcosystem.vue src/components/icons/IconSupport.vue src/components/icons/IconTooling.vue
rm -f src/components/NavMain.vue src/components/NavProjects.vue src/components/NavUser.vue
rm -f src/components/__tests__/HelloWorld.spec.ts
rm -f src/lib/breadcrumber.ts
```

- [ ] **Step 2: Add BW design tokens to styles.css**

Add after `@import "tw-animate-css";` and before `@custom-variant`:

```css
@theme inline {
  /* ... keep existing theme variables ... */

  /* BW Design System Colors */
  --color-turkis-dark: #094954;
  --color-turkis: #309AAF;
  --color-turkis-surface: #e8f4f6;
  --color-gelb: #FFFC00;
  --color-orange: #DD6F06;
  --color-rot: #FD4D4D;
  --color-schwarz: #2A2623;
  --color-grau-5: #6D6766;
  --color-grau-3: #BBB6B5;
  --color-grau-2: #E4E0E0;
  --color-grau-1: #F4F3F3;
  --color-grun: #609D0F;
  --color-blau: #508CF1;
  --color-lila: #C761EC;
}
```

Also update `:root` CSS variables to use BW palette for shadcn-vue semantic tokens:

```css
:root {
  /* ... existing ... */
  --primary: oklch(0.45 0.1 200);          /* Türkis Hell approximate */
  --primary-foreground: oklch(1 0 0);
  --sidebar-primary: oklch(0.24 0.06 200); /* Türkis Dunkel approximate */
  --sidebar-accent: oklch(0.94 0.01 200);  /* Türkis Surface approximate */
  --destructive: oklch(0.65 0.2 25);       /* Rot Hell approximate */
}
```

And add base typography in `@layer base`:

```css
@layer base {
  * {
    @apply border-border outline-ring/50;
  }
  body {
    @apply bg-background text-foreground;
    font-family: -apple-system, 'Segoe UI', system-ui, sans-serif;
    color: #2A2623;
  }
}
```

- [ ] **Step 3: Verify project still compiles**

Run: `cd /home/moamen/git-repos/guad/web && npm run build-only 2>&1 | head -20`
Expected: Build errors about missing imports (router references deleted files). That's expected — we'll fix the router next.

- [ ] **Step 4: Commit**

```bash
git add -A web/src/
git commit -m "chore: remove scaffolding placeholders and add BW design tokens"
```

---

### Task 2: TypeScript Types

Define all TypeScript interfaces matching the backend DTOs.

**Files:**
- Create: `web/src/types/index.ts`

- [ ] **Step 1: Create types file**

```typescript
// web/src/types/index.ts

// === Inbox ===
export interface InboxItem {
  id: string
  title: string
  description: string | null
  status: string
  createdDate: string
}

export interface CreateInboxItemRequest {
  title: string
  description?: string
}

export type ProcessAction = 'NEXT_ACTION' | 'PROJECT' | 'WAITING_FOR' | 'SOMEDAY_MAYBE' | 'REFERENCE' | 'TRASH'

export interface ProcessInboxItemRequest {
  action: ProcessAction
  projectId?: string
  delegatedTo?: string
  contextIds?: string[]
}

// === Actions ===
export type ActionStatus = 'NEXT' | 'IN_PROGRESS' | 'COMPLETED' | 'SOMEDAY_MAYBE'
export type EnergyLevel = 'HIGH' | 'MEDIUM' | 'LOW'

export interface ActionResponse {
  id: string
  description: string
  notes: string | null
  status: ActionStatus
  energyLevel: EnergyLevel | null
  estimatedDuration: string | null
  dueDate: string | null
  scheduledDate: string | null
  projectName: string | null
  projectId: string | null
  areaName: string | null
  areaId: string | null
  contexts: ContextResponse[]
  createdDate: string
  completedDate: string | null
}

export interface CreateActionRequest {
  description: string
  notes?: string
  projectId?: string
  areaId?: string
  contextIds?: string[]
  energyLevel?: EnergyLevel
  estimatedDuration?: string
  dueDate?: string
  scheduledDate?: string
}

export interface UpdateActionRequest {
  description?: string
  notes?: string
  projectId?: string
  areaId?: string
  contextIds?: string[]
  energyLevel?: EnergyLevel
  estimatedDuration?: string
  dueDate?: string
  scheduledDate?: string
}

// === Projects ===
export type ProjectStatus = 'ACTIVE' | 'SOMEDAY_MAYBE' | 'COMPLETED'

export interface ProjectResponse {
  id: string
  name: string
  description: string | null
  desiredOutcome: string | null
  status: ProjectStatus
  areaName: string | null
  areaId: string | null
  color: string | null
  nextActionCount: number
  createdDate: string
}

export interface ProjectDetailResponse {
  id: string
  name: string
  description: string | null
  desiredOutcome: string | null
  status: ProjectStatus
  areaName: string | null
  areaId: string | null
  color: string | null
  nextActions: ActionResponse[]
  waitingForItems: WaitingForResponse[]
  completedActions: ActionResponse[]
  createdDate: string
}

export interface CreateProjectRequest {
  name: string
  description?: string
  desiredOutcome?: string
  areaId?: string
  color?: string
}

// === Contexts ===
export interface ContextResponse {
  id: string
  name: string
  description: string | null
  color: string | null
  iconKey: string | null
}

export interface CreateContextRequest {
  name: string
  description?: string
  color?: string
  iconKey?: string
}

// === Areas ===
export interface AreaResponse {
  id: string
  name: string
  description: string | null
}

export interface CreateAreaRequest {
  name: string
  description?: string
}

// === Waiting For ===
export type WaitingForStatus = 'WAITING' | 'RESOLVED'

export interface WaitingForResponse {
  id: string
  title: string
  delegatedTo: string | null
  createdDate: string
  notes: string | null
  status: WaitingForStatus
  projectName: string | null
  projectId: string | null
}

export interface CreateWaitingForRequest {
  title: string
  delegatedTo?: string
  delegatedAt?: string
  followUpDate?: string
  notes?: string
  actionId?: string
  projectId?: string
}

// === Someday/Maybe ===
export interface SomedayMaybeResponse {
  actions: ActionResponse[]
  projects: ProjectResponse[]
}

// === Weekly Review ===
export type ReviewStep = 'CLEAR_INBOX' | 'REVIEW_NEXT_ACTIONS' | 'REVIEW_PROJECTS' | 'REVIEW_WAITING_FOR' | 'REVIEW_SOMEDAY_MAYBE' | 'DONE'

export interface WeeklyReviewResponse {
  id: string
  startedAt: string
  completedAt: string | null
  currentStep: ReviewStep
  notes: string | null
}

// === Dashboard ===
export interface DashboardResponse {
  inboxCount: number
  nextActionsCount: number
  activeProjectsCount: number
  waitingForCount: number
  somedayMaybeActionsCount: number
  weeklyReviewDue: boolean
  lastReviewDate: string | null
}

// === Auth (from JWT claims) ===
export interface UserInfo {
  id: string
  username: string
  email: string
}
```

- [ ] **Step 2: Verify file has no TypeScript errors**

Run: `cd /home/moamen/git-repos/guad/web && npx vue-tsc --noEmit 2>&1 | head -20`
Expected: May still have errors from deleted files (router). Types file itself should be clean.

- [ ] **Step 3: Commit**

```bash
git add web/src/types/index.ts
git commit -m "feat: add TypeScript interfaces for all backend DTOs"
```

---

### Task 3: API Client + Service Layer

Create the fetch-based API client with JWT interceptor and all API service modules.

**Files:**
- Create: `web/src/api/client.ts`
- Create: `web/src/api/inbox.ts`
- Create: `web/src/api/actions.ts`
- Create: `web/src/api/projects.ts`
- Create: `web/src/api/contexts.ts`
- Create: `web/src/api/areas.ts`
- Create: `web/src/api/waitingFor.ts`
- Create: `web/src/api/somedayMaybe.ts`
- Create: `web/src/api/review.ts`
- Create: `web/src/api/dashboard.ts`

- [ ] **Step 1: Create the API client**

```typescript
// web/src/api/client.ts
export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
  ) {
    super(message)
  }
}

let getAccessToken: (() => string | null) | null = null

export function setTokenProvider(provider: () => string | null) {
  getAccessToken = provider
}

let onUnauthorized: (() => void) | null = null

export function setUnauthorizedHandler(handler: () => void) {
  onUnauthorized = handler
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api'

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }

  const token = getAccessToken?.()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  })

  if (response.status === 401) {
    onUnauthorized?.()
    throw new ApiError(401, 'Unauthorized')
  }

  if (!response.ok) {
    const text = await response.text().catch(() => 'Request failed')
    throw new ApiError(response.status, text)
  }

  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return undefined as T
  }

  return response.json()
}

export const api = {
  get: <T>(path: string) => request<T>('GET', path),
  post: <T>(path: string, body?: unknown) => request<T>('POST', path, body),
  put: <T>(path: string, body?: unknown) => request<T>('PUT', path, body),
  patch: <T>(path: string, body?: unknown) => request<T>('PATCH', path, body),
  delete: <T>(path: string) => request<T>('DELETE', path),
}
```

- [ ] **Step 2: Create inbox API service**

```typescript
// web/src/api/inbox.ts
import type { InboxItem, CreateInboxItemRequest, ProcessInboxItemRequest } from '@/types'
import { api } from './client'

export const inboxApi = {
  list: () => api.get<InboxItem[]>('/inbox'),
  get: (id: string) => api.get<InboxItem>(`/inbox/${id}`),
  create: (data: CreateInboxItemRequest) => api.post<InboxItem>('/inbox', data),
  delete: (id: string) => api.delete<void>(`/inbox/${id}`),
  process: (id: string, data: ProcessInboxItemRequest) => api.post<void>(`/inbox/${id}/process`, data),
}
```

- [ ] **Step 3: Create actions API service**

```typescript
// web/src/api/actions.ts
import type { ActionResponse, CreateActionRequest, UpdateActionRequest } from '@/types'
import { api } from './client'

export const actionsApi = {
  list: (params?: { status?: string; contextId?: string }) => {
    const query = new URLSearchParams()
    if (params?.status) query.set('status', params.status)
    if (params?.contextId) query.set('contextId', params.contextId)
    const qs = query.toString()
    return api.get<ActionResponse[]>(`/actions${qs ? `?${qs}` : ''}`)
  },
  get: (id: string) => api.get<ActionResponse>(`/actions/${id}`),
  create: (data: CreateActionRequest) => api.post<ActionResponse>('/actions', data),
  update: (id: string, data: UpdateActionRequest) => api.put<ActionResponse>(`/actions/${id}`, data),
  delete: (id: string) => api.delete<void>(`/actions/${id}`),
  complete: (id: string) => api.patch<ActionResponse>(`/actions/${id}/complete`),
  updateStatus: (id: string, status: string) => api.patch<ActionResponse>(`/actions/${id}/status`, { status }),
}
```

- [ ] **Step 4: Create projects API service**

```typescript
// web/src/api/projects.ts
import type { ProjectResponse, ProjectDetailResponse, CreateProjectRequest } from '@/types'
import { api } from './client'

export const projectsApi = {
  list: () => api.get<ProjectResponse[]>('/projects'),
  get: (id: string) => api.get<ProjectDetailResponse>(`/projects/${id}`),
  create: (data: CreateProjectRequest) => api.post<ProjectResponse>('/projects', data),
  update: (id: string, data: CreateProjectRequest) => api.put<ProjectResponse>(`/projects/${id}`, data),
  delete: (id: string) => api.delete<void>(`/projects/${id}`),
  addAction: (id: string, description: string) => api.post<void>(`/projects/${id}/actions`, { description }),
  updateStatus: (id: string, status: string) => api.patch<ProjectResponse>(`/projects/${id}/status`, { status }),
}
```

- [ ] **Step 5: Create remaining API services**

```typescript
// web/src/api/contexts.ts
import type { ContextResponse, CreateContextRequest } from '@/types'
import { api } from './client'

export const contextsApi = {
  list: () => api.get<ContextResponse[]>('/contexts'),
  create: (data: CreateContextRequest) => api.post<ContextResponse>('/contexts', data),
  update: (id: string, data: CreateContextRequest) => api.put<ContextResponse>(`/contexts/${id}`, data),
  delete: (id: string) => api.delete<void>(`/contexts/${id}`),
}
```

```typescript
// web/src/api/areas.ts
import type { AreaResponse, CreateAreaRequest } from '@/types'
import { api } from './client'

export const areasApi = {
  list: () => api.get<AreaResponse[]>('/areas'),
  create: (data: CreateAreaRequest) => api.post<AreaResponse>('/areas', data),
  update: (id: string, data: CreateAreaRequest) => api.put<AreaResponse>(`/areas/${id}`, data),
  delete: (id: string) => api.delete<void>(`/areas/${id}`),
}
```

```typescript
// web/src/api/waitingFor.ts
import type { WaitingForResponse, CreateWaitingForRequest } from '@/types'
import { api } from './client'

export const waitingForApi = {
  list: () => api.get<WaitingForResponse[]>('/waiting-for'),
  get: (id: string) => api.get<WaitingForResponse>(`/waiting-for/${id}`),
  create: (data: CreateWaitingForRequest) => api.post<WaitingForResponse>('/waiting-for', data),
  update: (id: string, data: CreateWaitingForRequest) => api.put<WaitingForResponse>(`/waiting-for/${id}`, data),
  delete: (id: string) => api.delete<void>(`/waiting-for/${id}`),
  resolve: (id: string) => api.patch<WaitingForResponse>(`/waiting-for/${id}/resolve`),
}
```

```typescript
// web/src/api/somedayMaybe.ts
import type { SomedayMaybeResponse } from '@/types'
import { api } from './client'

export const somedayMaybeApi = {
  list: () => api.get<SomedayMaybeResponse>('/someday-maybe'),
}
```

```typescript
// web/src/api/review.ts
import type { WeeklyReviewResponse } from '@/types'
import { api } from './client'

export const reviewApi = {
  start: () => api.post<WeeklyReviewResponse>('/reviews'),
  getCurrent: () => api.get<WeeklyReviewResponse>('/reviews/current'),
  advanceStep: (id: string) => api.patch<WeeklyReviewResponse>(`/reviews/${id}/step`),
  complete: (id: string) => api.post<WeeklyReviewResponse>(`/reviews/${id}/complete`),
  getLast: () => api.get<WeeklyReviewResponse>('/reviews/last'),
}
```

```typescript
// web/src/api/dashboard.ts
import type { DashboardResponse } from '@/types'
import { api } from './client'

export const dashboardApi = {
  get: () => api.get<DashboardResponse>('/dashboard'),
}
```

- [ ] **Step 6: Verify all files compile**

Run: `cd /home/moamen/git-repos/guad/web && npx vue-tsc --noEmit 2>&1 | head -30`

- [ ] **Step 7: Commit**

```bash
git add web/src/api/
git commit -m "feat: add API client with JWT interceptor and all service modules"
```

---

### Task 4: Auth Store + OAuth2 Flow

Implement the OAuth2/PKCE authentication flow with Keycloak.

**Files:**
- Create: `web/src/stores/auth.ts`
- Create: `web/src/views/LoginView.vue` (rewrite)
- Create: `web/src/views/CallbackView.vue`

- [ ] **Step 1: Create auth store**

```typescript
// web/src/stores/auth.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types'
import { setTokenProvider, setUnauthorizedHandler } from '@/api/client'

const KEYCLOAK_URL = import.meta.env.VITE_KEYCLOAK_URL ?? 'http://localhost:8081/realms/master'
const CLIENT_ID = import.meta.env.VITE_KEYCLOAK_CLIENT_ID ?? 'guad-web'
const REDIRECT_URI = import.meta.env.VITE_KEYCLOAK_REDIRECT_URI ?? `${window.location.origin}/callback`

function generateCodeVerifier(): string {
  const array = new Uint8Array(32)
  crypto.getRandomValues(array)
  return btoa(String.fromCharCode(...array))
    .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

async function generateCodeChallenge(verifier: string): Promise<string> {
  const encoder = new TextEncoder()
  const data = encoder.encode(verifier)
  const digest = await crypto.subtle.digest('SHA-256', data)
  return btoa(String.fromCharCode(...new Uint8Array(digest)))
    .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

function parseJwtPayload(token: string): Record<string, unknown> {
  const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
  return JSON.parse(atob(base64))
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const user = ref<UserInfo | null>(null)
  const isAuthenticated = computed(() => !!accessToken.value)

  // Wire up API client
  setTokenProvider(() => accessToken.value)
  setUnauthorizedHandler(() => login())

  function extractUser(token: string): UserInfo {
    const payload = parseJwtPayload(token)
    return {
      id: payload.sub as string,
      username: (payload.preferred_username as string) ?? '',
      email: (payload.email as string) ?? '',
    }
  }

  function setTokens(access: string, refresh: string | null) {
    accessToken.value = access
    refreshToken.value = refresh
    user.value = extractUser(access)
    if (refresh) {
      localStorage.setItem('guad_refresh_token', refresh)
    }
  }

  function clearTokens() {
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    localStorage.removeItem('guad_refresh_token')
    sessionStorage.removeItem('pkce_verifier')
  }

  async function login() {
    const verifier = generateCodeVerifier()
    const challenge = await generateCodeChallenge(verifier)
    sessionStorage.setItem('pkce_verifier', verifier)

    const params = new URLSearchParams({
      client_id: CLIENT_ID,
      response_type: 'code',
      scope: 'openid profile email',
      redirect_uri: REDIRECT_URI,
      code_challenge: challenge,
      code_challenge_method: 'S256',
    })

    window.location.href = `${KEYCLOAK_URL}/protocol/openid-connect/auth?${params}`
  }

  async function handleCallback(code: string): Promise<boolean> {
    const verifier = sessionStorage.getItem('pkce_verifier')
    if (!verifier) return false

    const response = await fetch(`${KEYCLOAK_URL}/protocol/openid-connect/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        grant_type: 'authorization_code',
        client_id: CLIENT_ID,
        code,
        redirect_uri: REDIRECT_URI,
        code_verifier: verifier,
      }),
    })

    if (!response.ok) return false

    const data = await response.json()
    setTokens(data.access_token, data.refresh_token ?? null)
    sessionStorage.removeItem('pkce_verifier')
    return true
  }

  async function tryRefresh(): Promise<boolean> {
    const stored = refreshToken.value ?? localStorage.getItem('guad_refresh_token')
    if (!stored) return false

    try {
      const response = await fetch(`${KEYCLOAK_URL}/protocol/openid-connect/token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
          grant_type: 'refresh_token',
          client_id: CLIENT_ID,
          refresh_token: stored,
        }),
      })

      if (!response.ok) {
        clearTokens()
        return false
      }

      const data = await response.json()
      setTokens(data.access_token, data.refresh_token ?? null)
      return true
    } catch {
      clearTokens()
      return false
    }
  }

  function logout() {
    const idToken = accessToken.value
    clearTokens()
    const params = new URLSearchParams({
      client_id: CLIENT_ID,
      post_logout_redirect_uri: `${window.location.origin}/login`,
    })
    if (idToken) params.set('id_token_hint', idToken)
    window.location.href = `${KEYCLOAK_URL}/protocol/openid-connect/logout?${params}`
  }

  return {
    accessToken,
    user,
    isAuthenticated,
    login,
    handleCallback,
    tryRefresh,
    logout,
    clearTokens,
  }
})
```

- [ ] **Step 2: Create LoginView (redirect trigger)**

```vue
<!-- web/src/views/LoginView.vue -->
<script lang="ts" setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
onMounted(() => auth.login())
</script>

<template>
  <div class="flex min-h-screen items-center justify-center">
    <p class="text-grau-5 text-sm">Redirecting to login...</p>
  </div>
</template>
```

- [ ] **Step 3: Create CallbackView (OAuth2 callback handler)**

```vue
<!-- web/src/views/CallbackView.vue -->
<script lang="ts" setup>
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

onMounted(async () => {
  const code = route.query.code as string | undefined
  if (!code) {
    router.replace('/login')
    return
  }

  const success = await auth.handleCallback(code)
  if (success) {
    router.replace('/inbox')
  } else {
    router.replace('/login')
  }
})
</script>

<template>
  <div class="flex min-h-screen items-center justify-center">
    <p class="text-grau-5 text-sm">Signing in...</p>
  </div>
</template>
```

- [ ] **Step 4: Commit**

```bash
git add web/src/stores/auth.ts web/src/views/LoginView.vue web/src/views/CallbackView.vue
git commit -m "feat: OAuth2/PKCE auth flow with Keycloak"
```

---

### Task 5: Router + Dashboard Store + App Shell

Rewrite the router with auth guards, create the dashboard store for sidebar badges, and update the sidebar layout.

**Files:**
- Rewrite: `web/src/router.ts`
- Create: `web/src/stores/dashboard.ts`
- Rewrite: `web/src/components/AppSidebar.vue` → `web/src/components/app/AppSidebar.vue`
- Modify: `web/src/components/SidebarLayout.vue`

- [ ] **Step 1: Create dashboard store**

```typescript
// web/src/stores/dashboard.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { DashboardResponse } from '@/types'
import { dashboardApi } from '@/api/dashboard'

export const useDashboardStore = defineStore('dashboard', () => {
  const data = ref<DashboardResponse | null>(null)
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try {
      data.value = await dashboardApi.get()
    } finally {
      loading.value = false
    }
  }

  return { data, loading, fetch }
})
```

- [ ] **Step 2: Rewrite router with auth guards**

```typescript
// web/src/router.ts
import { createRouter, createWebHistory } from 'vue-router'
import SidebarLayout from '@/components/SidebarLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/callback',
      name: 'callback',
      component: () => import('@/views/CallbackView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: SidebarLayout,
      children: [
        { path: '', redirect: '/inbox' },
        {
          path: 'inbox',
          name: 'inbox',
          component: () => import('@/views/InboxView.vue'),
          meta: { title: 'Inbox' },
        },
        {
          path: 'next-actions',
          name: 'next-actions',
          component: () => import('@/views/NextActionsView.vue'),
          meta: { title: 'Next Actions' },
        },
        {
          path: 'projects',
          name: 'projects',
          component: () => import('@/views/ProjectsView.vue'),
          meta: { title: 'Projects' },
        },
        {
          path: 'projects/:id',
          name: 'project-detail',
          component: () => import('@/views/ProjectDetailView.vue'),
          meta: { title: 'Project' },
        },
        {
          path: 'waiting-for',
          name: 'waiting-for',
          component: () => import('@/views/WaitingForView.vue'),
          meta: { title: 'Waiting For' },
        },
        {
          path: 'someday-maybe',
          name: 'someday-maybe',
          component: () => import('@/views/SomedayMaybeView.vue'),
          meta: { title: 'Someday / Maybe' },
        },
        {
          path: 'weekly-review',
          name: 'weekly-review',
          component: () => import('@/views/WeeklyReviewView.vue'),
          meta: { title: 'Weekly Review' },
        },
        {
          path: 'contexts',
          name: 'contexts',
          component: () => import('@/views/ContextsView.vue'),
          meta: { title: 'Contexts' },
        },
        {
          path: 'areas',
          name: 'areas',
          component: () => import('@/views/AreasView.vue'),
          meta: { title: 'Areas' },
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/SettingsView.vue'),
          meta: { title: 'Settings' },
        },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  if (to.meta.public) return true

  const { useAuthStore } = await import('@/stores/auth')
  const auth = useAuthStore()

  if (auth.isAuthenticated) return true

  const refreshed = await auth.tryRefresh()
  if (refreshed) return true

  return { name: 'login' }
})

export default router
```

- [ ] **Step 3: Create stub views for all routes**

Create minimal placeholder views for every route so the app compiles and navigates. Each view is just a title:

```vue
<!-- web/src/views/NextActionsView.vue -->
<script lang="ts" setup></script>
<template><div><h1 class="font-serif text-2xl font-bold text-schwarz">Next Actions</h1></div></template>
```

Create the same pattern for: `ProjectsView.vue`, `ProjectDetailView.vue`, `WaitingForView.vue`, `SomedayMaybeView.vue`, `WeeklyReviewView.vue`, `ContextsView.vue`, `AreasView.vue`, `SettingsView.vue`. Keep existing `InboxView.vue` as a stub too.

- [ ] **Step 4: Create AppSidebar with BW design and badge counts**

```vue
<!-- web/src/components/app/AppSidebar.vue -->
<script setup lang="ts">
import type { SidebarProps } from '@/components/ui/sidebar'
import {
  Inbox,
  Zap,
  FolderOpen,
  Clock,
  CloudSun,
  RefreshCw,
  Tags,
  Mountain,
  Settings,
  LogOut,
  Plus,
} from 'lucide-vue-next'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuBadge,
  SidebarGroup,
  SidebarGroupLabel,
  SidebarGroupContent,
  SidebarRail,
  SidebarSeparator,
} from '@/components/ui/sidebar'
import {
  Avatar,
  AvatarFallback,
} from '@/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import IconLogo from '@/components/icons/IconLogo.vue'
import { useAuthStore } from '@/stores/auth'
import { useDashboardStore } from '@/stores/dashboard'
import { useRoute } from 'vue-router'
import { computed, onMounted } from 'vue'

withDefaults(defineProps<SidebarProps>(), {})

const auth = useAuthStore()
const dashboard = useDashboardStore()
const route = useRoute()

onMounted(() => dashboard.fetch())

const primaryNav = computed(() => [
  { title: 'Inbox', url: '/inbox', icon: Inbox, badge: dashboard.data?.inboxCount },
  { title: 'Next Actions', url: '/next-actions', icon: Zap, badge: dashboard.data?.nextActionsCount },
  { title: 'Projects', url: '/projects', icon: FolderOpen, badge: dashboard.data?.activeProjectsCount },
  { title: 'Waiting For', url: '/waiting-for', icon: Clock, badge: dashboard.data?.waitingForCount },
  { title: 'Someday / Maybe', url: '/someday-maybe', icon: CloudSun },
])

const reviewDue = computed(() => dashboard.data?.weeklyReviewDue ?? false)

const isActive = (url: string) => route.path === url || route.path.startsWith(url + '/')
</script>

<template>
  <Sidebar v-bind="$attrs">
    <SidebarHeader>
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton size="lg" as-child>
            <RouterLink to="/inbox">
              <div class="flex aspect-square size-8 items-center justify-center rounded-lg bg-turkis-dark text-white font-serif font-bold text-sm">
                G
              </div>
              <div class="grid flex-1 text-left text-sm leading-tight">
                <span class="truncate font-medium">Guad</span>
                <span class="truncate text-xs text-muted-foreground">GTD System</span>
              </div>
            </RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>

      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton class="text-turkis font-medium">
            <Plus class="size-4" />
            <span>Add Task</span>
            <kbd class="ml-auto text-[10px] text-muted-foreground font-mono">Q</kbd>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarHeader>

    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in primaryNav" :key="item.title">
              <SidebarMenuButton as-child :is-active="isActive(item.url)">
                <RouterLink :to="item.url">
                  <component :is="item.icon" />
                  <span>{{ item.title }}</span>
                </RouterLink>
              </SidebarMenuButton>
              <SidebarMenuBadge v-if="item.badge">{{ item.badge }}</SidebarMenuBadge>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>

      <SidebarSeparator />

      <SidebarGroup>
        <SidebarGroupLabel>Review</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton as-child :is-active="isActive('/weekly-review')">
                <RouterLink to="/weekly-review">
                  <RefreshCw />
                  <span>Weekly Review</span>
                </RouterLink>
              </SidebarMenuButton>
              <SidebarMenuBadge v-if="reviewDue">
                <span class="rounded-full bg-amber-100 px-1.5 py-0.5 text-[10px] font-semibold text-amber-800">Due</span>
              </SidebarMenuBadge>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>

      <SidebarSeparator />

      <SidebarGroup>
        <SidebarGroupLabel>Manage</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton as-child :is-active="isActive('/contexts')">
                <RouterLink to="/contexts"><Tags /><span>Contexts</span></RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
            <SidebarMenuItem>
              <SidebarMenuButton as-child :is-active="isActive('/areas')">
                <RouterLink to="/areas"><Mountain /><span>Areas</span></RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
            <SidebarMenuItem>
              <SidebarMenuButton as-child :is-active="isActive('/settings')">
                <RouterLink to="/settings"><Settings /><span>Settings</span></RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>

    <SidebarFooter>
      <SidebarMenu>
        <SidebarMenuItem>
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <SidebarMenuButton size="lg">
                <Avatar class="h-8 w-8 rounded-lg">
                  <AvatarFallback class="rounded-lg bg-turkis text-white text-xs">
                    {{ auth.user?.username?.charAt(0)?.toUpperCase() ?? '?' }}
                  </AvatarFallback>
                </Avatar>
                <div class="grid flex-1 text-left text-sm leading-tight">
                  <span class="truncate font-medium">{{ auth.user?.username ?? 'User' }}</span>
                  <span class="truncate text-xs text-muted-foreground">{{ auth.user?.email ?? '' }}</span>
                </div>
              </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent side="right" align="end" :side-offset="4">
              <DropdownMenuItem @click="auth.logout()">
                <LogOut class="mr-2 size-4" />
                Log out
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>

    <SidebarRail />
  </Sidebar>
</template>
```

- [ ] **Step 5: Update SidebarLayout**

Simplify `web/src/components/SidebarLayout.vue` to use the new AppSidebar:

```vue
<!-- web/src/components/SidebarLayout.vue -->
<script setup lang="ts">
import AppSidebar from '@/components/app/AppSidebar.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarInset, SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar'
</script>

<template>
  <SidebarProvider>
    <AppSidebar />
    <SidebarInset>
      <header class="flex h-12 shrink-0 items-center gap-2 px-4">
        <SidebarTrigger class="-ml-1" />
        <Separator orientation="vertical" class="mr-2 data-[orientation=vertical]:h-4" />
      </header>
      <div class="flex flex-1 flex-col p-6 pt-0">
        <RouterView />
      </div>
    </SidebarInset>
  </SidebarProvider>
</template>
```

- [ ] **Step 6: Delete old AppSidebar.vue at root components level**

```bash
rm -f web/src/components/AppSidebar.vue
```

- [ ] **Step 7: Verify build compiles**

Run: `cd /home/moamen/git-repos/guad/web && npm run build-only 2>&1 | tail -5`
Expected: Build succeeds (all routes have stub views, sidebar compiles).

- [ ] **Step 8: Commit**

```bash
git add -A web/src/
git commit -m "feat: app shell with sidebar navigation, routing, and auth guards"
```

---

### Task 6: Shared Task Components

Build the reusable task components used across multiple views.

**Files:**
- Create: `web/src/components/task/TaskCheckbox.vue`
- Create: `web/src/components/task/TaskRow.vue`
- Create: `web/src/components/task/InlineAddTask.vue`
- Create: `web/src/components/task/TriageBar.vue`

- [ ] **Step 1: Create TaskCheckbox**

```vue
<!-- web/src/components/task/TaskCheckbox.vue -->
<script setup lang="ts">
defineProps<{
  done?: boolean
}>()

defineEmits<{
  toggle: []
}>()
</script>

<template>
  <button
    class="flex size-5 shrink-0 items-center justify-center rounded-full border-2 transition-colors"
    :class="done
      ? 'border-grun bg-grun text-white'
      : 'border-grau-3 hover:border-turkis'"
    @click.stop="$emit('toggle')"
  >
    <svg v-if="done" class="size-3" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="2">
      <polyline points="2,6 5,9 10,3" />
    </svg>
  </button>
</template>
```

- [ ] **Step 2: Create TaskRow**

```vue
<!-- web/src/components/task/TaskRow.vue -->
<script setup lang="ts">
import TaskCheckbox from './TaskCheckbox.vue'

defineProps<{
  title: string
  meta?: string
  dueDate?: string | null
  done?: boolean
  selected?: boolean
}>()

defineEmits<{
  toggle: []
  click: []
}>()
</script>

<template>
  <div
    class="flex cursor-pointer items-center gap-3 rounded-md px-1 py-2.5 transition-colors hover:bg-grau-1"
    :class="{ 'bg-turkis-surface': selected }"
    @click="$emit('click')"
  >
    <TaskCheckbox :done="done" @toggle="$emit('toggle')" />
    <div class="min-w-0 flex-1">
      <div
        class="text-sm"
        :class="done ? 'line-through text-grau-3' : 'text-schwarz'"
      >
        {{ title }}
      </div>
      <div v-if="meta" class="mt-0.5 text-xs text-grau-3">{{ meta }}</div>
    </div>
    <div v-if="dueDate" class="shrink-0 text-xs font-medium text-orange">{{ dueDate }}</div>
  </div>
</template>
```

- [ ] **Step 3: Create InlineAddTask**

```vue
<!-- web/src/components/task/InlineAddTask.vue -->
<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'

const props = defineProps<{
  placeholder?: string
}>()

const emit = defineEmits<{
  submit: [data: { title: string; notes: string }]
}>()

const expanded = ref(false)
const title = ref('')
const notes = ref('')
const titleInput = ref<HTMLInputElement>()

function expand() {
  expanded.value = true
  setTimeout(() => titleInput.value?.focus(), 0)
}

function submit() {
  if (!title.value.trim()) return
  emit('submit', { title: title.value.trim(), notes: notes.value.trim() })
  title.value = ''
  notes.value = ''
  expanded.value = false
}

function cancel() {
  title.value = ''
  notes.value = ''
  expanded.value = false
}
</script>

<template>
  <!-- Collapsed -->
  <button
    v-if="!expanded"
    class="flex w-full items-center gap-2.5 rounded-md px-1 py-2.5 text-sm text-turkis hover:bg-turkis-surface transition-colors"
    @click="expand"
  >
    <div class="flex size-5 items-center justify-center rounded-full border-2 border-turkis">
      <Plus class="size-3" />
    </div>
    {{ placeholder ?? 'Add task' }}
  </button>

  <!-- Expanded -->
  <div v-else class="rounded-lg border border-grau-2 p-3.5" @keydown.escape="cancel">
    <input
      ref="titleInput"
      v-model="title"
      class="w-full text-[15px] font-medium text-schwarz outline-none placeholder:text-grau-3"
      placeholder="Task title"
      @keydown.enter.prevent="submit"
    />
    <input
      v-model="notes"
      class="mt-1 w-full text-[13px] text-grau-5 outline-none placeholder:text-grau-3"
      placeholder="Add a note..."
    />
    <div class="mt-3 flex items-center justify-end gap-2">
      <Button variant="ghost" size="sm" @click="cancel">Cancel</Button>
      <Button
        size="sm"
        class="bg-turkis text-white hover:bg-turkis/90"
        :disabled="!title.trim()"
        @click="submit"
      >
        Add Task
      </Button>
    </div>
  </div>
</template>
```

- [ ] **Step 4: Create TriageBar**

```vue
<!-- web/src/components/task/TriageBar.vue -->
<script setup lang="ts">
import type { ProcessAction } from '@/types'

defineEmits<{
  process: [action: ProcessAction]
}>()

const actions: { label: string; icon: string; action: ProcessAction }[] = [
  { label: 'Next Action', icon: '⚡', action: 'NEXT_ACTION' },
  { label: 'To Project', icon: '📁', action: 'PROJECT' },
  { label: 'Waiting For', icon: '⏳', action: 'WAITING_FOR' },
  { label: 'Someday', icon: '💭', action: 'SOMEDAY_MAYBE' },
  { label: 'Reference', icon: '📋', action: 'REFERENCE' },
  { label: 'Trash', icon: '🗑️', action: 'TRASH' },
]
</script>

<template>
  <div class="flex flex-wrap items-center justify-center gap-4 rounded-lg bg-turkis-surface px-4 py-2.5">
    <button
      v-for="a in actions"
      :key="a.action"
      class="flex items-center gap-1.5 text-[13px] font-medium text-turkis-dark hover:underline transition-colors"
      @click="$emit('process', a.action)"
    >
      <span>{{ a.icon }}</span> {{ a.label }}
    </button>
  </div>
</template>
```

- [ ] **Step 5: Commit**

```bash
git add web/src/components/task/
git commit -m "feat: shared task components (checkbox, row, inline add, triage bar)"
```

---

### Task 7: Inbox Page

Build the full Inbox page with list, inline add task, triage, and empty state.

**Files:**
- Create: `web/src/stores/inbox.ts`
- Rewrite: `web/src/views/InboxView.vue`

- [ ] **Step 1: Create inbox store**

```typescript
// web/src/stores/inbox.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { InboxItem, ProcessAction } from '@/types'
import { inboxApi } from '@/api/inbox'
import { useDashboardStore } from './dashboard'

export const useInboxStore = defineStore('inbox', () => {
  const items = ref<InboxItem[]>([])
  const loading = ref(false)
  const selectedId = ref<string | null>(null)

  async function fetch() {
    loading.value = true
    try {
      items.value = await inboxApi.list()
    } finally {
      loading.value = false
    }
  }

  async function add(title: string, description?: string) {
    const item = await inboxApi.create({ title, description })
    items.value.unshift(item)
    useDashboardStore().fetch()
  }

  async function remove(id: string) {
    await inboxApi.delete(id)
    items.value = items.value.filter(i => i.id !== id)
    useDashboardStore().fetch()
  }

  async function process(id: string, action: ProcessAction) {
    await inboxApi.process(id, { action })
    items.value = items.value.filter(i => i.id !== id)
    selectedId.value = null
    useDashboardStore().fetch()
  }

  return { items, loading, selectedId, fetch, add, remove, process }
})
```

- [ ] **Step 2: Build InboxView**

```vue
<!-- web/src/views/InboxView.vue -->
<script lang="ts" setup>
import { onMounted } from 'vue'
import { useInboxStore } from '@/stores/inbox'
import TaskRow from '@/components/task/TaskRow.vue'
import InlineAddTask from '@/components/task/InlineAddTask.vue'
import TriageBar from '@/components/task/TriageBar.vue'
import { Skeleton } from '@/components/ui/skeleton'
import type { ProcessAction } from '@/types'

const inbox = useInboxStore()
onMounted(() => inbox.fetch())

function formatAge(dateStr: string) {
  const date = new Date(dateStr)
  const now = new Date()
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24))
  if (diffDays === 0) return 'Added today'
  if (diffDays === 1) return 'Added yesterday'
  return `Added ${diffDays} days ago`
}

async function onAdd(data: { title: string; notes: string }) {
  await inbox.add(data.title, data.notes || undefined)
}

async function onProcess(action: ProcessAction) {
  if (!inbox.selectedId) return
  await inbox.process(inbox.selectedId, action)
}
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Inbox</h1>
    <p class="mt-0.5 text-[13px] text-grau-5">{{ inbox.items.length }} items to process</p>

    <!-- Loading -->
    <div v-if="inbox.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 5" :key="i" class="h-12 w-full rounded-md" />
    </div>

    <!-- Empty state -->
    <div v-else-if="inbox.items.length === 0" class="flex flex-col items-center py-16">
      <div class="flex size-16 items-center justify-center rounded-full bg-turkis-surface text-2xl">✓</div>
      <h2 class="mt-4 font-serif text-xl font-bold">Alles guad!</h2>
      <p class="mt-1 text-sm text-grau-5">Nix zum schaffe. Press <kbd class="rounded bg-grau-1 px-1.5 py-0.5 font-mono text-xs">Q</kbd> to capture something new.</p>
    </div>

    <!-- Task list -->
    <div v-else class="mt-4">
      <div class="divide-y divide-grau-1">
        <TaskRow
          v-for="item in inbox.items"
          :key="item.id"
          :title="item.title"
          :meta="formatAge(item.createdDate)"
          :selected="inbox.selectedId === item.id"
          @click="inbox.selectedId = inbox.selectedId === item.id ? null : item.id"
        />
      </div>

      <div class="mt-2">
        <InlineAddTask @submit="onAdd" />
      </div>

      <!-- Triage bar (visible when item selected) -->
      <div v-if="inbox.selectedId" class="mt-4">
        <TriageBar @process="onProcess" />
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 3: Verify build compiles**

Run: `cd /home/moamen/git-repos/guad/web && npm run build-only 2>&1 | tail -5`

- [ ] **Step 4: Commit**

```bash
git add web/src/stores/inbox.ts web/src/views/InboxView.vue
git commit -m "feat: inbox page with task list, inline add, triage processing"
```

---

### Task 8: Next Actions Page

Build the Next Actions page with context filter chips and grouped task list.

**Files:**
- Create: `web/src/stores/actions.ts`
- Create: `web/src/stores/contexts.ts`
- Rewrite: `web/src/views/NextActionsView.vue`

- [ ] **Step 1: Create contexts store**

```typescript
// web/src/stores/contexts.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ContextResponse, CreateContextRequest } from '@/types'
import { contextsApi } from '@/api/contexts'

export const useContextsStore = defineStore('contexts', () => {
  const items = ref<ContextResponse[]>([])
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try {
      items.value = await contextsApi.list()
    } finally {
      loading.value = false
    }
  }

  async function add(data: CreateContextRequest) {
    const item = await contextsApi.create(data)
    items.value.push(item)
  }

  async function update(id: string, data: CreateContextRequest) {
    const updated = await contextsApi.update(id, data)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = updated
  }

  async function remove(id: string) {
    await contextsApi.delete(id)
    items.value = items.value.filter(i => i.id !== id)
  }

  return { items, loading, fetch, add, update, remove }
})
```

- [ ] **Step 2: Create actions store**

```typescript
// web/src/stores/actions.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ActionResponse, CreateActionRequest, UpdateActionRequest } from '@/types'
import { actionsApi } from '@/api/actions'
import { useDashboardStore } from './dashboard'

export const useActionsStore = defineStore('actions', () => {
  const items = ref<ActionResponse[]>([])
  const loading = ref(false)

  async function fetch(params?: { status?: string; contextId?: string }) {
    loading.value = true
    try {
      items.value = await actionsApi.list(params)
    } finally {
      loading.value = false
    }
  }

  async function create(data: CreateActionRequest) {
    const action = await actionsApi.create(data)
    items.value.push(action)
    useDashboardStore().fetch()
    return action
  }

  async function update(id: string, data: UpdateActionRequest) {
    const updated = await actionsApi.update(id, data)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = updated
    return updated
  }

  async function complete(id: string) {
    // Optimistic update
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = { ...items.value[idx], status: 'COMPLETED' }
    try {
      await actionsApi.complete(id)
      items.value = items.value.filter(i => i.id !== id)
      useDashboardStore().fetch()
    } catch {
      // Roll back
      if (idx >= 0) items.value[idx] = { ...items.value[idx], status: 'NEXT' }
    }
  }

  async function remove(id: string) {
    await actionsApi.delete(id)
    items.value = items.value.filter(i => i.id !== id)
    useDashboardStore().fetch()
  }

  async function updateStatus(id: string, status: string) {
    const updated = await actionsApi.updateStatus(id, status)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = updated
  }

  return { items, loading, fetch, create, update, complete, remove, updateStatus }
})
```

- [ ] **Step 3: Build NextActionsView**

```vue
<!-- web/src/views/NextActionsView.vue -->
<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useActionsStore } from '@/stores/actions'
import { useContextsStore } from '@/stores/contexts'
import TaskRow from '@/components/task/TaskRow.vue'
import InlineAddTask from '@/components/task/InlineAddTask.vue'
import { Skeleton } from '@/components/ui/skeleton'

const actions = useActionsStore()
const contexts = useContextsStore()
const activeContextId = ref<string | null>(null)

onMounted(() => {
  actions.fetch({ status: 'NEXT' })
  contexts.fetch()
})

const filteredActions = computed(() => {
  if (!activeContextId.value) return actions.items
  return actions.items.filter(a => a.contexts.some(c => c.id === activeContextId.value))
})

const groupedByContext = computed(() => {
  const groups = new Map<string, typeof actions.items>()
  for (const action of filteredActions.value) {
    const ctxName = action.contexts.length > 0 ? action.contexts[0].name : 'No Context'
    if (!groups.has(ctxName)) groups.set(ctxName, [])
    groups.get(ctxName)!.push(action)
  }
  return groups
})

function formatDueDate(date: string | null) {
  if (!date) return null
  const d = new Date(date)
  return d.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' })
}

async function onAdd(data: { title: string; notes: string }) {
  await actions.create({
    description: data.title,
    notes: data.notes || undefined,
    contextIds: activeContextId.value ? [activeContextId.value] : undefined,
  })
}
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Next Actions</h1>
    <p class="mt-0.5 text-[13px] text-grau-5">{{ filteredActions.length }} actions across {{ groupedByContext.size }} contexts</p>

    <!-- Filter chips -->
    <div class="mt-3 flex flex-wrap gap-1.5">
      <button
        class="rounded-full px-3.5 py-1 text-[13px] transition-colors"
        :class="activeContextId === null ? 'bg-turkis-dark text-white' : 'bg-grau-1 text-schwarz hover:bg-grau-2'"
        @click="activeContextId = null"
      >All</button>
      <button
        v-for="ctx in contexts.items"
        :key="ctx.id"
        class="rounded-full px-3.5 py-1 text-[13px] transition-colors"
        :class="activeContextId === ctx.id ? 'bg-turkis-dark text-white' : 'bg-grau-1 text-schwarz hover:bg-grau-2'"
        @click="activeContextId = activeContextId === ctx.id ? null : ctx.id"
      >{{ ctx.name }}</button>
    </div>

    <!-- Loading -->
    <div v-if="actions.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 5" :key="i" class="h-12 w-full rounded-md" />
    </div>

    <!-- Grouped tasks -->
    <div v-else class="mt-4">
      <div v-for="[contextName, contextActions] in groupedByContext" :key="contextName" class="mb-4">
        <h3 class="text-xs font-semibold uppercase tracking-wide text-turkis">{{ contextName }}</h3>
        <div class="mt-1 divide-y divide-grau-1">
          <TaskRow
            v-for="action in contextActions"
            :key="action.id"
            :title="action.description"
            :meta="action.projectName ?? undefined"
            :due-date="formatDueDate(action.dueDate)"
            @toggle="actions.complete(action.id)"
          />
        </div>
      </div>

      <InlineAddTask @submit="onAdd" />
    </div>
  </div>
</template>
```

- [ ] **Step 4: Commit**

```bash
git add web/src/stores/actions.ts web/src/stores/contexts.ts web/src/views/NextActionsView.vue
git commit -m "feat: next actions page with context grouping and filter chips"
```

---

### Task 9: Task Detail Side Panel + Quick Capture Modal

Build the side panel for editing tasks and the `Q` shortcut quick capture modal.

**Files:**
- Create: `web/src/composables/useTaskDetail.ts`
- Create: `web/src/composables/useQuickCapture.ts`
- Create: `web/src/components/app/TaskDetailPanel.vue`
- Create: `web/src/components/app/QuickCaptureModal.vue`
- Modify: `web/src/components/SidebarLayout.vue` (add panel + modal)

- [ ] **Step 1: Create useTaskDetail composable**

```typescript
// web/src/composables/useTaskDetail.ts
import { ref } from 'vue'
import type { ActionResponse } from '@/types'

const isOpen = ref(false)
const selectedAction = ref<ActionResponse | null>(null)

export function useTaskDetail() {
  function open(action: ActionResponse) {
    selectedAction.value = action
    isOpen.value = true
  }

  function close() {
    isOpen.value = false
    selectedAction.value = null
  }

  return { isOpen, selectedAction, open, close }
}
```

- [ ] **Step 2: Create useQuickCapture composable**

```typescript
// web/src/composables/useQuickCapture.ts
import { ref } from 'vue'
import { useEventListener } from '@vueuse/core'

const isOpen = ref(false)

export function useQuickCapture() {
  function open() { isOpen.value = true }
  function close() { isOpen.value = false }

  useEventListener('keydown', (e: KeyboardEvent) => {
    if (e.key === 'q' && !isOpen.value) {
      const target = e.target as HTMLElement
      if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable) return
      e.preventDefault()
      open()
    }
  })

  return { isOpen, open, close }
}
```

- [ ] **Step 3: Create TaskDetailPanel**

```vue
<!-- web/src/components/app/TaskDetailPanel.vue -->
<script setup lang="ts">
import { ref, watch } from 'vue'
import { useTaskDetail } from '@/composables/useTaskDetail'
import { useActionsStore } from '@/stores/actions'
import TaskCheckbox from '@/components/task/TaskCheckbox.vue'
import { Button } from '@/components/ui/button'
import { X } from 'lucide-vue-next'
import type { UpdateActionRequest } from '@/types'

const { isOpen, selectedAction, close } = useTaskDetail()
const actions = useActionsStore()

const form = ref<UpdateActionRequest>({})

watch(selectedAction, (action) => {
  if (action) {
    form.value = {
      description: action.description,
      notes: action.notes ?? undefined,
      projectId: action.projectId ?? undefined,
      areaId: action.areaId ?? undefined,
      contextIds: action.contexts.map(c => c.id),
      energyLevel: action.energyLevel ?? undefined,
      estimatedDuration: action.estimatedDuration ?? undefined,
      dueDate: action.dueDate ?? undefined,
    }
  }
})

async function save() {
  if (!selectedAction.value) return
  await actions.update(selectedAction.value.id, form.value)
  close()
}

async function onComplete() {
  if (!selectedAction.value) return
  await actions.complete(selectedAction.value.id)
  close()
}

async function onDelete() {
  if (!selectedAction.value) return
  await actions.remove(selectedAction.value.id)
  close()
}

function formatDueDate(date: string | null | undefined) {
  if (!date) return '—'
  return new Date(date).toLocaleDateString('en-US', { weekday: 'long', month: 'short', day: 'numeric' })
}
</script>

<template>
  <Transition name="slide">
    <div v-if="isOpen && selectedAction" class="w-[340px] shrink-0 border-l border-grau-2 bg-white">
      <div class="flex items-center justify-between border-b border-grau-1 px-5 py-4">
        <button class="text-grau-3 hover:text-schwarz" @click="close"><X class="size-5" /></button>
        <Button size="sm" variant="ghost" class="text-turkis font-semibold" @click="save">Save</Button>
      </div>

      <div class="overflow-y-auto p-5">
        <!-- Title -->
        <div class="flex items-start gap-3 mb-5">
          <TaskCheckbox :done="selectedAction.status === 'COMPLETED'" class="mt-0.5" @toggle="onComplete" />
          <input
            v-model="form.description"
            class="text-lg font-semibold text-schwarz outline-none w-full"
          />
        </div>

        <!-- Metadata fields -->
        <div class="divide-y divide-grau-1">
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-5">Project</span>
            <span class="text-[13px] font-medium">{{ selectedAction.projectName ?? '—' }}</span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-5">Context</span>
            <span v-if="selectedAction.contexts.length" class="rounded-full bg-turkis-surface px-2.5 py-0.5 text-xs font-medium text-turkis-dark">
              {{ selectedAction.contexts[0].name }}
            </span>
            <span v-else class="text-[13px]">—</span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-5">Due date</span>
            <span class="text-[13px]" :class="selectedAction.dueDate ? 'font-medium text-orange' : ''">
              {{ formatDueDate(selectedAction.dueDate) }}
            </span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-5">Area</span>
            <span class="text-[13px]">{{ selectedAction.areaName ?? '—' }}</span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-5">Energy</span>
            <span class="text-[13px]">{{ selectedAction.energyLevel ?? '—' }}</span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-5">Time needed</span>
            <span class="text-[13px]">{{ selectedAction.estimatedDuration ?? '—' }}</span>
          </div>
        </div>

        <!-- Notes -->
        <div class="mt-4">
          <label class="text-[13px] text-grau-5">Notes</label>
          <textarea
            v-model="form.notes"
            class="mt-1.5 w-full rounded-lg bg-grau-1 p-3 text-[13px] leading-relaxed text-grau-5 outline-none min-h-[60px] resize-y"
            placeholder="Add notes..."
          />
        </div>

        <!-- Delete -->
        <button class="mt-4 w-full py-3 text-center text-[13px] text-rot hover:underline" @click="onDelete">
          Delete Task
        </button>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.slide-enter-active, .slide-leave-active {
  transition: transform 0.2s ease, opacity 0.2s ease;
}
.slide-enter-from, .slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>
```

- [ ] **Step 4: Create QuickCaptureModal**

```vue
<!-- web/src/components/app/QuickCaptureModal.vue -->
<script setup lang="ts">
import { ref } from 'vue'
import { useQuickCapture } from '@/composables/useQuickCapture'
import { useInboxStore } from '@/stores/inbox'
import { Button } from '@/components/ui/button'

const { isOpen, close } = useQuickCapture()
const inbox = useInboxStore()

const title = ref('')
const notes = ref('')
const titleInput = ref<HTMLInputElement>()

async function submit() {
  if (!title.value.trim()) return
  await inbox.add(title.value.trim(), notes.value.trim() || undefined)
  title.value = ''
  notes.value = ''
  close()
}
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="isOpen" class="fixed inset-0 z-50 flex items-start justify-center pt-[20vh]" @click.self="close">
        <div class="absolute inset-0 bg-schwarz/15" />
        <div class="relative w-[480px] rounded-xl bg-white p-6 shadow-xl" @keydown.escape="close" @keydown.meta.enter="submit" @keydown.ctrl.enter="submit">
          <h2 class="font-serif text-lg font-bold mb-4">Quick Capture</h2>
          <div class="rounded-lg border border-turkis p-3.5">
            <input
              ref="titleInput"
              v-model="title"
              class="w-full text-[15px] font-medium text-schwarz outline-none placeholder:text-grau-3"
              placeholder="What's on your mind?"
              autofocus
              @keydown.enter.prevent="submit"
            />
            <input
              v-model="notes"
              class="mt-1 w-full text-[13px] text-grau-5 outline-none placeholder:text-grau-3"
              placeholder="Add a note..."
            />
            <div class="mt-3 flex justify-end">
              <Button size="sm" class="bg-turkis text-white hover:bg-turkis/90" :disabled="!title.trim()" @click="submit">
                Save to Inbox
              </Button>
            </div>
          </div>
          <p class="mt-3 text-center text-xs text-grau-3">
            Press <kbd class="rounded bg-grau-1 px-1 py-0.5 font-mono">Esc</kbd> to close ·
            <kbd class="rounded bg-grau-1 px-1 py-0.5 font-mono">⌘ Enter</kbd> to save
          </p>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
```

- [ ] **Step 5: Update SidebarLayout to include panel and modal**

Modify `web/src/components/SidebarLayout.vue`:

```vue
<script setup lang="ts">
import AppSidebar from '@/components/app/AppSidebar.vue'
import TaskDetailPanel from '@/components/app/TaskDetailPanel.vue'
import QuickCaptureModal from '@/components/app/QuickCaptureModal.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarInset, SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar'
import { useQuickCapture } from '@/composables/useQuickCapture'

// Initialize Q shortcut listener
useQuickCapture()
</script>

<template>
  <SidebarProvider>
    <AppSidebar />
    <SidebarInset class="flex flex-row">
      <div class="flex flex-1 flex-col overflow-hidden">
        <header class="flex h-12 shrink-0 items-center gap-2 px-4">
          <SidebarTrigger class="-ml-1" />
          <Separator orientation="vertical" class="mr-2 data-[orientation=vertical]:h-4" />
        </header>
        <div class="flex-1 overflow-y-auto p-6 pt-0">
          <RouterView />
        </div>
      </div>
      <TaskDetailPanel />
    </SidebarInset>
    <QuickCaptureModal />
  </SidebarProvider>
</template>
```

- [ ] **Step 6: Commit**

```bash
git add web/src/composables/ web/src/components/app/TaskDetailPanel.vue web/src/components/app/QuickCaptureModal.vue web/src/components/SidebarLayout.vue
git commit -m "feat: task detail side panel and quick capture modal (Q shortcut)"
```

---

### Task 10: Projects Page + Project Detail

Build the projects list and project detail sub-page.

**Files:**
- Create: `web/src/stores/projects.ts`
- Rewrite: `web/src/views/ProjectsView.vue`
- Rewrite: `web/src/views/ProjectDetailView.vue`

- [ ] **Step 1: Create projects store**

```typescript
// web/src/stores/projects.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ProjectResponse, ProjectDetailResponse, CreateProjectRequest } from '@/types'
import { projectsApi } from '@/api/projects'
import { useDashboardStore } from './dashboard'

export const useProjectsStore = defineStore('projects', () => {
  const items = ref<ProjectResponse[]>([])
  const detail = ref<ProjectDetailResponse | null>(null)
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try {
      items.value = await projectsApi.list()
    } finally {
      loading.value = false
    }
  }

  async function fetchDetail(id: string) {
    loading.value = true
    try {
      detail.value = await projectsApi.get(id)
    } finally {
      loading.value = false
    }
  }

  async function addAction(projectId: string, description: string) {
    await projectsApi.addAction(projectId, description)
    await fetchDetail(projectId)
    useDashboardStore().fetch()
  }

  return { items, detail, loading, fetch, fetchDetail, addAction }
})
```

- [ ] **Step 2: Build ProjectsView**

```vue
<!-- web/src/views/ProjectsView.vue -->
<script lang="ts" setup>
import { computed, onMounted } from 'vue'
import { useProjectsStore } from '@/stores/projects'
import { Skeleton } from '@/components/ui/skeleton'
import { ChevronRight } from 'lucide-vue-next'

const projects = useProjectsStore()
onMounted(() => projects.fetch())

const groupedByArea = computed(() => {
  const groups = new Map<string, typeof projects.items>()
  for (const p of projects.items) {
    const area = p.areaName ?? 'No Area'
    if (!groups.has(area)) groups.set(area, [])
    groups.get(area)!.push(p)
  }
  return groups
})
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Projects</h1>
    <p class="mt-0.5 text-[13px] text-grau-5">{{ projects.items.length }} active projects</p>

    <div v-if="projects.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 5" :key="i" class="h-12 w-full rounded-md" />
    </div>

    <div v-else class="mt-4">
      <div v-for="[areaName, areaProjects] in groupedByArea" :key="areaName" class="mb-4">
        <h3 class="text-xs font-semibold uppercase tracking-wide text-turkis">{{ areaName }}</h3>
        <div class="mt-1 divide-y divide-grau-1">
          <RouterLink
            v-for="p in areaProjects"
            :key="p.id"
            :to="`/projects/${p.id}`"
            class="flex items-center justify-between rounded-md px-1 py-3 hover:bg-grau-1 transition-colors"
          >
            <div>
              <div class="text-sm font-medium text-schwarz">{{ p.name }}</div>
              <div class="mt-0.5 text-xs text-grau-3">{{ p.nextActionCount }} next action{{ p.nextActionCount !== 1 ? 's' : '' }}</div>
            </div>
            <ChevronRight class="size-4 text-grau-3" />
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 3: Build ProjectDetailView**

```vue
<!-- web/src/views/ProjectDetailView.vue -->
<script lang="ts" setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useProjectsStore } from '@/stores/projects'
import { useTaskDetail } from '@/composables/useTaskDetail'
import TaskRow from '@/components/task/TaskRow.vue'
import InlineAddTask from '@/components/task/InlineAddTask.vue'
import { Skeleton } from '@/components/ui/skeleton'
import { ArrowLeft } from 'lucide-vue-next'

const route = useRoute()
const projects = useProjectsStore()
const { open } = useTaskDetail()

const projectId = route.params.id as string
onMounted(() => projects.fetchDetail(projectId))

async function onAddAction(data: { title: string }) {
  await projects.addAction(projectId, data.title)
}
</script>

<template>
  <div v-if="projects.loading">
    <Skeleton class="h-8 w-48 rounded-md" />
    <Skeleton class="mt-2 h-4 w-32 rounded-md" />
    <Skeleton class="mt-6 h-12 w-full rounded-md" />
  </div>

  <div v-else-if="projects.detail">
    <RouterLink to="/projects" class="mb-3 inline-flex items-center gap-1 text-sm text-turkis hover:underline">
      <ArrowLeft class="size-4" /> Projects
    </RouterLink>

    <h1 class="font-serif text-2xl font-bold text-schwarz">{{ projects.detail.name }}</h1>
    <p class="mt-0.5 text-[13px] text-grau-5">{{ projects.detail.areaName }}</p>
    <p v-if="projects.detail.desiredOutcome" class="mt-1.5 text-sm italic text-grau-5 leading-relaxed">
      "{{ projects.detail.desiredOutcome }}"
    </p>

    <!-- Next Actions -->
    <h3 class="mt-6 text-xs font-semibold uppercase tracking-wide text-turkis">Next Actions</h3>
    <div class="mt-1 divide-y divide-grau-1">
      <TaskRow
        v-for="action in projects.detail.nextActions"
        :key="action.id"
        :title="action.description"
        :meta="action.contexts.map(c => c.name).join(', ') || undefined"
        :due-date="action.dueDate ? new Date(action.dueDate).toLocaleDateString('en-US', { weekday: 'short' }) : null"
        @click="open(action)"
      />
    </div>

    <!-- Waiting For -->
    <h3 v-if="projects.detail.waitingForItems.length" class="mt-6 text-xs font-semibold uppercase tracking-wide text-blau">Waiting For</h3>
    <div v-if="projects.detail.waitingForItems.length" class="mt-1 divide-y divide-grau-1">
      <div v-for="wf in projects.detail.waitingForItems" :key="wf.id" class="py-3 px-1">
        <div class="text-sm text-schwarz">{{ wf.title }}</div>
        <div class="mt-1 flex gap-3 text-xs">
          <span class="text-blau">👤 {{ wf.delegatedTo }}</span>
          <span class="text-grau-3">Delegated {{ new Date(wf.createdDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) }}</span>
        </div>
      </div>
    </div>

    <!-- Completed -->
    <h3 v-if="projects.detail.completedActions.length" class="mt-6 text-xs font-semibold uppercase tracking-wide text-grun">Completed</h3>
    <div v-if="projects.detail.completedActions.length" class="mt-1 divide-y divide-grau-1 opacity-60">
      <TaskRow
        v-for="action in projects.detail.completedActions"
        :key="action.id"
        :title="action.description"
        :meta="`Completed ${new Date(action.completedDate!).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}`"
        done
      />
    </div>

    <!-- Add action -->
    <div class="mt-4">
      <InlineAddTask placeholder="+ Add next action" @submit="onAddAction" />
    </div>
  </div>
</template>
```

- [ ] **Step 4: Commit**

```bash
git add web/src/stores/projects.ts web/src/views/ProjectsView.vue web/src/views/ProjectDetailView.vue
git commit -m "feat: projects list and project detail with actions, waiting for, completed"
```

---

### Task 11: Waiting For Page

**Files:**
- Create: `web/src/stores/waitingFor.ts`
- Rewrite: `web/src/views/WaitingForView.vue`

- [ ] **Step 1: Create waitingFor store**

```typescript
// web/src/stores/waitingFor.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { WaitingForResponse, CreateWaitingForRequest } from '@/types'
import { waitingForApi } from '@/api/waitingFor'
import { useDashboardStore } from './dashboard'

export const useWaitingForStore = defineStore('waitingFor', () => {
  const items = ref<WaitingForResponse[]>([])
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try { items.value = await waitingForApi.list() }
    finally { loading.value = false }
  }

  async function add(data: CreateWaitingForRequest) {
    const item = await waitingForApi.create(data)
    items.value.push(item)
    useDashboardStore().fetch()
  }

  async function resolve(id: string) {
    await waitingForApi.resolve(id)
    items.value = items.value.filter(i => i.id !== id)
    useDashboardStore().fetch()
  }

  return { items, loading, fetch, add, resolve }
})
```

- [ ] **Step 2: Build WaitingForView**

```vue
<!-- web/src/views/WaitingForView.vue -->
<script lang="ts" setup>
import { onMounted } from 'vue'
import { useWaitingForStore } from '@/stores/waitingFor'
import { Skeleton } from '@/components/ui/skeleton'

const waitingFor = useWaitingForStore()
onMounted(() => waitingFor.fetch())

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

function daysSince(dateStr: string) {
  return Math.floor((Date.now() - new Date(dateStr).getTime()) / (1000 * 60 * 60 * 24))
}
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Waiting For</h1>
    <p class="mt-0.5 text-[13px] text-grau-5">{{ waitingFor.items.length }} items delegated</p>

    <div v-if="waitingFor.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 4" :key="i" class="h-16 w-full rounded-md" />
    </div>

    <div v-else class="mt-4 divide-y divide-grau-1">
      <div v-for="item in waitingFor.items" :key="item.id" class="py-3 px-1">
        <div class="text-sm text-schwarz">{{ item.title }}</div>
        <div v-if="item.projectName" class="mt-0.5 text-xs text-grau-5">{{ item.projectName }}</div>
        <div class="mt-1.5 flex gap-3 text-xs">
          <span v-if="item.delegatedTo" class="text-blau">👤 {{ item.delegatedTo }}</span>
          <span :class="daysSince(item.createdDate) >= 7 ? 'text-orange' : 'text-grau-3'">
            Delegated {{ formatDate(item.createdDate) }}
            <template v-if="daysSince(item.createdDate) >= 7"> · {{ daysSince(item.createdDate) }} days</template>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 3: Commit**

```bash
git add web/src/stores/waitingFor.ts web/src/views/WaitingForView.vue
git commit -m "feat: waiting for page with person tags and age indicators"
```

---

### Task 12: Someday/Maybe Page

**Files:**
- Create: `web/src/stores/somedayMaybe.ts`
- Rewrite: `web/src/views/SomedayMaybeView.vue`

- [ ] **Step 1: Create store and build view**

```typescript
// web/src/stores/somedayMaybe.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { SomedayMaybeResponse } from '@/types'
import { somedayMaybeApi } from '@/api/somedayMaybe'

export const useSomedayMaybeStore = defineStore('somedayMaybe', () => {
  const data = ref<SomedayMaybeResponse | null>(null)
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try { data.value = await somedayMaybeApi.list() }
    finally { loading.value = false }
  }

  return { data, loading, fetch }
})
```

```vue
<!-- web/src/views/SomedayMaybeView.vue -->
<script lang="ts" setup>
import { onMounted } from 'vue'
import { useSomedayMaybeStore } from '@/stores/somedayMaybe'
import TaskRow from '@/components/task/TaskRow.vue'
import { Skeleton } from '@/components/ui/skeleton'
import { ChevronRight } from 'lucide-vue-next'

const store = useSomedayMaybeStore()
onMounted(() => store.fetch())
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Someday / Maybe</h1>

    <div v-if="store.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 5" :key="i" class="h-12 w-full rounded-md" />
    </div>

    <div v-else-if="store.data" class="mt-4">
      <h3 v-if="store.data.actions.length" class="text-xs font-semibold uppercase tracking-wide text-lila">Actions</h3>
      <div class="mt-1 divide-y divide-grau-1">
        <TaskRow
          v-for="action in store.data.actions"
          :key="action.id"
          :title="action.description"
          :meta="action.projectName ?? undefined"
        />
      </div>

      <h3 v-if="store.data.projects.length" class="mt-6 text-xs font-semibold uppercase tracking-wide text-lila">Projects</h3>
      <div class="mt-1 divide-y divide-grau-1">
        <RouterLink
          v-for="p in store.data.projects"
          :key="p.id"
          :to="`/projects/${p.id}`"
          class="flex items-center justify-between rounded-md px-1 py-3 hover:bg-grau-1 transition-colors"
        >
          <div class="text-sm font-medium text-schwarz">{{ p.name }}</div>
          <ChevronRight class="size-4 text-grau-3" />
        </RouterLink>
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 2: Commit**

```bash
git add web/src/stores/somedayMaybe.ts web/src/views/SomedayMaybeView.vue
git commit -m "feat: someday/maybe page with actions and projects sections"
```

---

### Task 13: Weekly Review Wizard

**Files:**
- Create: `web/src/stores/review.ts`
- Rewrite: `web/src/views/WeeklyReviewView.vue`

- [ ] **Step 1: Create review store**

```typescript
// web/src/stores/review.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { WeeklyReviewResponse, ReviewStep } from '@/types'
import { reviewApi } from '@/api/review'
import { useDashboardStore } from './dashboard'

const STEP_ORDER: ReviewStep[] = [
  'CLEAR_INBOX', 'REVIEW_NEXT_ACTIONS', 'REVIEW_PROJECTS', 'REVIEW_WAITING_FOR', 'REVIEW_SOMEDAY_MAYBE', 'DONE'
]

const STEP_LABELS: Record<ReviewStep, string> = {
  CLEAR_INBOX: 'Clear Inbox',
  REVIEW_NEXT_ACTIONS: 'Review Next Actions',
  REVIEW_PROJECTS: 'Review Projects',
  REVIEW_WAITING_FOR: 'Review Waiting For',
  REVIEW_SOMEDAY_MAYBE: 'Review Someday/Maybe',
  DONE: 'Done',
}

const STEP_QUESTIONS: Record<ReviewStep, string> = {
  CLEAR_INBOX: 'Process every item in your inbox. Is it actionable? What\'s the next action?',
  REVIEW_NEXT_ACTIONS: 'Is each action still relevant? Mark off anything completed.',
  REVIEW_PROJECTS: 'Is each project still relevant? Does every active project have a next action?',
  REVIEW_WAITING_FOR: 'Is anyone overdue? Do you need to follow up?',
  REVIEW_SOMEDAY_MAYBE: 'Anything ready to activate? Anything to remove?',
  DONE: 'Sauber gmacht!',
}

export const useReviewStore = defineStore('review', () => {
  const review = ref<WeeklyReviewResponse | null>(null)
  const loading = ref(false)

  const currentStepIndex = computed(() => {
    if (!review.value) return 0
    return STEP_ORDER.indexOf(review.value.currentStep)
  })

  const isDone = computed(() => review.value?.currentStep === 'DONE')

  async function start() {
    loading.value = true
    try { review.value = await reviewApi.start() }
    finally { loading.value = false }
  }

  async function loadCurrent() {
    loading.value = true
    try { review.value = await reviewApi.getCurrent() }
    catch { review.value = null }
    finally { loading.value = false }
  }

  async function advance() {
    if (!review.value) return
    review.value = await reviewApi.advanceStep(review.value.id)
  }

  async function complete() {
    if (!review.value) return
    review.value = await reviewApi.complete(review.value.id)
    useDashboardStore().fetch()
  }

  return { review, loading, currentStepIndex, isDone, start, loadCurrent, advance, complete, STEP_ORDER, STEP_LABELS, STEP_QUESTIONS }
})
```

- [ ] **Step 2: Build WeeklyReviewView**

```vue
<!-- web/src/views/WeeklyReviewView.vue -->
<script lang="ts" setup>
import { onMounted } from 'vue'
import { useReviewStore } from '@/stores/review'
import { Button } from '@/components/ui/button'

const store = useReviewStore()
onMounted(() => store.loadCurrent())

async function startOrAdvance() {
  if (!store.review) {
    await store.start()
  } else if (store.currentStepIndex >= 4) {
    await store.complete()
  } else {
    await store.advance()
  }
}
</script>

<template>
  <div>
    <!-- No active review -->
    <div v-if="!store.review" class="flex flex-col items-center py-16">
      <h1 class="font-serif text-2xl font-bold text-schwarz">Weekly Review</h1>
      <p class="mt-2 text-sm text-grau-5">Time to review your system. This takes about 30 minutes.</p>
      <Button class="mt-6 bg-turkis-dark text-white hover:bg-turkis-dark/90" @click="store.start()">
        Start Weekly Review
      </Button>
    </div>

    <!-- Active review -->
    <div v-else>
      <div class="flex items-center justify-between">
        <RouterLink to="/inbox" class="text-sm text-turkis hover:underline">Exit Review</RouterLink>
        <span v-if="!store.isDone" class="text-[13px] text-grau-5">Step {{ store.currentStepIndex + 1 }} of 5</span>
      </div>

      <!-- Progress bar -->
      <div v-if="!store.isDone" class="mt-3 h-1 rounded-full bg-grau-1 overflow-hidden">
        <div class="h-full rounded-full bg-turkis transition-all" :style="{ width: `${((store.currentStepIndex + 1) / 5) * 100}%` }" />
      </div>

      <!-- Done state -->
      <div v-if="store.isDone" class="flex flex-col items-center py-16">
        <div class="flex size-16 items-center justify-center rounded-full bg-turkis-surface text-2xl">✓</div>
        <h2 class="mt-4 font-serif text-2xl font-bold">Sauber gmacht!</h2>
        <p class="mt-1 text-sm text-grau-5">Your system is up to date. See you next week.</p>
        <RouterLink to="/inbox" class="mt-6 text-sm text-turkis hover:underline">Back to Inbox</RouterLink>
      </div>

      <!-- Current step -->
      <div v-else class="mt-4">
        <h1 class="font-serif text-2xl font-bold text-schwarz">
          {{ store.STEP_LABELS[store.review.currentStep] }}
        </h1>
        <p class="mt-1.5 text-sm text-grau-5 leading-relaxed">
          {{ store.STEP_QUESTIONS[store.review.currentStep] }}
        </p>

        <!-- Step breadcrumbs -->
        <div class="mt-8 flex justify-center gap-4 text-[11px]">
          <span
            v-for="(step, i) in store.STEP_ORDER.slice(0, 5)"
            :key="step"
            :class="{
              'text-grun': i < store.currentStepIndex,
              'font-semibold text-schwarz': i === store.currentStepIndex,
              'text-grau-3': i > store.currentStepIndex,
            }"
          >
            <template v-if="i < store.currentStepIndex">✓ </template>
            {{ store.STEP_LABELS[step].replace('Review ', '') }}
          </span>
        </div>

        <!-- Next button -->
        <Button class="mt-6 w-full bg-turkis-dark text-white hover:bg-turkis-dark/90" @click="startOrAdvance">
          <template v-if="store.currentStepIndex < 4">
            Next: {{ store.STEP_LABELS[store.STEP_ORDER[store.currentStepIndex + 1]] }} →
          </template>
          <template v-else>Complete Review</template>
        </Button>
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 3: Commit**

```bash
git add web/src/stores/review.ts web/src/views/WeeklyReviewView.vue
git commit -m "feat: weekly review wizard with 5-step flow and completion"
```

---

### Task 14: Contexts + Areas Management Pages

**Files:**
- Create: `web/src/stores/areas.ts`
- Rewrite: `web/src/views/ContextsView.vue`
- Rewrite: `web/src/views/AreasView.vue`

- [ ] **Step 1: Create areas store**

```typescript
// web/src/stores/areas.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { AreaResponse, CreateAreaRequest } from '@/types'
import { areasApi } from '@/api/areas'

export const useAreasStore = defineStore('areas', () => {
  const items = ref<AreaResponse[]>([])
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try { items.value = await areasApi.list() }
    finally { loading.value = false }
  }

  async function add(data: CreateAreaRequest) {
    const item = await areasApi.create(data)
    items.value.push(item)
  }

  async function update(id: string, data: CreateAreaRequest) {
    const updated = await areasApi.update(id, data)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = updated
  }

  async function remove(id: string) {
    await areasApi.delete(id)
    items.value = items.value.filter(i => i.id !== id)
  }

  return { items, loading, fetch, add, update, remove }
})
```

- [ ] **Step 2: Build ContextsView**

```vue
<!-- web/src/views/ContextsView.vue -->
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useContextsStore } from '@/stores/contexts'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Pencil, Trash2, Plus } from 'lucide-vue-next'

const contexts = useContextsStore()
onMounted(() => contexts.fetch())

const showAdd = ref(false)
const newName = ref('')

async function addContext() {
  if (!newName.value.trim()) return
  await contexts.add({ name: newName.value.trim() })
  newName.value = ''
  showAdd.value = false
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between">
      <h1 class="font-serif text-2xl font-bold text-schwarz">Contexts</h1>
      <Button size="sm" variant="outline" @click="showAdd = !showAdd">
        <Plus class="mr-1 size-4" /> Add
      </Button>
    </div>

    <div v-if="showAdd" class="mt-3 flex gap-2">
      <Input v-model="newName" placeholder="Context name (e.g. @Office)" class="flex-1" @keydown.enter="addContext" />
      <Button size="sm" class="bg-turkis text-white" @click="addContext">Save</Button>
    </div>

    <div class="mt-4 divide-y divide-grau-1">
      <div v-for="ctx in contexts.items" :key="ctx.id" class="flex items-center justify-between py-3 px-1">
        <div>
          <div class="text-sm font-medium text-schwarz">{{ ctx.name }}</div>
          <div v-if="ctx.description" class="mt-0.5 text-xs text-grau-3">{{ ctx.description }}</div>
        </div>
        <button class="text-grau-3 hover:text-rot" @click="contexts.remove(ctx.id)">
          <Trash2 class="size-4" />
        </button>
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 3: Build AreasView** (same pattern as ContextsView)

```vue
<!-- web/src/views/AreasView.vue -->
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useAreasStore } from '@/stores/areas'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Trash2, Plus } from 'lucide-vue-next'

const areas = useAreasStore()
onMounted(() => areas.fetch())

const showAdd = ref(false)
const newName = ref('')

async function addArea() {
  if (!newName.value.trim()) return
  await areas.add({ name: newName.value.trim() })
  newName.value = ''
  showAdd.value = false
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between">
      <h1 class="font-serif text-2xl font-bold text-schwarz">Areas</h1>
      <Button size="sm" variant="outline" @click="showAdd = !showAdd">
        <Plus class="mr-1 size-4" /> Add
      </Button>
    </div>

    <div v-if="showAdd" class="mt-3 flex gap-2">
      <Input v-model="newName" placeholder="Area name (e.g. Work)" class="flex-1" @keydown.enter="addArea" />
      <Button size="sm" class="bg-turkis text-white" @click="addArea">Save</Button>
    </div>

    <div class="mt-4 divide-y divide-grau-1">
      <div v-for="area in areas.items" :key="area.id" class="flex items-center justify-between py-3 px-1">
        <div>
          <div class="text-sm font-medium text-schwarz">{{ area.name }}</div>
          <div v-if="area.description" class="mt-0.5 text-xs text-grau-3">{{ area.description }}</div>
        </div>
        <button class="text-grau-3 hover:text-rot" @click="areas.remove(area.id)">
          <Trash2 class="size-4" />
        </button>
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 4: Commit**

```bash
git add web/src/stores/areas.ts web/src/views/ContextsView.vue web/src/views/AreasView.vue
git commit -m "feat: contexts and areas management pages with CRUD"
```

---

### Task 15: Settings Page

**Files:**
- Rewrite: `web/src/views/SettingsView.vue`

- [ ] **Step 1: Build SettingsView**

```vue
<!-- web/src/views/SettingsView.vue -->
<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { LogOut } from 'lucide-vue-next'

const auth = useAuthStore()
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Settings</h1>

    <div class="mt-6 flex items-center gap-4">
      <Avatar class="size-12 rounded-lg">
        <AvatarFallback class="rounded-lg bg-turkis text-white text-lg">
          {{ auth.user?.username?.charAt(0)?.toUpperCase() ?? '?' }}
        </AvatarFallback>
      </Avatar>
      <div>
        <div class="font-medium text-schwarz">{{ auth.user?.username ?? 'User' }}</div>
        <div class="text-sm text-grau-5">{{ auth.user?.email ?? '' }}</div>
      </div>
    </div>

    <Separator class="my-6" />

    <Button variant="outline" class="text-rot border-rot/30 hover:bg-rot/5" @click="auth.logout()">
      <LogOut class="mr-2 size-4" /> Log out
    </Button>
  </div>
</template>
```

- [ ] **Step 2: Commit**

```bash
git add web/src/views/SettingsView.vue
git commit -m "feat: settings page with user profile and logout"
```

---

### Task 16: Final Build Verification

Verify the entire app compiles and all routes work.

- [ ] **Step 1: Run type check**

Run: `cd /home/moamen/git-repos/guad/web && npm run type-check 2>&1 | tail -10`
Expected: No errors.

- [ ] **Step 2: Run build**

Run: `cd /home/moamen/git-repos/guad/web && npm run build 2>&1 | tail -10`
Expected: Build succeeds.

- [ ] **Step 3: Run lint**

Run: `cd /home/moamen/git-repos/guad/web && npm run lint 2>&1 | tail -20`
Expected: No errors (warnings are acceptable).

- [ ] **Step 4: Fix any issues found and commit**

```bash
git add -A web/src/
git commit -m "fix: resolve any build/lint issues"
```
