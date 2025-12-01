<script setup lang="tsx">
import CRUDTable, { type Column } from '../components/table/CRUDTable.vue'
import { ElMessage, ElMessageBox, ElSwitch } from 'element-plus'
import { onMounted, ref, computed } from 'vue'
import { AdminUserApi } from '../api/apis/admin-user'
import { RoleApi } from '../api/apis/role'
import { AclApi } from '../api/apis/acl'
import type { AdminUser } from '../api/types'
import View from '../components/View.vue'

const columns: Column[] = [
  { label: 'ID', prop: 'id', width: 80, align: 'center' },
  { label: '账号', prop: 'username', width: 160, className: "font-bold text-15px" },
  {
    label: '启用', prop: 'enabled', width: 80, align: 'center', valueType: 'bool', component: View,
    render: (value: boolean, row: any, _index: number) => h(ElSwitch, {
      modelValue: value, activeValue: true, inactiveValue: false, activeText: "", inactiveText: "",
      "onUpdate:modelValue": (v: any) => AdminUserApi.update(row.id, { ...row, enabled: v }).finally(() => {
        ElMessage.success("操作成功")
        tableRef.value?.refresh()
      })
    })
  },
  { label: '租户', prop: 'tenantName', width: 160 },
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
const emptyForm = (): AdminUser => ({ username: '', password: '', enabled: true, isStaff: false, tenantId: null, roles: [] })
const form = ref<AdminUser>(emptyForm())
const showResetPwd = ref(false)

const allRoles = ref<{ id: number; name: string; code: string; scopeType?: string; scopeId?: number | null }[]>([])
const loadRoles = async () => {
  const json = await RoleApi.list({ page: 0, size: 9999, scopeId: form.value.tenantId })
  allRoles.value = (json.data?.content as any[]) || []
}

const roleMap = computed(() => {
  const m = new Map<number, { id: number; name: string; code: string,scopeType?:string }>()
  for (const r of allRoles.value) m.set(Number(r.id), r as any)
  return m
})

const roleLabel = (id: number) => {
  const r = roleMap.value.get(Number(id))
  return r ? `${r.name}[${r.scopeType || ''}][${r.code}]` : String(id)
}

const refreshRoles = async () => {
  if (form.value.tenantId) await loadRoles()
}

const openCreate = async () => { editing.value = null; form.value = emptyForm(); await loadRoles(); dialogVisible.value = true }
const openEdit = async (row: AdminUser) => {
  const json = await AdminUserApi.get(row.id as number)
  form.value = json.data
  form.value.password = ''
  editing.value = { id: row.id } as any
  await loadRoles()
  const ur = await AclApi.getUserRoles(row.id as number)
  form.value.roles = (ur.data || []).map((x: any) => x.roleId)
  showResetPwd.value = false
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.value.username) { ElMessage.error('请输入账号'); return }
  if (editing.value?.id) {
    const json = await AdminUserApi.update(editing.value.id as number, form.value)
    if (json.code >= 200 && json.code < 300) {
      if (Array.isArray(form.value.roles)) await AclApi.replaceUserRoles(editing.value.id as number, form.value.roles as number[])
      ElMessage.success('更新成功')
      dialogVisible.value = false
      await tableRef.value?.refresh()
    } else { ElMessage.error(json.message || '更新失败') }
  } else {
    const json = await AdminUserApi.create(form.value)
    if (json.code >= 200 && json.code < 300) {
      const uid = (json.data as any)?.id
      if (uid && Array.isArray(form.value.roles)) await AclApi.replaceUserRoles(uid as number, form.value.roles as number[])
      ElMessage.success('创建成功')
      dialogVisible.value = false
      await tableRef.value?.refresh()
    } else { ElMessage.error(json.message || '创建失败') }
  }
}

const remove = async (row: AdminUser) => {
  await ElMessageBox.confirm('删除后不可恢复，是否继续？', '提示', { type: 'warning' })
  const res = await AdminUserApi.remove(row.id as number)
  const ok = res.status >= 200 && res.status < 300
  const msg = (res.data as any)?.message || (ok ? '操作成功' : '操作失败')
  ElMessage[ok ? 'success' : 'error'](msg)
  await tableRef.value?.refresh()
}

const tableRef = ref<{ refresh: () => Promise<void> } | null>(null)
onMounted(async () => { await loadRoles(); await tableRef.value?.refresh() })
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>用户管理</span>
        <el-button type="primary" @click="openCreate">新增用户</el-button>
      </div>
    </template>

    <CRUDTable ref="tableRef" :columns="columns" :request="request" row-key="id" selection>
      <template #roles="{ row }">
        <div>
          <el-tag v-for="r in row.roles || []" :key="r" size="small" class="mr-1">{{ roleLabel(r) }}</el-tag>
        </div>
      </template>
      <template #actions="{ row }">
        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="remove(row)">删除</el-button>
      </template>
    </CRUDTable>

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
        <el-form-item label="工作人员">
          <el-checkbox v-model="form.isStaff">是</el-checkbox>
        </el-form-item>
        <el-form-item label="租户ID">
          <el-input-number v-model="form.tenantId" @change="refreshRoles" :min="0" :max="999999999" :controls="false" />
        </el-form-item>
        <el-form-item v-if="editing?.id" label="重置密码">
          <div class="flex items-center gap-2 w-full">
            <el-button @click="showResetPwd = true" v-if="!showResetPwd">启用重置</el-button>
            <el-input v-else v-model="form.password" type="password" placeholder="请输入新密码" />
          </div>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roles" multiple filterable collapse-tags collapse-tags-tooltip placeholder="请选择角色"
            style="width: 100%">
            <el-option v-for="r in allRoles" :key="r.id" :label="`${r.name}（${r.code}）`" :value="Number(r.id)" />
          </el-select>
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

<style scoped></style>
