import { describe, it, expect, vi, beforeEach } from 'vitest'

const mockApi = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  patch: vi.fn(),
  delete: vi.fn(),
}))

vi.mock('./client', () => ({ api: mockApi }))

import { inboxApi } from './inbox'
import { actionsApi } from './actions'
import { areasApi } from './areas'
import { contextsApi } from './contexts'
import { projectsApi } from './projects'
import { waitingForApi } from './waitingFor'
import { reviewApi } from './review'
import { somedayMaybeApi } from './somedayMaybe'
import { dashboardApi } from './dashboard'

beforeEach(() => {
  vi.clearAllMocks()
})

// ─── Inbox ───────────────────────────────────────────────────────────

describe('inboxApi', () => {
  it('list calls GET /inbox', () => {
    inboxApi.list()
    expect(mockApi.get).toHaveBeenCalledWith('/inbox')
  })

  it('get calls GET /inbox/:id', () => {
    inboxApi.get('abc')
    expect(mockApi.get).toHaveBeenCalledWith('/inbox/abc')
  })

  it('create calls POST /inbox with body', () => {
    const data = { title: 'test' }
    inboxApi.create(data as any)
    expect(mockApi.post).toHaveBeenCalledWith('/inbox', data)
  })

  it('delete calls DELETE /inbox/:id', () => {
    inboxApi.delete('abc')
    expect(mockApi.delete).toHaveBeenCalledWith('/inbox/abc')
  })

  it('process calls POST /inbox/:id/process with body', () => {
    const data = { action: 'do' }
    inboxApi.process('abc', data as any)
    expect(mockApi.post).toHaveBeenCalledWith('/inbox/abc/process', data)
  })
})

// ─── Actions ─────────────────────────────────────────────────────────

describe('actionsApi', () => {
  it('get calls GET /actions/:id', () => {
    actionsApi.get('a1')
    expect(mockApi.get).toHaveBeenCalledWith('/actions/a1')
  })

  it('create calls POST /actions with body', () => {
    const data = { description: 'do something' }
    actionsApi.create(data as any)
    expect(mockApi.post).toHaveBeenCalledWith('/actions', data)
  })

  it('update calls PUT /actions/:id with body', () => {
    const data = { description: 'updated' }
    actionsApi.update('a1', data as any)
    expect(mockApi.put).toHaveBeenCalledWith('/actions/a1', data)
  })

  it('delete calls DELETE /actions/:id', () => {
    actionsApi.delete('a1')
    expect(mockApi.delete).toHaveBeenCalledWith('/actions/a1')
  })

  it('complete calls PATCH /actions/:id/complete', () => {
    actionsApi.complete('a1')
    expect(mockApi.patch).toHaveBeenCalledWith('/actions/a1/complete')
  })

  it('updateStatus calls PATCH /actions/:id/status with body', () => {
    actionsApi.updateStatus('a1', 'done')
    expect(mockApi.patch).toHaveBeenCalledWith('/actions/a1/status', { status: 'done' })
  })

  describe('list query string serialization', () => {
    it('calls GET /actions with no params', () => {
      actionsApi.list()
      expect(mockApi.get).toHaveBeenCalledWith('/actions')
    })

    it('calls GET /actions with no params when empty object', () => {
      actionsApi.list({})
      expect(mockApi.get).toHaveBeenCalledWith('/actions')
    })

    it('calls GET /actions?status=... with status only', () => {
      actionsApi.list({ status: 'active' })
      expect(mockApi.get).toHaveBeenCalledWith('/actions?status=active')
    })

    it('calls GET /actions?contextId=... with contextId only', () => {
      actionsApi.list({ contextId: 'ctx-1' })
      expect(mockApi.get).toHaveBeenCalledWith('/actions?contextId=ctx-1')
    })

    it('calls GET /actions?status=...&contextId=... with both', () => {
      actionsApi.list({ status: 'active', contextId: 'ctx-1' })
      expect(mockApi.get).toHaveBeenCalledWith('/actions?status=active&contextId=ctx-1')
    })
  })
})

// ─── Areas ───────────────────────────────────────────────────────────

describe('areasApi', () => {
  it('list calls GET /areas', () => {
    areasApi.list()
    expect(mockApi.get).toHaveBeenCalledWith('/areas')
  })

  it('create calls POST /areas with body', () => {
    const data = { name: 'Health' }
    areasApi.create(data as any)
    expect(mockApi.post).toHaveBeenCalledWith('/areas', data)
  })

  it('update calls PUT /areas/:id with body', () => {
    const data = { name: 'Finance' }
    areasApi.update('ar1', data as any)
    expect(mockApi.put).toHaveBeenCalledWith('/areas/ar1', data)
  })

  it('delete calls DELETE /areas/:id', () => {
    areasApi.delete('ar1')
    expect(mockApi.delete).toHaveBeenCalledWith('/areas/ar1')
  })
})

// ─── Contexts ────────────────────────────────────────────────────────

