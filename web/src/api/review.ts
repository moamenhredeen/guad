import type { WeeklyReviewResponse } from '@/types'
import { api } from './client'

export const reviewApi = {
  start: () => api.post<WeeklyReviewResponse>('/reviews'),
  getCurrent: () => api.get<WeeklyReviewResponse>('/reviews/current'),
  advanceStep: (id: string) => api.patch<WeeklyReviewResponse>(`/reviews/${id}/step`),
  complete: (id: string) => api.post<WeeklyReviewResponse>(`/reviews/${id}/complete`),
  getLast: () => api.get<WeeklyReviewResponse>('/reviews/last'),
}
