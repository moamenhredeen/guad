import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { InboxItem, ProcessAction } from '@/types'
import { inboxApi } from '@/api/inbox'
import { useDashboardStore } from './dashboard'

export const useInboxStore = defineStore('inbox', () => {
  const items = ref<InboxItem[]>([])
  const loading = ref(false)
  const selectedId = ref<string | null>(null)

  async function fetch() {
    loading.value = true
    try {
      items.value = await inboxApi.list()
    } finally {
      loading.value = false
    }
  }

  async function add(title: string, description?: string) {
    const item = await inboxApi.create({ title, description })
    items.value.unshift(item)
    useDashboardStore().fetch()
  }

  async function remove(id: string) {
    await inboxApi.delete(id)
    items.value = items.value.filter(i => i.id !== id)
    useDashboardStore().fetch()
  }

  async function process(id: string, action: ProcessAction) {
    await inboxApi.process(id, { action })
    items.value = items.value.filter(i => i.id !== id)
    selectedId.value = null
    useDashboardStore().fetch()
  }

  return { items, loading, selectedId, fetch, add, remove, process }
})

