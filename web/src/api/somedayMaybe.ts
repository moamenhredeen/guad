import type { SomedayMaybeResponse } from '@/types'
import { api } from './client'

export const somedayMaybeApi = {
  list: () => api.get<SomedayMaybeResponse>('/someday-maybe'),
}
