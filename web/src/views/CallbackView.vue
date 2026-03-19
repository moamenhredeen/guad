<script lang="ts" setup>
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

onMounted(async () => {
  const code = route.query.code as string | undefined
  if (!code) {
    router.replace('/login')
    return
  }

  const success = await auth.handleCallback(code)
  if (success) {
    router.replace('/inbox')
  } else {
    router.replace('/login')
  }
})
</script>

<template>
  <div class="flex min-h-screen items-center justify-center">
    <p class="text-grau-5 text-sm">Signing in...</p>
  </div>
</template>
