<script setup lang="ts">
import { Pencil, MoreHorizontal } from "lucide-vue-next";
import TaskCheckbox from "./TaskCheckbox.vue";

defineProps<{
  title: string;
  meta?: string;
  dueDate?: string | null;
  done?: boolean;
  selected?: boolean;
}>();

defineEmits<{
  toggle: [];
  click: [];
  edit: [];
  more: [];
}>();
</script>

<template>
  <div
    class="group flex cursor-pointer items-center gap-3 rounded-md px-4 py-2 transition-colors hover:bg-grau-5"
    :class="{ 'bg-grau-5': selected }"
    @click="$emit('click')"
  >
    <TaskCheckbox :done="done" @toggle="$emit('toggle')" />
    <div class="min-w-0 flex-1">
      <div class="text-sm" :class="done ? 'line-through text-grau-20' : 'text-schwarz'">
        {{ title }}
      </div>
      <div v-if="meta" class="mt-0.5 text-xs text-grau-50">{{ meta }}</div>
    </div>

    <div v-if="dueDate" class="shrink-0 text-xs font-medium text-rot-50">{{ dueDate }}</div>

    <div
      class="flex shrink-0 items-center gap-0.5 opacity-0 transition-opacity group-hover:opacity-100"
    >
      <button
        class="rounded p-2 text-grau-50 hover:bg-grau-10 hover:text-grau-80"
        @click.stop="$emit('edit')"
      >
        <Pencil class="size-4" />
      </button>
      <button
        class="rounded p-2 text-grau-50 hover:bg-grau-10 hover:text-grau-80"
        @click.stop="$emit('more')"
      >
        <MoreHorizontal class="size-4" />
      </button>
    </div>
  </div>
</template>
