import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types'
import { setTokenProvider, setUnauthorizedHandler } from '@/api/client'

const KEYCLOAK_URL = import.meta.env.VITE_KEYCLOAK_URL ?? 'http://localhost:8081/realms/master'
const CLIENT_ID = import.meta.env.VITE_KEYCLOAK_CLIENT_ID ?? 'guad-web'
const REDIRECT_URI = import.meta.env.VITE_KEYCLOAK_REDIRECT_URI ?? `${window.location.origin}/callback`

function generateCodeVerifier(): string {
  const array = new Uint8Array(32)
  crypto.getRandomValues(array)
  return btoa(String.fromCharCode(...array))
    .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

async function generateCodeChallenge(verifier: string): Promise<string> {
  const encoder = new TextEncoder()
  const data = encoder.encode(verifier)
  const digest = await crypto.subtle.digest('SHA-256', data)
  return btoa(String.fromCharCode(...new Uint8Array(digest)))
    .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

function parseJwtPayload(token: string): Record<string, unknown> {
  const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
  return JSON.parse(atob(base64))
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const user = ref<UserInfo | null>(null)
  const isAuthenticated = computed(() => !!accessToken.value)

  // Wire up API client
  setTokenProvider(() => accessToken.value)
  setUnauthorizedHandler(() => login())

  function extractUser(token: string): UserInfo {
    const payload = parseJwtPayload(token)
    return {
      id: payload.sub as string,
      username: (payload.preferred_username as string) ?? '',
      email: (payload.email as string) ?? '',
    }
  }

  function setTokens(access: string, refresh: string | null) {
    accessToken.value = access
    refreshToken.value = refresh
    user.value = extractUser(access)
    if (refresh) {
      localStorage.setItem('guad_refresh_token', refresh)
    }
  }

  function clearTokens() {
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    localStorage.removeItem('guad_refresh_token')
    sessionStorage.removeItem('pkce_verifier')
  }

  async function login() {
    const verifier = generateCodeVerifier()
    const challenge = await generateCodeChallenge(verifier)
    sessionStorage.setItem('pkce_verifier', verifier)

    const params = new URLSearchParams({
      client_id: CLIENT_ID,
      response_type: 'code',
      scope: 'openid profile email',
      redirect_uri: REDIRECT_URI,
      code_challenge: challenge,
      code_challenge_method: 'S256',
    })

    window.location.href = `${KEYCLOAK_URL}/protocol/openid-connect/auth?${params}`
  }

  async function handleCallback(code: string): Promise<boolean> {
    const verifier = sessionStorage.getItem('pkce_verifier')
    if (!verifier) return false

    const response = await fetch(`${KEYCLOAK_URL}/protocol/openid-connect/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        grant_type: 'authorization_code',
        client_id: CLIENT_ID,
        code,
        redirect_uri: REDIRECT_URI,
        code_verifier: verifier,
      }),
    })

    if (!response.ok) return false

    const data = await response.json()
    setTokens(data.access_token, data.refresh_token ?? null)
    sessionStorage.removeItem('pkce_verifier')
    return true
  }

  async function tryRefresh(): Promise<boolean> {
    const stored = refreshToken.value ?? localStorage.getItem('guad_refresh_token')
    if (!stored) return false

    try {
      const response = await fetch(`${KEYCLOAK_URL}/protocol/openid-connect/token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
          grant_type: 'refresh_token',
          client_id: CLIENT_ID,
          refresh_token: stored,
        }),
      })

      if (!response.ok) {
        clearTokens()
        return false
      }

      const data = await response.json()
      setTokens(data.access_token, data.refresh_token ?? null)
      return true
    } catch {
      clearTokens()
      return false
    }
  }

  function logout() {
    const idToken = accessToken.value
    clearTokens()
    const params = new URLSearchParams({
      client_id: CLIENT_ID,
      post_logout_redirect_uri: `${window.location.origin}/login`,
    })
    if (idToken) params.set('id_token_hint', idToken)
    window.location.href = `${KEYCLOAK_URL}/protocol/openid-connect/logout?${params}`
  }

  return {
    accessToken,
    user,
    isAuthenticated,
    login,
    handleCallback,
    tryRefresh,
    logout,
    clearTokens,
  }
})
