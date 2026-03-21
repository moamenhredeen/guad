import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import InboxView from './InboxView.vue'
import { useInboxStore } from '@/stores/inbox'
import { buildInboxItem, resetIdCounter } from '@/test/factories'

import { defineComponent } from 'vue'

const TaskRowStub = defineComponent({
  name: 'TaskRow',
  props: ['title', 'meta', 'selected', 'dueDate'],
  template: '<div class="task-row-stub"><slot /></div>',
})

const stubs = {
  TaskRow: TaskRowStub,
  InlineAddTask: { template: '<div />' },
  TriageBar: { template: '<div />' },
  Skeleton: { template: '<div class="skeleton-stub" />' },
}

function mountView(storeOverrides: Record<string, unknown> = {}) {
  const pinia = createTestingPinia({
    createSpy: vi.fn,
    initialState: {
      inbox: { items: [], loading: false, selectedId: null, ...storeOverrides },
    },
  })

  const wrapper = mount(InboxView, {
    global: { plugins: [pinia], stubs },
  })

  const inbox = useInboxStore()
  return { wrapper, inbox }
}

describe('InboxView', () => {
  beforeEach(() => {
    resetIdCounter()
  })

  describe('loading state', () => {
    it('renders Skeleton stubs when loading', async () => {
      const { wrapper } = mountView({ loading: true })
      await flushPromises()

      const skeletons = wrapper.findAll('.skeleton-stub')
      expect(skeletons.length).toBe(5)
    })
  })

  describe('empty state', () => {
    it('renders "Alles guad!" when items is empty and not loading', async () => {
      const { wrapper } = mountView({ items: [], loading: false })
      await flushPromises()

      expect(wrapper.text()).toContain('Alles guad!')
      expect(wrapper.text()).toContain('Nix zum schaffe')
    })
  })

  describe('list state', () => {
    it('renders TaskRow stubs with correct props', async () => {
      vi.useFakeTimers()
      vi.setSystemTime(new Date('2026-03-22T10:00:00Z'))

      const items = [
        buildInboxItem({ title: 'First task', createdAt: '2026-03-20T10:00:00Z' }),
        buildInboxItem({ title: 'Second task', createdAt: '2026-03-21T10:00:00Z' }),
      ]
      const { wrapper } = mountView({ items })
      await flushPromises()

      const rows = wrapper.findAllComponents({ name: 'TaskRow' })
      expect(rows).toHaveLength(2)
      expect(rows[0]!.props('title')).toBe('First task')
      expect(rows[0]!.props('meta')).toBe('Added 2 days ago')
      expect(rows[1]!.props('title')).toBe('Second task')
      expect(rows[1]!.props('meta')).toBe('Added yesterday')

      vi.useRealTimers()
    })
  })

  describe('item count', () => {
    it('shows "N items to process" text', async () => {
      const items = [buildInboxItem(), buildInboxItem(), buildInboxItem()]
      const { wrapper } = mountView({ items })
      await flushPromises()

      expect(wrapper.text()).toContain('3 items to process')
    })
  })

  describe('formatAge', () => {
    afterEach(() => {
      vi.useRealTimers()
    })

    it('shows "Added 2 days ago" for item created 2 days ago', async () => {
      vi.useFakeTimers()
      vi.setSystemTime(new Date('2026-03-22T10:00:00Z'))

      const items = [buildInboxItem({ createdAt: '2026-03-20T10:00:00Z' })]
      const { wrapper } = mountView({ items })
      await flushPromises()

      const row = wrapper.findComponent({ name: 'TaskRow' })
      expect(row.props('meta')).toBe('Added 2 days ago')
    })

    it('shows "Added today" for item created today', async () => {
      vi.useFakeTimers()
      vi.setSystemTime(new Date('2026-03-22T10:00:00Z'))

      const items = [buildInboxItem({ createdAt: '2026-03-22T08:00:00Z' })]
      const { wrapper } = mountView({ items })
      await flushPromises()

      const row = wrapper.findComponent({ name: 'TaskRow' })
      expect(row.props('meta')).toBe('Added today')
    })

    it('shows "Added yesterday" for item created yesterday', async () => {
      vi.useFakeTimers()
      vi.setSystemTime(new Date('2026-03-22T10:00:00Z'))

      const items = [buildInboxItem({ createdAt: '2026-03-21T10:00:00Z' })]
      const { wrapper } = mountView({ items })
      await flushPromises()

      const row = wrapper.findComponent({ name: 'TaskRow' })
      expect(row.props('meta')).toBe('Added yesterday')
    })
  })

  describe('fetch on mount', () => {
    it('calls inbox.fetch on mount', async () => {
      const { inbox } = mountView()
      await flushPromises()

      expect(inbox.fetch).toHaveBeenCalledOnce()
    })
  })
})
