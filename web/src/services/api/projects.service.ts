import { apiClient } from './client'
import type { PaginatedResponse } from './users.service'

export interface Project {
  id: string
  title: string
  description?: string
  desired_outcome?: string
  status: 'active' | 'on_hold' | 'completed' | 'someday_maybe' | 'cancelled'
  created_date: string
  completed_date?: string
  target_date?: string
  last_reviewed_date?: string
  area_id: string
  user_id: string
}

export interface ProjectsListParams {
  page?: number
  limit?: number
  user_id?: string
  status?: Project['status']
  area_id?: string
  search?: string
  sort_by?: string
  sort_order?: 'asc' | 'desc'
}

export const projectsService = {
  async getProjects(params?: ProjectsListParams): Promise<PaginatedResponse<Project>> {
    return apiClient.get<PaginatedResponse<Project>>('/admin/projects', params)
  },

  async getProject(id: string): Promise<Project> {
    return apiClient.get<Project>(`/admin/projects/${id}`)
  },

  async updateProject(id: string, data: Partial<Project>): Promise<Project> {
    return apiClient.patch<Project>(`/admin/projects/${id}`, data)
  },

  async deleteProject(id: string): Promise<void> {
    return apiClient.delete(`/admin/projects/${id}`)
  },

  async bulkUpdate(ids: string[], data: Partial<Project>): Promise<void> {
    return apiClient.patch('/admin/projects/bulk', { ids, ...data })
  },

  async bulkDelete(ids: string[]): Promise<void> {
    return apiClient.post('/admin/projects/bulk-delete', { ids })
  },
}

