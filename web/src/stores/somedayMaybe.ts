import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { SomedayMaybeResponse } from '@/types'
import { somedayMaybeApi } from '@/api/somedayMaybe'

export const useSomedayMaybeStore = defineStore('somedayMaybe', () => {
  const data = ref<SomedayMaybeResponse | null>(null)
  const loading = ref(false)

  async function fetch() {
    loading.value = true
    try { data.value = await somedayMaybeApi.list() }
    finally { loading.value = false }
  }

  return { data, loading, fetch }
})
