import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useActionsStore } from './actions'
import { buildAction } from '@/test/factories'

const { mockList, mockCreate, mockUpdate, mockDelete, mockComplete, mockUpdateStatus } = vi.hoisted(() => ({
  mockList: vi.fn(),
  mockCreate: vi.fn(),
  mockUpdate: vi.fn(),
  mockDelete: vi.fn(),
  mockComplete: vi.fn(),
  mockUpdateStatus: vi.fn(),
}))

vi.mock('@/api/actions', () => ({
  actionsApi: {
    list: (...args: unknown[]) => mockList(...args),
    create: (...args: unknown[]) => mockCreate(...args),
    update: (...args: unknown[]) => mockUpdate(...args),
    delete: (...args: unknown[]) => mockDelete(...args),
    complete: (...args: unknown[]) => mockComplete(...args),
    updateStatus: (...args: unknown[]) => mockUpdateStatus(...args),
  },
}))

vi.mock('@/api/dashboard', () => ({
  dashboardApi: {
    get: vi.fn().mockResolvedValue({ inboxCount: 0, nextActionsCount: 0, activeProjectsCount: 0, waitingForCount: 0, somedayMaybeActionsCount: 0, weeklyReviewDue: false, lastReviewDate: null }),
  },
}))

describe('useActionsStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('fetch', () => {
    it('sets items and manages loading state', async () => {
      const actions = [buildAction(), buildAction()]
      mockList.mockResolvedValue(actions)

      const store = useActionsStore()
      const promise = store.fetch({ status: 'NEXT' })
      expect(store.loading).toBe(true)

      await promise
      expect(store.loading).toBe(false)
      expect(store.items).toEqual(actions)
      expect(mockList).toHaveBeenCalledWith({ status: 'NEXT' })
    })
  })

  describe('create', () => {
    it('appends action and calls dashboard.fetch', async () => {
      const action = buildAction({ id: '1' })
      mockCreate.mockResolvedValue(action)

      const store = useActionsStore()
      const result = await store.create({ description: 'Do something' })

      expect(result).toEqual(action)
      expect(store.items).toHaveLength(1)
      expect(store.items[0]).toEqual(action)
    })
  })

  describe('update', () => {
    it('replaces item in-place', async () => {
      const original = buildAction({ id: '1', description: 'Old' })
      const updated = buildAction({ id: '1', description: 'New' })
      mockUpdate.mockResolvedValue(updated)

      const store = useActionsStore()
      store.items = [original]

      const result = await store.update('1', { description: 'New' })

      expect(result).toEqual(updated)
      expect(store.items[0]!.description).toBe('New')
    })
  })

  describe('complete', () => {
    it('optimistically sets COMPLETED then removes item on success', async () => {
      const action = buildAction({ id: '1', status: 'NEXT' })
      mockComplete.mockImplementation(() => {
        // Verify optimistic update happened before API resolves
        expect(store.items[0]!.status).toBe('COMPLETED')
        return Promise.resolve()
      })

      const store = useActionsStore()
      store.items = [action]

      await store.complete('1')

      expect(store.items).toHaveLength(0)
    })

    it('rolls back to NEXT on API failure', async () => {
      const action = buildAction({ id: '1', status: 'NEXT' })
      mockComplete.mockRejectedValue(new Error('fail'))

      const store = useActionsStore()
      store.items = [action]

      await store.complete('1')

      expect(store.items[0]!.status).toBe('NEXT')
      expect(store.items).toHaveLength(1)
    })
  })

  describe('remove', () => {
    it('filters item out and calls dashboard.fetch', async () => {
      mockDelete.mockResolvedValue(undefined)
      const action = buildAction({ id: '1' })

      const store = useActionsStore()
      store.items = [action, buildAction({ id: '2' })]

      await store.remove('1')

      expect(store.items).toHaveLength(1)
      expect(store.items[0]!.id).toBe('2')
    })
  })

  describe('updateStatus', () => {
    it('updates status in-place', async () => {
      const action = buildAction({ id: '1', status: 'NEXT' })
      const updated = buildAction({ id: '1', status: 'WAITING' as never })
      mockUpdateStatus.mockResolvedValue(updated)

      const store = useActionsStore()
      store.items = [action]

      await store.updateStatus('1', 'WAITING')

      expect(mockUpdateStatus).toHaveBeenCalledWith('1', 'WAITING')
      expect(store.items[0]).toEqual(updated)
    })
  })
})
