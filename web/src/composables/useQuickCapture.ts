import { ref } from 'vue'
import { useEventListener } from '@vueuse/core'

const isOpen = ref(false)

export function useQuickCapture() {
  function open() { isOpen.value = true }
  function close() { isOpen.value = false }

  useEventListener('keydown', (e: KeyboardEvent) => {
    if (e.key === 'q' && !isOpen.value) {
      const target = e.target as HTMLElement
      if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable) return
      e.preventDefault()
      open()
    }
  })

  return { isOpen, open, close }
}
