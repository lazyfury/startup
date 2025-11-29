<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MenuApi } from '../api/apis/menu'
import { SCOPE_TYPES } from '../api/types'
import IconPicker from '../components/IconPicker.vue'
import type { MenuItem, Permission } from '../api/types'

const loading = ref(false)
const tree = ref<MenuItem[]>([])
const dialogVisible = ref(false)
const editing = ref<MenuItem | null>(null)
const form = ref<MenuItem>({ name: '', code: '', path: '', component: '', redirect: '', icon: '', type: 'MENU', orderNo: 0, hidden: false, status: true, parentId: null })

const autoPermVisible = ref(false)
const autoTarget = ref<MenuItem | null>(null)
const ACTIONS = ['view', 'create', 'update', 'delete']
const autoForm = ref<{ scopeType: string; scopeId: number | null; actions: string[] }>({ scopeType: 'SYSTEM', scopeId: null, actions: [...ACTIONS] })
const existingPerms = ref<Permission[]>([])
const existingActions = computed(() => {
  const base = autoTarget.value ? `menu:${autoTarget.value.code}:` : ''
  return existingPerms.value
    .map((p) => (p.code || '').startsWith(base) ? (p.code || '').slice(base.length) : '')
    .filter((s) => !!s)
})
const newAction = ref('')
const willCreateActions = computed(() => autoForm.value.actions.filter((a) => !existingActions.value.includes(a)))
const previewCodes = computed(() => {
  const base = autoTarget.value ? `menu:${autoTarget.value.code}:` : ''
  return willCreateActions.value.map((a) => `${base}${a}`)
})

const annotateLevels = (nodes: MenuItem[], level = 0) => {
  for (const n of nodes) {
    ;(n as any)._level = level
    if (Array.isArray((n as any).children)) annotateLevels((n as any).children, level + 1)
  }
}

const refresh = async () => {
  loading.value = true
  try {
    const json = await MenuApi.tree({ includeDisabled: true })
    if (json.code >= 200 && json.code < 300) {
      tree.value = json.data || []
      annotateLevels(tree.value)
    }
  } finally {
    loading.value = false
  }
}

const emptyForm = (): MenuItem => ({ name: '', code: '', path: '', component: '', redirect: '', icon: '', type: 'MENU', orderNo: 0, hidden: false, status: true, parentId: null })
const openCreateRoot = () => { editing.value = null; form.value = { ...emptyForm(), type: 'DIRECTORY' }; dialogVisible.value = true }
const openCreateChild = (parent: MenuItem) => { editing.value = null; form.value = { ...emptyForm(), parentId: parent.id || null, type: 'MENU' }; dialogVisible.value = true }

const openEdit = async (node: MenuItem) => {
  const json = await MenuApi.get(node.id as number)
  form.value = json.data
  editing.value = { id: node.id } as any
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.value.name || !form.value.code) {
    ElMessage.error('请填写标题与编码')
    return
  }
  if (editing.value?.id) {
    const json = await MenuApi.update(editing.value.id as number, form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('更新成功'); dialogVisible.value = false; await refresh() }
    else { ElMessage.error(json.message || '更新失败') }
  } else {
    const json = await MenuApi.create(form.value)
    if (json.code >= 200 && json.code < 300) { ElMessage.success('创建成功'); dialogVisible.value = false; await refresh() }
    else { ElMessage.error(json.message || '创建失败') }
  }
}

const remove = async (node: MenuItem) => {
  await ElMessageBox.confirm('删除后不可恢复，是否继续？', '提示', { type: 'warning' })
  const res = await MenuApi.remove(node.id as number)
  if (res.status === 204 || res.ok) { ElMessage.success('删除成功'); await refresh() }
  else { const json = await res.json().catch(() => null); ElMessage.error(json?.message || '删除失败') }
}

const openAutoPerm = (node: MenuItem) => {
  autoTarget.value = node
  autoForm.value = { scopeType: 'SYSTEM', scopeId: null, actions: [...ACTIONS] }
  existingPerms.value = []
  autoPermVisible.value = true
  loadExistingPerms()
}

const submitAutoPerm = async () => {
  if (!autoTarget.value?.id) return
  const json = await MenuApi.autoCreatePermissions(autoTarget.value.id as number, autoForm.value)
  if (json.code === 201 || (json.code >= 200 && json.code < 300)) {
    ElMessage.success(`已创建 ${Array.isArray(json.data) ? json.data.length : 0} 个权限`)
    autoPermVisible.value = false
  } else {
    ElMessage.error(json.message || '创建权限失败')
  }
}

const loadExistingPerms = async () => {
  if (!autoTarget.value?.id) return
  const json = await MenuApi.getPermissions(autoTarget.value.id as number, { scopeType: autoForm.value.scopeType, scopeId: autoForm.value.scopeId ?? undefined })
  if (json.code >= 200 && json.code < 300) {
    existingPerms.value = json.data || []
  }
}

watch(() => [autoForm.value.scopeType, autoForm.value.scopeId], () => { loadExistingPerms() })

