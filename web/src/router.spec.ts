import { describe, it, expect, vi, beforeEach } from 'vitest'

// Shared mock state — reset in beforeEach
let authState = { isAuthenticated: false, tryRefresh: vi.fn() }

vi.mock('@/stores/auth', () => ({
  useAuthStore: () => authState,
}))

vi.mock('@/components/SidebarLayout.vue', () => ({ default: { template: '<div><router-view /></div>' } }))
vi.mock('@/views/LoginView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/CallbackView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/InboxView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/NextActionsView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/ProjectsView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/ProjectDetailView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/WaitingForView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/SomedayMaybeView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/WeeklyReviewView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/ContextsView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/AreasView.vue', () => ({ default: { template: '<div />' } }))
vi.mock('@/views/SettingsView.vue', () => ({ default: { template: '<div />' } }))

import router from '@/router'

describe('router auth guard', () => {
  beforeEach(async () => {
    authState = { isAuthenticated: false, tryRefresh: vi.fn() }
    // Start each test from the login page (public) so we have a known state
    authState.isAuthenticated = true
    await router.push('/login')
    authState.isAuthenticated = false
    authState.tryRefresh = vi.fn()
  })

  it('allows navigation to public route /login without auth', async () => {
    await router.push('/login')
    expect(router.currentRoute.value.name).toBe('login')
  })

  it('allows navigation to public route /callback without auth', async () => {
    await router.push('/callback')
    expect(router.currentRoute.value.name).toBe('callback')
  })

  it('redirects unauthenticated user to login from protected route', async () => {
    authState.tryRefresh.mockResolvedValue(false)
    await router.push('/inbox')
    expect(router.currentRoute.value.name).toBe('login')
  })

  it('calls tryRefresh when user is not authenticated', async () => {
    authState.tryRefresh.mockResolvedValue(false)
    await router.push('/inbox')
    expect(authState.tryRefresh).toHaveBeenCalledOnce()
  })

  it('allows navigation when tryRefresh succeeds', async () => {
    authState.tryRefresh.mockResolvedValue(true)
    await router.push('/inbox')
    expect(router.currentRoute.value.name).toBe('inbox')
  })

  it('allows authenticated user to access protected route', async () => {
    authState.isAuthenticated = true
    await router.push('/inbox')
    expect(router.currentRoute.value.name).toBe('inbox')
    expect(authState.tryRefresh).not.toHaveBeenCalled()
  })

  it('redirects / to /inbox (with auth)', async () => {
    authState.isAuthenticated = true
    await router.push('/')
    expect(router.currentRoute.value.name).toBe('inbox')
  })
})
