<script setup lang="ts">
const props = defineProps<{ value: any; row?: any; index?: number; options?: { currency?: string; locale?: string } }>()
const display = (() => {
  const n = Number(props.value)
  if (!Number.isFinite(n)) return String(props.value ?? '')
  const currency = props.options?.currency || 'CNY'
  const locale = props.options?.locale || undefined
  return new Intl.NumberFormat(locale, { style: 'currency', currency }).format(n)
})()
</script>

<template>
  {{ display }}
</template>

<style scoped>
</style>
