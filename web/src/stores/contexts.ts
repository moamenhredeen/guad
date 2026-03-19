import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ContextResponse, CreateContextRequest } from '@/types'
import { contextsApi } from '@/api/contexts'

export const useContextsStore = defineStore('contexts', () => {
  const items = ref<ContextResponse[]>([])
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try {
      items.value = await contextsApi.list()
    } finally {
      loading.value = false
    }
  }

  async function add(data: CreateContextRequest) {
    const item = await contextsApi.create(data)
    items.value.push(item)
  }

  async function update(id: string, data: CreateContextRequest) {
    const updated = await contextsApi.update(id, data)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = updated
  }

  async function remove(id: string) {
    await contextsApi.delete(id)
    items.value = items.value.filter(i => i.id !== id)
  }

  return { items, loading, fetch, add, update, remove }
})
