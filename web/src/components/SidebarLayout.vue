<script setup lang="ts">
import AppSidebar from '@/components/app/AppSidebar.vue'
import TaskDetailPanel from '@/components/app/TaskDetailPanel.vue'
import QuickCaptureModal from '@/components/app/QuickCaptureModal.vue'
import { Separator } from '@/components/ui/separator'
import { SidebarInset, SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar'
import { useQuickCapture } from '@/composables/useQuickCapture'

// Initialize Q shortcut listener
useQuickCapture()
</script>

<template>
  <SidebarProvider>
    <AppSidebar />
    <SidebarInset class="flex flex-row">
      <div class="flex flex-1 flex-col overflow-hidden">
        <header class="flex h-12 shrink-0 items-center gap-2 px-4">
          <SidebarTrigger class="-ml-1" />
          <Separator orientation="vertical" class="mr-2 data-[orientation=vertical]:h-4" />
        </header>
        <div class="flex-1 overflow-y-auto p-6 pt-0">
          <RouterView />
        </div>
      </div>
      <TaskDetailPanel />
    </SidebarInset>
    <QuickCaptureModal />
  </SidebarProvider>
</template>
