<template>
  <label class="base-field" :class="{ 'base-field--error': Boolean(error) }">
    <span v-if="label" class="base-field__label">{{ label }}</span>
    <div class="base-field__control">
      <select
        class="base-field__input"
        :value="normalizedValue"
        :disabled="disabled"
        @change="onChange"
      >
        <option v-if="placeholder && !hasEmptyOption" value="" disabled>{{ placeholder }}</option>
        <option v-for="option in options" :key="String(option.value)" :value="option.value" :disabled="option.disabled">
          {{ option.label }}
        </option>
      </select>
      <span class="base-field__chevron" aria-hidden="true">▾</span>
    </div>
    <span v-if="error" class="base-field__error">{{ error }}</span>
  </label>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export interface SelectOption {
  label: string
  value: string | number
  disabled?: boolean
}

const props = withDefaults(
  defineProps<{
    modelValue: string | number | null
    options: SelectOption[]
    label?: string
    placeholder?: string
    error?: string
    disabled?: boolean
  }>(),
  {
    modelValue: '',
    label: '',
    placeholder: '',
    error: '',
    disabled: false,
  },
)

const emit = defineEmits<{
  (event: 'update:modelValue', value: string | number): void
}>()

const normalizedValue = computed(() => (props.modelValue === null ? '' : String(props.modelValue)))
const hasEmptyOption = computed(() => props.options.some((option) => String(option.value) === ''))

const onChange = (event: Event) => {
  const target = event.target as HTMLSelectElement
  const selected = props.options.find((option) => String(option.value) === target.value)
  emit('update:modelValue', selected ? selected.value : target.value)
}
</script>

<style scoped>
.base-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.base-field__label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-text-muted);
}

.base-field__control {
  position: relative;
}

.base-field__input {
  width: 100%;
  padding: 12px 38px 12px 14px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  transition: border 0.2s ease, box-shadow 0.2s ease;
  appearance: none;
}

.base-field__input:focus {
  outline: none;
  border-color: rgba(28, 155, 138, 0.5);
  box-shadow: 0 0 0 3px rgba(28, 155, 138, 0.18);
}

.base-field__input:disabled {
  background: var(--color-surface-muted);
  cursor: not-allowed;
}

.base-field__chevron {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-muted);
  font-size: 12px;
  pointer-events: none;
}

.base-field__error {
  color: #c0392b;
  font-size: 12px;
}

.base-field--error .base-field__input {
  border-color: rgba(192, 57, 43, 0.5);
}
</style>
