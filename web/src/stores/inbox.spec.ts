import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useInboxStore } from './inbox'
import { buildInboxItem } from '@/test/factories'

const { mockList, mockCreate, mockDelete, mockProcess } = vi.hoisted(() => ({
  mockList: vi.fn(),
  mockCreate: vi.fn(),
  mockDelete: vi.fn(),
  mockProcess: vi.fn(),
}))

vi.mock('@/api/inbox', () => ({
  inboxApi: {
    list: (...args: unknown[]) => mockList(...args),
    create: (...args: unknown[]) => mockCreate(...args),
    delete: (...args: unknown[]) => mockDelete(...args),
    process: (...args: unknown[]) => mockProcess(...args),
  },
}))

vi.mock('@/api/dashboard', () => ({
  dashboardApi: {
    get: vi.fn().mockResolvedValue({ inboxCount: 0, nextActionsCount: 0, activeProjectsCount: 0, waitingForCount: 0, somedayMaybeActionsCount: 0, weeklyReviewDue: false, lastReviewDate: null }),
  },
}))

describe('useInboxStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('fetch', () => {
    it('sets items and manages loading state', async () => {
      const items = [buildInboxItem(), buildInboxItem({ title: 'Second' })]
      mockList.mockResolvedValue(items)

      const store = useInboxStore()
      expect(store.loading).toBe(false)

      const promise = store.fetch()
      expect(store.loading).toBe(true)

      await promise
      expect(store.loading).toBe(false)
      expect(store.items).toEqual(items)
    })

    it('sets loading to false even on error', async () => {
      mockList.mockRejectedValue(new Error('fail'))
      const store = useInboxStore()

      await expect(store.fetch()).rejects.toThrow('fail')
      expect(store.loading).toBe(false)
    })
  })

  describe('add', () => {
    it('prepends item and calls dashboard.fetch', async () => {
      const existing = buildInboxItem({ id: '10' })
      const created = buildInboxItem({ id: '20', title: 'New' })
      mockCreate.mockResolvedValue(created)

      const store = useInboxStore()
      store.items = [existing]

      await store.add('New', 'desc')

      expect(mockCreate).toHaveBeenCalledWith({ title: 'New', description: 'desc' })
      expect(store.items[0]).toEqual(created)
      expect(store.items).toHaveLength(2)
    })
  })

  describe('remove', () => {
    it('filters item out and calls dashboard.fetch', async () => {
      mockDelete.mockResolvedValue(undefined)
      const item = buildInboxItem({ id: '5' })

      const store = useInboxStore()
      store.items = [item, buildInboxItem({ id: '6' })]

      await store.remove('5')

      expect(mockDelete).toHaveBeenCalledWith('5')
      expect(store.items).toHaveLength(1)
      expect(store.items[0]!.id).toBe('6')
    })
  })

  describe('process', () => {
    it('filters item, clears selectedId, and calls dashboard.fetch', async () => {
      mockProcess.mockResolvedValue(undefined)
      const item = buildInboxItem({ id: '7' })

      const store = useInboxStore()
      store.items = [item]
      store.selectedId = '7'

      await store.process('7', 'NEXT_ACTION')

      expect(mockProcess).toHaveBeenCalledWith('7', { action: 'NEXT_ACTION' })
      expect(store.items).toHaveLength(0)
      expect(store.selectedId).toBeNull()
    })
  })
})
