import type { DashboardResponse } from '@/types'
import { api } from './client'

export const dashboardApi = {
  get: () => api.get<DashboardResponse>('/dashboard'),
}
