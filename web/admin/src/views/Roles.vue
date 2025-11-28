<script setup lang="ts">
import ConfigTable, { type Column } from '../components/ConfigTable.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref, computed } from 'vue'
import { RoleApi } from '../api/apis/role'
import { PermissionApi } from '../api/apis/permission'
import { AclApi } from '../api/apis/acl'
import type { Role, Permission } from '../api/types'
import { SCOPE_TYPES } from '../api/types'

const columns:Column[] = [
  { label: 'ID', prop: 'id', width: 80, align: 'center' },
  { label: '名称', prop: 'name', width: 160 },
  { label: '编码', prop: 'code', width: 160 },
  { label: '范围类型', prop: 'scopeType', width: 120 },
  { label: '范围ID', prop: 'scopeId', width: 100, align: 'center' },
  { label: '启用', prop: 'status', width: 80, align: 'center' },
  { label: '更新于', prop: 'updatedAt', },
  { label: '操作', slot: 'actions', width: 280, align: 'center' }
]

const request = async ({ page, pageSize }: { page: number; pageSize: number }) => {
  const json = await RoleApi.list({ page: page - 1, size: pageSize })
  const data = json.data
  return { list: data.content, total: data.totalElements }
}

const dialogVisible = ref(false)
const editing = ref<Role | null>(null)
const emptyForm = (): Role => ({ name: '', code: '', scopeType: 'SYSTEM', scopeId: null, status: true })
const form = ref<Role>(emptyForm())

const assignVisible = ref(false)
const assignRole = ref<Role | null>(null)
const allPermissions = ref<Permission[]>([])
const selectedPermIds = ref<number[]>([])
const permKeyword = ref('')
const tagOptions = ref<string[]>([])
const selectedTag = ref<string | null>(null)
const filteredPerms = computed(() => {
  const k = permKeyword.value.trim().toLowerCase()
  const st = assignRole.value?.scopeType
  const sid = assignRole.value?.scopeId ?? null
  return allPermissions.value
    .filter((p) => (!st || p.scopeType === st) && (sid === null || p.scopeId === sid))
    .filter((p) => (p.name || '').toLowerCase().includes(k) || (p.code || '').toLowerCase().includes(k) || (p.tag || '').toLowerCase().includes(k))
})

const openCreate = () => { editing.value = null; form.value = emptyForm(); dialogVisible.value = true }
const openEdit = async (row: Role) => { const json = await RoleApi.get(row.id as number); form.value = json.data; editing.value = { id: row.id } as any; dialogVisible.value = true }

const submit = async () => {
  if (!form.value.name || !form.value.code) { ElMessage.error('请填写名称与编码'); return }
  if (editing.value?.id) {
    const json = await RoleApi.update(editing.value.id as number, form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('更新成功'); dialogVisible.value = false; await tableRef.value?.refresh() }
    else { ElMessage.error(json.message || '更新失败') }
  } else {
    const json = await RoleApi.create(form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('创建成功'); dialogVisible.value = false; await tableRef.value?.refresh() }
    else { ElMessage.error(json.message || '创建失败') }
  }
}

const remove = async (row: Role) => {
  await ElMessageBox.confirm('删除后不可恢复，是否继续？', '提示', { type: 'warning' })
  const res = await RoleApi.remove(row.id as number)
  const json = await res.json().catch(() => null)
  ElMessage[res.ok ? 'success' : 'error'](json?.message || (res.ok ? '操作成功' : '操作失败'))
  await tableRef.value?.refresh()
}

const tableRef = ref<{ refresh: () => Promise<void> } | null>(null)
onMounted(() => { tableRef.value?.refresh() })
async function openAssign(row: Role) {
  assignRole.value = row
  const permsJson = await PermissionApi.list({ page: 0, size: 9999 })
  allPermissions.value = (permsJson.data?.content as any[]) || []
  const rpJson = await AclApi.getRolePermissions(row.id as number)
  selectedPermIds.value = (rpJson.data || []).map((rp: any) => rp.permissionId)
  const tagsJson = await PermissionApi.tags({ scopeType: row.scopeType, scopeId: row.scopeId ?? null })
  tagOptions.value = tagsJson.data || []
  assignVisible.value = true
}

async function submitAssign() {
  if (!assignRole.value?.id) return
  const res = await AclApi.replaceRolePermissions(assignRole.value.id as number, selectedPermIds.value)
  if (res.ok) {
    assignVisible.value = false
  }
}

async function onTagChange(val: string | null) {
  const st = assignRole.value?.scopeType
  const sid = assignRole.value?.scopeId ?? null
  if (val && val.length > 0) {
    const json = await PermissionApi.searchByTag({ q: val, scopeType: st, scopeId: sid, limit: 500 })
    allPermissions.value = json.data || []
  } else {
    const permsJson = await PermissionApi.list({ page: 0, size: 9999 })
    allPermissions.value = (permsJson.data?.content as any[]) || []
  }
}
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>角色管理</span>
        <el-button type="primary" @click="openCreate">新增角色</el-button>
      </div>
    </template>

    <ConfigTable ref="tableRef" :columns="columns" :request="request" row-key="id" selection>
      <template #actions="{ row }">
        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
        <el-button type="primary" link @click="openAssign(row)">指派权限</el-button>
        <el-button type="danger" link @click="remove(row)">删除</el-button>
      </template>
    </ConfigTable>

    <el-dialog v-model="dialogVisible" :title="editing?.id ? '编辑角色' : '新增角色'" width="520px">
      <el-form label-width="88px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="范围类型">
          <el-select v-model="form.scopeType" style="width: 100%">
            <el-option v-for="st in SCOPE_TYPES" :key="st" :label="st" :value="st" />
          </el-select>
        </el-form-item>
        <el-form-item label="范围ID">
          <el-input-number v-model="form.scopeId" :min="0" :max="999999999" :controls="false" />
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
    <el-dialog v-model="assignVisible" title="指派权限" width="640px">
      <div class="flex flex-col gap-2">
        <div>角色：{{ assignRole?.name }}（{{ assignRole?.code }}）</div>
        <div class="flex gap-2 items-center">
          <el-input v-model="permKeyword" placeholder="搜索权限/编码/标签" />
          <el-select v-model="selectedTag" placeholder="按标签筛选" clearable filterable style="min-width: 200px" @change="onTagChange">
            <el-option v-for="t in tagOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </div>
        <el-checkbox-group v-model="selectedPermIds">
          <el-checkbox v-for="p in filteredPerms" :key="p.id" :label="p.id">{{ p.name }}（{{ p.code }}）<span v-if="p.tag" class="text-gray-500">【{{ p.tag }}】</span></el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="assignVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssign">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
</style>

 
