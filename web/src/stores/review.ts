import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { WeeklyReviewResponse, ReviewStep } from '@/types'
import { reviewApi } from '@/api/review'
import { useDashboardStore } from './dashboard'

const STEP_ORDER: ReviewStep[] = [
  'CLEAR_INBOX', 'REVIEW_NEXT_ACTIONS', 'REVIEW_PROJECTS', 'REVIEW_WAITING_FOR', 'REVIEW_SOMEDAY_MAYBE', 'DONE'
]

const STEP_LABELS: Record<ReviewStep, string> = {
  CLEAR_INBOX: 'Clear Inbox',
  REVIEW_NEXT_ACTIONS: 'Review Next Actions',
  REVIEW_PROJECTS: 'Review Projects',
  REVIEW_WAITING_FOR: 'Review Waiting For',
  REVIEW_SOMEDAY_MAYBE: 'Review Someday/Maybe',
  DONE: 'Done',
}

const STEP_QUESTIONS: Record<ReviewStep, string> = {
  CLEAR_INBOX: 'Process every item in your inbox. Is it actionable? What\'s the next action?',
  REVIEW_NEXT_ACTIONS: 'Is each action still relevant? Mark off anything completed.',
  REVIEW_PROJECTS: 'Is each project still relevant? Does every active project have a next action?',
  REVIEW_WAITING_FOR: 'Is anyone overdue? Do you need to follow up?',
  REVIEW_SOMEDAY_MAYBE: 'Anything ready to activate? Anything to remove?',
  DONE: 'Sauber gmacht!',
}

export const useReviewStore = defineStore('review', () => {
  const review = ref<WeeklyReviewResponse | null>(null)
  const loading = ref(false)

  const currentStepIndex = computed(() => {
    if (!review.value) return 0
    return STEP_ORDER.indexOf(review.value.currentStep)
  })

  const isDone = computed(() => review.value?.currentStep === 'DONE')

  async function start() {
    loading.value = true
    try { review.value = await reviewApi.start() }
    finally { loading.value = false }
  }

  async function loadCurrent() {
    loading.value = true
    try { review.value = await reviewApi.getCurrent() }
    catch { review.value = null }
    finally { loading.value = false }
  }

  async function advance() {
    if (!review.value) return
    review.value = await reviewApi.advanceStep(review.value.id)
  }

  async function complete() {
    if (!review.value) return
    review.value = await reviewApi.complete(review.value.id)
    useDashboardStore().fetch()
  }

  return { review, loading, currentStepIndex, isDone, start, loadCurrent, advance, complete, STEP_ORDER, STEP_LABELS, STEP_QUESTIONS }
})
