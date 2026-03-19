<script lang="ts" setup>
import { onMounted } from 'vue'
import { useReviewStore } from '@/stores/review'
import { Button } from '@/components/ui/button'

const store = useReviewStore()
onMounted(() => store.loadCurrent())

async function startOrAdvance() {
  if (!store.review) {
    await store.start()
  } else if (store.currentStepIndex >= 4) {
    await store.complete()
  } else {
    await store.advance()
  }
}
</script>

<template>
  <div>
    <!-- No active review -->
    <div v-if="!store.review" class="flex flex-col items-center py-16">
      <h1 class="font-serif text-2xl font-bold text-schwarz">Weekly Review</h1>
      <p class="mt-2 text-sm text-grau-5">Time to review your system. This takes about 30 minutes.</p>
      <Button class="mt-6 bg-turkis-dark text-white hover:bg-turkis-dark/90" @click="store.start()">
        Start Weekly Review
      </Button>
    </div>

    <!-- Active review -->
    <div v-else>
      <div class="flex items-center justify-between">
        <RouterLink to="/inbox" class="text-sm text-turkis hover:underline">Exit Review</RouterLink>
        <span v-if="!store.isDone" class="text-[13px] text-grau-5">Step {{ store.currentStepIndex + 1 }} of 5</span>
      </div>

      <!-- Progress bar -->
      <div v-if="!store.isDone" class="mt-3 h-1 rounded-full bg-grau-1 overflow-hidden">
        <div class="h-full rounded-full bg-turkis transition-all" :style="{ width: `${((store.currentStepIndex + 1) / 5) * 100}%` }" />
      </div>

      <!-- Done state -->
      <div v-if="store.isDone" class="flex flex-col items-center py-16">
        <div class="flex size-16 items-center justify-center rounded-full bg-turkis-surface text-2xl">✓</div>
        <h2 class="mt-4 font-serif text-2xl font-bold">Sauber gmacht!</h2>
        <p class="mt-1 text-sm text-grau-5">Your system is up to date. See you next week.</p>
        <RouterLink to="/inbox" class="mt-6 text-sm text-turkis hover:underline">Back to Inbox</RouterLink>
      </div>

      <!-- Current step -->
      <div v-else class="mt-4">
        <h1 class="font-serif text-2xl font-bold text-schwarz">
          {{ store.STEP_LABELS[store.review.currentStep] }}
        </h1>
        <p class="mt-1.5 text-sm text-grau-5 leading-relaxed">
          {{ store.STEP_QUESTIONS[store.review.currentStep] }}
        </p>

        <!-- Step breadcrumbs -->
        <div class="mt-8 flex justify-center gap-4 text-[11px]">
          <span
            v-for="(step, i) in store.STEP_ORDER.slice(0, 5)"
            :key="step"
            :class="{
              'text-grun': i < store.currentStepIndex,
              'font-semibold text-schwarz': i === store.currentStepIndex,
              'text-grau-3': i > store.currentStepIndex,
            }"
          >
            <template v-if="i < store.currentStepIndex">✓ </template>
            {{ store.STEP_LABELS[step].replace('Review ', '') }}
          </span>
        </div>

        <!-- Next button -->
        <Button class="mt-6 w-full bg-turkis-dark text-white hover:bg-turkis-dark/90" @click="startOrAdvance">
          <template v-if="store.currentStepIndex < 4">
            Next: {{ store.STEP_LABELS[store.STEP_ORDER[store.currentStepIndex + 1]] }} →
          </template>
          <template v-else>Complete Review</template>
        </Button>
      </div>
    </div>
  </div>
</template>
