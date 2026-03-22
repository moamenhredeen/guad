<script setup lang="ts">
import AppSidebar from "@/components/app/AppSidebar.vue";
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { useQuickCapture } from "@/composables/useQuickCapture";

// Initialize Q shortcut listener
useQuickCapture();
</script>

<template>
  <SidebarProvider>
    <AppSidebar />
    <SidebarInset class="flex flex-col">
      <header class="flex h-12 shrink-0 items-center gap-2 px-4">
        <SidebarTrigger />
      </header>
      <div class="flex-1 overflow-y-auto">
        <RouterView />
      </div>
    </SidebarInset>
  </SidebarProvider>
</template>
