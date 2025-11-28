<script setup lang="ts">
import ConfigTable, { type Column } from '../components/ConfigTable.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { ConfigGroupApi } from '../api/apis/config-group'
import type { ConfigGroup } from '../api/types'
import { SCOPE_TYPES } from '../api/types'


const columns:Column[] = [
  { label: 'ID', prop: 'id', width: 80, align: 'center' },
  { label: '名称', prop: 'name',width:100 },
  { label: '编码', prop: 'code',width:100 },
  { label: '描述', prop: 'description' },
  { label: '范围类型', prop: 'scopeType', width: 120 },
  { label: '范围ID', prop: 'scopeId', width: 100, align: 'center' },
  { label: '启用', prop: 'status', width: 80, align: 'center' },
  { label: '更新于', prop: 'updatedAt' },
  { label: '操作', slot: 'actions', width: 200, align: 'center' }
]

const request = async ({ page, pageSize }: { page: number; pageSize: number }) => {
  const json = await ConfigGroupApi.list({ page: page - 1, size: pageSize })
  const data = json.data
  return { list: data.content, total: data.totalElements }
}

const dialogVisible = ref(false)
const editing = ref<ConfigGroup | null>(null)

const emptyForm = (): ConfigGroup => ({ name: '', code: '', description: '', scopeType: 'SYSTEM', scopeId: null, status: true })
const form = ref<ConfigGroup>(emptyForm())

const openCreate = () => {
  editing.value = null
  form.value = emptyForm()
  dialogVisible.value = true
}

const openEdit = async (row: ConfigGroup) => {
  const json = await ConfigGroupApi.get(row.id as number)
  form.value = json.data
  editing.value = { id: row.id } as any
  dialogVisible.value = true
}

const submit = async () => {
  if (editing.value?.id) {
    const json = await ConfigGroupApi.update(editing.value.id as number, form.value)
    if (json.code >= 200 && json.code < 300) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      await tableRef.value?.refresh()
    } else {
      ElMessage.error(json.message || '更新失败')
    }
  } else {
    const json = await ConfigGroupApi.create(form.value)
    if (json.code >= 200 && json.code < 300) {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      await tableRef.value?.refresh()
    } else {
      ElMessage.error(json.message || '创建失败')
    }
  }
}

const remove = async (row: ConfigGroup) => {
  await ElMessageBox.confirm('禁用将使该分组失效，是否继续？', '提示', { type: 'warning' })
  const res = await ConfigGroupApi.remove(row.id as number)
  if (res.status === 204) {
    ElMessage.success('禁用成功')
    await tableRef.value?.refresh()
    return
  }
  const json = await res.json().catch(() => null)
  ElMessage[res.ok ? 'success' : 'error'](json?.message || (res.ok ? '操作成功' : '操作失败'))
  await tableRef.value?.refresh()
}

const tableRef = ref<{ refresh: () => Promise<void> } | null>(null)


onMounted(() => {
  tableRef.value?.refresh()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>配置分组管理</span>
        <el-button type="primary" @click="openCreate">新增分组</el-button>
      </div>
    </template>

    <ConfigTable ref="tableRef" :columns="columns" :request="request" row-key="id" selection>
      <template #actions="{ row }">
        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="remove(row)">禁用</el-button>
      </template>
    </ConfigTable>

    <el-dialog v-model="dialogVisible" :title="editing?.id ? '编辑分组' : '新增分组'" width="520px">
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
        <el-form-item label="范围类型">
          <el-select v-model="form.scopeType" style="width: 100%">
            <el-option v-for="st in SCOPE_TYPES" :key="st" :label="st" :value="st" />
          </el-select>
        </el-form-item>
        <el-form-item label="范围ID">
          <el-input-number v-model="form.scopeId" :min="0" :max="999999999" :controls="false" style="width: 100%" />
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
