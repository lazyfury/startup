<script setup lang="ts">
import CRUDTable, { type Column } from '../components/table/CRUDTable.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { TenantApi, type Tenant } from '../api/apis/tenant'

const columns:Column[] = [
  { label: 'ID', prop: 'id', width: 80, align: 'center' },
  { label: '名称', prop: 'name', width: 160 },
  { label: '编码', prop: 'code', width: 160 },
  { label: '启用', prop: 'status', width: 80, align: 'center' },
  { label: '更新于', prop: 'updatedAt' },
  { label: '操作', slot: 'actions', width: 220, align: 'center' }
]

const request = async ({ page, pageSize }: { page: number; pageSize: number }) => {
  const json = await TenantApi.list({ page: page - 1, size: pageSize })
  const data = json.data
  return { list: data.content, total: data.totalElements }
}

const dialogVisible = ref(false)
const editing = ref<Tenant | null>(null)
const emptyForm = (): Tenant => ({ name: '', code: '', description: '', status: true })
const form = ref<Tenant>(emptyForm())

const openCreate = () => { editing.value = null; form.value = emptyForm(); dialogVisible.value = true }
const openEdit = async (row: Tenant) => { const json = await TenantApi.get(row.id as number); form.value = json.data; editing.value = { id: row.id } as any; dialogVisible.value = true }

const submit = async () => {
  if (!form.value.name || !form.value.code) { ElMessage.error('请填写名称与编码'); return }
  if (editing.value?.id) {
    const json = await TenantApi.update(editing.value.id as number, form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('更新成功'); dialogVisible.value = false; await tableRef.value?.refresh() }
    else { ElMessage.error(json.message || '更新失败') }
  } else {
    const json = await TenantApi.create(form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('创建成功'); dialogVisible.value = false; await tableRef.value?.refresh() }
    else { ElMessage.error(json.message || '创建失败') }
  }
}

const remove = async (row: Tenant) => {
  await ElMessageBox.confirm('删除后不可恢复，是否继续？', '提示', { type: 'warning' })
  const res = await TenantApi.remove(row.id as number)
  const ok = res.status >= 200 && res.status < 300
  const msg = (res.data as any)?.message || (ok ? '操作成功' : '操作失败')
  ElMessage[ok ? 'success' : 'error'](msg)
  await tableRef.value?.refresh()
}

const tableRef = ref<{ refresh: () => Promise<void> } | null>(null)
onMounted(() => { tableRef.value?.refresh() })
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>租户管理</span>
        <el-button type="primary" @click="openCreate">新增租户</el-button>
      </div>
    </template>

    <CRUDTable ref="tableRef" :columns="columns" :request="request" row-key="id" selection>
      <template #actions="{ row }">
        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="remove(row)">删除</el-button>
      </template>
    </CRUDTable>

    <el-dialog v-model="dialogVisible" :title="editing?.id ? '编辑租户' : '新增租户'" width="520px">
      <el-form label-width="88px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.status" />
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
