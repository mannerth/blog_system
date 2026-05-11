<template>
  <div class="toast-layer">
    <div v-for="toast in toasts" :key="toast.id" class="toast" :class="`toast--${toast.type}`">
      <div class="toast__content">
        <strong class="toast__title">{{ toast.title }}</strong>
        <span v-if="toast.message" class="toast__message">{{ toast.message }}</span>
      </div>
      <button class="toast__close" type="button" @click="removeToast(toast.id)">关闭</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive } from 'vue'

export type ToastType = 'success' | 'error' | 'warning'

interface ToastItem {
  id: string
  title: string
  message?: string
  type: ToastType
  timeout: number
}

const toasts = reactive<ToastItem[]>([])

const addToast = (toast: ToastItem) => {
  toasts.push(toast)
  if (toast.timeout > 0) {
    window.setTimeout(() => removeToast(toast.id), toast.timeout)
  }
}

const removeToast = (id: string) => {
  const index = toasts.findIndex((item) => item.id === id)
  if (index >= 0) toasts.splice(index, 1)
}

const handleToastEvent = (event: Event) => {
  const custom = event as CustomEvent<{
    title: string
    message?: string
    type?: ToastType
    timeout?: number
  }>
  const detail = custom.detail
  if (!detail?.title) return
  addToast({
    id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
    title: detail.title,
    message: detail.message,
    type: detail.type ?? 'success',
    timeout: detail.timeout ?? 3000,
  })
}

onMounted(() => window.addEventListener('toast', handleToastEvent))
onUnmounted(() => window.removeEventListener('toast', handleToastEvent))

defineExpose({
  addToast,
  removeToast,
})
</script>

<style scoped>
.toast-layer {
  position: fixed;
  right: 24px;
  bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 9999;
}

.toast {
  width: min(320px, 90vw);
  padding: 14px 16px;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
  background: var(--color-heading);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  animation: toast-in 0.2s ease;
}

.toast--success {
  background: #1a8f7f;
}

.toast--error {
  background: #c0392b;
}

.toast--warning {
  background: #d9822b;
}

.toast__content {
  display: grid;
  gap: 4px;
}

.toast__title {
  font-size: 14px;
}

.toast__message {
  font-size: 12px;
  opacity: 0.85;
}

.toast__close {
  border: none;
  background: transparent;
  color: inherit;
  font-size: 12px;
  cursor: pointer;
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 640px) {
  .toast-layer {
    left: 20px;
    right: 20px;
  }
}
</style>
