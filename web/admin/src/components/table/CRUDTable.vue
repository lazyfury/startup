<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import TextCell from '../cells/TextCell.vue'
import BoolCell from '../cells/BoolCell.vue'
import NumberCell from '../cells/NumberCell.vue'
import PriceCell from '../cells/PriceCell.vue'

export type Column = {
  label: string
  prop?: string
  type?: 'index' | 'selection' | 'expand'
  width?: string | number
  align?: 'left' | 'center' | 'right'
  fixed?: 'left' | 'right'
  sortable?: boolean
  slot?: string
  valueType?: 'text' | 'number' | 'price' | 'bool'
  formatOptions?: { decimals?: number; currency?: string; locale?: string }
  render?: (value: any, row: any, index: number) => any
  component?: any,
  className?:string
}

const props = defineProps<{
  columns: Column[]
  request?: (params: { page: number; pageSize: number; filters?: any; sorts?: any }) => Promise<{ list: any[]; total: number }>
  data?: any[]
  rowKey?: string | ((row: any) => string)
  selection?: boolean
  index?: boolean
  pagination?: boolean
  page?: number
  pageSize?: number
  pageSizes?: number[]
  autoFetch?: boolean
  loading?: boolean,
}>()

const emit = defineEmits<{
  (e: 'update:page', v: number): void
  (e: 'update:pageSize', v: number): void
  (e: 'selection-change', v: any[]): void
  (e: 'refresh'): void
  (e: 'loaded', v: { list: any[]; total: number }): void
}>()

const innerPage = ref(props.page ?? 1)
const innerPageSize = ref(props.pageSize ?? 10)
const innerTotal = ref(0)
const innerLoading = ref(false)
const innerData = ref<any[]>([])

const useRemote = computed(() => !!props.request)
const dataSource = computed(() => (useRemote.value ? innerData.value : props.data || []))

const getValue = (row: any, path?: string) => {
  if (!path) return row
  return String(path).split('.').reduce((acc: any, k: string) => (acc ? acc[k] : undefined), row) || "-"
}

const resolveCell = (c: Column) => {
  if (c.component) return c.component
  switch (c.valueType) {
    case 'bool': return BoolCell
    case 'number': return NumberCell
    case 'price': return PriceCell
    case 'text':
    default: return TextCell
  }
}

const fetch = async () => {
  if (!props.request) return
  innerLoading.value = true
  try {
    const res = await props.request({ page: innerPage.value, pageSize: innerPageSize.value })
    innerData.value = res.list
    innerTotal.value = res.total
    emit('loaded', res)
  } finally {
    innerLoading.value = false
  }
}

const handleSizeChange = async (size: number) => {
  innerPageSize.value = size
  emit('update:pageSize', size)
  if (useRemote.value) await fetch()
}

const handleCurrentChange = async (page: number) => {
  innerPage.value = page
  emit('update:page', page)
  if (useRemote.value) await fetch()
}

const handleSelectionChange = (rows: any[]) => emit('selection-change', rows)
const refresh = async () => {
  emit('refresh')
  if (useRemote.value) await fetch()
}

onMounted(async () => {
  if (useRemote.value && props.autoFetch !== false) await fetch()
})

defineExpose({ refresh })
</script>

<template>
  <div class="crud-table">
    <slot name="toolbar" :refresh="refresh">
      <div class="table-toolbar">
        <el-button :disabled="(loading ?? innerLoading) === true" @click="refresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </slot>
    <el-table
      :border="true"
      :data="dataSource"
      :row-key="rowKey"
      v-loading="loading ?? innerLoading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="selection" type="selection" width="48" />
      <el-table-column v-if="index" type="index" width="56" />
      <el-table-column
        v-for="c in columns"
        :class-name="c.className"
        :key="c.prop || c.label"
        :type="c.type"
        :prop="c.prop"
        :label="c.label"
        :width="c.width"
        :align="c.align"
        :fixed="c.fixed"
        :sortable="c.sortable"
      >
        <template v-if="c.slot" #default="scope">
          <slot :name="c.slot" v-bind="scope" ></slot>
        </template>
        <template v-else #default="scope">
          <component
            :is="resolveCell(c)"
            :value="getValue(scope.row, c.prop)"
            :options="c.formatOptions"
            :row="scope.row"
            :index="scope.$index"
          >
          {{ c.render ? c.render?.(getValue(scope.row, c.prop), scope.row, scope.$index) : getValue(scope.row,c.prop) }}
        </component>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="pagination !== false" class="table-pagination">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :current-page="innerPage"
        :page-size="innerPageSize"
        :page-sizes="pageSizes || [10, 20, 50, 100]"
        :total="useRemote ? innerTotal : dataSource.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.crud-table { display: flex; flex-direction: column; gap: 12px; }
.table-toolbar { display: flex; justify-content: flex-start; }
.table-pagination { display: flex; justify-content: flex-end; }
</style>
