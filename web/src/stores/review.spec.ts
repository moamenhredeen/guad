import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useReviewStore } from './review'
import { buildReview } from '@/test/factories'

const { mockStart, mockGetCurrent, mockAdvanceStep, mockComplete } = vi.hoisted(() => ({
  mockStart: vi.fn(),
  mockGetCurrent: vi.fn(),
  mockAdvanceStep: vi.fn(),
  mockComplete: vi.fn(),
}))

vi.mock('@/api/review', () => ({
  reviewApi: {
    start: (...args: unknown[]) => mockStart(...args),
    getCurrent: (...args: unknown[]) => mockGetCurrent(...args),
    advanceStep: (...args: unknown[]) => mockAdvanceStep(...args),
    complete: (...args: unknown[]) => mockComplete(...args),
  },
}))

vi.mock('@/api/dashboard', () => ({
  dashboardApi: {
    get: vi.fn().mockResolvedValue({ inboxCount: 0, nextActionsCount: 0, activeProjectsCount: 0, waitingForCount: 0, somedayMaybeActionsCount: 0, weeklyReviewDue: false, lastReviewDate: null }),
  },
}))

describe('useReviewStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('start', () => {
    it('sets review and manages loading state', async () => {
      const review = buildReview()
      mockStart.mockResolvedValue(review)

      const store = useReviewStore()
      const promise = store.start()
      expect(store.loading).toBe(true)

      await promise
      expect(store.loading).toBe(false)
      expect(store.review).toEqual(review)
    })
  })

  describe('loadCurrent', () => {
    it('sets review on success', async () => {
      const review = buildReview()
      mockGetCurrent.mockResolvedValue(review)

      const store = useReviewStore()
      await store.loadCurrent()

      expect(store.review).toEqual(review)
      expect(store.loading).toBe(false)
    })

    it('sets review to null on error', async () => {
      mockGetCurrent.mockRejectedValue(new Error('not found'))

      const store = useReviewStore()
      await store.loadCurrent()

      expect(store.review).toBeNull()
      expect(store.loading).toBe(false)
    })
  })

  describe('advance', () => {
    it('advances the review step', async () => {
      const initial = buildReview({ id: '1', currentStep: 'CLEAR_INBOX' })
      const advanced = buildReview({ id: '1', currentStep: 'REVIEW_NEXT_ACTIONS' })
      mockAdvanceStep.mockResolvedValue(advanced)

      const store = useReviewStore()
      store.review = initial

      await store.advance()

      expect(mockAdvanceStep).toHaveBeenCalledWith('1')
      expect(store.review).toEqual(advanced)
    })

    it('does nothing when review is null', async () => {
      const store = useReviewStore()
      await store.advance()

      expect(mockAdvanceStep).not.toHaveBeenCalled()
    })
  })

  describe('complete', () => {
    it('completes review and calls dashboard.fetch', async () => {
      const initial = buildReview({ id: '1' })
      const completed = buildReview({ id: '1', currentStep: 'DONE', completedAt: '2026-03-20T12:00:00Z' })
      mockComplete.mockResolvedValue(completed)

      const store = useReviewStore()
      store.review = initial

      await store.complete()

      expect(mockComplete).toHaveBeenCalledWith('1')
      expect(store.review).toEqual(completed)
    })

    it('does nothing when review is null', async () => {
      const store = useReviewStore()
      await store.complete()

      expect(mockComplete).not.toHaveBeenCalled()
    })
  })

  describe('currentStepIndex', () => {
    it('returns 0 when review is null', () => {
      const store = useReviewStore()
      expect(store.currentStepIndex).toBe(0)
    })

    it('returns correct index for current step', () => {
      const store = useReviewStore()
      store.review = buildReview({ currentStep: 'REVIEW_PROJECTS' })
      expect(store.currentStepIndex).toBe(2)
    })
  })

  describe('isDone', () => {
    it('returns false when review is null', () => {
      const store = useReviewStore()
      expect(store.isDone).toBe(false)
    })

    it('returns true when step is DONE', () => {
      const store = useReviewStore()
      store.review = buildReview({ currentStep: 'DONE' })
      expect(store.isDone).toBe(true)
    })

    it('returns false when step is not DONE', () => {
      const store = useReviewStore()
      store.review = buildReview({ currentStep: 'CLEAR_INBOX' })
      expect(store.isDone).toBe(false)
    })
  })
})
