import { describe, it, expect, vi, beforeEach } from 'vitest'
import { ref } from 'vue'
import { mount, flushPromises } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import NextActionsView from './NextActionsView.vue'
import { useActionsStore } from '@/stores/actions'
import { useContextsStore } from '@/stores/contexts'
import { buildAction, buildContext, resetIdCounter } from '@/test/factories'

vi.mock('@/composables/useTaskDetail', () => ({
  useTaskDetail: () => ({
    open: vi.fn(),
    close: vi.fn(),
    isOpen: ref(false),
    selectedAction: ref(null),
  }),
}))

import { defineComponent } from 'vue'

const TaskRowStub = defineComponent({
  name: 'TaskRow',
  props: ['title', 'meta', 'selected', 'dueDate'],
  template: '<div class="task-row-stub"><slot /></div>',
})

const stubs = {
  TaskRow: TaskRowStub,
  InlineAddTask: { template: '<div />' },
  Skeleton: { template: '<div class="skeleton-stub" />' },
}

function mountView(overrides: { actions?: Record<string, unknown>; contexts?: Record<string, unknown> } = {}) {
  const pinia = createTestingPinia({
    createSpy: vi.fn,
    initialState: {
      actions: { items: [], loading: false, ...overrides.actions },
      contexts: { items: [], loading: false, ...overrides.contexts },
    },
  })

  const wrapper = mount(NextActionsView, {
    global: { plugins: [pinia], stubs },
  })

  const actions = useActionsStore()
  const contexts = useContextsStore()
  return { wrapper, actions, contexts }
}

describe('NextActionsView', () => {
  beforeEach(() => {
    resetIdCounter()
  })

  describe('loading state', () => {
    it('renders skeletons when actions.loading is true', async () => {
      const { wrapper } = mountView({ actions: { loading: true } })
      await flushPromises()

      const skeletons = wrapper.findAll('.skeleton-stub')
      expect(skeletons.length).toBe(5)
    })
  })

  describe('groups by context', () => {
    it('shows group headings for each context', async () => {
      const ctx = buildContext({ id: 'ctx-1', name: '@Computer' })
      const items = [
        buildAction({ description: 'Action 1', contexts: [ctx] }),
        buildAction({ description: 'Action 2', contexts: [ctx] }),
        buildAction({ description: 'Action 3', contexts: [] }),
      ]
      const { wrapper } = mountView({ actions: { items } })
      await flushPromises()

      const headings = wrapper.findAll('h3')
      expect(headings).toHaveLength(2)
      expect(headings[0]!.text()).toBe('@Computer')
      expect(headings[1]!.text()).toBe('No Context')
    })
  })

  describe('action count and context count text', () => {
    it('shows "3 actions across 2 contexts"', async () => {
      const ctx = buildContext({ id: 'ctx-1', name: '@Computer' })
      const items = [
        buildAction({ description: 'Action 1', contexts: [ctx] }),
        buildAction({ description: 'Action 2', contexts: [ctx] }),
        buildAction({ description: 'Action 3', contexts: [] }),
      ]
      const { wrapper } = mountView({ actions: { items } })
      await flushPromises()

      expect(wrapper.text()).toContain('3 actions across 2 contexts')
    })
  })

  describe('context filter', () => {
    it('filters actions when a context chip is clicked', async () => {
      const ctx = buildContext({ id: 'ctx-1', name: '@Computer' })
      const items = [
        buildAction({ description: 'Computer task', contexts: [ctx] }),
        buildAction({ description: 'No context task', contexts: [] }),
      ]
      const { wrapper } = mountView({
        actions: { items },
        contexts: { items: [ctx] },
      })
      await flushPromises()

      // Initially shows all actions
      let rows = wrapper.findAllComponents({ name: 'TaskRow' })
      expect(rows).toHaveLength(2)

      // Click the "@Computer" filter chip
      const buttons = wrapper.findAll('button')
      const computerBtn = buttons.find((b) => b.text() === '@Computer')
      expect(computerBtn).toBeTruthy()
      await computerBtn!.trigger('click')
      await flushPromises()

      // Now only shows the matching action
      rows = wrapper.findAllComponents({ name: 'TaskRow' })
      expect(rows).toHaveLength(1)
      expect(rows[0]!.props('title')).toBe('Computer task')
    })
  })

  describe('fetch on mount', () => {
    it('calls actions.fetch and contexts.fetch on mount', async () => {
      const { actions, contexts } = mountView()
      await flushPromises()

      expect(actions.fetch).toHaveBeenCalledOnce()
      expect(contexts.fetch).toHaveBeenCalledOnce()
    })
  })
})
