<script lang="ts" setup>
import { onMounted } from 'vue'
import { useSomedayMaybeStore } from '@/stores/somedayMaybe'
import TaskRow from '@/components/task/TaskRow.vue'
import { Skeleton } from '@/components/ui/skeleton'
import { ChevronRight } from 'lucide-vue-next'

const store = useSomedayMaybeStore()
onMounted(() => store.fetch())
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Someday / Maybe</h1>

    <div v-if="store.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 5" :key="i" class="h-12 w-full rounded-md" />
    </div>

    <div v-else-if="store.data" class="mt-4">
      <h3 v-if="store.data.actions.length" class="text-xs font-semibold uppercase tracking-wide text-lila">Actions</h3>
      <div class="mt-1 divide-y divide-grau-1">
        <TaskRow
          v-for="action in store.data.actions"
          :key="action.id"
          :title="action.description"
          :meta="action.projectName ?? undefined"
        />
      </div>

      <h3 v-if="store.data.projects.length" class="mt-6 text-xs font-semibold uppercase tracking-wide text-lila">Projects</h3>
      <div class="mt-1 divide-y divide-grau-1">
        <RouterLink
          v-for="p in store.data.projects"
          :key="p.id"
          :to="`/projects/${p.id}`"
          class="flex items-center justify-between rounded-md px-1 py-3 hover:bg-grau-1 transition-colors"
        >
          <div class="text-sm font-medium text-schwarz">{{ p.name }}</div>
          <ChevronRight class="size-4 text-grau-3" />
        </RouterLink>
      </div>
    </div>
  </div>
</template>
