import { createRouter, createWebHistory } from 'vue-router'
import SidebarLayout from '@/components/SidebarLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/callback',
      name: 'callback',
      component: () => import('@/views/CallbackView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: SidebarLayout,
      children: [
        { path: '', redirect: '/inbox' },
        {
          path: 'inbox',
          name: 'inbox',
          component: () => import('@/views/InboxView.vue'),
          meta: { title: 'Inbox' },
        },
        {
          path: 'next-actions',
          name: 'next-actions',
          component: () => import('@/views/NextActionsView.vue'),
          meta: { title: 'Next Actions' },
        },
        {
          path: 'projects',
          name: 'projects',
          component: () => import('@/views/ProjectsView.vue'),
          meta: { title: 'Projects' },
        },
        {
          path: 'projects/:id',
          name: 'project-detail',
          component: () => import('@/views/ProjectDetailView.vue'),
          meta: { title: 'Project' },
        },
        {
          path: 'waiting-for',
          name: 'waiting-for',
          component: () => import('@/views/WaitingForView.vue'),
          meta: { title: 'Waiting For' },
        },
        {
          path: 'someday-maybe',
          name: 'someday-maybe',
          component: () => import('@/views/SomedayMaybeView.vue'),
          meta: { title: 'Someday / Maybe' },
        },
        {
          path: 'weekly-review',
          name: 'weekly-review',
          component: () => import('@/views/WeeklyReviewView.vue'),
          meta: { title: 'Weekly Review' },
        },
        {
          path: 'contexts',
          name: 'contexts',
          component: () => import('@/views/ContextsView.vue'),
          meta: { title: 'Contexts' },
        },
        {
          path: 'areas',
          name: 'areas',
          component: () => import('@/views/AreasView.vue'),
          meta: { title: 'Areas' },
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/SettingsView.vue'),
          meta: { title: 'Settings' },
        },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  if (to.meta.public) return true

  const { useAuthStore } = await import('@/stores/auth')
  const auth = useAuthStore()

  if (auth.isAuthenticated) return true

  const refreshed = await auth.tryRefresh()
  if (refreshed) return true

  return { name: 'login' }
})

export default router
