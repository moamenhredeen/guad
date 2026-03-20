<script setup lang="ts">
import type { ProcessAction } from '@/types'

defineEmits<{
  process: [action: ProcessAction]
}>()

const actions: { label: string; icon: string; action: ProcessAction }[] = [
  { label: 'Next Action', icon: '⚡', action: 'NEXT_ACTION' },
  { label: 'To Project', icon: '📁', action: 'PROJECT' },
  { label: 'Waiting For', icon: '⏳', action: 'WAITING_FOR' },
  { label: 'Someday', icon: '💭', action: 'SOMEDAY_MAYBE' },
  { label: 'Reference', icon: '📋', action: 'REFERENCE' },
  { label: 'Trash', icon: '🗑️', action: 'TRASH' },
]
</script>

<template>
  <div class="flex flex-wrap items-center justify-center gap-4 rounded-lg bg-grau-5 px-4 py-2.5">
    <button
      v-for="a in actions"
      :key="a.action"
      class="flex items-center gap-1.5 text-[13px] font-medium text-schwarz hover:underline transition-colors"
      @click="$emit('process', a.action)"
    >
      <span>{{ a.icon }}</span> {{ a.label }}
    </button>
  </div>
</template>
