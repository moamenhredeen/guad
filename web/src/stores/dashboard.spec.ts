import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useDashboardStore } from './dashboard'
import { buildDashboard } from '@/test/factories'

const { mockGet } = vi.hoisted(() => ({
  mockGet: vi.fn(),
}))

vi.mock('@/api/dashboard', () => ({
  dashboardApi: {
    get: (...args: unknown[]) => mockGet(...args),
  },
}))

describe('useDashboardStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('has correct initial state', () => {
    const store = useDashboardStore()
    expect(store.data).toBeNull()
    expect(store.loading).toBe(false)
  })

  describe('fetch', () => {
    it('sets data and manages loading state', async () => {
      const dashboard = buildDashboard({ inboxCount: 5, nextActionsCount: 3 })
      mockGet.mockResolvedValue(dashboard)

      const store = useDashboardStore()
      const promise = store.fetch()
      expect(store.loading).toBe(true)

      await promise
      expect(store.loading).toBe(false)
      expect(store.data).toEqual(dashboard)
    })

    it('sets loading to false even on error', async () => {
      mockGet.mockRejectedValue(new Error('fail'))
      const store = useDashboardStore()

      await expect(store.fetch()).rejects.toThrow('fail')
      expect(store.loading).toBe(false)
    })
  })
})
