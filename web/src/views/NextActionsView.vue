<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useActionsStore } from '@/stores/actions'
import { useContextsStore } from '@/stores/contexts'
import TaskRow from '@/components/task/TaskRow.vue'
import InlineAddTask from '@/components/task/InlineAddTask.vue'
import { Skeleton } from '@/components/ui/skeleton'

const actions = useActionsStore()
const contexts = useContextsStore()
const activeContextId = ref<string | null>(null)

onMounted(() => {
  actions.fetch({ status: 'NEXT' })
  contexts.fetch()
})

const filteredActions = computed(() => {
  if (!activeContextId.value) return actions.items
  return actions.items.filter(a => a.contexts.some(c => c.id === activeContextId.value))
})

const groupedByContext = computed(() => {
  const groups = new Map<string, typeof actions.items>()
  for (const action of filteredActions.value) {
    const ctxName = action.contexts.length > 0 ? action.contexts[0]!.name : 'No Context'
    if (!groups.has(ctxName)) groups.set(ctxName, [])
    groups.get(ctxName)!.push(action)
  }
  return groups
})

function formatDueDate(date: string | null) {
  if (!date) return null
  const d = new Date(date)
  return d.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' })
}

async function onAdd(data: { title: string; notes: string }) {
  await actions.create({
    description: data.title,
    notes: data.notes || undefined,
    contextIds: activeContextId.value ? [activeContextId.value] : undefined,
  })
}
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Next Actions</h1>
    <p class="mt-0.5 text-[13px] text-grau-50">{{ filteredActions.length }} actions across {{ groupedByContext.size }} contexts</p>

    <!-- Filter chips -->
    <div class="mt-3 flex flex-wrap gap-1.5">
      <button
        class="rounded-full px-3.5 py-1 text-[13px] transition-colors"
        :class="activeContextId === null ? 'bg-schwarz text-white' : 'bg-grau-5 text-schwarz hover:bg-grau-10'"
        @click="activeContextId = null"
      >All</button>
      <button
        v-for="ctx in contexts.items"
        :key="ctx.id"
        class="rounded-full px-3.5 py-1 text-[13px] transition-colors"
        :class="activeContextId === ctx.id ? 'bg-schwarz text-white' : 'bg-grau-5 text-schwarz hover:bg-grau-10'"
        @click="activeContextId = activeContextId === ctx.id ? null : ctx.id"
      >{{ ctx.name }}</button>
    </div>

    <!-- Loading -->
    <div v-if="actions.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 5" :key="i" class="h-12 w-full rounded-md" />
    </div>

    <!-- Grouped tasks -->
    <div v-else class="mt-4">
      <div v-for="[contextName, contextActions] in groupedByContext" :key="contextName" class="mb-4">
        <h3 class="text-xs font-semibold uppercase tracking-wide text-grau-80">{{ contextName }}</h3>
        <div class="mt-1 divide-y divide-grau-5">
          <TaskRow
            v-for="action in contextActions"
            :key="action.id"
            :title="action.description"
            :meta="action.projectName ?? undefined"
            :due-date="formatDueDate(action.dueDate)"
            @toggle="actions.complete(action.id)"
          />
        </div>
      </div>

      <InlineAddTask @submit="onAdd" />
    </div>
  </div>
</template>
