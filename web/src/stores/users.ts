import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { usersService, type User, type UserStats, type UsersListParams, type PaginatedResponse } from '@/services/api'

export const useUsersStore = defineStore('users', () => {
  const users = ref<User[]>([])
  const selectedUser = ref<User | null>(null)
  const userStats = ref<UserStats | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    page: 1,
    limit: 20,
    total: 0,
    total_pages: 0,
  })

  const hasMore = computed(() => pagination.value.page < pagination.value.total_pages)

  async function fetchUsers(params?: UsersListParams) {
    isLoading.value = true
    error.value = null
    try {
      const response = await usersService.getUsers({
        page: pagination.value.page,
        limit: pagination.value.limit,
        ...params,
      })
      users.value = response.data
      pagination.value = {
        page: response.page,
        limit: response.limit,
        total: response.total,
        total_pages: response.total_pages,
      }
      return response
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch users'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchUser(id: string) {
    isLoading.value = true
    error.value = null
    try {
      selectedUser.value = await usersService.getUser(id)
      return selectedUser.value
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch user'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchUserStats(id: string) {
    isLoading.value = true
    error.value = null
    try {
      userStats.value = await usersService.getUserStats(id)
      return userStats.value
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch user stats'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function updateUser(id: string, data: Partial<User>) {
    isLoading.value = true
    error.value = null
    try {
      const updated = await usersService.updateUser(id, data)
      // Update in list if present
      const index = users.value.findIndex(u => u.id === id)
      if (index !== -1) {
        users.value[index] = updated
      }
      // Update selected if it's the same user
      if (selectedUser.value?.id === id) {
        selectedUser.value = updated
      }
      return updated
    } catch (err: any) {
      error.value = err.message || 'Failed to update user'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function deactivateUser(id: string) {
    isLoading.value = true
    error.value = null
    try {
      await usersService.deactivateUser(id)
      await fetchUser(id) // Refresh user data
    } catch (err: any) {
      error.value = err.message || 'Failed to deactivate user'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function activateUser(id: string) {
    isLoading.value = true
    error.value = null
    try {
      await usersService.activateUser(id)
      await fetchUser(id) // Refresh user data
    } catch (err: any) {
      error.value = err.message || 'Failed to activate user'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function setSelectedUser(user: User | null) {
    selectedUser.value = user
  }

  function clearError() {
    error.value = null
  }

  return {
    users,
    selectedUser,
    userStats,
    isLoading,
    error,
    pagination,
    hasMore,
    fetchUsers,
    fetchUser,
    fetchUserStats,
    updateUser,
    deactivateUser,
    activateUser,
    setSelectedUser,
    clearError,
  }
})

