<template>
  <section class="placeholder">
    <div class="placeholder__header">
      <p class="placeholder__eyebrow">Coming soon</p>
      <h1>{{ title }}</h1>
      <p class="placeholder__desc">{{ description }}</p>
    </div>
    <div class="placeholder__card">
      <div class="placeholder__row">
        <span class="placeholder__label">Route</span>
        <span class="placeholder__value">{{ route.path }}</span>
      </div>
      <div v-if="hasId" class="placeholder__row">
        <span class="placeholder__label">ID</span>
        <span class="placeholder__value">{{ routeId }}</span>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => (route.meta.title as string) || 'Page')
const description = computed(
  () => (route.meta.description as string) || 'This page is being prepared.'
)
const routeId = computed(() => (route.params.id ? String(route.params.id) : ''))
const hasId = computed(() => routeId.value.length > 0)
</script>

<style scoped>
.placeholder {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 32px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.placeholder__eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.18em;
  color: var(--color-text-muted);
  margin-bottom: 8px;
}

.placeholder__desc {
  color: var(--color-text-muted);
  max-width: 520px;
}

.placeholder__card {
  display: grid;
  gap: 12px;
  padding: 16px 18px;
  border-radius: var(--radius-md);
  background: var(--color-surface-muted);
  border: 1px solid var(--color-border);
}

.placeholder__row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.placeholder__label {
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 11px;
}

.placeholder__value {
  color: var(--color-heading);
  font-weight: 600;
}

@media (max-width: 640px) {
  .placeholder {
    padding: 24px;
  }

  .placeholder__row {
    flex-direction: column;
    gap: 6px;
  }
}
</style>
