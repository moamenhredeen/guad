import { apiClient } from './client'

export interface User {
  id: string
  email: string
  name: string
  created_date: string
  preferences?: Record<string, any>
}

export interface UserStats {
  total_actions: number
  total_projects: number
  total_inbox_items: number
  active_projects: number
  completed_actions: number
}

export interface UsersListParams {
  page?: number
  limit?: number
  search?: string
  sort_by?: string
  sort_order?: 'asc' | 'desc'
}

export interface PaginatedResponse<T> {
  data: T[]
  total: number
  page: number
  limit: number
  total_pages: number
}

export const usersService = {
  async getUsers(params?: UsersListParams): Promise<PaginatedResponse<User>> {
    return apiClient.get<PaginatedResponse<User>>('/admin/users', params)
  },

  async getUser(id: string): Promise<User> {
    return apiClient.get<User>(`/admin/users/${id}`)
  },

  async getUserStats(id: string): Promise<UserStats> {
    return apiClient.get<UserStats>(`/admin/users/${id}/stats`)
  },

  async updateUser(id: string, data: Partial<User>): Promise<User> {
    return apiClient.patch<User>(`/admin/users/${id}`, data)
  },

  async deactivateUser(id: string): Promise<void> {
    return apiClient.patch(`/admin/users/${id}/deactivate`, {})
  },

  async activateUser(id: string): Promise<void> {
    return apiClient.patch(`/admin/users/${id}/activate`, {})
  },
}

