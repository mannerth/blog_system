<script setup lang="ts">
import { RouterView } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import ToastProvider from '@/components/ToastProvider.vue'
</script>

<template>
  <AppLayout>
    <RouterView />
  </AppLayout>
  <ToastProvider />
</template>
