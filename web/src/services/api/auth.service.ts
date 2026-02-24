import { apiClient } from './client'

export interface AdminLoginRequest {
  email: string
  password: string
}

export interface AdminLoginResponse {
  token: string
  admin: Admin
}

export interface Admin {
  id: string
  email: string
  name: string
  created_at: string
}

export const authService = {
  async login(credentials: AdminLoginRequest): Promise<AdminLoginResponse> {
    const response = await apiClient.post<AdminLoginResponse>(
      '/admin/auth/login',
      credentials
    )
    apiClient.setToken(response.token)
    return response
  },

  async logout(): Promise<void> {
    apiClient.setToken(null)
  },

  async getCurrentAdmin(): Promise<Admin> {
    return apiClient.get<Admin>('/admin/auth/me')
  },

  async verifyOTP(otp: string): Promise<AdminLoginResponse> {
    const response = await apiClient.post<AdminLoginResponse>(
      '/admin/auth/verify-otp',
      { otp }
    )
    apiClient.setToken(response.token)
    return response
  },
}

