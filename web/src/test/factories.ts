import type {
  InboxItem,
  ActionResponse,
  ProjectResponse,
  ProjectDetailResponse,
  ContextResponse,
  AreaResponse,
  WaitingForResponse,
  WeeklyReviewResponse,
  DashboardResponse,
  SomedayMaybeResponse,
} from '@/types'

let _id = 1
function nextId(): string {
  return String(_id++)
}

export function resetIdCounter() {
  _id = 1
}

export function buildInboxItem(overrides: Partial<InboxItem> = {}): InboxItem {
  return {
    id: nextId(),
    title: 'Test inbox item',
    description: null,
    status: 'UNPROCESSED',
    createdAt: '2026-03-20T10:00:00Z',
    ...overrides,
  }
}

export function buildAction(overrides: Partial<ActionResponse> = {}): ActionResponse {
  return {
    id: nextId(),
    description: 'Test action',
    notes: null,
    status: 'NEXT',
    energyLevel: null,
    estimatedDuration: null,
    dueDate: null,
    scheduledDate: null,
    projectName: null,
    projectId: null,
    areaName: null,
    areaId: null,
    contexts: [],
    createdDate: '2026-03-20T10:00:00Z',
    completedDate: null,
    ...overrides,
  }
}

export function buildProject(overrides: Partial<ProjectResponse> = {}): ProjectResponse {
  return {
    id: nextId(),
    name: 'Test project',
    description: null,
    desiredOutcome: null,
    status: 'ACTIVE',
    areaName: null,
    areaId: null,
    color: null,
    nextActionCount: 0,
    createdDate: '2026-03-20T10:00:00Z',
    ...overrides,
  }
}

export function buildProjectDetail(overrides: Partial<ProjectDetailResponse> = {}): ProjectDetailResponse {
  return {
    id: nextId(),
    name: 'Test project',
    description: null,
    desiredOutcome: null,
    status: 'ACTIVE',
    areaName: null,
    areaId: null,
    color: null,
    nextActions: [],
    waitingForItems: [],
    completedActions: [],
    createdDate: '2026-03-20T10:00:00Z',
    ...overrides,
  }
}

export function buildContext(overrides: Partial<ContextResponse> = {}): ContextResponse {
  return {
    id: nextId(),
    name: 'Test context',
    description: null,
    color: null,
    iconKey: null,
    ...overrides,
  }
}

export function buildArea(overrides: Partial<AreaResponse> = {}): AreaResponse {
  return {
    id: nextId(),
    name: 'Test area',
    description: null,
    ...overrides,
  }
}

export function buildWaitingFor(overrides: Partial<WaitingForResponse> = {}): WaitingForResponse {
  return {
    id: nextId(),
    title: 'Waiting for test',
    delegatedTo: null,
    createdDate: '2026-03-20T10:00:00Z',
    notes: null,
    status: 'WAITING',
    projectName: null,
    projectId: null,
    ...overrides,
  }
}

export function buildReview(overrides: Partial<WeeklyReviewResponse> = {}): WeeklyReviewResponse {
  return {
    id: nextId(),
    startedAt: '2026-03-20T10:00:00Z',
    completedAt: null,
    currentStep: 'CLEAR_INBOX',
    notes: null,
    ...overrides,
  }
}

export function buildDashboard(overrides: Partial<DashboardResponse> = {}): DashboardResponse {
  return {
    inboxCount: 0,
    nextActionsCount: 0,
    activeProjectsCount: 0,
    waitingForCount: 0,
    somedayMaybeActionsCount: 0,
    weeklyReviewDue: false,
    lastReviewDate: null,
    ...overrides,
  }
}

export function buildSomedayMaybe(overrides: Partial<SomedayMaybeResponse> = {}): SomedayMaybeResponse {
  return {
    actions: [],
    projects: [],
    ...overrides,
  }
}
