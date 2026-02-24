import { apiClient } from './client'

export interface DashboardStats {
  total_users: number
  active_users_30d: number
  total_inbox_items: {
    unprocessed: number
    processed: number
  }
  total_actions: {
    next_action: number
    waiting_for: number
    scheduled: number
    someday_maybe: number
    completed: number
  }
  total_projects: {
    active: number
    on_hold: number
    completed: number
    someday_maybe: number
    cancelled: number
  }
  storage_usage: {
    total_bytes: number
    total_files: number
  }
  recent_activity: ActivityEvent[]
}

export interface ActivityEvent {
  id: string
  type: string
  description: string
  user_id?: string
  entity_type?: string
  entity_id?: string
  created_at: string
}

export const dashboardService = {
  async getStats(): Promise<DashboardStats> {
    return apiClient.get<DashboardStats>('/admin/dashboard/stats')
  },
}

