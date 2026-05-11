<template>
  <div class="state">
    <div class="state__icon">◎</div>
    <h3 class="state__title">{{ title }}</h3>
    <p v-if="description" class="state__desc">{{ description }}</p>
    <div v-if="$slots.action" class="state__action">
      <slot name="action" />
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
    title: '暂无内容',
    description: '',
  },
)
</script>

<style scoped>
.state {
  padding: 32px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  border: 1px dashed var(--color-border);
  text-align: center;
  display: grid;
  gap: 12px;
  justify-items: center;
}

.state__icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: var(--color-primary-soft);
  display: grid;
  place-items: center;
  color: var(--color-primary-strong);
  font-size: 20px;
}

.state__title {
  margin: 0;
}

.state__desc {
  margin: 0;
  color: var(--color-text-muted);
}

.state__action {
  margin-top: 6px;
}
</style>
