<template>
  <div class="p-4">
    <div class="title-2 mb-3">获取行情jh链接</div>

    <form class="space-y-3" @submit.prevent="submit">
      <div class="grid grid-cols-1 gap-3 md:grid-cols-2">

        <label class="block">
          <span class="text-sm text-gray-700">moduleKey</span>
          <input v-model="form.moduleKey" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="模块标识" />
        </label>

        <label class="block">
          <span class="text-sm text-gray-700">exchangeRate</span>
          <input v-model="form.exchangeRate" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="汇率" />
        </label>

        <label class="block">
          <span class="text-sm text-gray-700">unitExchangeRate</span>
          <input v-model="form.unitExchangeRate" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="单位汇率" />
        </label>

        <label class="block">
          <span class="text-sm text-gray-700">unit</span>
          <input v-model="form.unit" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="单位" />
        </label>

        <label class="block">
          <span class="text-sm text-gray-700">attach</span>
          <input v-model="form.attach" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="附加信息" />
        </label>

        <label class="block">
          <span class="text-sm text-gray-700">clientType</span>
          <input v-model="form.clientType" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="客户端类型" />
        </label>

        <label class="block">
          <span class="text-sm text-gray-700">roundType</span>
          <input v-model="form.roundType" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="舍入类型" />
        </label>

        <label class="block">
          <span class="text-sm text-gray-700">chargeShowWay</span>
          <input v-model="form.chargeShowWay" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="展示方式" />
        </label>

        <label class="block">
          <span class="text-sm text-gray-700">userCharge</span>
          <input v-model="form.userCharge" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="用户费用" />
        </label>

        <label class="block md:col-span-2">
          <span class="text-sm text-gray-700">payUrl</span>
          <input v-model="form.payUrl" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="支付回调地址" />
        </label>

        <label class="block md:col-span-2">
          <span class="text-sm text-gray-700">expireUrl</span>
          <input v-model="form.expireUrl" type="text" class="block w-full border border-gray-300 rounded-1 px-2 py-1" placeholder="过期回调地址" />
        </label>

        <label class="block md:col-span-2">
          <span class="text-sm text-gray-700">params (JSON)</span>
          <textarea v-model="paramsText" rows="5" class="block w-full border border-gray-300 rounded-1 px-2 py-1 font-mono" placeholder='{"amount":100}'></textarea>
        </label>
      </div>

      <div class="flex items-center gap-2">
        <button type="submit" class="inline-flex items-center px-3 py-1 rounded-1 bg-blue-600 hover:bg-blue-700 text-white">请求链接</button>
        <span v-if="loading" class="text-gray-500">请求中...</span>
      </div>
    </form>

    <div v-if="linkUrl" class="mt-4">
      <div class="title-3 mb-2">生成的链接</div>
      <div class="flex items-center gap-2 mb-2">
        <a :href="linkUrl" target="_blank" rel="noopener" class="underline text-blue-600">{{ linkUrl }}</a>
        <button type="button" class="inline-flex items-center px-2 py-1 rounded-1 bg-gray-200 hover:bg-gray-300" @click="copyLink">复制链接</button>
        <button type="button" class="inline-flex items-center px-2 py-1 rounded-1 bg-blue-600 hover:bg-blue-700 text-white" @click="openLink">打开链接</button>
      </div>
      <div class="text-gray-600 text-sm" v-if="linkMeta.mchUuidNo || linkMeta.uuidNo">
        <span v-if="linkMeta.mchUuidNo">mchUuidNo: {{ linkMeta.mchUuidNo }}</span>
        <span v-if="linkMeta.uuidNo" class="ml-2">uuidNo: {{ linkMeta.uuidNo }}</span>
      </div>
    </div>

    <div v-if="error" class="msg error mt-3">{{ error }}</div>

    <div v-if="result" class="mt-4">
      <div class="title-3 mb-2">响应数据</div>
      <pre class="bg-gray-100 p-3 rounded-1 overflow-auto text-sm font-mono">{{ pretty(result) }}</pre>
    </div>
  </div>
  
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import http from './lib/http';

type HqjhGetUrlRequest = {
  serviceUserKey?: string;
  payUrl?: string;
  expireUrl?: string;
  mchUuidNo?: string;
  moduleKey?: string;
  exchangeRate?: string;
  unitExchangeRate?: string;
  unit?: string;
  attach?: string;
  clientType?: string;
  roundType?: string;
  params?: Record<string, any> | null;
  chargeShowWay?: string;
  userCharge?: string;
};

const form = reactive<HqjhGetUrlRequest>({
  payUrl: `${location.origin}/hqjh/pay`,
  expireUrl: `${location.origin}/hqjh/expire`,
  moduleKey: '',
  exchangeRate: '',
  unitExchangeRate: '',
  unit: '',
  attach: '',
  clientType: '',
  roundType: '',
  chargeShowWay: '',
  userCharge: '',
  params: null,
});

const paramsText = ref('');
const loading = ref(false);
const error = ref('');
const result = ref<any>(null);
const linkUrl = ref('');
const linkMeta = reactive<{ mchUuidNo?: string; uuidNo?: string }>({});

function pretty(obj: any) {
  try { return JSON.stringify(obj, null, 2); } catch { return String(obj); }
}

async function submit() {
  error.value = '';
  result.value = null;
  loading.value = true;
  try {
    let parsed: any = null;
    if (paramsText.value && paramsText.value.trim().length > 0) {
      parsed = JSON.parse(paramsText.value);
    }

    const payload: HqjhGetUrlRequest = { ...form, params: parsed };

    const { data: json } = await http.post('/api/hqjh/getUrl', payload);
    const data: any = (json as any)?.data ?? json;
    const hqjh: any = data?.hqjh ?? data;
    const rawUrl: any = hqjh?.url ?? hqjh?.data?.url ?? hqjh?.data?.hqjh?.url ?? null;
    linkUrl.value = sanitizeUrl(rawUrl);
    linkMeta.mchUuidNo = hqjh?.mchUuidNo ?? data?.mchUuidNo;
    linkMeta.uuidNo = hqjh?.uuidNo ?? data?.uuidNo;
    result.value = hqjh ?? json;
  } catch (e: any) {
    error.value = e?.message || '请求失败';
  } finally {
    loading.value = false;
  }
}

function sanitizeUrl(u: any): string {
  if (!u) return '';
  let s = String(u).trim();
  s = s.replace(/^`+|`+$/g, '');
  s = s.replace(/^"+|"+$/g, '');
  s = s.replace(/^'+|'+$/g, '');
  return s;
}

async function copyLink() {
  if (!linkUrl.value) return;
  try {
    await navigator.clipboard.writeText(linkUrl.value);
  } catch {
    const ta = document.createElement('textarea');
    ta.value = linkUrl.value;
    document.body.appendChild(ta);
    ta.select();
    document.execCommand('copy');
    document.body.removeChild(ta);
  }
}

function openLink() {
  if (!linkUrl.value) return;
  window.open(linkUrl.value, '_blank');
}
</script>

<style lang="scss" scoped>
.title-2 { font-size: 1.25rem; font-weight: 600; }
.title-3 { font-size: 1.125rem; font-weight: 600; }
.msg.error { color: #dc2626; }
</style>