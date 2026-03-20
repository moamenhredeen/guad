<script setup lang="ts">
defineProps<{
  done?: boolean
}>()

defineEmits<{
  toggle: []
}>()
</script>

<template>
  <button
    class="flex size-5 shrink-0 items-center justify-center rounded-full border-2 transition-colors"
    :class="done
      ? 'border-grun-80 bg-grun-80 text-white'
      : 'border-grau-20 hover:border-schwarz'"
    @click.stop="$emit('toggle')"
  >
    <svg v-if="done" class="size-3" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="2">
      <polyline points="2,6 5,9 10,3" />
    </svg>
  </button>
</template>
