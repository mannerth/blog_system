<template>
  <span class="base-tag" :class="`base-tag--${variant}`">
    <slot />
  </span>
</template>

<script setup lang="ts">
type TagVariant = 'solid' | 'outline'

withDefaults(
  defineProps<{
    variant?: TagVariant
  }>(),
  {
    variant: 'solid',
  },
)
</script>

<style scoped>
.base-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.base-tag--solid {
  background: var(--color-primary-soft);
  color: var(--color-primary-strong);
}

.base-tag--outline {
  background: transparent;
  border: 1px solid rgba(28, 155, 138, 0.4);
  color: var(--color-primary-strong);
}
</style>
