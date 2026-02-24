import { createRouter, createWebHistory } from 'vue-router'
import SidebarLayout from './components/SidebarLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: SidebarLayout,
      children: [
        {
          path: '/dashboard',
          name: 'dashboard',
          component: () => import('./views/DashboardView.vue'),
          meta: {
            title: 'Dashboard',
          },
        },
        {
          path: '/inbox',
          name: 'inbox',
          component: () => import('./views/InboxView.vue'),
          meta: {
            title: 'Inbox',
          },
        },
        {
          path: '/users',
          name: 'users',
          children: [
            {
              path: '',
              name: 'users-list',
              component: () => import('./views/users/UserListView.vue'),
              meta: {
                title: 'Users List',
              },
            },
            {
              path: ':id',
              name: 'user-details',
              component: () => import('./views/users/UserDetailsView.vue'),
              meta: {
                title: 'User Details',
              },
            },
          ],
        }
      ],
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('./views/SignUpView.vue'),
      meta: {
        title: 'Sign Up',
      },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('./views/LoginView.vue'),
      meta: {
        title: 'Login',
      },
    },
    {
      path: '/otp',
      name: 'otp',
      component: () => import('./views/OTPVerificationView.vue'),
      meta: {
        title: 'OTP Verification',
      },
    }
  ],
})

export default router
