<script setup lang="ts">
defineProps<{ value: any; row?: any; index?: number; options?: any }>()
</script>

<template>
  {{ String(value ?? '') }}
</template>

<style scoped>
</style>
