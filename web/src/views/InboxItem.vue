<script setup lang="ts">
import { Checkbox } from "@/components/ui/checkbox";
defineProps<{
  title: string;
  description: string;
  done: boolean;
}>();
</script>

<template>
  <li class="flex gap-4 border-b border-zinc-100 p-3 hover:bg-zinc-50">
    <Checkbox class="mt-1" @value="done"></Checkbox>
    <div>
      <h1 :class="done ? 'line-through' : 'underline'">{{ title }}</h1>
      <p class="text-zinc-500">{{ description }}</p>
    </div>
  </li>
</template>
