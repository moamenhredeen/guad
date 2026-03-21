import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import ProjectsView from './ProjectsView.vue'
import { useProjectsStore } from '@/stores/projects'
import { buildProject, resetIdCounter } from '@/test/factories'

const stubs = {
  RouterLink: { template: '<a><slot /></a>', props: ['to'] },
  ChevronRight: { template: '<span />' },
  Skeleton: { template: '<div class="skeleton-stub" />' },
}

function mountView(storeOverrides: Record<string, unknown> = {}) {
  const pinia = createTestingPinia({
    createSpy: vi.fn,
    initialState: {
      projects: { items: [], loading: false, detail: null, ...storeOverrides },
    },
  })

  const wrapper = mount(ProjectsView, {
    global: { plugins: [pinia], stubs },
  })

  const projects = useProjectsStore()
  return { wrapper, projects }
}

describe('ProjectsView', () => {
  beforeEach(() => {
    resetIdCounter()
  })

  describe('loading state', () => {
    it('shows skeletons when loading', async () => {
      const { wrapper } = mountView({ loading: true })
      await flushPromises()

      const skeletons = wrapper.findAll('.skeleton-stub')
      expect(skeletons.length).toBe(5)
    })
  })

  describe('project count', () => {
    it('shows "N active projects" text', async () => {
      const items = [buildProject(), buildProject()]
      const { wrapper } = mountView({ items })
      await flushPromises()

      expect(wrapper.text()).toContain('2 active projects')
    })
  })

  describe('groups by area', () => {
    it('renders group headings for each area', async () => {
      const items = [
        buildProject({ name: 'Project A', areaName: 'Work' }),
        buildProject({ name: 'Project B', areaName: 'Work' }),
        buildProject({ name: 'Project C', areaName: null }),
      ]
      const { wrapper } = mountView({ items })
      await flushPromises()

      const headings = wrapper.findAll('h3')
      expect(headings).toHaveLength(2)
      expect(headings[0]!.text()).toBe('Work')
      expect(headings[1]!.text()).toBe('No Area')
    })
  })

  describe('next action count pluralization', () => {
    it('shows "1 next action" for singular', async () => {
      const items = [buildProject({ nextActionCount: 1 })]
      const { wrapper } = mountView({ items })
      await flushPromises()

      expect(wrapper.text()).toContain('1 next action')
      expect(wrapper.text()).not.toContain('1 next actions')
    })

    it('shows "3 next actions" for plural', async () => {
      const items = [buildProject({ nextActionCount: 3 })]
      const { wrapper } = mountView({ items })
      await flushPromises()

      expect(wrapper.text()).toContain('3 next actions')
    })
  })

  describe('fetch on mount', () => {
    it('calls projects.fetch on mount', async () => {
      const { projects } = mountView()
      await flushPromises()

      expect(projects.fetch).toHaveBeenCalledOnce()
    })
  })
})
