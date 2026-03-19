import type { ProjectResponse, ProjectDetailResponse, CreateProjectRequest } from '@/types'
import { api } from './client'

export const projectsApi = {
  list: () => api.get<ProjectResponse[]>('/projects'),
  get: (id: string) => api.get<ProjectDetailResponse>(`/projects/${id}`),
  create: (data: CreateProjectRequest) => api.post<ProjectResponse>('/projects', data),
  update: (id: string, data: CreateProjectRequest) => api.put<ProjectResponse>(`/projects/${id}`, data),
  delete: (id: string) => api.delete<void>(`/projects/${id}`),
  addAction: (id: string, description: string) => api.post<void>(`/projects/${id}/actions`, { description }),
  updateStatus: (id: string, status: string) => api.patch<ProjectResponse>(`/projects/${id}/status`, { status }),
}
