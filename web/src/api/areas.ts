import type { AreaResponse, CreateAreaRequest } from '@/types'
import { api } from './client'

export const areasApi = {
  list: () => api.get<AreaResponse[]>('/areas'),
  create: (data: CreateAreaRequest) => api.post<AreaResponse>('/areas', data),
  update: (id: string, data: CreateAreaRequest) => api.put<AreaResponse>(`/areas/${id}`, data),
  delete: (id: string) => api.delete<void>(`/areas/${id}`),
}
