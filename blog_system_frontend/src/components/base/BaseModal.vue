<template>
  <teleport to="body">
    <transition name="modal">
      <div v-if="props.modelValue" class="base-modal" role="dialog" aria-modal="true">
        <div class="base-modal__overlay" @click="close"></div>
        <div class="base-modal__panel">
          <header class="base-modal__header">
            <div>
              <p v-if="eyebrow" class="base-modal__eyebrow">{{ eyebrow }}</p>
              <h3 class="base-modal__title">{{ title }}</h3>
            </div>
            <button class="base-modal__close" type="button" @click="close">关闭</button>
          </header>
          <div class="base-modal__body">
            <slot />
          </div>
          <footer v-if="$slots.footer" class="base-modal__footer">
            <slot name="footer" />
          </footer>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    modelValue: boolean
    title: string
    eyebrow?: string
  }>(),
  {
    modelValue: false,
    title: '提示',
    eyebrow: '',
  },
)

const emit = defineEmits<{
  (event: 'update:modelValue', value: boolean): void
  (event: 'close'): void
}>()

const close = () => {
  emit('update:modelValue', false)
  emit('close')
}
</script>

<style scoped>
.base-modal {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.base-modal__overlay {
  position: absolute;
  inset: 0;
  background: rgba(13, 42, 47, 0.45);
}

.base-modal__panel {
  position: relative;
  width: min(520px, 90vw);
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.base-modal__header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.base-modal__eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-text-muted);
}

.base-modal__title {
  margin: 0;
}

.base-modal__close {
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
}

.base-modal__footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
