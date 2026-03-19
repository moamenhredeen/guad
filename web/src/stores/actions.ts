import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ActionResponse, CreateActionRequest, UpdateActionRequest } from '@/types'
import { actionsApi } from '@/api/actions'
import { useDashboardStore } from './dashboard'

export const useActionsStore = defineStore('actions', () => {
  const items = ref<ActionResponse[]>([])
  const loading = ref(false)

  async function fetch(params?: { status?: string; contextId?: string }) {
    loading.value = true
    try {
      items.value = await actionsApi.list(params)
    } finally {
      loading.value = false
    }
  }

  async function create(data: CreateActionRequest) {
    const action = await actionsApi.create(data)
    items.value.push(action)
    useDashboardStore().fetch()
    return action
  }

  async function update(id: string, data: UpdateActionRequest) {
    const updated = await actionsApi.update(id, data)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = updated
    return updated
  }

  async function complete(id: string) {
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = { ...items.value[idx]!, status: 'COMPLETED' as const }
    try {
      await actionsApi.complete(id)
      items.value = items.value.filter(i => i.id !== id)
      useDashboardStore().fetch()
    } catch {
      if (idx >= 0) items.value[idx] = { ...items.value[idx]!, status: 'NEXT' as const }
    }
  }

  async function remove(id: string) {
    await actionsApi.delete(id)
    items.value = items.value.filter(i => i.id !== id)
    useDashboardStore().fetch()
  }

  async function updateStatus(id: string, status: string) {
    const updated = await actionsApi.updateStatus(id, status)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = updated
  }

  return { items, loading, fetch, create, update, complete, remove, updateStatus }
})

