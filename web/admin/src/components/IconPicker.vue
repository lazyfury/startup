<script setup lang="ts">
import { ref, computed } from 'vue'
import * as Icons from '@element-plus/icons-vue'

const props = defineProps<{ modelValue?: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

const keyword = ref('')
const names = Object.keys(Icons)
const filtered = computed(() => {
  const k = keyword.value.trim().toLowerCase()
  return names.filter((n) => n.toLowerCase().includes(k))
})
const select = (name: string) => emit('update:modelValue', name)
</script>

<template>
  <div class="icon-picker">
    <el-popover placement="bottom-start" trigger="click" width="520">
      <template #reference>
        <el-button class="w-full" plain>
          <el-icon v-if="props.modelValue"><component :is="props.modelValue" /></el-icon>
          <span class="ml-2">{{ props.modelValue || '选择图标' }}</span>
        </el-button>
      </template>
      <div class="picker-panel">
        <el-input v-model="keyword" placeholder="搜索图标" class="mb-2" />
        <div class="grid">
          <div v-for="n in filtered" :key="n" class="item" @click="select(n)">
            <el-icon><component :is="n" /></el-icon>
            <div class="name">{{ n }}</div>
          </div>
        </div>
        <div class="actions">
          <el-button size="small" @click="select('')">清除</el-button>
        </div>
      </div>
    </el-popover>
  </div>
  
</template>

<style scoped>
.picker-panel { display: flex; flex-direction: column; gap: 8px; }
.grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 8px; max-height: 300px; overflow: auto; }
.item { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 8px; border: 1px solid var(--el-border-color); border-radius: 6px; cursor: pointer; transition: all .2s; }
.item:hover { background: var(--el-fill-color-light); }
.name { font-size: 12px; text-align: center; margin-top: 4px; word-break: break-all; }
.actions { display: flex; justify-content: flex-end; }
</style>
