import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { DashboardResponse } from '@/types'
import { dashboardApi } from '@/api/dashboard'

export const useDashboardStore = defineStore('dashboard', () => {
  const data = ref<DashboardResponse | null>(null)
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try {
      data.value = await dashboardApi.get()
    } finally {
      loading.value = false
    }
  }

  return { data, loading, fetch }
})
