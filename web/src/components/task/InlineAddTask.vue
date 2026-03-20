<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'

const props = defineProps<{
  placeholder?: string
}>()

const emit = defineEmits<{
  submit: [data: { title: string; notes: string }]
}>()

const expanded = ref(false)
const title = ref('')
const notes = ref('')
const titleInput = ref<HTMLInputElement>()

function expand() {
  expanded.value = true
  setTimeout(() => titleInput.value?.focus(), 0)
}

function submit() {
  if (!title.value.trim()) return
  emit('submit', { title: title.value.trim(), notes: notes.value.trim() })
  title.value = ''
  notes.value = ''
  expanded.value = false
}

function cancel() {
  title.value = ''
  notes.value = ''
  expanded.value = false
}
</script>

<template>
  <!-- Collapsed -->
  <button
    v-if="!expanded"
    class="flex w-full items-center gap-2.5 rounded-md px-1 py-2.5 text-sm text-grau-80 hover:bg-grau-5 transition-colors"
    @click="expand"
  >
    <div class="flex size-5 items-center justify-center rounded-full border-2 border-schwarz">
      <Plus class="size-3" />
    </div>
    {{ placeholder ?? 'Add task' }}
  </button>

  <!-- Expanded -->
  <div v-else class="rounded-lg border border-grau-10 p-3.5" @keydown.escape="cancel">
    <input
      ref="titleInput"
      v-model="title"
      class="w-full text-[15px] font-medium text-schwarz outline-none placeholder:text-grau-20"
      placeholder="Task title"
      @keydown.enter.prevent="submit"
    />
    <input
      v-model="notes"
      class="mt-1 w-full text-[13px] text-grau-50 outline-none placeholder:text-grau-20"
      placeholder="Add a note..."
    />
    <div class="mt-3 flex items-center justify-end gap-2">
      <Button variant="ghost" size="sm" @click="cancel">Cancel</Button>
      <Button
        size="sm"
        class="bg-schwarz text-white hover:bg-schwarz/90"
        :disabled="!title.trim()"
        @click="submit"
      >
        Add Task
      </Button>
    </div>
  </div>
</template>