const addNewAction = () => {
  const act = newAction.value.trim().toLowerCase()
  if (!act) return
  if (!autoForm.value.actions.includes(act)) autoForm.value.actions.push(act)
  newAction.value = ''
}

onMounted(refresh)
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span>菜单管理</span>
        <div class="flex items-center gap-2">
          <el-button @click="refresh" :loading="loading">刷新</el-button>
          <el-button type="primary" @click="openCreateRoot">新增根菜单</el-button>
        </div>
      </div>
    </template>
    <el-table :data="tree" row-key="id" :tree-props="{ children: 'children' }" v-loading="loading">
      <el-table-column expanded width="50"></el-table-column>
      <el-table-column label="标题" min-width="200">
        <template #default="{ row }">
          <div class="flex items-center gap-2" :style="{ paddingLeft: `${(row as any)._level ? (row as any)._level * 16 : 0}px` }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
            <span>{{ row.name }}</span>
            <span class="text-gray-500">{{ row.path }}</span>
            <el-tag v-if="row.type === 'DIRECTORY'" size="small" type="info">目录</el-tag>
            <el-tag v-if="row.hidden" size="small" type="info">隐藏</el-tag>
            <el-tag v-if="row.status === false" size="small" type="warning">禁用</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="orderNo" width="100" />
      <el-table-column label="类型" width="120">
        <template #default="{ row }">
          <span>{{ row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="360" align="center">
        <template #default="{ row }">
          <div class="flex items-center gap-2 justify-center">
            <el-button link type="primary" @click="openCreateChild(row)">新增子菜单</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="openAutoPerm(row)">自动创建权限</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing?.id ? '编辑菜单' : '新增菜单'" width="560px">
      <el-form label-width="100px">
      <el-form-item label="标题">
        <el-input v-model="form.name" placeholder="请输入菜单标题" />
      </el-form-item>
      <el-form-item label="编码">
        <el-input v-model="form.code" placeholder="请输入唯一编码" />
      </el-form-item>
      <el-form-item label="路径">
        <el-input v-model="form.path" placeholder="请输入路由路径" />
      </el-form-item>
      <el-form-item label="组件">
        <el-input v-model="form.component" placeholder="请输入组件路径" />
      </el-form-item>
      <el-form-item label="重定向">
        <el-input v-model="form.redirect" placeholder="请输入重定向路径" />
      </el-form-item>
      <el-form-item label="图标">
        <IconPicker v-model="form.icon" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
          <el-option label="目录" value="DIRECTORY" />
          <el-option label="菜单" value="MENU" />
          <el-option label="按钮" value="BUTTON" />
        </el-select>
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.orderNo" :min="0" :max="9999" :controls="false" />
      </el-form-item>
      <el-form-item label="隐藏">
        <el-switch v-model="form.hidden" />
      </el-form-item>
      <el-form-item label="启用">
        <el-switch v-model="form.status" />
      </el-form-item>
      <el-form-item label="父级ID">
        <el-input-number v-model="form.parentId" :min="0" :max="999999999" :controls="false" />
      </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">提交</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="autoPermVisible" title="为菜单自动创建权限" width="560px">
      <div class="flex flex-col gap-3">
        <div>目标菜单：{{ autoTarget?.name }}（{{ autoTarget?.code }}）</div>
        <el-form label-width="100px">
          <el-form-item label="范围类型">
            <el-select v-model="autoForm.scopeType" style="width: 100%">
              <el-option v-for="st in SCOPE_TYPES" :key="st" :label="st" :value="st" />
            </el-select>
          </el-form-item>
          <el-form-item label="范围ID">
            <el-input-number v-model="autoForm.scopeId" :min="0" :max="999999999" :controls="false" />
          </el-form-item>
          <el-form-item label="动作">
            <el-checkbox-group v-model="autoForm.actions">
              <el-checkbox v-for="act in ACTIONS" :key="act" :label="act">{{ act }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="已有动作">
            <div class="flex flex-wrap gap-2">
              <el-tag v-for="act in existingActions" :key="act" type="success" size="small">{{ act }}</el-tag>
              <span v-if="existingActions.length === 0" class="text-gray-500">暂无</span>
            </div>
          </el-form-item>
          <el-form-item label="添加动作">
            <div class="flex items-center gap-2 w-full">
              <el-input v-model="newAction" placeholder="自定义动作，如 publish" />
              <el-button @click="addNewAction">添加</el-button>
            </div>
          </el-form-item>
          <el-form-item label="预览将创建">
            <div class="flex flex-wrap gap-2">
              <el-tag v-for="code in previewCodes" :key="code" type="info" size="small">{{ code }}</el-tag>
              <span v-if="previewCodes.length === 0" class="text-gray-500">无新增</span>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="autoPermVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAutoPerm">创建</el-button>
        </div>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.expand-row{padding-left:16px;padding-right:0;padding-top:0;padding-bottom:0;display:flex;align-items:center;gap:24px;font-size:14px}
</style>
