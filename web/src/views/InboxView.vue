<script lang="ts" setup>
import { onMounted } from "vue";
import { useInboxStore } from "@/stores/inbox";
import TaskRow from "@/components/task/TaskRow.vue";
import InlineAddTask from "@/components/task/InlineAddTask.vue";
import { Skeleton } from "@/components/ui/skeleton";
import type { ProcessAction } from "@/types";

const inbox = useInboxStore();
onMounted(() => inbox.fetch());

function formatAge(dateStr: string) {
  const date = new Date(dateStr);
  const now = new Date();
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
  if (diffDays === 0) return "Added today";
  if (diffDays === 1) return "Added yesterday";
  return `Added ${diffDays} days ago`;
}

async function onAdd(data: { title: string; notes: string }) {
  await inbox.add(data.title, data.notes || undefined);
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function onProcess(action: ProcessAction) {
  if (!inbox.selectedId) return;
  await inbox.process(inbox.selectedId, action);
}
</script>

<template>
  <div class="mx-auto max-w-200">
    <h1 class="font-serif text-2xl font-bold text-schwarz">Inbox</h1>
    <p class="mt-0.5 text-[13px] text-grau-50">{{ inbox.items.length }} items to process</p>

    <!-- Loading -->
    <div v-if="inbox.loading" class="mt-4 space-y-3">
      <Skeleton v-for="i in 5" :key="i" class="h-12 w-full rounded-md" />
    </div>

    <!-- Empty state -->
    <div v-else-if="inbox.items.length === 0" class="flex flex-col items-center py-16">
      <div class="flex size-16 items-center justify-center rounded-full bg-grau-5 text-2xl">✓</div>
      <h2 class="mt-4 font-serif text-xl font-bold">Alles guad!</h2>
      <p class="mt-1 text-sm text-grau-50">
        Nix zum schaffe. Press
        <kbd class="rounded bg-grau-5 px-1.5 py-0.5 font-mono text-xs">Q</kbd> to capture something
        new.
      </p>
    </div>

    <!-- Task list -->
    <div v-else class="mt-4">
      <TaskRow
        v-for="item in inbox.items"
        class="border-b border-grau-5"
        :key="item.id"
        :title="item.title"
        :meta="formatAge(item.createdAt)"
        :selected="inbox.selectedId === item.id"
        @more="console.log('more')"
      />

      <InlineAddTask @submit="onAdd" />
    </div>
  </div>
</template>
