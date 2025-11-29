<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { PermissionApi } from '../api/apis/permission'
import type { Permission } from '../api/types'
import { SCOPE_TYPES } from '../api/types'

const dialogVisible = ref(false)
const editing = ref<Permission | null>(null)
const emptyForm = (): Permission => ({ name: '', code: '', description: '', tag: '', parentId: null, scopeType: 'SYSTEM', scopeId: null, status: true })
const form = ref<Permission>(emptyForm())
const loading = ref(false)
const all = ref<Permission[]>([])
const tree = ref<Permission[]>([])
const refresh = async () => {
  loading.value = true
  try {
    const json = await PermissionApi.tree()
    tree.value = json.data || []
  } finally {
    loading.value = false
  }
}

const openCreate = async () => { editing.value = null; form.value = emptyForm(); await loadAll(); dialogVisible.value = true }
const openCreateRoot = async () => { editing.value = null; form.value = { ...emptyForm(), parentId: null }; await loadAll(); dialogVisible.value = true }
const openEdit = async (row: Permission) => { const json = await PermissionApi.get(row.id as number); form.value = json.data; editing.value = { id: row.id } as any; await loadAll(); dialogVisible.value = true }

const submit = async () => {
  if (!form.value.name || !form.value.code) { ElMessage.error('请填写名称与编码'); return }
  if (editing.value?.id) {
    const json = await PermissionApi.update(editing.value.id as number, form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('更新成功'); dialogVisible.value = false; await loadAll(); await refresh() }
    else { ElMessage.error(json.message || '更新失败') }
  } else {
    const json = await PermissionApi.create(form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('创建成功'); dialogVisible.value = false; await loadAll(); await refresh() }
    else { ElMessage.error(json.message || '创建失败') }
  }
}

const remove = async (row: Permission) => {
  await ElMessageBox.confirm('删除后不可恢复，是否继续？', '提示', { type: 'warning' })
  const res = await PermissionApi.remove(row.id as number)
  const json = await res.json().catch(() => null)
  ElMessage[res.ok ? 'success' : 'error'](json?.message || (res.ok ? '操作成功' : '操作失败'))
  await loadAll()
  await refresh()
}

void remove

async function loadAll() { const json = await PermissionApi.list({ page: 0, size: 9999 }); all.value = (json.data?.content as any[]) || [] }
onMounted(async () => { await loadAll(); await refresh() })

function addChild(parent: Permission) {
  editing.value = null
  const child = emptyForm()
  child.parentId = parent.id as number
  child.scopeType = parent.scopeType
  child.scopeId = parent.scopeId ?? null
  child.tag = parent.tag || ''
  form.value = child
  dialogVisible.value = true
}
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>权限管理</span>
        <div class="flex items-center gap-2">
          <el-button @click="refresh" :loading="loading">刷新</el-button>
          <el-button type="primary" @click="openCreateRoot">新增根权限</el-button>
          <el-button type="primary" @click="openCreate">新增权限</el-button>
        </div>
      </div>
    </template>
    <div class="mt-0">
      <el-tree :data="tree" node-key="id" default-expand-all :props="{ children: 'children', label: 'name' }" v-loading="loading">
        <template #default="{ data }">
          <div class="flex items-center justify-between w-full">
            <div class="flex items-center gap-2">
              <span>{{ data.name }}</span>
              <span class="text-gray-500">{{ data.code }}</span>
              <el-tag v-if="data.status === false" size="small" type="warning">禁用</el-tag>
              <el-tag v-if="data.tag" size="small" type="info">{{ data.tag }}</el-tag>
            </div>
            <div class="flex items-center gap-2">
              <el-button link type="primary" @click="addChild(data)">新增子权限</el-button>
              <el-button link type="primary" @click="openEdit(data)">编辑</el-button>
              <el-button link type="danger" @click="remove(data)">删除</el-button>
            </div>
          </div>
        </template>
      </el-tree>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing?.id ? '编辑权限' : '新增权限'" width="560px">
      <el-form label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tag" placeholder="请输入标签" />
        </el-form-item>
        <el-form-item label="父权限">
          <el-select v-model="form.parentId" placeholder="请选择父权限" clearable filterable style="width: 100%">
            <el-option v-for="p in all" :key="p.id" :label="`${p.name}（${p.code}）`" :value="Number(p.id)" />
          </el-select>
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
  </el-card>
</template>

<style scoped>
</style>
