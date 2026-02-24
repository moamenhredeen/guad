import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { actionsService, type Action, type ActionsListParams, type PaginatedResponse } from '@/services/api'

export const useActionsStore = defineStore('actions', () => {
  const actions = ref<Action[]>([])
  const selectedAction = ref<Action | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    page: 1,
    limit: 20,
    total: 0,
    total_pages: 0,
  })
  const filters = ref<ActionsListParams>({})

  const hasMore = computed(() => pagination.value.page < pagination.value.total_pages)

  async function fetchActions(params?: ActionsListParams) {
    isLoading.value = true
    error.value = null
    try {
      const mergedParams = { ...filters.value, ...params }
      const response = await actionsService.getActions({
        page: pagination.value.page,
        limit: pagination.value.limit,
        ...mergedParams,
      })
      actions.value = response.data
      pagination.value = {
        page: response.page,
        limit: response.limit,
        total: response.total,
        total_pages: response.total_pages,
      }
      return response
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch actions'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchAction(id: string) {
    isLoading.value = true
    error.value = null
    try {
      selectedAction.value = await actionsService.getAction(id)
      return selectedAction.value
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch action'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function updateAction(id: string, data: Partial<Action>) {
    isLoading.value = true
    error.value = null
    try {
      const updated = await actionsService.updateAction(id, data)
      // Update in list if present
      const index = actions.value.findIndex(a => a.id === id)
      if (index !== -1) {
        actions.value[index] = updated
      }
      // Update selected if it's the same action
      if (selectedAction.value?.id === id) {
        selectedAction.value = updated
      }
      return updated
    } catch (err: any) {
      error.value = err.message || 'Failed to update action'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function deleteAction(id: string) {
    isLoading.value = true
    error.value = null
    try {
      await actionsService.deleteAction(id)
      // Remove from list
      actions.value = actions.value.filter(a => a.id !== id)
      // Clear selected if it's the deleted action
      if (selectedAction.value?.id === id) {
        selectedAction.value = null
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to delete action'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function bulkUpdate(ids: string[], data: Partial<Action>) {
    isLoading.value = true
    error.value = null
    try {
      await actionsService.bulkUpdate(ids, data)
      // Refresh list
      await fetchActions()
    } catch (err: any) {
      error.value = err.message || 'Failed to bulk update actions'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function bulkDelete(ids: string[]) {
    isLoading.value = true
    error.value = null
    try {
      await actionsService.bulkDelete(ids)
      // Remove from list
      actions.value = actions.value.filter(a => !ids.includes(a.id))
    } catch (err: any) {
      error.value = err.message || 'Failed to bulk delete actions'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function setFilters(newFilters: ActionsListParams) {
    filters.value = { ...filters.value, ...newFilters }
    pagination.value.page = 1 // Reset to first page
  }

  function clearFilters() {
    filters.value = {}
    pagination.value.page = 1
  }

  function setSelectedAction(action: Action | null) {
    selectedAction.value = action
  }

  function clearError() {
    error.value = null
  }

  return {
    actions,
    selectedAction,
    isLoading,
    error,
    pagination,
    filters,
    hasMore,
    fetchActions,
    fetchAction,
    updateAction,
    deleteAction,
    bulkUpdate,
    bulkDelete,
    setFilters,
    clearFilters,
    setSelectedAction,
    clearError,
  }
})

