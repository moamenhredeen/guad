<script lang="ts" setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useProjectsStore } from '@/stores/projects'
import { useTaskDetail } from '@/composables/useTaskDetail'
import TaskRow from '@/components/task/TaskRow.vue'
import InlineAddTask from '@/components/task/InlineAddTask.vue'
import { Skeleton } from '@/components/ui/skeleton'
import { ArrowLeft } from 'lucide-vue-next'

const route = useRoute()
const projects = useProjectsStore()
const { open } = useTaskDetail()

const projectId = route.params.id as string
onMounted(() => projects.fetchDetail(projectId))

async function onAddAction(data: { title: string }) {
  await projects.addAction(projectId, data.title)
}
</script>

<template>
  <div v-if="projects.loading">
    <Skeleton class="h-8 w-48 rounded-md" />
    <Skeleton class="mt-2 h-4 w-32 rounded-md" />
    <Skeleton class="mt-6 h-12 w-full rounded-md" />
  </div>

  <div v-else-if="projects.detail">
    <RouterLink to="/projects" class="mb-3 inline-flex items-center gap-1 text-sm text-grau-80 hover:underline">
      <ArrowLeft class="size-4" /> Projects
    </RouterLink>

    <h1 class="font-serif text-2xl font-bold text-schwarz">{{ projects.detail.name }}</h1>
    <p class="mt-0.5 text-[13px] text-grau-50">{{ projects.detail.areaName }}</p>
    <p v-if="projects.detail.desiredOutcome" class="mt-1.5 text-sm italic text-grau-50 leading-relaxed">
      "{{ projects.detail.desiredOutcome }}"
    </p>

    <!-- Next Actions -->
    <h3 class="mt-6 text-xs font-semibold uppercase tracking-wide text-grau-80">Next Actions</h3>
    <div class="mt-1 divide-y divide-grau-5">
      <TaskRow
        v-for="action in projects.detail.nextActions"
        :key="action.id"
        :title="action.description"
        :meta="action.contexts.map(c => c.name).join(', ') || undefined"
        :due-date="action.dueDate ? new Date(action.dueDate).toLocaleDateString('en-US', { weekday: 'short' }) : null"
        @click="open(action)"
      />
    </div>

    <!-- Waiting For -->
    <h3 v-if="projects.detail.waitingForItems.length" class="mt-6 text-xs font-semibold uppercase tracking-wide text-blau-50">Waiting For</h3>
    <div v-if="projects.detail.waitingForItems.length" class="mt-1 divide-y divide-grau-5">
      <div v-for="wf in projects.detail.waitingForItems" :key="wf.id" class="py-3 px-1">
        <div class="text-sm text-schwarz">{{ wf.title }}</div>
        <div class="mt-1 flex gap-3 text-xs">
          <span class="text-blau-50">👤 {{ wf.delegatedTo }}</span>
          <span class="text-grau-20">Delegated {{ new Date(wf.createdDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) }}</span>
        </div>
      </div>
    </div>

    <!-- Completed -->
    <h3 v-if="projects.detail.completedActions.length" class="mt-6 text-xs font-semibold uppercase tracking-wide text-grun-80">Completed</h3>
    <div v-if="projects.detail.completedActions.length" class="mt-1 divide-y divide-grau-5 opacity-60">
      <TaskRow
        v-for="action in projects.detail.completedActions"
        :key="action.id"
        :title="action.description"
        :meta="`Completed ${new Date(action.completedDate!).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}`"
        done
      />
    </div>

    <!-- Add action -->
    <div class="mt-4">
      <InlineAddTask placeholder="+ Add next action" @submit="onAddAction" />
    </div>
  </div>
</template>
