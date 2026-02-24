import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { inboxService, type InboxItem, type InboxListParams, type PaginatedResponse } from '@/services/api'

export const useInboxStore = defineStore('inbox', () => {
  const items = ref<InboxItem[]>([])
  const selectedItem = ref<InboxItem | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    page: 1,
    limit: 20,
    total: 0,
    total_pages: 0,
  })
  const filters = ref<InboxListParams>({})

  const hasMore = computed(() => pagination.value.page < pagination.value.total_pages)

  async function fetchItems(params?: InboxListParams) {
    isLoading.value = true
    error.value = null
    try {
      const mergedParams = { ...filters.value, ...params }
      const response = await inboxService.getInboxItems({
        page: pagination.value.page,
        limit: pagination.value.limit,
        ...mergedParams,
      })
      items.value = response.data
      pagination.value = {
        page: response.page,
        limit: response.limit,
        total: response.total,
        total_pages: response.total_pages,
      }
      return response
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch inbox items'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchItem(id: string) {
    isLoading.value = true
    error.value = null
    try {
      selectedItem.value = await inboxService.getInboxItem(id)
      return selectedItem.value
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch inbox item'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function updateItem(id: string, data: Partial<InboxItem>) {
    isLoading.value = true
    error.value = null
    try {
      const updated = await inboxService.updateInboxItem(id, data)
      // Update in list if present
      const index = items.value.findIndex(i => i.id === id)
      if (index !== -1) {
        items.value[index] = updated
      }
      // Update selected if it's the same item
      if (selectedItem.value?.id === id) {
        selectedItem.value = updated
      }
      return updated
    } catch (err: any) {
      error.value = err.message || 'Failed to update inbox item'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function deleteItem(id: string) {
    isLoading.value = true
    error.value = null
    try {
      await inboxService.deleteInboxItem(id)
      // Remove from list
      items.value = items.value.filter(i => i.id !== id)
      // Clear selected if it's the deleted item
      if (selectedItem.value?.id === id) {
        selectedItem.value = null
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to delete inbox item'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function bulkUpdate(ids: string[], data: Partial<InboxItem>) {
    isLoading.value = true
    error.value = null
    try {
      await inboxService.bulkUpdate(ids, data)
      // Refresh list
      await fetchItems()
    } catch (err: any) {
      error.value = err.message || 'Failed to bulk update inbox items'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function bulkDelete(ids: string[]) {
    isLoading.value = true
    error.value = null
    try {
      await inboxService.bulkDelete(ids)
      // Remove from list
      items.value = items.value.filter(i => !ids.includes(i.id))
    } catch (err: any) {
      error.value = err.message || 'Failed to bulk delete inbox items'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function setFilters(newFilters: InboxListParams) {
    filters.value = { ...filters.value, ...newFilters }
    pagination.value.page = 1 // Reset to first page
  }

  function clearFilters() {
    filters.value = {}
    pagination.value.page = 1
  }

  function setSelectedItem(item: InboxItem | null) {
    selectedItem.value = item
  }

  function clearError() {
    error.value = null
  }

  return {
    items,
    selectedItem,
    isLoading,
    error,
    pagination,
    filters,
    hasMore,
    fetchItems,
    fetchItem,
    updateItem,
    deleteItem,
    bulkUpdate,
    bulkDelete,
    setFilters,
    clearFilters,
    setSelectedItem,
    clearError,
  }
})

