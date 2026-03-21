import { afterEach } from 'vitest'
import { setTokenProvider, setUnauthorizedHandler } from '@/api/client'

// Reset client module-level globals after each test to prevent leaks
// from auth store side effects (setTokenProvider / setUnauthorizedHandler)
afterEach(() => {
  setTokenProvider(null as unknown as () => string | null)
  setUnauthorizedHandler(null as unknown as () => void)
})
