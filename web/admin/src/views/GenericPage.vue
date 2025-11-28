<script setup lang="ts">
defineOptions({ name: 'GenericPage' })
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<template>
  <el-card shadow="never">
    <h3>{{ route.meta.title || route.path }}</h3>
    <p>这是动态菜单页面：{{ route.path }}</p>
  </el-card>
  
</template>