describe('contextsApi', () => {
  it('list calls GET /contexts', () => {
    contextsApi.list()
    expect(mockApi.get).toHaveBeenCalledWith('/contexts')
  })

  it('create calls POST /contexts with body', () => {
    const data = { name: 'Home' }
    contextsApi.create(data as any)
    expect(mockApi.post).toHaveBeenCalledWith('/contexts', data)
  })

  it('update calls PUT /contexts/:id with body', () => {
    const data = { name: 'Office' }
    contextsApi.update('c1', data as any)
    expect(mockApi.put).toHaveBeenCalledWith('/contexts/c1', data)
  })

  it('delete calls DELETE /contexts/:id', () => {
    contextsApi.delete('c1')
    expect(mockApi.delete).toHaveBeenCalledWith('/contexts/c1')
  })
})

// ─── Projects ────────────────────────────────────────────────────────

describe('projectsApi', () => {
  it('list calls GET /projects', () => {
    projectsApi.list()
    expect(mockApi.get).toHaveBeenCalledWith('/projects')
  })

  it('get calls GET /projects/:id', () => {
    projectsApi.get('p1')
    expect(mockApi.get).toHaveBeenCalledWith('/projects/p1')
  })

  it('create calls POST /projects with body', () => {
    const data = { name: 'Website' }
    projectsApi.create(data as any)
    expect(mockApi.post).toHaveBeenCalledWith('/projects', data)
  })

  it('update calls PUT /projects/:id with body', () => {
    const data = { name: 'Redesign' }
    projectsApi.update('p1', data as any)
    expect(mockApi.put).toHaveBeenCalledWith('/projects/p1', data)
  })

  it('delete calls DELETE /projects/:id', () => {
    projectsApi.delete('p1')
    expect(mockApi.delete).toHaveBeenCalledWith('/projects/p1')
  })

  it('addAction calls POST /projects/:id/actions with description', () => {
    projectsApi.addAction('p1', 'write docs')
    expect(mockApi.post).toHaveBeenCalledWith('/projects/p1/actions', { description: 'write docs' })
  })

  it('updateStatus calls PATCH /projects/:id/status with status', () => {
    projectsApi.updateStatus('p1', 'completed')
    expect(mockApi.patch).toHaveBeenCalledWith('/projects/p1/status', { status: 'completed' })
  })
})

// ─── Waiting For ─────────────────────────────────────────────────────

describe('waitingForApi', () => {
  it('list calls GET /waiting-for', () => {
    waitingForApi.list()
    expect(mockApi.get).toHaveBeenCalledWith('/waiting-for')
  })

  it('get calls GET /waiting-for/:id', () => {
    waitingForApi.get('w1')
    expect(mockApi.get).toHaveBeenCalledWith('/waiting-for/w1')
  })

  it('create calls POST /waiting-for with body', () => {
    const data = { description: 'reply from Bob' }
    waitingForApi.create(data as any)
    expect(mockApi.post).toHaveBeenCalledWith('/waiting-for', data)
  })

  it('update calls PUT /waiting-for/:id with body', () => {
    const data = { description: 'updated' }
    waitingForApi.update('w1', data as any)
    expect(mockApi.put).toHaveBeenCalledWith('/waiting-for/w1', data)
  })

  it('delete calls DELETE /waiting-for/:id', () => {
    waitingForApi.delete('w1')
    expect(mockApi.delete).toHaveBeenCalledWith('/waiting-for/w1')
  })

  it('resolve calls PATCH /waiting-for/:id/resolve', () => {
    waitingForApi.resolve('w1')
    expect(mockApi.patch).toHaveBeenCalledWith('/waiting-for/w1/resolve')
  })
})

// ─── Review ──────────────────────────────────────────────────────────

describe('reviewApi', () => {
  it('start calls POST /reviews', () => {
    reviewApi.start()
    expect(mockApi.post).toHaveBeenCalledWith('/reviews')
  })

  it('getCurrent calls GET /reviews/current', () => {
    reviewApi.getCurrent()
    expect(mockApi.get).toHaveBeenCalledWith('/reviews/current')
  })

  it('advanceStep calls PATCH /reviews/:id/step', () => {
    reviewApi.advanceStep('r1')
    expect(mockApi.patch).toHaveBeenCalledWith('/reviews/r1/step')
  })

  it('complete calls POST /reviews/:id/complete', () => {
    reviewApi.complete('r1')
    expect(mockApi.post).toHaveBeenCalledWith('/reviews/r1/complete')
  })

  it('getLast calls GET /reviews/last', () => {
    reviewApi.getLast()
    expect(mockApi.get).toHaveBeenCalledWith('/reviews/last')
  })
})

// ─── Someday / Maybe ─────────────────────────────────────────────────

describe('somedayMaybeApi', () => {
  it('list calls GET /someday-maybe', () => {
    somedayMaybeApi.list()
    expect(mockApi.get).toHaveBeenCalledWith('/someday-maybe')
  })
})

// ─── Dashboard ───────────────────────────────────────────────────────

describe('dashboardApi', () => {
  it('get calls GET /dashboard', () => {
    dashboardApi.get()
    expect(mockApi.get).toHaveBeenCalledWith('/dashboard')
  })
})
