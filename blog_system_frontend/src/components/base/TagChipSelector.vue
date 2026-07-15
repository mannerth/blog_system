<template>
  <div class="tag-chip-field">
    <span v-if="label" class="tag-chip-field__label">{{ label }}</span>
    <div class="tag-chip-field__chips">
      <button
        v-for="option in options"
        :key="String(option.value)"
        class="tag-chip-field__chip"
        :class="{ 'tag-chip-field__chip--active': isSelected(option.value) }"
        type="button"
        @click="toggle(option.value)"
      >
        {{ option.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface SelectOption {
  label: string
  value: string | number
  disabled?: boolean
}

const props = defineProps<{
  modelValue: number[]
  options: SelectOption[]
  label?: string
}>()

const emit = defineEmits<{
  (event: 'update:modelValue', value: number[]): void
}>()

const isSelected = (value: string | number) => props.modelValue.includes(Number(value))

const toggle = (value: string | number) => {
  const numValue = Number(value)
  const next = props.modelValue.includes(numValue)
    ? props.modelValue.filter((v) => v !== numValue)
    : [...props.modelValue, numValue]
  emit('update:modelValue', next)
}
</script>

<style scoped>
.tag-chip-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tag-chip-field__label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-text-muted);
}

.tag-chip-field__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-chip-field__chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, border-color 0.2s ease;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  color: var(--color-text-muted);
}

.tag-chip-field__chip:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.tag-chip-field__chip--active {
  background: var(--color-primary-soft);
  border-color: var(--color-primary);
  color: var(--color-primary-strong);
}

.tag-chip-field__chip--active:hover {
  background: var(--color-primary);
  color: #fff;
}
</style>
