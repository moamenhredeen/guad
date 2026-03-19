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
