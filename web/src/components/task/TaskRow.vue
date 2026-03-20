<script setup lang="ts">
import TaskCheckbox from './TaskCheckbox.vue'

defineProps<{
  title: string
  meta?: string
  dueDate?: string | null
  done?: boolean
  selected?: boolean
}>()

defineEmits<{
  toggle: []
  click: []
}>()
</script>

<template>
  <div
    class="flex cursor-pointer items-center gap-3 rounded-md px-1 py-2.5 transition-colors hover:bg-grau-5"
    :class="{ 'bg-grau-5': selected }"
    @click="$emit('click')"
  >
    <TaskCheckbox :done="done" @toggle="$emit('toggle')" />
    <div class="min-w-0 flex-1">
      <div
        class="text-sm"
        :class="done ? 'line-through text-grau-20' : 'text-schwarz'"
      >
        {{ title }}
      </div>
      <div v-if="meta" class="mt-0.5 text-xs text-grau-20">{{ meta }}</div>
    </div>
    <div v-if="dueDate" class="shrink-0 text-xs font-medium text-rot-50">{{ dueDate }}</div>
  </div>
</template>
