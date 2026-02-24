import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../auth'
import { authService } from '@/services/api'

vi.mock('@/services/api', () => ({
  authService: {
    login: vi.fn(),
    verifyOTP: vi.fn(),
    getCurrentAdmin: vi.fn(),
    logout: vi.fn(),
  },
  apiClient: {
    setToken: vi.fn(),
  },
}))

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('initial state', () => {
    it('should have initial state', () => {
      const store = useAuthStore()

      expect(store.admin).toBeNull()
      expect(store.token).toBeNull()
      expect(store.isLoading).toBe(false)
      expect(store.error).toBeNull()
      expect(store.isAuthenticated).toBe(false)
    })
  })

  describe('login', () => {
    it('should login successfully', async () => {
      const store = useAuthStore()
      const mockResponse = {
        token: 'test-token',
        admin: {
          id: '1',
          email: 'admin@test.com',
          name: 'Admin',
          created_at: '2024-01-01',
        },
      }

      vi.mocked(authService.login).mockResolvedValueOnce(mockResponse)

      await store.login('admin@test.com', 'password')

      expect(store.token).toBe('test-token')
      expect(store.admin).toEqual(mockResponse.admin)
      expect(store.isAuthenticated).toBe(true)
      expect(store.error).toBeNull()
    })

    it('should handle login error', async () => {
      const store = useAuthStore()
      const error = { message: 'Invalid credentials', status: 401 }

      vi.mocked(authService.login).mockRejectedValueOnce(error)

      await expect(store.login('admin@test.com', 'wrong')).rejects.toEqual(error)
      expect(store.error).toBe('Invalid credentials')
      expect(store.isAuthenticated).toBe(false)
    })
  })

  describe('verifyOTP', () => {
    it('should verify OTP successfully', async () => {
      const store = useAuthStore()
      const mockResponse = {
        token: 'test-token',
        admin: {
          id: '1',
          email: 'admin@test.com',
          name: 'Admin',
          created_at: '2024-01-01',
        },
      }

      vi.mocked(authService.verifyOTP).mockResolvedValueOnce(mockResponse)

      await store.verifyOTP('123456')

      expect(store.token).toBe('test-token')
      expect(store.admin).toEqual(mockResponse.admin)
      expect(store.isAuthenticated).toBe(true)
    })
  })

  describe('fetchCurrentAdmin', () => {
    it('should fetch current admin', async () => {
      const store = useAuthStore()
      const mockAdmin = {
        id: '1',
        email: 'admin@test.com',
        name: 'Admin',
        created_at: '2024-01-01',
      }

      vi.mocked(authService.getCurrentAdmin).mockResolvedValueOnce(mockAdmin)

      await store.fetchCurrentAdmin()

      expect(store.admin).toEqual(mockAdmin)
    })
  })

  describe('logout', () => {
    it('should logout and clear state', async () => {
      const store = useAuthStore()
      store.token = 'test-token'
      store.admin = {
        id: '1',
        email: 'admin@test.com',
        name: 'Admin',
        created_at: '2024-01-01',
      }

      await store.logout()

      expect(store.token).toBeNull()
      expect(store.admin).toBeNull()
      expect(store.isAuthenticated).toBe(false)
    })
  })

  describe('clearError', () => {
    it('should clear error', () => {
      const store = useAuthStore()
      store.error = 'Some error'

      store.clearError()

      expect(store.error).toBeNull()
    })
  })
})

