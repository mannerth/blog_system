<template>
  <label class="base-field" :class="{ 'base-field--error': Boolean(error) }">
    <span v-if="label" class="base-field__label">{{ label }}</span>
    <div class="base-field__control">
      <textarea
        class="base-field__input base-field__textarea"
        :rows="rows"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        @input="onInput"
      ></textarea>
      <button v-if="showClear" type="button" class="base-field__clear" @click="clear">
        清除
      </button>
    </div>
    <span v-if="error" class="base-field__error">{{ error }}</span>
  </label>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    modelValue: string
    label?: string
    placeholder?: string
    error?: string
    clearable?: boolean
    disabled?: boolean
    rows?: number
  }>(),
  {
    modelValue: '',
    placeholder: '',
    error: '',
    clearable: false,
    disabled: false,
    rows: 4,
  },
)

const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void
  (event: 'clear'): void
}>()

const showClear = computed(() => props.clearable && !props.disabled && props.modelValue.length > 0)

const onInput = (event: Event) => {
  const target = event.target as HTMLTextAreaElement
  emit('update:modelValue', target.value)
}

const clear = () => {
  emit('update:modelValue', '')
  emit('clear')
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
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  transition: border 0.2s ease, box-shadow 0.2s ease;
  resize: vertical;
}

.base-field__textarea {
  min-height: 120px;
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

.base-field__clear {
  position: absolute;
  right: 10px;
  top: 12px;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  font-size: 12px;
  cursor: pointer;
}

.base-field__error {
  color: #c0392b;
  font-size: 12px;
}

.base-field--error .base-field__input {
  border-color: rgba(192, 57, 43, 0.5);
}
</style>
