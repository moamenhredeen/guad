import type { InboxItem, CreateInboxItemRequest, ProcessInboxItemRequest } from '@/types'
import { api } from './client'

export const inboxApi = {
  list: () => api.get<InboxItem[]>('/inbox'),
  get: (id: string) => api.get<InboxItem>(`/inbox/${id}`),
  create: (data: CreateInboxItemRequest) => api.post<InboxItem>('/inbox', data),
  delete: (id: string) => api.delete<void>(`/inbox/${id}`),
  process: (id: string, data: ProcessInboxItemRequest) => api.post<void>(`/inbox/${id}/process`, data),
}
