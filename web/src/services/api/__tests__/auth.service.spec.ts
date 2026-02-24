import { describe, it, expect, beforeEach, vi } from 'vitest'
import { authService } from '../auth.service'
import { apiClient } from '../client'

vi.mock('../client', () => ({
  apiClient: {
    post: vi.fn(),
    get: vi.fn(),
    setToken: vi.fn(),
  },
}))

describe('authService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('login', () => {
    it('should login successfully and set token', async () => {
      const mockResponse = {
        token: 'test-token',
        admin: {
          id: '1',
          email: 'admin@test.com',
          name: 'Admin User',
          created_at: '2024-01-01',
        },
      }

      vi.mocked(apiClient.post).mockResolvedValueOnce(mockResponse)

      const result = await authService.login({
        email: 'admin@test.com',
        password: 'password',
      })

      expect(apiClient.post).toHaveBeenCalledWith('/admin/auth/login', {
        email: 'admin@test.com',
        password: 'password',
      })
      expect(apiClient.setToken).toHaveBeenCalledWith('test-token')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('verifyOTP', () => {
    it('should verify OTP successfully and set token', async () => {
      const mockResponse = {
        token: 'test-token',
        admin: {
          id: '1',
          email: 'admin@test.com',
          name: 'Admin User',
          created_at: '2024-01-01',
        },
      }

      vi.mocked(apiClient.post).mockResolvedValueOnce(mockResponse)

      const result = await authService.verifyOTP('123456')

      expect(apiClient.post).toHaveBeenCalledWith('/admin/auth/verify-otp', {
        otp: '123456',
      })
      expect(apiClient.setToken).toHaveBeenCalledWith('test-token')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getCurrentAdmin', () => {
    it('should fetch current admin', async () => {
      const mockAdmin = {
        id: '1',
        email: 'admin@test.com',
        name: 'Admin User',
        created_at: '2024-01-01',
      }

      vi.mocked(apiClient.get).mockResolvedValueOnce(mockAdmin)

      const result = await authService.getCurrentAdmin()

      expect(apiClient.get).toHaveBeenCalledWith('/admin/auth/me')
      expect(result).toEqual(mockAdmin)
    })
  })

  describe('logout', () => {
    it('should clear token', async () => {
      await authService.logout()

      expect(apiClient.setToken).toHaveBeenCalledWith(null)
    })
  })
})

