<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useContextsStore } from '@/stores/contexts'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Trash2, Plus } from 'lucide-vue-next'

const contexts = useContextsStore()
onMounted(() => contexts.fetch())

const showAdd = ref(false)
const newName = ref('')

async function addContext() {
  if (!newName.value.trim()) return
  await contexts.add({ name: newName.value.trim() })
  newName.value = ''
  showAdd.value = false
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between">
      <h1 class="font-serif text-2xl font-bold text-schwarz">Contexts</h1>
      <Button size="sm" variant="outline" @click="showAdd = !showAdd">
        <Plus class="mr-1 size-4" /> Add
      </Button>
    </div>

    <div v-if="showAdd" class="mt-3 flex gap-2">
      <Input v-model="newName" placeholder="Context name (e.g. @Office)" class="flex-1" @keydown.enter="addContext" />
      <Button size="sm" class="bg-turkis text-white" @click="addContext">Save</Button>
    </div>

    <div class="mt-4 divide-y divide-grau-1">
      <div v-for="ctx in contexts.items" :key="ctx.id" class="flex items-center justify-between py-3 px-1">
        <div>
          <div class="text-sm font-medium text-schwarz">{{ ctx.name }}</div>
          <div v-if="ctx.description" class="mt-0.5 text-xs text-grau-3">{{ ctx.description }}</div>
        </div>
        <button class="text-grau-3 hover:text-rot" @click="contexts.remove(ctx.id)">
          <Trash2 class="size-4" />
        </button>
      </div>
    </div>
  </div>
</template>
