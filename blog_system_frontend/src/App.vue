<script setup lang="ts">
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import ToastProvider from '@/components/ToastProvider.vue'

const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
</script>

<template>
  <AdminLayout v-if="isAdminRoute">
    <RouterView />
  </AdminLayout>
  <AppLayout v-else>
    <RouterView />
  </AppLayout>
  <ToastProvider />
</template>
