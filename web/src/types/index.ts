// === API Envelope ===
export interface PageMeta {
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface ApiResponse<T> {
  data: T
  meta: PageMeta | null
}

// === Inbox ===
export interface InboxItem {
  id: string
  title: string
  description: string | null
  status: string
  createdAt: string
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
