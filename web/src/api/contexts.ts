import type { ContextResponse, CreateContextRequest } from '@/types'
import { api } from './client'

export const contextsApi = {
  list: () => api.get<ContextResponse[]>('/contexts'),
  create: (data: CreateContextRequest) => api.post<ContextResponse>('/contexts', data),
  update: (id: string, data: CreateContextRequest) => api.put<ContextResponse>(`/contexts/${id}`, data),
  delete: (id: string) => api.delete<void>(`/contexts/${id}`),
}
