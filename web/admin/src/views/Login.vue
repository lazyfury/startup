<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { UserApi } from '../api/apis/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const submit = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.error('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const json = await UserApi.login({ username: form.value.username, password: form.value.password })
    if (json.code >= 200 && json?.data?.token) {
      userStore.setToken(json.data.token)
      await userStore.loadProfile()
      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/'
      router.replace(redirect)
    } else {
      ElMessage.error(json?.message || '登录失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '请求失败')
  } finally {
    loading.value = false
  }
}

const createAdminDebug = async () => {
  try {
    const json = await UserApi.createAdminDebug()
    if (json.code === 201 || json.code === 200) {
      ElMessage.success('管理员已创建或存在')
    } else if (json.code === 403) {
      ElMessage.warning('调试接口已关闭')
    } else {
      ElMessage.error(json?.message || '创建失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '请求失败')
  }
}
</script>

<template>
  <el-container style="min-height: 100vh">
    <el-main style="min-height: 100vh; display: flex; align-items: center; justify-content: center">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-0 items-stretch">

        <el-card style="width: 420px;border-radius: 12px 0 0 12px;" class="flex flex-col" body-class="p-4 flex-1 bg-primary flex flex-col !text-white" shadow="never">
          <div style="text-align:center; margin-bottom: 16px">
            <h2>平台介绍</h2>
            <div style="font-size: 12px;">Startup Admin 管理平台</div>
          </div>
          <div class="space-y-2 text-sm">
            <p>支持权限、配置分组、动态菜单与标签导航。</p>
            <p>采用 Element Plus、UnoCSS、Pinia、Vue Router 与 Vite。</p>
          </div>
          <el-divider />
          <ul class="text-sm list-disc pl-5 flex-1">
            <li>现代化组件库与类型提示，开发效率高</li>
            <li>响应式布局与暗色主题可拓展</li>
            <li>模块化路由与菜单配置，便于扩展</li>
          </ul>
        </el-card>

        <el-card style="width: 420px;border-radius: 0 12px 12px 0;border-left: 0;" class="p-4" shadow="never">
          <div style="text-align:center; margin-bottom: 16px">
            <h2 class="text-2xl font-bold">Admin 登录</h2>
            <div style="font-size: 12px; color: var(--el-text-color-secondary)">欢迎使用后台管理系统</div>
          </div>
          <el-form label-position="top">
            <el-form-item label="账号">
              <el-input size="large" v-model="form.username" placeholder="请输入账号" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input size="large" v-model="form.password" type="password" placeholder="请输入密码" />
            </el-form-item>
            <div class="flex flex-col gap-2">
              <el-button type="primary" size="large" :loading="loading" class="w-full" @click="submit">登录</el-button>
              <div>
                <el-button class="w-full" size="large" @click="createAdminDebug">创建管理员（调试）</el-button>
              </div>
            </div>
          </el-form>
          <el-divider />
          <div class="text-center text-sm" style="color: var(--el-text-color-secondary)">
            首次使用可点击“创建管理员（调试）”初始化账号
          </div>
        </el-card>
        
      </div>
    </el-main>
  </el-container>

</template>

<style scoped>

</style>
