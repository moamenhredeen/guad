import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ProjectResponse, ProjectDetailResponse } from '@/types'
import { projectsApi } from '@/api/projects'
import { useDashboardStore } from './dashboard'

export const useProjectsStore = defineStore('projects', () => {
  const items = ref<ProjectResponse[]>([])
  const detail = ref<ProjectDetailResponse | null>(null)
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try {
      items.value = await projectsApi.list()
    } finally {
      loading.value = false
    }
  }

  async function fetchDetail(id: string) {
    loading.value = true
    try {
      detail.value = await projectsApi.get(id)
    } finally {
      loading.value = false
    }
  }

  async function addAction(projectId: string, description: string) {
    await projectsApi.addAction(projectId, description)
    await fetchDetail(projectId)
    useDashboardStore().fetch()
  }

  return { items, detail, loading, fetch, fetchDetail, addAction }
})

