import { describe, it, expect, beforeEach, vi } from 'vitest'
import { ApiClient, type ApiError } from '../client'

describe('ApiClient', () => {
  let client: ApiClient
  const mockFetch = vi.fn()

  beforeEach(() => {
    global.fetch = mockFetch
    mockFetch.mockClear()
    localStorage.clear()
    client = new ApiClient('http://api.test')
  })

  describe('Token Management', () => {
    it('should set and get token', () => {
      client.setToken('test-token')
      expect(client.getToken()).toBe('test-token')
      expect(localStorage.getItem('admin_token')).toBe('test-token')
    })

    it('should remove token when set to null', () => {
      client.setToken('test-token')
      client.setToken(null)
      expect(client.getToken()).toBeNull()
      expect(localStorage.getItem('admin_token')).toBeNull()
    })

    it('should retrieve token from localStorage', () => {
      localStorage.setItem('admin_token', 'stored-token')
      expect(client.getToken()).toBe('stored-token')
    })
  })

  describe('GET requests', () => {
    it('should make GET request successfully', async () => {
      const mockData = { id: '1', name: 'Test' }
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockData,
        headers: new Headers({ 'content-type': 'application/json' }),
      })

      const result = await client.get('/test')

      expect(mockFetch).toHaveBeenCalledWith(
        'http://api.test/test',
        expect.objectContaining({ method: 'GET' })
      )
      expect(result).toEqual(mockData)
    })

    it('should include query parameters', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({}),
        headers: new Headers({ 'content-type': 'application/json' }),
      })

      await client.get('/test', { page: 1, limit: 10 })

      expect(mockFetch).toHaveBeenCalledWith(
        'http://api.test/test?page=1&limit=10',
        expect.any(Object)
      )
    })

    it('should include authorization header when token is set', async () => {
      client.setToken('test-token')
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({}),
        headers: new Headers({ 'content-type': 'application/json' }),
      })

      await client.get('/test')

      expect(mockFetch).toHaveBeenCalledWith(
        'http://api.test/test',
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer test-token',
          }),
        })
      )
    })
  })

  describe('POST requests', () => {
    it('should make POST request with data', async () => {
      const requestData = { name: 'Test' }
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: '1', ...requestData }),
        headers: new Headers({ 'content-type': 'application/json' }),
      })

      const result = await client.post('/test', requestData)

      expect(mockFetch).toHaveBeenCalledWith(
        'http://api.test/test',
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify(requestData),
        })
      )
      expect(result).toEqual({ id: '1', ...requestData })
    })
  })

  describe('Error Handling', () => {
    it('should throw ApiError on non-ok response', async () => {
      const errorResponse = { message: 'Resource not found' }
      const headers = new Headers({ 'content-type': 'application/json' })
      const mockResponse = {
        ok: false,
        status: 404,
        statusText: 'Not Found',
        json: async () => errorResponse,
        headers,
        get: (key: string) => headers.get(key),
      }
      mockFetch.mockResolvedValueOnce(mockResponse)

      await expect(client.get('/test')).rejects.toMatchObject({
        message: 'Resource not found',
        status: 404,
      })
    })

    it('should handle network errors', async () => {
      mockFetch.mockRejectedValueOnce(new Error('Network error'))

      await expect(client.get('/test')).rejects.toMatchObject({
        message: 'Network error',
        status: 0,
      })
    })

    it('should handle non-JSON error responses', async () => {
      // When content-type is not JSON, json() should not be called
      const headers = new Headers({ 'content-type': 'text/html' })
      const mockResponse = {
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
        headers,
        get: (key: string) => headers.get(key),
      }
      mockFetch.mockResolvedValueOnce(mockResponse)

      await expect(client.get('/test')).rejects.toMatchObject({
        message: 'Internal Server Error',
        status: 500,
      })
    })

    it('should handle empty responses', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        headers: new Headers(),
      })

      const result = await client.delete('/test')
      expect(result).toEqual({})
    })
  })
})

