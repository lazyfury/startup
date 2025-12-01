<script setup lang="ts">
import CRUDTable, { type Column } from '../components/table/CRUDTable.vue'
import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
import { onMounted, ref, watch, nextTick } from 'vue'
import { RoleApi } from '../api/apis/role'
import { PermissionApi } from '../api/apis/permission'
import { AclApi } from '../api/apis/acl'
import type { Role, Permission } from '../api/types'
import { SCOPE_TYPES } from '../api/types'

const columns: Column[] = [
  { label: 'ID', prop: 'id', width: 80, align: 'center' },
  { label: '名称', prop: 'name', width: 160 },
  { label: '编码', prop: 'code', width: 160,component:ElTag },
  { label: '范围类型', prop: 'scopeType', width: 120,component:ElTag },
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
const permTree = ref<Permission[]>([])
const selectedPermIds = ref<number[]>([])
const permKeyword = ref('')
const tagOptions = ref<string[]>([])
const selectedTag = ref<string>('')
const treeRef = ref<any>(null)

const filterNode = (val: string, data: any) => {
  const k = (val || '').trim().toLowerCase()
  const kt = (selectedTag.value || '').trim().toLowerCase()
  const s = `${data.name || ''} ${data.code || ''} ${data.tag || ''}`.toLowerCase()
  const matchText = !k || s.includes(k)
  const matchTag = !kt || ((data.tag || '').toLowerCase().includes(kt))
  return matchText && matchTag
}
watch(permKeyword, (v) => treeRef.value?.filter(v))
watch(selectedTag, () => treeRef.value?.filter(permKeyword.value))

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
  const ok = res.status >= 200 && res.status < 300
  const msg = (res.data as any)?.message || (ok ? '操作成功' : '操作失败')
  ElMessage[ok ? 'success' : 'error'](msg)
  await tableRef.value?.refresh()
}

const tableRef = ref<{ refresh: () => Promise<void> } | null>(null)
onMounted(() => { tableRef.value?.refresh() })
async function openAssign(row: Role) {
  assignRole.value = row
  const permsJson = await PermissionApi.tree({ scopeType: row.scopeType, scopeId: row.scopeId ?? null })
  permTree.value = (permsJson.data as any[]) || []
  const rpJson = await AclApi.getRolePermissions(row.id as number)
  selectedPermIds.value = (rpJson.data || []).map((rp: any) => rp.permissionId)
  const tagsJson = await PermissionApi.tags({ scopeType: row.scopeType, scopeId: row.scopeId ?? null })
  tagOptions.value = tagsJson.data || []
  assignVisible.value = true
  await nextTick()
  treeRef.value?.setCheckedKeys(selectedPermIds.value)
}

async function submitAssign() {
  if (!assignRole.value?.id) return
  selectedPermIds.value = treeRef.value?.getCheckedKeys(false) || []
  const res = await AclApi.replaceRolePermissions(assignRole.value.id as number, selectedPermIds.value)
  const ok = (res.status ?? 200) >= 200 && (res.status ?? 200) < 300
  if (ok) assignVisible.value = false
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

    <CRUDTable ref="tableRef" :columns="columns" :request="request" row-key="id" selection>
      <template #actions="{ row }">
        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
        <el-button type="primary" link @click="openAssign(row)">指派权限</el-button>
        <el-button type="danger" link @click="remove(row)">删除</el-button>
      </template>
    </CRUDTable>

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
        <div class="mb-4 font-bold">角色：{{ assignRole?.name }}（{{ assignRole?.code }}）</div>
        <div class="gap-2 items-center">
          <div>
            <el-form-item label="搜索" label-position="top">
              <el-input v-model="permKeyword" placeholder="搜索权限/编码/标签" />
            </el-form-item>
          </div>
          <div>
            <el-form-item label="标签" label-position="top">
              <el-radio-group class="gap-2" v-model="selectedTag" size="small">
                <el-radio border size="small" class="!mr-2" label="">全部</el-radio>
                <el-radio border size="small" class="!mr-2" v-for="t in tagOptions" :key="t" :label="t">{{ t
                  }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </div>
        <el-card class="flex-1 p-0" shadow="never">
          <el-tree ref="treeRef" :data="permTree" node-key="id" show-checkbox
            :props="{ label: 'name', children: 'children' }" :filter-node-method="filterNode"
            :default-checked-keys="selectedPermIds" />
        </el-card>
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

<style scoped></style>
