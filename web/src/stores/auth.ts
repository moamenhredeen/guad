import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { authService, type Admin, apiClient } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const admin = ref<Admin | null>(null)
  const token = ref<string | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value && !!admin.value)

  async function login(email: string, password: string) {
    isLoading.value = true
    error.value = null
    try {
      const response = await authService.login({ email, password })
      token.value = response.token
      admin.value = response.admin
      apiClient.setToken(response.token) // Sync with API client
      return response
    } catch (err: any) {
      error.value = err.message || 'Login failed'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function verifyOTP(otp: string) {
    isLoading.value = true
    error.value = null
    try {
      const response = await authService.verifyOTP(otp)
      token.value = response.token
      admin.value = response.admin
      apiClient.setToken(response.token) // Sync with API client
      return response
    } catch (err: any) {
      error.value = err.message || 'OTP verification failed'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchCurrentAdmin() {
    isLoading.value = true
    error.value = null
    try {
      admin.value = await authService.getCurrentAdmin()
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch admin info'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function logout() {
    await authService.logout()
    admin.value = null
    token.value = null
    error.value = null
    apiClient.setToken(null) // Clear token from API client
  }

  function clearError() {
    error.value = null
  }

  return {
    admin,
    token,
    isLoading,
    error,
    isAuthenticated,
    login,
    verifyOTP,
    fetchCurrentAdmin,
    logout,
    clearError,
  }
})

