<script setup lang="ts">
defineProps<{ value: any; row?: any; index?: number; options?: any }>()
</script>

<template>
  <el-tag :type="value ? 'success' : 'danger'">
  {{ value ? '是' : '否' }}
  </el-tag>
</template>

<style scoped>
</style>
