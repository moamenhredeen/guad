import { describe, it, expect, beforeAll, beforeEach, afterEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from './auth'

// --- helpers -----------------------------------------------------------

function makeJwt(payload: Record<string, unknown>): string {
  const header = btoa(JSON.stringify({ alg: 'RS256', typ: 'JWT' }))
  const body = btoa(JSON.stringify(payload))
  return `${header}.${body}.fake-signature`
}

const TEST_PAYLOAD = {
  sub: 'user-123',
  preferred_username: 'testuser',
  email: 'test@example.com',
}

const TEST_JWT = makeJwt(TEST_PAYLOAD)
const TEST_REFRESH = 'refresh-token-abc'

// --- crypto.subtle polyfill -------------------------------------------

beforeAll(async () => {
  if (!globalThis.crypto?.subtle) {
    // @ts-expect-error -- node:crypto not in client tsconfig
    const nodeCrypto = await import('node:crypto')
    const subtle = {
      digest: async (_algo: string, data: BufferSource) => {
        const buf = new Uint8Array(data as ArrayBuffer)
        return nodeCrypto.createHash('sha256').update(buf).digest().buffer
      },
    }
    Object.defineProperty(globalThis.crypto, 'subtle', { value: subtle, configurable: true })
  }
})

// --- window.location mock ---------------------------------------------

const originalLocation = window.location

afterEach(() => {
  Object.defineProperty(window, 'location', {
    value: originalLocation,
    writable: true,
    configurable: true,
  })
  localStorage.clear()
  sessionStorage.clear()
})

function mockWindowLocation(): { getHref: () => string } {
  let href = 'http://localhost:3000'
  Object.defineProperty(window, 'location', {
    value: {
      ...originalLocation,
      origin: 'http://localhost:3000',
      get href() { return href },
      set href(v: string) { href = v },
    },
    writable: true,
    configurable: true,
  })
  return { getHref: () => href }
}

// --- tests ------------------------------------------------------------

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    sessionStorage.clear()
  })

  // 1. Initial state
  it('has correct initial state', () => {
    const store = useAuthStore()
    expect(store.accessToken).toBeNull()
    expect(store.user).toBeNull()
    expect(store.isAuthenticated).toBe(false)
  })

  // 2. handleCallback success
  it('handleCallback success: sets tokens, extracts user, clears pkce_verifier', async () => {
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ access_token: TEST_JWT, refresh_token: TEST_REFRESH }),
    })
    vi.stubGlobal('fetch', mockFetch)

    sessionStorage.setItem('pkce_verifier', 'test-verifier')

    const store = useAuthStore()
    const result = await store.handleCallback('auth-code-123')

    expect(result).toBe(true)
    expect(store.accessToken).toBe(TEST_JWT)
    expect(store.user).toEqual({
      id: 'user-123',
      username: 'testuser',
      email: 'test@example.com',
    })
    expect(store.isAuthenticated).toBe(true)
    expect(localStorage.getItem('guad_refresh_token')).toBe(TEST_REFRESH)
    expect(sessionStorage.getItem('pkce_verifier')).toBeNull()
  })

  // 3. handleCallback missing verifier
  it('handleCallback returns false when pkce_verifier is missing', async () => {
    const mockFetch = vi.fn()
    vi.stubGlobal('fetch', mockFetch)

    const store = useAuthStore()
    const result = await store.handleCallback('auth-code-123')

    expect(result).toBe(false)
    expect(mockFetch).not.toHaveBeenCalled()
  })

  // 4. handleCallback API failure
  it('handleCallback returns false on API failure', async () => {
    const mockFetch = vi.fn().mockResolvedValue({ ok: false })
    vi.stubGlobal('fetch', mockFetch)

    sessionStorage.setItem('pkce_verifier', 'test-verifier')

    const store = useAuthStore()
    const result = await store.handleCallback('auth-code-123')

    expect(result).toBe(false)
    expect(store.accessToken).toBeNull()
    expect(store.user).toBeNull()
  })

  // 5. tryRefresh success
  it('tryRefresh success: sets new tokens', async () => {
    const newJwt = makeJwt({ sub: 'user-456', preferred_username: 'refreshed', email: 'r@e.com' })
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ access_token: newJwt, refresh_token: 'new-refresh' }),
    })
    vi.stubGlobal('fetch', mockFetch)

    // Seed the store with initial tokens via a successful handleCallback
    const seedFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ access_token: TEST_JWT, refresh_token: TEST_REFRESH }),
    })
    vi.stubGlobal('fetch', seedFetch)
    sessionStorage.setItem('pkce_verifier', 'v')
    const store = useAuthStore()
    await store.handleCallback('code')

    // Now stub fetch for the refresh call
    vi.stubGlobal('fetch', mockFetch)

    const result = await store.tryRefresh()

    expect(result).toBe(true)
    expect(store.accessToken).toBe(newJwt)
    expect(store.user).toEqual({ id: 'user-456', username: 'refreshed', email: 'r@e.com' })
    expect(localStorage.getItem('guad_refresh_token')).toBe('new-refresh')
  })

  // 6. tryRefresh from localStorage
  it('tryRefresh uses localStorage when refreshToken ref is null', async () => {
    const newJwt = makeJwt({ sub: 'user-789', preferred_username: 'ls-user', email: 'ls@e.com' })
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ access_token: newJwt, refresh_token: 'new-r' }),
    })
    vi.stubGlobal('fetch', mockFetch)

    localStorage.setItem('guad_refresh_token', 'stored-refresh')

    const store = useAuthStore()
    // refreshToken ref is null since we never called setTokens
    const result = await store.tryRefresh()

    expect(result).toBe(true)
    expect(store.accessToken).toBe(newJwt)

    // Verify fetch was called with the stored refresh token
    const body = mockFetch.mock.calls[0]![1].body as URLSearchParams
    expect(body.get('refresh_token')).toBe('stored-refresh')
  })

  // 7. tryRefresh no stored token
  it('tryRefresh returns false when no refresh token is available', async () => {
    const mockFetch = vi.fn()
    vi.stubGlobal('fetch', mockFetch)

    const store = useAuthStore()
    const result = await store.tryRefresh()

    expect(result).toBe(false)
    expect(mockFetch).not.toHaveBeenCalled()
  })

  // 8. tryRefresh API failure
  it('tryRefresh clears tokens on API failure', async () => {
    const mockFetch = vi.fn().mockResolvedValue({ ok: false })
    vi.stubGlobal('fetch', mockFetch)

    localStorage.setItem('guad_refresh_token', 'old-refresh')

    const store = useAuthStore()
    const result = await store.tryRefresh()

    expect(result).toBe(false)
    expect(store.accessToken).toBeNull()
    expect(store.user).toBeNull()
    expect(localStorage.getItem('guad_refresh_token')).toBeNull()
  })

  // 9. tryRefresh network error
  it('tryRefresh clears tokens on network error', async () => {
    const mockFetch = vi.fn().mockRejectedValue(new TypeError('Failed to fetch'))
    vi.stubGlobal('fetch', mockFetch)

    localStorage.setItem('guad_refresh_token', 'old-refresh')

    const store = useAuthStore()
    const result = await store.tryRefresh()

    expect(result).toBe(false)
    expect(store.accessToken).toBeNull()
    expect(store.user).toBeNull()
    expect(localStorage.getItem('guad_refresh_token')).toBeNull()
  })

  // 10. clearTokens
  it('clearTokens clears all refs, localStorage, and sessionStorage', async () => {
    // First, populate the store
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ access_token: TEST_JWT, refresh_token: TEST_REFRESH }),
    })
    vi.stubGlobal('fetch', mockFetch)
    sessionStorage.setItem('pkce_verifier', 'v')

    const store = useAuthStore()
    await store.handleCallback('code')

    // Verify populated
    expect(store.accessToken).toBe(TEST_JWT)

    store.clearTokens()

    expect(store.accessToken).toBeNull()
    expect(store.user).toBeNull()
    expect(store.isAuthenticated).toBe(false)
    expect(localStorage.getItem('guad_refresh_token')).toBeNull()
    expect(sessionStorage.getItem('pkce_verifier')).toBeNull()
  })

  // 11. logout
  it('logout clears tokens and redirects to keycloak logout URL', async () => {
    const { getHref } = mockWindowLocation()

    // Populate store first
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ access_token: TEST_JWT, refresh_token: TEST_REFRESH }),
    })
    vi.stubGlobal('fetch', mockFetch)
    sessionStorage.setItem('pkce_verifier', 'v')

    const store = useAuthStore()
    await store.handleCallback('code')

    const tokenBeforeLogout = store.accessToken

    store.logout()

    // Tokens cleared
    expect(store.accessToken).toBeNull()
    expect(store.user).toBeNull()

    // Redirect URL
    const url = new URL(getHref())
    expect(url.pathname).toBe('/realms/master/protocol/openid-connect/logout')
    expect(url.searchParams.get('client_id')).toBe('guad-web')
    expect(url.searchParams.get('post_logout_redirect_uri')).toBe('http://localhost:3000/login')
    expect(url.searchParams.get('id_token_hint')).toBe(tokenBeforeLogout)
  })
})
