<template>
  <button
    class="base-button"
    :class="`base-button--${variant}`"
    :type="type"
    :disabled="isDisabled"
    :aria-busy="loading"
  >
    <span v-if="loading" class="base-button__spinner" aria-hidden="true"></span>
    <span class="base-button__label"><slot /></span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

type ButtonVariant = 'primary' | 'ghost' | 'outline'

const props = withDefaults(
  defineProps<{
    variant?: ButtonVariant
    loading?: boolean
    disabled?: boolean
    type?: 'button' | 'submit' | 'reset'
  }>(),
  {
    variant: 'primary',
    loading: false,
    disabled: false,
    type: 'button',
  },
)

const isDisabled = computed(() => props.disabled || props.loading)
const { variant, loading, type } = props
</script>

<style scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: var(--radius-sm);
  border: none;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.02em;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease, color 0.2s ease;
}

.base-button:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(28, 155, 138, 0.28);
}

.base-button--primary {
  color: #ffffff;
  background: linear-gradient(120deg, var(--color-primary), #3ab7a0);
  box-shadow: var(--shadow-sm);
}

.base-button--ghost {
  color: var(--color-primary-strong);
  background: var(--color-primary-soft);
}

.base-button--outline {
  color: var(--color-primary-strong);
  background: transparent;
  border: 1px solid rgba(28, 155, 138, 0.3);
}

.base-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.base-button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}

.base-button__spinner {
  width: 14px;
  height: 14px;
  border-radius: 999px;
  border: 2px solid currentColor;
  border-right-color: transparent;
  border-top-color: transparent;
  animation: base-button-spin 0.6s linear infinite;
}

.base-button__label {
  display: inline-flex;
  align-items: center;
}

@keyframes base-button-spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
