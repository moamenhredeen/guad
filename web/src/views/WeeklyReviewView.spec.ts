import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import WeeklyReviewView from './WeeklyReviewView.vue'
import { useReviewStore } from '@/stores/review'
import { buildReview, resetIdCounter } from '@/test/factories'

const stubs = {
  Button: { template: '<button><slot /></button>' },
  RouterLink: { template: '<a><slot /></a>', props: ['to'] },
}

function mountView(storeOverrides: Record<string, unknown> = {}) {
  const pinia = createTestingPinia({
    createSpy: vi.fn,
    initialState: {
      review: { review: null, loading: false, ...storeOverrides },
    },
  })

  const wrapper = mount(WeeklyReviewView, {
    global: { plugins: [pinia], stubs },
  })

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const store = useReviewStore() as any
  return { wrapper, store }
}

describe('WeeklyReviewView', () => {
  beforeEach(() => {
    resetIdCounter()
  })

  describe('no active review', () => {
    it('shows "Weekly Review" heading and "Start Weekly Review" button', async () => {
      const { wrapper } = mountView({ review: null })
      await flushPromises()

      expect(wrapper.text()).toContain('Weekly Review')
      expect(wrapper.text()).toContain('Start Weekly Review')
      expect(wrapper.text()).toContain('This takes about 30 minutes')
    })
  })

  describe('active review at step 0', () => {
    it('shows step label, step counter, and progress bar', async () => {
      const review = buildReview({ currentStep: 'CLEAR_INBOX' })
      const { wrapper, store } = mountView({ review })
      // Writable getters: must set manually
      store.currentStepIndex = 0
      store.isDone = false
      await flushPromises()

      expect(wrapper.text()).toContain('Clear Inbox')
      expect(wrapper.text()).toContain('Step 1 of 5')
    })
  })

  describe('active review done', () => {
    it('shows "Sauber gmacht!" completion message and "Back to Inbox" link', async () => {
      const review = buildReview({ currentStep: 'DONE', completedAt: '2026-03-22T10:00:00Z' })
      const { wrapper, store } = mountView({ review })
      store.currentStepIndex = 5
      store.isDone = true
      await flushPromises()

      expect(wrapper.text()).toContain('Sauber gmacht!')
      expect(wrapper.text()).toContain('Your system is up to date')
      expect(wrapper.text()).toContain('Back to Inbox')
    })
  })

  describe('step counter text', () => {
    it('shows "Step 2 of 5" when currentStepIndex is 1', async () => {
      const review = buildReview({ currentStep: 'REVIEW_NEXT_ACTIONS' })
      const { wrapper, store } = mountView({ review })
      store.currentStepIndex = 1
      store.isDone = false
      await flushPromises()

      expect(wrapper.text()).toContain('Step 2 of 5')
    })
  })

  describe('fetch on mount', () => {
    it('calls loadCurrent on mount', async () => {
      const { store } = mountView()
      await flushPromises()

      expect(store.loadCurrent).toHaveBeenCalledOnce()
    })
  })
})
