<script lang="ts" setup>
import { computed, onMounted } from 'vue'
import { useProjectsStore } from '@/stores/projects'
import { Skeleton } from '@/components/ui/skeleton'
import { ChevronRight } from 'lucide-vue-next'

const projects = useProjectsStore()
onMounted(() => projects.fetch())

const groupedByArea = computed(() => {
  const groups = new Map<string, typeof projects.items>()
  for (const p of projects.items) {
    const area = p.areaName ?? 'No Area'
    if (!groups.has(area)) groups.set(area, [])
    groups.get(area)!.push(p)
  }
  return groups
})
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Projects</h1>
    <p class="mt-0.5 text-[13px] text-grau-5">{{ projects.items.length }} active projects</p>

    <div v-if="projects.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 5" :key="i" class="h-12 w-full rounded-md" />
    </div>

    <div v-else class="mt-4">
      <div v-for="[areaName, areaProjects] in groupedByArea" :key="areaName" class="mb-4">
        <h3 class="text-xs font-semibold uppercase tracking-wide text-turkis">{{ areaName }}</h3>
        <div class="mt-1 divide-y divide-grau-1">
          <RouterLink
            v-for="p in areaProjects"
            :key="p.id"
            :to="`/projects/${p.id}`"
            class="flex items-center justify-between rounded-md px-1 py-3 hover:bg-grau-1 transition-colors"
          >
            <div>
              <div class="text-sm font-medium text-schwarz">{{ p.name }}</div>
              <div class="mt-0.5 text-xs text-grau-3">{{ p.nextActionCount }} next action{{ p.nextActionCount !== 1 ? 's' : '' }}</div>
            </div>
            <ChevronRight class="size-4 text-grau-3" />
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>
