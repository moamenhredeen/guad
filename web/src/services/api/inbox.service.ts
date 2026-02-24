import { apiClient } from './client'
import type { PaginatedResponse } from './users.service'

export interface InboxItem {
  id: string
  title: string
  description?: string
  captured_date: string
  source?: string
  status: 'unprocessed' | 'processed'
  user_id: string
}

export interface InboxListParams {
  page?: number
  limit?: number
  user_id?: string
  status?: InboxItem['status']
  search?: string
  sort_by?: string
  sort_order?: 'asc' | 'desc'
}

export const inboxService = {
  async getInboxItems(params?: InboxListParams): Promise<PaginatedResponse<InboxItem>> {
    return apiClient.get<PaginatedResponse<InboxItem>>('/admin/inbox', params)
  },

  async getInboxItem(id: string): Promise<InboxItem> {
    return apiClient.get<InboxItem>(`/admin/inbox/${id}`)
  },

  async updateInboxItem(id: string, data: Partial<InboxItem>): Promise<InboxItem> {
    return apiClient.patch<InboxItem>(`/admin/inbox/${id}`, data)
  },

  async deleteInboxItem(id: string): Promise<void> {
    return apiClient.delete(`/admin/inbox/${id}`)
  },

  async bulkUpdate(ids: string[], data: Partial<InboxItem>): Promise<void> {
    return apiClient.patch('/admin/inbox/bulk', { ids, ...data })
  },

  async bulkDelete(ids: string[]): Promise<void> {
    return apiClient.post('/admin/inbox/bulk-delete', { ids })
  },
}

