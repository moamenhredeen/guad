import { describe, it, expect, beforeEach, vi } from 'vitest'
import { usersService } from '../users.service'
import { apiClient } from '../client'

vi.mock('../client', () => ({
  apiClient: {
    get: vi.fn(),
    patch: vi.fn(),
  },
}))

describe('usersService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getUsers', () => {
    it('should fetch users with pagination', async () => {
      const mockResponse = {
        data: [
          { id: '1', email: 'user1@test.com', name: 'User 1', created_date: '2024-01-01' },
          { id: '2', email: 'user2@test.com', name: 'User 2', created_date: '2024-01-02' },
        ],
        total: 2,
        page: 1,
        limit: 20,
        total_pages: 1,
      }

      vi.mocked(apiClient.get).mockResolvedValueOnce(mockResponse)

      const result = await usersService.getUsers({ page: 1, limit: 20 })

      expect(apiClient.get).toHaveBeenCalledWith('/admin/users', { page: 1, limit: 20 })
      expect(result).toEqual(mockResponse)
    })

    it('should handle search parameter', async () => {
      const mockResponse = {
        data: [],
        total: 0,
        page: 1,
        limit: 20,
        total_pages: 0,
      }

      vi.mocked(apiClient.get).mockResolvedValueOnce(mockResponse)

      await usersService.getUsers({ search: 'test' })

      expect(apiClient.get).toHaveBeenCalledWith('/admin/users', { search: 'test' })
    })
  })

  describe('getUser', () => {
    it('should fetch single user', async () => {
      const mockUser = {
        id: '1',
        email: 'user@test.com',
        name: 'Test User',
        created_date: '2024-01-01',
      }

      vi.mocked(apiClient.get).mockResolvedValueOnce(mockUser)

      const result = await usersService.getUser('1')

      expect(apiClient.get).toHaveBeenCalledWith('/admin/users/1')
      expect(result).toEqual(mockUser)
    })
  })

  describe('getUserStats', () => {
    it('should fetch user statistics', async () => {
      const mockStats = {
        total_actions: 10,
        total_projects: 5,
        total_inbox_items: 20,
        active_projects: 3,
        completed_actions: 7,
      }

      vi.mocked(apiClient.get).mockResolvedValueOnce(mockStats)

      const result = await usersService.getUserStats('1')

      expect(apiClient.get).toHaveBeenCalledWith('/admin/users/1/stats')
      expect(result).toEqual(mockStats)
    })
  })

  describe('updateUser', () => {
    it('should update user', async () => {
      const updateData = { name: 'Updated Name' }
      const mockUser = {
        id: '1',
        email: 'user@test.com',
        name: 'Updated Name',
        created_date: '2024-01-01',
      }

      vi.mocked(apiClient.patch).mockResolvedValueOnce(mockUser)

      const result = await usersService.updateUser('1', updateData)

      expect(apiClient.patch).toHaveBeenCalledWith('/admin/users/1', updateData)
      expect(result).toEqual(mockUser)
    })
  })

  describe('deactivateUser', () => {
    it('should deactivate user', async () => {
      vi.mocked(apiClient.patch).mockResolvedValueOnce(undefined)

      await usersService.deactivateUser('1')

      expect(apiClient.patch).toHaveBeenCalledWith('/admin/users/1/deactivate', {})
    })
  })

  describe('activateUser', () => {
    it('should activate user', async () => {
      vi.mocked(apiClient.patch).mockResolvedValueOnce(undefined)

      await usersService.activateUser('1')

      expect(apiClient.patch).toHaveBeenCalledWith('/admin/users/1/activate', {})
    })
  })
})

