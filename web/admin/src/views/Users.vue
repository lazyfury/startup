<script setup lang="ts">
import ConfigTable, { type Column } from '../components/ConfigTable.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { AdminUserApi } from '../api/apis/admin-user'
import type { AdminUser } from '../api/types'

const columns:Column[] = [
  { label: 'ID', prop: 'id', width: 80, align: 'center' },
  { label: '账号', prop: 'username', width: 160 },
  { label: '启用', prop: 'enabled', width: 80, align: 'center' },
  { label: '角色', slot: 'roles' },
  { label: '更新于', prop: 'updatedAt' },
  { label: '操作', slot: 'actions', width: 220, align: 'center' }
]

const request = async ({ page, pageSize }: { page: number; pageSize: number }) => {
  const json = await AdminUserApi.list({ page: page - 1, size: pageSize })
  const data = json.data
  return { list: data.content, total: data.totalElements }
}

const dialogVisible = ref(false)
const editing = ref<AdminUser | null>(null)
const emptyForm = (): AdminUser => ({ username: '', password: '', enabled: true, tenantId: null, roles: [] })
const form = ref<AdminUser>(emptyForm())

const openCreate = () => { editing.value = null; form.value = emptyForm(); dialogVisible.value = true }
const openEdit = async (row: AdminUser) => { const json = await AdminUserApi.get(row.id as number); form.value = json.data; editing.value = { id: row.id } as any; dialogVisible.value = true }

const submit = async () => {
  if (!form.value.username) { ElMessage.error('请输入账号'); return }
  if (editing.value?.id) {
    const json = await AdminUserApi.update(editing.value.id as number, form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('更新成功'); dialogVisible.value = false; await tableRef.value?.refresh() }
    else { ElMessage.error(json.message || '更新失败') }
  } else {
    const json = await AdminUserApi.create(form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('创建成功'); dialogVisible.value = false; await tableRef.value?.refresh() }
    else { ElMessage.error(json.message || '创建失败') }
  }
}

const remove = async (row: AdminUser) => {
  await ElMessageBox.confirm('删除后不可恢复，是否继续？', '提示', { type: 'warning' })
  const res = await AdminUserApi.remove(row.id as number)
  const json = await res.json().catch(() => null)
  ElMessage[res.ok ? 'success' : 'error'](json?.message || (res.ok ? '操作成功' : '操作失败'))
  await tableRef.value?.refresh()
}

const tableRef = ref<{ refresh: () => Promise<void> } | null>(null)
onMounted(() => { tableRef.value?.refresh() })
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>用户管理</span>
        <el-button type="primary" @click="openCreate">新增用户</el-button>
      </div>
    </template>

    <ConfigTable ref="tableRef" :columns="columns" :request="request" row-key="id" selection>
      <template #roles="{ row }">
        <el-tag v-for="r in row.roles || []" :key="r" size="small" class="mr-1">{{ r }}</el-tag>
      </template>
      <template #actions="{ row }">
        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="remove(row)">删除</el-button>
      </template>
    </ConfigTable>

    <el-dialog v-model="dialogVisible" :title="editing?.id ? '编辑用户' : '新增用户'" width="520px">
      <el-form label-width="88px">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item v-if="!editing?.id" label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item label="租户ID">
          <el-input-number v-model="form.tenantId" :min="0" :max="999999999" :controls="false" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
</style>
