import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useWaitingForStore } from './waitingFor'
import { buildWaitingFor } from '@/test/factories'

const { mockList, mockCreate, mockResolve } = vi.hoisted(() => ({
  mockList: vi.fn(),
  mockCreate: vi.fn(),
  mockResolve: vi.fn(),
}))

vi.mock('@/api/waitingFor', () => ({
  waitingForApi: {
    list: (...args: unknown[]) => mockList(...args),
    create: (...args: unknown[]) => mockCreate(...args),
    resolve: (...args: unknown[]) => mockResolve(...args),
  },
}))

vi.mock('@/api/dashboard', () => ({
  dashboardApi: {
    get: vi.fn().mockResolvedValue({ inboxCount: 0, nextActionsCount: 0, activeProjectsCount: 0, waitingForCount: 0, somedayMaybeActionsCount: 0, weeklyReviewDue: false, lastReviewDate: null }),
  },
}))

describe('useWaitingForStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('fetch', () => {
    it('sets items and manages loading state', async () => {
      const items = [buildWaitingFor(), buildWaitingFor()]
      mockList.mockResolvedValue(items)

      const store = useWaitingForStore()
      const promise = store.fetch()
      expect(store.loading).toBe(true)

      await promise
      expect(store.loading).toBe(false)
      expect(store.items).toEqual(items)
    })
  })

  describe('add', () => {
    it('appends item and calls dashboard.fetch', async () => {
      const item = buildWaitingFor({ id: '1' })
      mockCreate.mockResolvedValue(item)

      const store = useWaitingForStore()
      await store.add({ title: 'Wait for review' })

      expect(mockCreate).toHaveBeenCalledWith({ title: 'Wait for review' })
      expect(store.items).toHaveLength(1)
      expect(store.items[0]).toEqual(item)
    })
  })

  describe('resolve', () => {
    it('filters item out and calls dashboard.fetch', async () => {
      mockResolve.mockResolvedValue(undefined)

      const store = useWaitingForStore()
      store.items = [buildWaitingFor({ id: '1' }), buildWaitingFor({ id: '2' })]

      await store.resolve('1')

      expect(mockResolve).toHaveBeenCalledWith('1')
      expect(store.items).toHaveLength(1)
      expect(store.items[0]!.id).toBe('2')
    })
  })
})
