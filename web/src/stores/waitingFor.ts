import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { WaitingForResponse, CreateWaitingForRequest } from '@/types'
import { waitingForApi } from '@/api/waitingFor'
import { useDashboardStore } from './dashboard'

export const useWaitingForStore = defineStore('waitingFor', () => {
  const items = ref<WaitingForResponse[]>([])
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try { items.value = await waitingForApi.list() }
    finally { loading.value = false }
  }

  async function add(data: CreateWaitingForRequest) {
    const item = await waitingForApi.create(data)
    items.value.push(item)
    useDashboardStore().fetch()
  }

  async function resolve(id: string) {
    await waitingForApi.resolve(id)
    items.value = items.value.filter(i => i.id !== id)
    useDashboardStore().fetch()
  }

  return { items, loading, fetch, add, resolve }
})
