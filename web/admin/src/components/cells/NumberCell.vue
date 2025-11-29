<script setup lang="ts">
const props = defineProps<{ value: any; row?: any; index?: number; options?: { decimals?: number; locale?: string } }>()
const display = (() => {
  const n = Number(props.value)
  if (!Number.isFinite(n)) return String(props.value ?? '')
  const locale = props.options?.locale || undefined
  const decimals = props.options?.decimals
  const fmt = typeof decimals === 'number' ? { minimumFractionDigits: decimals, maximumFractionDigits: decimals } : undefined
  return n.toLocaleString(locale, fmt as any)
})()
</script>

<template>
  {{ display }}
</template>

<style scoped>
</style>
