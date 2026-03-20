<script setup lang="ts">
import { ref, watch } from 'vue'
import { useTaskDetail } from '@/composables/useTaskDetail'
import { useActionsStore } from '@/stores/actions'
import TaskCheckbox from '@/components/task/TaskCheckbox.vue'
import { Button } from '@/components/ui/button'
import { X } from 'lucide-vue-next'
import type { UpdateActionRequest } from '@/types'

const { isOpen, selectedAction, close } = useTaskDetail()
const actions = useActionsStore()

const form = ref<UpdateActionRequest>({})

watch(selectedAction, (action) => {
  if (action) {
    form.value = {
      description: action.description,
      notes: action.notes ?? undefined,
      projectId: action.projectId ?? undefined,
      areaId: action.areaId ?? undefined,
      contextIds: action.contexts.map(c => c.id),
      energyLevel: action.energyLevel ?? undefined,
      estimatedDuration: action.estimatedDuration ?? undefined,
      dueDate: action.dueDate ?? undefined,
    }
  }
})

async function save() {
  if (!selectedAction.value) return
  await actions.update(selectedAction.value.id, form.value)
  close()
}

async function onComplete() {
  if (!selectedAction.value) return
  await actions.complete(selectedAction.value.id)
  close()
}

async function onDelete() {
  if (!selectedAction.value) return
  await actions.remove(selectedAction.value.id)
  close()
}

function formatDueDate(date: string | null | undefined) {
  if (!date) return '—'
  return new Date(date).toLocaleDateString('en-US', { weekday: 'long', month: 'short', day: 'numeric' })
}
</script>

<template>
  <Transition name="slide">
    <div v-if="isOpen && selectedAction" class="w-[340px] shrink-0 border-l border-grau-50 bg-white">
      <div class="flex items-center justify-between border-b border-grau-5 px-5 py-4">
        <button class="text-grau-20 hover:text-schwarz" @click="close"><X class="size-5" /></button>
        <Button size="sm" variant="ghost" class="text-grau-80 font-semibold" @click="save">Save</Button>
      </div>

      <div class="overflow-y-auto p-5">
        <!-- Title -->
        <div class="flex items-start gap-3 mb-5">
          <TaskCheckbox :done="selectedAction.status === 'COMPLETED'" class="mt-0.5" @toggle="onComplete" />
          <input
            v-model="form.description"
            class="text-lg font-semibold text-schwarz outline-none w-full"
          />
        </div>

        <!-- Metadata fields -->
        <div class="divide-y divide-grau-5">
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-50">Project</span>
            <span class="text-[13px] font-medium">{{ selectedAction.projectName ?? '—' }}</span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-50">Context</span>
            <span v-if="selectedAction.contexts.length" class="rounded-full bg-grau-5 px-2.5 py-0.5 text-xs font-medium text-schwarz">
              {{ selectedAction.contexts[0]!.name }}
            </span>
            <span v-else class="text-[13px]">—</span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-50">Due date</span>
            <span class="text-[13px]" :class="selectedAction.dueDate ? 'font-medium text-rot-50' : ''">
              {{ formatDueDate(selectedAction.dueDate) }}
            </span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-50">Area</span>
            <span class="text-[13px]">{{ selectedAction.areaName ?? '—' }}</span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-50">Energy</span>
            <span class="text-[13px]">{{ selectedAction.energyLevel ?? '—' }}</span>
          </div>
          <div class="flex items-center justify-between py-3">
            <span class="text-[13px] text-grau-50">Time needed</span>
            <span class="text-[13px]">{{ selectedAction.estimatedDuration ?? '—' }}</span>
          </div>
        </div>

        <!-- Notes -->
        <div class="mt-4">
          <label class="text-[13px] text-grau-50">Notes</label>
          <textarea
            v-model="form.notes"
            class="mt-1.5 w-full rounded-lg bg-grau-5 p-3 text-[13px] leading-relaxed text-grau-50 outline-none min-h-[60px] resize-y"
            placeholder="Add notes..."
          />
        </div>

        <!-- Delete -->
        <button class="mt-4 w-full py-3 text-center text-[13px] text-rot-80 hover:underline" @click="onDelete">
          Delete Task
        </button>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.slide-enter-active, .slide-leave-active {
  transition: transform 0.2s ease, opacity 0.2s ease;
}
.slide-enter-from, .slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>
