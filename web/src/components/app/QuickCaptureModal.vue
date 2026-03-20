<script setup lang="ts">
import { ref } from 'vue'
import { useQuickCapture } from '@/composables/useQuickCapture'
import { useInboxStore } from '@/stores/inbox'
import { Button } from '@/components/ui/button'

const { isOpen, close } = useQuickCapture()
const inbox = useInboxStore()

const title = ref('')
const notes = ref('')
const titleInput = ref<HTMLInputElement>()

async function submit() {
  if (!title.value.trim()) return
  await inbox.add(title.value.trim(), notes.value.trim() || undefined)
  title.value = ''
  notes.value = ''
  close()
}
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="isOpen" class="fixed inset-0 z-50 flex items-start justify-center pt-[20vh]" @click.self="close">
        <div class="absolute inset-0 bg-schwarz/15" />
        <div class="relative w-[480px] rounded-xl bg-white p-6 shadow-xl" @keydown.escape="close" @keydown.meta.enter="submit" @keydown.ctrl.enter="submit">
          <h2 class="font-serif text-lg font-bold mb-4">Quick Capture</h2>
          <div class="rounded-lg border border-schwarz p-3.5">
            <input
              ref="titleInput"
              v-model="title"
              class="w-full text-[15px] font-medium text-schwarz outline-none placeholder:text-grau-20"
              placeholder="What's on your mind?"
              autofocus
              @keydown.enter.prevent="submit"
            />
            <input
              v-model="notes"
              class="mt-1 w-full text-[13px] text-grau-50 outline-none placeholder:text-grau-20"
              placeholder="Add a note..."
            />
            <div class="mt-3 flex justify-end">
              <Button size="sm" class="bg-schwarz text-white hover:bg-schwarz/90" :disabled="!title.trim()" @click="submit">
                Save to Inbox
              </Button>
            </div>
          </div>
          <p class="mt-3 text-center text-xs text-grau-20">
            Press <kbd class="rounded bg-grau-5 px-1 py-0.5 font-mono">Esc</kbd> to close ·
            <kbd class="rounded bg-grau-5 px-1 py-0.5 font-mono">⌘ Enter</kbd> to save
          </p>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
