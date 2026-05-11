<template>
  <div class="pagination">
    <div class="pagination__info">共 {{ total }} 条，{{ totalPages }} 页</div>
    <div class="pagination__controls">
      <button class="pagination__button" type="button" :disabled="page <= 1" @click="prev">上一页</button>
      <span class="pagination__current">{{ page }}</span>
      <button class="pagination__button" type="button" :disabled="page >= totalPages" @click="next">下一页</button>
      <select class="pagination__size" :value="size" @change="onSizeChange">
        <option v-for="option in pageSizes" :key="option" :value="option">{{ option }} / 页</option>
      </select>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    total: number
    page: number
    size: number
    pageSizes?: number[]
  }>(),
  {
    total: 0,
    page: 1,
    size: 10,
    pageSizes: () => [10, 20, 30, 50],
  },
)

const emit = defineEmits<{
  (event: 'update:page', value: number): void
  (event: 'update:size', value: number): void
}>()

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.size)))

const prev = () => emit('update:page', Math.max(1, props.page - 1))
const next = () => emit('update:page', Math.min(totalPages.value, props.page + 1))

const onSizeChange = (event: Event) => {
  const target = event.target as HTMLSelectElement
  emit('update:size', Number(target.value))
}
</script>

<style scoped>
.pagination {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-radius: var(--radius-md);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
}

.pagination__info {
  font-size: 13px;
  color: var(--color-text-muted);
}

.pagination__controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination__button {
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  border: 1px solid rgba(28, 155, 138, 0.3);
  background: transparent;
  color: var(--color-primary-strong);
  cursor: pointer;
}

.pagination__button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination__current {
  min-width: 32px;
  text-align: center;
  font-weight: 600;
  color: var(--color-heading);
}

.pagination__size {
  padding: 6px 10px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
}

@media (max-width: 640px) {
  .pagination {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
