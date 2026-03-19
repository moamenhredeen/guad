<script lang="ts" setup>
import { onMounted } from 'vue'
import { useWaitingForStore } from '@/stores/waitingFor'
import { Skeleton } from '@/components/ui/skeleton'

const waitingFor = useWaitingForStore()
onMounted(() => waitingFor.fetch())

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

function daysSince(dateStr: string) {
  return Math.floor((Date.now() - new Date(dateStr).getTime()) / (1000 * 60 * 60 * 24))
}
</script>

<template>
  <div>
    <h1 class="font-serif text-2xl font-bold text-schwarz">Waiting For</h1>
    <p class="mt-0.5 text-[13px] text-grau-5">{{ waitingFor.items.length }} items delegated</p>

    <div v-if="waitingFor.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 4" :key="i" class="h-16 w-full rounded-md" />
    </div>

    <div v-else class="mt-4 divide-y divide-grau-1">
      <div v-for="item in waitingFor.items" :key="item.id" class="py-3 px-1">
        <div class="text-sm text-schwarz">{{ item.title }}</div>
        <div v-if="item.projectName" class="mt-0.5 text-xs text-grau-5">{{ item.projectName }}</div>
        <div class="mt-1.5 flex gap-3 text-xs">
          <span v-if="item.delegatedTo" class="text-blau">👤 {{ item.delegatedTo }}</span>
          <span :class="daysSince(item.createdDate) >= 7 ? 'text-orange' : 'text-grau-3'">
            Delegated {{ formatDate(item.createdDate) }}
            <template v-if="daysSince(item.createdDate) >= 7"> · {{ daysSince(item.createdDate) }} days</template>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
