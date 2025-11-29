<script setup lang="ts">
import CRUDTable, { type Column } from '../components/table/CRUDTable.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { ConfigGroupApi } from '../api/apis/config-group'
import { ConfigApi } from '../api/apis/config'
import type { ConfigGroup, ConfigSetting } from '../api/types'
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
  const res = await ConfigApi.deactivateGroup(row.id as number)
  const json = await res.json().catch(() => null)
  if (res.ok) {
    ElMessage.success('禁用成功')
  } else {
    ElMessage.error(json?.message || '禁用失败')
  }
  await tableRef.value?.refresh()
}

const tableRef = ref<{ refresh: () => Promise<void> } | null>(null)


onMounted(() => {
  tableRef.value?.refresh()
})

const configVisible = ref(false)
const settings = ref<ConfigSetting[]>([])
const currentGroup = ref<ConfigGroup | null>(null)

const openConfig = async (row: ConfigGroup) => {
  currentGroup.value = row
  const json = await ConfigApi.listByGroup(row.id as number)
  settings.value = (json.data || []).map((s: any) => ({
    id: s.id,
    key: s.key,
    value: String(s.value ?? ''),
    type: s.type || 'text',
    scopeType: s.scopeType,
    scopeId: s.scopeId,
    groupId: s.groupId,
    status: s.status !== false,
    createdAt: s.createdAt,
    updatedAt: s.updatedAt
  }))
  configVisible.value = true
}

const addSetting = () => {
  settings.value.push({ key: '', value: '', type: 'text', status: true })
}

const inputValueProxy = {
  get: (s: ConfigSetting) => {
    if ((s.type || '').toLowerCase() === 'bool') return s.value === 'true'
    if ((s.type || '').toLowerCase() === 'number' || (s.type || '').toLowerCase() === 'price') return Number(s.value || '0')
    return s.value || ''
  },
  set: (s: ConfigSetting, v: any) => {
    if ((s.type || '').toLowerCase() === 'bool') s.value = v ? 'true' : 'false'
    else if ((s.type || '').toLowerCase() === 'number' || (s.type || '').toLowerCase() === 'price') s.value = String(Number(v ?? 0))
    else s.value = String(v ?? '')
  }
}

const saveConfig = async () => {
  if (!currentGroup.value?.id) return
  const payload = settings.value.map(s => ({
    id: s.id,
    key: s.key,
    value: String(s.value ?? ''),
    type: s.type || 'text',
    status: s.status !== false
  }))
  const json = await ConfigApi.saveByGroup(currentGroup.value.id as number, payload as any)
  if (json.code >= 200 && json.code < 300) {
    ElMessage.success('保存成功')
    configVisible.value = false
  } else {
    ElMessage.error(json.message || '保存失败')
  }
}
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>配置分组管理</span>
        <el-button type="primary" @click="openCreate">新增分组</el-button>
      </div>
    </template>

    <CRUDTable ref="tableRef" :columns="columns" :request="request" row-key="id" selection>
      <template #actions="{ row }">
        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
        <el-button link @click="openConfig(row)">配置</el-button>
        <el-button type="danger" link @click="remove(row)">禁用</el-button>
      </template>
    </CRUDTable>

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
    <el-dialog v-model="configVisible" :title="`分组配置：${currentGroup?.name || ''}`" width="720px">
      <div class="flex flex-col gap-3">
        <div class="flex justify-end">
          <el-button size="small" @click="addSetting">新增配置</el-button>
        </div>
        <el-table :data="settings" border>
          <el-table-column label="键" width="220">
            <template #default="{ row }">
              <el-input v-model="row.key" placeholder="配置键" />
            </template>
          </el-table-column>
          <el-table-column label="类型" width="160">
            <template #default="{ row }">
              <el-select v-model="row.type" placeholder="类型" style="width: 140px">
                <el-option label="文本" value="text" />
                <el-option label="数字" value="number" />
                <el-option label="价格" value="price" />
                <el-option label="布尔" value="bool" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="值">
            <template #default="{ row }">
              <template v-if="(row.type || '').toLowerCase() === 'bool'">
                <el-switch :model-value="inputValueProxy.get(row) as boolean" @change="(v:any)=>inputValueProxy.set(row,v)" />
              </template>
              <template v-else-if="(row.type || '').toLowerCase() === 'number' || (row.type || '').toLowerCase() === 'price'">
                <el-input-number :model-value="inputValueProxy.get(row) as number" :min="0" :max="999999999" :controls="false" @update:model-value="(v:any)=>inputValueProxy.set(row,v)" />
              </template>
              <template v-else>
                <el-input v-model="row.value" placeholder="配置值" />
              </template>
            </template>
          </el-table-column>
          <el-table-column label="启用" width="100" align="center">
            <template #default="{ row }">
              <el-switch v-model="row.status" />
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="configVisible = false">取消</el-button>
          <el-button type="primary" @click="saveConfig">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
</style>
