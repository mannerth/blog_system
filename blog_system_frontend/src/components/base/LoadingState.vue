<template>
  <div class="state">
    <span class="state__spinner" aria-hidden="true"></span>
    <div class="state__content">
      <h3 class="state__title">{{ title }}</h3>
      <p v-if="description" class="state__desc">{{ description }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    title?: string
    description?: string
  }>(),
  {
    title: '加载中',
    description: '内容正在努力呈现',
  },
)
</script>

<style scoped>
.state {
  padding: 28px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  gap: 16px;
}

.state__spinner {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: 4px solid rgba(28, 155, 138, 0.2);
  border-top-color: var(--color-primary);
  animation: spin 0.8s linear infinite;
}

.state__title {
  margin: 0;
}

.state__desc {
  margin: 6px 0 0;
  color: var(--color-text-muted);
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
