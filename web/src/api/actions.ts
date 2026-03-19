import type { ActionResponse, CreateActionRequest, UpdateActionRequest } from '@/types'
import { api } from './client'

export const actionsApi = {
  list: (params?: { status?: string; contextId?: string }) => {
    const query = new URLSearchParams()
    if (params?.status) query.set('status', params.status)
    if (params?.contextId) query.set('contextId', params.contextId)
    const qs = query.toString()
    return api.get<ActionResponse[]>(`/actions${qs ? `?${qs}` : ''}`)
  },
  get: (id: string) => api.get<ActionResponse>(`/actions/${id}`),
  create: (data: CreateActionRequest) => api.post<ActionResponse>('/actions', data),
  update: (id: string, data: UpdateActionRequest) => api.put<ActionResponse>(`/actions/${id}`, data),
  delete: (id: string) => api.delete<void>(`/actions/${id}`),
  complete: (id: string) => api.patch<ActionResponse>(`/actions/${id}/complete`),
  updateStatus: (id: string, status: string) => api.patch<ActionResponse>(`/actions/${id}/status`, { status }),
}
