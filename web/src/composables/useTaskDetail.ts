import { ref } from 'vue'
import type { ActionResponse } from '@/types'

const isOpen = ref(false)
const selectedAction = ref<ActionResponse | null>(null)

export function useTaskDetail() {
  function open(action: ActionResponse) {
    selectedAction.value = action
    isOpen.value = true
  }

  function close() {
    isOpen.value = false
    selectedAction.value = null
  }

  return { isOpen, selectedAction, open, close }
}
