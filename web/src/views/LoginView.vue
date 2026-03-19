<script lang="ts" setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
onMounted(() => auth.login())
</script>

<template>
  <div class="flex min-h-screen items-center justify-center">
    <p class="text-grau-5 text-sm">Redirecting to login...</p>
  </div>
</template>
