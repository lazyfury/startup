<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'

const props = defineProps<{ menus: any[]; collapsed: boolean }>()
const router = useRouter()
const route = useRoute()
const active = computed(() => route.path)

const handleSelect = (index: string) => {
  router.push(index)
}
</script>

<template>
  <el-menu class="sidebar-menu" :default-active="active" :collapse="props.collapsed" @select="handleSelect" router>
    <template v-for="m in props.menus" :key="m.path">
      <el-sub-menu v-if="m.children?.length" :index="m.path">
        <template #title>
          <el-icon v-if="m.icon">
            <component :is="m.icon" />
          </el-icon>
          <span>{{ m.title }}</span>
        </template>
        <el-menu-item v-for="c in m.children" :key="c.path" :index="c.path">
          <el-icon v-if="c.icon">
            <component :is="c.icon" />
          </el-icon>
          <span>{{ c.title }}</span>
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item v-else :index="m.path">
        <el-icon v-if="m.icon">
          <component :is="m.icon" />
        </el-icon>
        <span>{{ m.title }}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<style scoped lang="scss">
.sidebar-menu {
  transition: all 0.3s !important;
}
</style>
