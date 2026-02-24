import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUsersStore } from '../users'
import { usersService } from '@/services/api'

vi.mock('@/services/api', () => ({
  usersService: {
    getUsers: vi.fn(),
    getUser: vi.fn(),
    getUserStats: vi.fn(),
    updateUser: vi.fn(),
    deactivateUser: vi.fn(),
    activateUser: vi.fn(),
  },
}))

describe('useUsersStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('initial state', () => {
    it('should have initial state', () => {
      const store = useUsersStore()

      expect(store.users).toEqual([])
      expect(store.selectedUser).toBeNull()
      expect(store.userStats).toBeNull()
      expect(store.isLoading).toBe(false)
      expect(store.error).toBeNull()
      expect(store.pagination.page).toBe(1)
    })
  })

  describe('fetchUsers', () => {
    it('should fetch users successfully', async () => {
      const store = useUsersStore()
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

      vi.mocked(usersService.getUsers).mockResolvedValueOnce(mockResponse)

      await store.fetchUsers()

      expect(store.users).toEqual(mockResponse.data)
      expect(store.pagination.total).toBe(2)
      expect(store.pagination.page).toBe(1)
      expect(store.error).toBeNull()
    })

    it('should handle fetch error', async () => {
      const store = useUsersStore()
      const error = { message: 'Failed to fetch', status: 500 }

      vi.mocked(usersService.getUsers).mockRejectedValueOnce(error)

      await expect(store.fetchUsers()).rejects.toEqual(error)
      expect(store.error).toBe('Failed to fetch')
    })
  })

  describe('fetchUser', () => {
    it('should fetch single user', async () => {
      const store = useUsersStore()
      const mockUser = {
        id: '1',
        email: 'user@test.com',
        name: 'Test User',
        created_date: '2024-01-01',
      }

      vi.mocked(usersService.getUser).mockResolvedValueOnce(mockUser)

      await store.fetchUser('1')

      expect(store.selectedUser).toEqual(mockUser)
    })
  })

  describe('updateUser', () => {
    it('should update user in list', async () => {
      const store = useUsersStore()
      store.users = [
        { id: '1', email: 'user@test.com', name: 'Old Name', created_date: '2024-01-01' },
      ]

      const updatedUser = {
        id: '1',
        email: 'user@test.com',
        name: 'New Name',
        created_date: '2024-01-01',
      }

      vi.mocked(usersService.updateUser).mockResolvedValueOnce(updatedUser)

      await store.updateUser('1', { name: 'New Name' })

      expect(store.users[0]?.name).toBe('New Name')
    })

    it('should update selected user if it matches', async () => {
      const store = useUsersStore()
      const user = {
        id: '1',
        email: 'user@test.com',
        name: 'Old Name',
        created_date: '2024-01-01',
      }
      store.selectedUser = user

      const updatedUser = { ...user, name: 'New Name' }
      vi.mocked(usersService.updateUser).mockResolvedValueOnce(updatedUser)

      await store.updateUser('1', { name: 'New Name' })

      expect(store.selectedUser?.name).toBe('New Name')
    })
  })

  describe('hasMore', () => {
    it('should return true when more pages available', () => {
      const store = useUsersStore()
      store.pagination = {
        page: 1,
        limit: 20,
        total: 100,
        total_pages: 5,
      }

      expect(store.hasMore).toBe(true)
    })

    it('should return false when on last page', () => {
      const store = useUsersStore()
      store.pagination = {
        page: 5,
        limit: 20,
        total: 100,
        total_pages: 5,
      }

      expect(store.hasMore).toBe(false)
    })
  })
})

