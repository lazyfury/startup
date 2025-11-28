import { createApp } from 'vue'
import '@unocss/reset/tailwind.css'
import 'virtual:uno.css'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as Icons from '@element-plus/icons-vue'
import router from './router'
import { fetchMenus } from './modules/menu'
import { createPinia } from 'pinia'
import { useUserStore } from './stores/user'

const app = createApp(App)
for (const [key, component] of Object.entries(Icons)) {
  app.component(key, component as any)
}
app.use(ElementPlus, { locale: zhCn, size: 'default', zIndex: 3000 })
const pinia = createPinia()
app.use(pinia)



const add = (m: any) => {
  const exists = router.getRoutes().some((r) => r.path === m.path)
  if (!exists) {
    router.addRoute('admin', {
      path: m.path,
      name: m.path,
      component: () => import('./views/GenericPage.vue'),
      meta: { title: m.title }
    })
  }
  m.children?.forEach(add)
}

fetchMenus().then((menus) => {
    menus.forEach(add)
}).finally(async () => {
  const userStore = useUserStore()
  if (localStorage.getItem('token')) userStore.setToken(localStorage.getItem('token'))
  await userStore.loadProfile()
  console.log(router.getRoutes())
  app.use(router)
  app.mount('#app')
})
