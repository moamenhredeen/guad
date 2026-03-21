import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { api, ApiError, setTokenProvider, setUnauthorizedHandler } from '@/api/client'

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

interface MockResponseInit {
  status?: number
  ok?: boolean
  headers?: Record<string, string>
  body?: unknown
  textError?: boolean // make text() reject
}

function mockResponse(init: MockResponseInit = {}): Response {
  const {
    status = 200,
    ok = status >= 200 && status < 300,
    headers: rawHeaders = {},
    body,
    textError = false,
  } = init

  const headersMap = new Headers(rawHeaders)

  return {
    ok,
    status,
    headers: headersMap,
    json: body !== undefined ? vi.fn().mockResolvedValue(body) : vi.fn().mockRejectedValue(new Error('no body')),
    text: textError ? vi.fn().mockRejectedValue(new Error('text failed')) : vi.fn().mockResolvedValue(typeof body === 'string' ? body : JSON.stringify(body ?? '')),
  } as unknown as Response
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('API client', () => {
  let mockFetch: ReturnType<typeof vi.fn>

  beforeEach(() => {
    mockFetch = vi.fn()
    vi.stubGlobal('fetch', mockFetch)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  // -----------------------------------------------------------------------
  // Envelope unwrap
  // -----------------------------------------------------------------------
  describe('envelope unwrap', () => {
    it('returns the data field from the response envelope', async () => {
      const payload = { id: 1, name: 'test' }
      mockFetch.mockResolvedValue(
        mockResponse({ body: { data: payload, meta: null } }),
      )

      const result = await api.get('/items')

      expect(result).toEqual(payload)
    })

    it('returns data when meta is present', async () => {
      const payload = [{ id: 1 }]
      const meta = { page: 0, size: 10, totalElements: 1, totalPages: 1 }
      mockFetch.mockResolvedValue(
        mockResponse({ body: { data: payload, meta } }),
      )

      const result = await api.get('/items')

      expect(result).toEqual(payload)
    })
  })

  // -----------------------------------------------------------------------
  // 204 handling
  // -----------------------------------------------------------------------
  describe('204 No Content', () => {
    it('returns undefined without parsing body', async () => {
      const resp = mockResponse({ status: 204 })
      mockFetch.mockResolvedValue(resp)

      const result = await api.delete('/items/1')

      expect(result).toBeUndefined()
      expect(resp.json).not.toHaveBeenCalled()
    })
  })

  // -----------------------------------------------------------------------
  // content-length "0"
  // -----------------------------------------------------------------------
  describe('content-length "0"', () => {
    it('returns undefined when content-length is "0"', async () => {
      const resp = mockResponse({
        status: 200,
        headers: { 'content-length': '0' },
      })
      mockFetch.mockResolvedValue(resp)

      const result = await api.get('/ping')

      expect(result).toBeUndefined()
      expect(resp.json).not.toHaveBeenCalled()
    })
  })

  // -----------------------------------------------------------------------
  // 401 unauthorized
  // -----------------------------------------------------------------------
  describe('401 unauthorized', () => {
    it('calls onUnauthorized handler and throws ApiError', async () => {
      const handler = vi.fn()
      setUnauthorizedHandler(handler)
      mockFetch.mockResolvedValue(mockResponse({ status: 401 }))

      await expect(api.get('/secret')).rejects.toThrow(ApiError)
      await expect(api.get('/secret')).rejects.toThrow('Unauthorized')

      expect(handler).toHaveBeenCalled()
    })

    it('throws ApiError with status 401', async () => {
      mockFetch.mockResolvedValue(mockResponse({ status: 401 }))

      try {
        await api.get('/secret')
        expect.unreachable('should have thrown')
      } catch (err) {
        expect(err).toBeInstanceOf(ApiError)
        expect((err as ApiError).status).toBe(401)
        expect((err as ApiError).message).toBe('Unauthorized')
      }
    })
  })

  // -----------------------------------------------------------------------
  // Non-OK response
  // -----------------------------------------------------------------------
  describe('non-OK response', () => {
    it('throws ApiError with status and response text', async () => {
      mockFetch.mockResolvedValue(
        mockResponse({ status: 500, body: 'Internal Server Error' }),
      )

      try {
        await api.get('/fail')
        expect.unreachable('should have thrown')
      } catch (err) {
        expect(err).toBeInstanceOf(ApiError)
        expect((err as ApiError).status).toBe(500)
        expect((err as ApiError).message).toBe('Internal Server Error')
      }
    })

    it('falls back to "Request failed" when text() rejects', async () => {
      mockFetch.mockResolvedValue(
        mockResponse({ status: 502, textError: true }),
      )

      try {
        await api.get('/fail')
        expect.unreachable('should have thrown')
      } catch (err) {
        expect(err).toBeInstanceOf(ApiError)
        expect((err as ApiError).status).toBe(502)
        expect((err as ApiError).message).toBe('Request failed')
      }
    })
  })

  // -----------------------------------------------------------------------
  // Auth header
  // -----------------------------------------------------------------------
  describe('authorization header', () => {
    it('includes Bearer token when token provider returns a token', async () => {
      setTokenProvider(() => 'my-jwt-token')
      mockFetch.mockResolvedValue(
        mockResponse({ body: { data: null, meta: null } }),
      )

      await api.get('/protected')

      const [, init] = mockFetch.mock.calls[0] as [string, RequestInit]
      expect((init.headers as Record<string, string>)['Authorization']).toBe(
        'Bearer my-jwt-token',
      )
    })

    it('does not include Authorization header when no token provider is set', async () => {
      // test-setup.ts resets provider to null each test
      mockFetch.mockResolvedValue(
        mockResponse({ body: { data: null, meta: null } }),
      )

      await api.get('/public')

      const [, init] = mockFetch.mock.calls[0] as [string, RequestInit]
      expect((init.headers as Record<string, string>)['Authorization']).toBeUndefined()
    })

    it('does not include Authorization header when token provider returns null', async () => {
      setTokenProvider(() => null)
      mockFetch.mockResolvedValue(
        mockResponse({ body: { data: null, meta: null } }),
      )

      await api.get('/public')

      const [, init] = mockFetch.mock.calls[0] as [string, RequestInit]
      expect((init.headers as Record<string, string>)['Authorization']).toBeUndefined()
    })
  })

  // -----------------------------------------------------------------------
  // Request body
  // -----------------------------------------------------------------------
  describe('request body', () => {
    it.each(['post', 'put', 'patch'] as const)(
      'api.%s sends JSON.stringify body',
      async (method) => {
        mockFetch.mockResolvedValue(
          mockResponse({ body: { data: { id: 1 }, meta: null } }),
        )

        const payload = { name: 'test', value: 42 }
        await api[method]('/items', payload)

        const [, init] = mockFetch.mock.calls[0] as [string, RequestInit]
        expect(init.body).toBe(JSON.stringify(payload))
      },
    )

    it.each(['get', 'delete'] as const)(
      'api.%s sends no body',
      async (method) => {
        mockFetch.mockResolvedValue(
          mockResponse({ status: 204 }),
        )

        await api[method]('/items/1')

        const [, init] = mockFetch.mock.calls[0] as [string, RequestInit]
        expect(init.body).toBeUndefined()
      },
    )
  })

  // -----------------------------------------------------------------------
  // HTTP methods
  // -----------------------------------------------------------------------
  describe('HTTP methods', () => {
    it.each([
      ['get', 'GET'],
      ['post', 'POST'],
      ['put', 'PUT'],
      ['patch', 'PATCH'],
      ['delete', 'DELETE'],
    ] as const)('api.%s uses %s method', async (fn, expectedMethod) => {
      mockFetch.mockResolvedValue(
        mockResponse({ status: 204 }),
      )

      await (api as Record<string, (path: string, body?: unknown) => Promise<unknown>>)[fn]!('/test')

      const [url, init] = mockFetch.mock.calls[0]! as [string, RequestInit]
      expect(init.method).toBe(expectedMethod)
      expect(url).toBe(`http://localhost:8080/api/test`)
    })
  })
})
