<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { LogOut } from 'lucide-vue-next'

const auth = useAuthStore()
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Settings</h1>

    <div class="mt-6 flex items-center gap-4">
      <Avatar class="size-12 rounded-lg">
        <AvatarFallback class="rounded-lg bg-schwarz text-white text-lg">
          {{ auth.user?.username?.charAt(0)?.toUpperCase() ?? '?' }}
        </AvatarFallback>
      </Avatar>
      <div>
        <div class="font-medium text-schwarz">{{ auth.user?.username ?? 'User' }}</div>
        <div class="text-sm text-grau-50">{{ auth.user?.email ?? '' }}</div>
      </div>
    </div>

    <Separator class="my-6" />

    <Button variant="outline" class="text-rot-80 border-rot-80/30 hover:bg-rot-80/5" @click="auth.logout()">
      <LogOut class="mr-2 size-4" /> Log out
    </Button>
  </div>
</template>
