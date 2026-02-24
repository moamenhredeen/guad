import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { projectsService, type Project, type ProjectsListParams, type PaginatedResponse } from '@/services/api'

export const useProjectsStore = defineStore('projects', () => {
  const projects = ref<Project[]>([])
  const selectedProject = ref<Project | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    page: 1,
    limit: 20,
    total: 0,
    total_pages: 0,
  })
  const filters = ref<ProjectsListParams>({})

  const hasMore = computed(() => pagination.value.page < pagination.value.total_pages)

  async function fetchProjects(params?: ProjectsListParams) {
    isLoading.value = true
    error.value = null
    try {
      const mergedParams = { ...filters.value, ...params }
      const response = await projectsService.getProjects({
        page: pagination.value.page,
        limit: pagination.value.limit,
        ...mergedParams,
      })
      projects.value = response.data
      pagination.value = {
        page: response.page,
        limit: response.limit,
        total: response.total,
        total_pages: response.total_pages,
      }
      return response
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch projects'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchProject(id: string) {
    isLoading.value = true
    error.value = null
    try {
      selectedProject.value = await projectsService.getProject(id)
      return selectedProject.value
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch project'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function updateProject(id: string, data: Partial<Project>) {
    isLoading.value = true
    error.value = null
    try {
      const updated = await projectsService.updateProject(id, data)
      // Update in list if present
      const index = projects.value.findIndex(p => p.id === id)
      if (index !== -1) {
        projects.value[index] = updated
      }
      // Update selected if it's the same project
      if (selectedProject.value?.id === id) {
        selectedProject.value = updated
      }
      return updated
    } catch (err: any) {
      error.value = err.message || 'Failed to update project'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function deleteProject(id: string) {
    isLoading.value = true
    error.value = null
    try {
      await projectsService.deleteProject(id)
      // Remove from list
      projects.value = projects.value.filter(p => p.id !== id)
      // Clear selected if it's the deleted project
      if (selectedProject.value?.id === id) {
        selectedProject.value = null
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to delete project'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function bulkUpdate(ids: string[], data: Partial<Project>) {
    isLoading.value = true
    error.value = null
    try {
      await projectsService.bulkUpdate(ids, data)
      // Refresh list
      await fetchProjects()
    } catch (err: any) {
      error.value = err.message || 'Failed to bulk update projects'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function bulkDelete(ids: string[]) {
    isLoading.value = true
    error.value = null
    try {
      await projectsService.bulkDelete(ids)
      // Remove from list
      projects.value = projects.value.filter(p => !ids.includes(p.id))
    } catch (err: any) {
      error.value = err.message || 'Failed to bulk delete projects'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function setFilters(newFilters: ProjectsListParams) {
    filters.value = { ...filters.value, ...newFilters }
    pagination.value.page = 1 // Reset to first page
  }

  function clearFilters() {
    filters.value = {}
    pagination.value.page = 1
  }

  function setSelectedProject(project: Project | null) {
    selectedProject.value = project
  }

  function clearError() {
    error.value = null
  }

  return {
    projects,
    selectedProject,
    isLoading,
    error,
    pagination,
    filters,
    hasMore,
    fetchProjects,
    fetchProject,
    updateProject,
    deleteProject,
    bulkUpdate,
    bulkDelete,
    setFilters,
    clearFilters,
    setSelectedProject,
    clearError,
  }
})

