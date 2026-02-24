<script setup lang="ts">
import type { SidebarProps } from "@/components/ui/sidebar";

import {
  Frame,
  Map,
  PieChart,
  Settings2,
  LayoutDashboard,
  Inbox,
  Calendar,
  Blocks,
  Trash2,
  Users,
  MessageCircleQuestion,
} from "lucide-vue-next";
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";
import NavMain from "@/components/NavMain.vue";
import NavProjects from "@/components/NavProjects.vue";
import NavUser from "@/components/NavUser.vue";

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar";
import IconLogo from "./icons/IconLogo.vue";

const props = withDefaults(defineProps<SidebarProps>(), {});

// This is sample data.
const data = {
  user: {
    name: "Tue",
    email: "m@example.com",
    avatar: "/avatars/shadcn.jpg",
  },
  navMain: [
    {
      title: "Home",
      url: "/dashboard",
      icon: LayoutDashboard,
    },
    {
      title: "Inbox",
      url: "/inbox",
      icon: Inbox,
      badge: "20",
    },
    {
      title: "Users",
      url: "/users",
      icon: Users,
    },
  ],
  navSecondary: [
    {
      title: "Calendar",
      url: "#",
      icon: Calendar,
    },
    {
      title: "Settings",
      url: "#",
      icon: Settings2,
    },
    {
      title: "Templates",
      url: "#",
      icon: Blocks,
    },
    {
      title: "Trash",
      url: "#",
      icon: Trash2,
    },
    {
      title: "Help",
      url: "#",
      icon: MessageCircleQuestion,
    },
  ],
  projects: [
    {
      name: "Design Engineering",
      url: "#",
      icon: Frame,
    },
    {
      name: "Sales & Marketing",
      url: "#",
      icon: PieChart,
    },
    {
      name: "Travel",
      url: "#",
      icon: Map,
    },
  ],
};
</script>

<template>
  <Sidebar v-bind="props">
    <SidebarHeader>
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton size="lg" as-child>
            <RouterLink to="/dashboard">
              <div
                class="text-sidebar-primary-foreground flex aspect-square size-15 items-center justify-center rounded-lg"
              >
                <!-- <img src="/logo.png" alt="Logo" class="size-8 bg-gray-50" /> -->
                <IconLogo />
              </div>
              <div class="grid flex-1 text-left text-sm leading-tight">
                <span class="truncate font-medium">Tue</span>
                <span class="truncate text-xs">GTD System</span>
              </div>
            </RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
      <NavMain :items="data.navMain" />
    </SidebarHeader>
    <SidebarContent>
      <NavProjects :projects="data.projects" />
    </SidebarContent>
    <SidebarFooter>
      <NavUser :user="data.user" />
    </SidebarFooter>
    <SidebarRail />
  </Sidebar>
</template>
