import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { AreaResponse, CreateAreaRequest } from '@/types'
import { areasApi } from '@/api/areas'

export const useAreasStore = defineStore('areas', () => {
  const items = ref<AreaResponse[]>([])
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try { items.value = await areasApi.list() }
    finally { loading.value = false }
  }

  async function add(data: CreateAreaRequest) {
    const item = await areasApi.create(data)
    items.value.push(item)
  }

  async function update(id: string, data: CreateAreaRequest) {
    const updated = await areasApi.update(id, data)
    const idx = items.value.findIndex(i => i.id === id)
    if (idx >= 0) items.value[idx] = updated
  }

  async function remove(id: string) {
    await areasApi.delete(id)
    items.value = items.value.filter(i => i.id !== id)
  }

  return { items, loading, fetch, add, update, remove }
})
