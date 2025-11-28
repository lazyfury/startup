<script setup lang="ts">
import { ref, onMounted, watch, computed, onBeforeMount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import SidebarMenu from '../modules/SidebarMenu.vue'
import TabsBar from '../modules/TabsBar.vue'
import { fetchMenus } from '../modules/menu'
import { useUserStore } from '../stores/user'
import { Fold } from '@element-plus/icons-vue'

const COLLAPSE_KEY = 'admin-sidebar-collapsed'
const collapsed = ref(false)
const router = useRouter()
const route = useRoute()
const menus = ref<any[]>([])
const userStore = useUserStore()

const logout = () => {
  localStorage.removeItem('token')
  ElMessage.success('已退出')
  router.replace('/login')
}

onBeforeMount(async () => {
  try {
    const raw = localStorage.getItem(COLLAPSE_KEY)
    if (raw !== null) collapsed.value = raw === '1' || raw === 'true'
  } catch { }
})

onMounted(async () => {
  menus.value = await fetchMenus()

  // menus.value.forEach(add)

})

watch(collapsed, (v) => {
  try {
    localStorage.setItem(COLLAPSE_KEY, v ? '1' : '0')
  } catch { }
})

const breadcrumbs = computed(() => {
  const p = route.path
  const dfs = (list: any[]): any[] | null => {
    for (const m of list || []) {
      if (m.path === p) return [m]
      if (m.children?.length) {
        const sub = dfs(m.children)
        if (sub) return [m, ...sub]
      }
    }
    return null
  }
  const chain = dfs(menus.value) || []
  if (!chain.length) {
    const title = (route.meta?.title as string) || p
    return [{ title, path: p }]
  }
  return chain.map((m, i) => {
    const target = i < chain.length - 1 && m.children?.length ? m.children[0] : m
    return { title: m.title, path: target.path }
  })
})
</script>

<template>
  <el-container style="min-height: 100vh">
    <el-aside  :width="collapsed ? '64px' : '200px'" style="transition: all 0.4s !important;">
      <div class="flex flex-row items-center justify-center p-2">
        <el-avatar :size="30" :src="userStore.user?.avatarUrl"></el-avatar>
        <div v-if="!collapsed" class="text-3xl font-bold text-white ml-2">{{ 'Admin' }}</div>
      </div>

      <SidebarMenu :menus="menus" :collapsed="collapsed" />
    </el-aside>
    <el-container>
      <el-header class=" bg-[#fefefe] flex items-center gap-4">
        <div class="toolbar flex-1">
          <el-button link @click="collapsed = !collapsed">
            <el-icon :size="24">
              <Fold :style="[{
                transform: collapsed ? 'rotate(180deg)' : 'rotate(0deg)'
              }]" />
            </el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="(c,i) in breadcrumbs"
              :key="c.path"
              :to="i < breadcrumbs.length - 1 ? c.path : undefined"
            >
              {{ c.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
          <div style="flex:1"></div>
          <el-popover placement="bottom-end" trigger="click">
            <template #reference>
              <div class="flex items-center gap-2 cursor-pointer">
                <el-avatar :size="32" :src="userStore.user?.avatarUrl">{{ userStore.user?.username?.[0] || 'U'
                }}</el-avatar>
                <span>{{ userStore.user?.username || '未登录' }}</span>
              </div>
            </template>
            <div class="flex flex-col items-center">
              <div class="flex items-center gap-3 mb-2">
                <el-avatar :size="40" :src="userStore.user?.avatarUrl">{{ userStore.user?.username?.[0] || 'U'
                }}</el-avatar>
                <div>
                  <div class="font-600">{{ userStore.user?.username || '未登录' }}</div>
                  <div class="text-gray-500 text-12px">ID: {{ userStore.user?.id ?? '-' }}</div>
                </div>
              </div>
              <el-divider />
              <div class="flex gap-2 justify-end">
                <el-button link @click="logout">退出登录</el-button>
              </div>
            </div>
          </el-popover>
        </div>
      </el-header>
      <el-main class="bg-[#f9f9f9]">
        <TabsBar />
        <router-view v-slot="{ Component, route }">
          <keep-alive :include="['GenericPage']">
            <component :is="Component" :key="route.fullPath" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 8px
}
</style>
