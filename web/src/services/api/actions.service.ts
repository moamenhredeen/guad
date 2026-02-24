import { apiClient } from './client'
import type { PaginatedResponse } from './users.service'

export interface Action {
  id: string
  description: string
  notes?: string
  status: 'next_action' | 'waiting_for' | 'scheduled' | 'someday_maybe' | 'completed'
  created_date: string
  completed_date?: string
  due_date?: string
  scheduled_date?: string
  scheduled_time?: string
  is_time_specific: boolean
  estimated_duration?: number
  energy_level?: 'high' | 'medium' | 'low'
  location?: string
  project_id?: string
  area_id?: string
  user_id: string
  contexts?: Context[]
}

export interface Context {
  id: string
  name: string
  color?: string
  icon?: string
}

export interface ActionsListParams {
  page?: number
  limit?: number
  user_id?: string
  status?: Action['status']
  context_ids?: string[]
  area_id?: string
  energy_level?: Action['energy_level']
  search?: string
  sort_by?: string
  sort_order?: 'asc' | 'desc'
}

export const actionsService = {
  async getActions(params?: ActionsListParams): Promise<PaginatedResponse<Action>> {
    return apiClient.get<PaginatedResponse<Action>>('/admin/actions', params)
  },

  async getAction(id: string): Promise<Action> {
    return apiClient.get<Action>(`/admin/actions/${id}`)
  },

  async updateAction(id: string, data: Partial<Action>): Promise<Action> {
    return apiClient.patch<Action>(`/admin/actions/${id}`, data)
  },

  async deleteAction(id: string): Promise<void> {
    return apiClient.delete(`/admin/actions/${id}`)
  },

  async bulkUpdate(ids: string[], data: Partial<Action>): Promise<void> {
    return apiClient.patch('/admin/actions/bulk', { ids, ...data })
  },

  async bulkDelete(ids: string[]): Promise<void> {
    return apiClient.post('/admin/actions/bulk-delete', { ids })
  },
}

