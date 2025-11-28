<script setup lang="ts">
import type { TabPaneName } from 'element-plus';
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

type TabItem = { title: string; path: string }
const STORAGE_KEY = 'admin-tabs'
const STORAGE_ACTIVE_KEY = 'admin-tabs-active'

const route = useRoute()
const router = useRouter()
const tabs = ref<TabItem[]>([])
const active = ref('')

const save = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(tabs.value))
  localStorage.setItem(STORAGE_ACTIVE_KEY, active.value || '')
}
const load = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) tabs.value = JSON.parse(raw)
    const act = localStorage.getItem(STORAGE_ACTIVE_KEY)
    if (act) active.value = act
  } catch {}
}

const ensureTab = (r = route) => {
  const path = r.path
  const title = (r.meta?.title as string) || path
  if (!tabs.value.some((t) => t.path === path)) {
    tabs.value.push({ path, title })
    save()
  }
  active.value = path
  try { localStorage.setItem(STORAGE_ACTIVE_KEY, active.value) } catch {}
}

const remove = (path: string) => {
  const idx = tabs.value.findIndex((t) => t.path === path)
  if (idx >= 0) {
    tabs.value.splice(idx, 1)
    save()
    if (active.value === path) {
      const next = tabs.value[idx - 1] || tabs.value[idx] || { path: '/' }
      router.push(next.path)
    }
  }
}

const clickTab = (path: string) => {
  router.push(path)
}

// 先恢复缓存，避免初始化时覆盖
load()

onMounted(() => {
  if (active.value && active.value !== route.path) {
    router.push(active.value)
  } else {
    ensureTab(route)
  }
})

// 路由变化时同步标签，不在初始化时立即触发，避免覆盖缓存
watch(() => route.fullPath, () => ensureTab(route))
</script>

<template>
  <el-tabs v-model="active" class="tabs-bar" type="card" @tab-click="(pane: any) => clickTab(pane.paneName)" @tab-remove="(name: TabPaneName) => remove(name as string)">
    <el-tab-pane
      v-for="t in tabs"
      :key="t.path"
      :name="t.path"
      :label="t.title"
      :closable="t.path !== '/'"
    />
  </el-tabs>
</template>

<style lang="scss" scoped>
:deep.tabs-bar {

   .el-tabs__item.is-active{
    border-bottom-color: #f5f5f5 !important;
  }
}
</style>
