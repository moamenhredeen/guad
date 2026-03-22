<script setup lang="ts">
import type { SidebarProps } from "@/components/ui/sidebar";
import {
  Inbox,
  Zap,
  FolderOpen,
  Clock,
  CloudSun,
  RefreshCw,
  Tags,
  Mountain,
  Settings,
  LogOut,
  Plus,
} from "lucide-vue-next";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuBadge,
  SidebarGroup,
  SidebarGroupLabel,
  SidebarGroupContent,
  SidebarSeparator,
} from "@/components/ui/sidebar";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useAuthStore } from "@/stores/auth";
import { useDashboardStore } from "@/stores/dashboard";
import { useQuickCapture } from "@/composables/useQuickCapture";
import { useRoute } from "vue-router";
import { computed, onMounted } from "vue";

withDefaults(defineProps<SidebarProps>(), {});

const auth = useAuthStore();
const dashboard = useDashboardStore();
const { open: openQuickCapture } = useQuickCapture();
const route = useRoute();

onMounted(() => dashboard.fetch());

const primaryNav = computed(() => [
  { title: "Inbox", url: "/inbox", icon: Inbox, badge: dashboard.data?.inboxCount },
  {
    title: "Next Actions",
    url: "/next-actions",
    icon: Zap,
    badge: dashboard.data?.nextActionsCount,
  },
  {
    title: "Projects",
    url: "/projects",
    icon: FolderOpen,
    badge: dashboard.data?.activeProjectsCount,
  },
  {
    title: "Waiting For",
    url: "/waiting-for",
    icon: Clock,
    badge: dashboard.data?.waitingForCount,
  },
  { title: "Someday / Maybe", url: "/someday-maybe", icon: CloudSun },
]);

const reviewDue = computed(() => dashboard.data?.weeklyReviewDue ?? false);

const isActive = (url: string) => route.path === url || route.path.startsWith(url + "/");
</script>

<template>
  <Sidebar v-bind="$attrs">
    <SidebarHeader>
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton size="lg" as-child>
            <RouterLink to="/inbox">
              <div
                class="flex aspect-square size-8 items-center justify-center rounded-lg bg-gelb text-schwarz font-serif font-bold text-sm"
              >
                G
              </div>
              <div class="grid flex-1 text-left text-sm leading-tight">
                <span class="truncate font-medium">Guad</span>
                <span class="truncate text-xs text-muted-foreground">GTD System</span>
              </div>
            </RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>

      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton class="text-schwarz font-medium" @click="openQuickCapture">
            <Plus class="size-4" />
            <span>Add Task</span>
            <kbd class="ml-auto text-[10px] text-muted-foreground font-mono">Q</kbd>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarHeader>

    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in primaryNav" :key="item.title">
              <SidebarMenuButton as-child :is-active="isActive(item.url)">
                <RouterLink :to="item.url">
                  <component :is="item.icon" />
                  <span>{{ item.title }}</span>
                </RouterLink>
              </SidebarMenuButton>
              <SidebarMenuBadge v-if="item.badge">{{ item.badge }}</SidebarMenuBadge>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>

      <SidebarSeparator />

      <SidebarGroup>
        <SidebarGroupLabel>Review</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton as-child :is-active="isActive('/weekly-review')">
                <RouterLink to="/weekly-review">
                  <RefreshCw />
                  <span>Weekly Review</span>
                </RouterLink>
              </SidebarMenuButton>
              <SidebarMenuBadge v-if="reviewDue">
                <span
                  class="rounded-full bg-amber-100 px-1.5 py-0.5 text-[10px] font-semibold text-amber-800"
                  >Due</span
                >
              </SidebarMenuBadge>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>

      <SidebarSeparator />

      <SidebarGroup>
        <SidebarGroupLabel>Manage</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton as-child :is-active="isActive('/contexts')">
                <RouterLink to="/contexts"><Tags /><span>Contexts</span></RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
            <SidebarMenuItem>
              <SidebarMenuButton as-child :is-active="isActive('/areas')">
                <RouterLink to="/areas"><Mountain /><span>Areas</span></RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
            <SidebarMenuItem>
              <SidebarMenuButton as-child :is-active="isActive('/settings')">
                <RouterLink to="/settings"><Settings /><span>Settings</span></RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>

    <SidebarFooter>
      <SidebarMenu>
        <SidebarMenuItem>
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <SidebarMenuButton size="lg">
                <Avatar class="h-8 w-8 rounded-lg">
                  <AvatarFallback class="rounded-lg bg-schwarz text-white text-xs">
                    {{ auth.user?.username?.charAt(0)?.toUpperCase() ?? "?" }}
                  </AvatarFallback>
                </Avatar>
                <div class="grid flex-1 text-left text-sm leading-tight">
                  <span class="truncate font-medium">{{ auth.user?.username ?? "User" }}</span>
                  <span class="truncate text-xs text-muted-foreground">{{
                    auth.user?.email ?? ""
                  }}</span>
                </div>
              </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent side="right" align="end" :side-offset="4">
              <DropdownMenuItem @click="auth.logout()">
                <LogOut class="mr-2 size-4" />
                Log out
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>
  </Sidebar>
</template>
