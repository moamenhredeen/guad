import { ref } from 'vue'
import { defineStore } from 'pinia'
import { dashboardService, type DashboardStats } from '@/services/api'

export const useDashboardStore = defineStore('dashboard', () => {
  const stats = ref<DashboardStats | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  async function fetchStats() {
    isLoading.value = true
    error.value = null
    try {
      stats.value = await dashboardService.getStats()
      return stats.value
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch dashboard stats'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function clearError() {
    error.value = null
  }

  return {
    stats,
    isLoading,
    error,
    fetchStats,
    clearError,
  }
})

