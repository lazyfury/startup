import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import Login from '../views/Login.vue'
import AdminLayout from '../layouts/AdminLayout.vue'

const routes: RouteRecordRaw[] = [
  { path: '/login', name: 'login', component: Login, meta: { requiresAuth: false } },
  {
    path: '/',
    name: 'admin',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '仪表盘' } },
      { path: 'system/users', name: 'users', component: () => import('../views/Users.vue'), meta: { title: '用户管理' } },
      { path: 'system/roles', name: 'roles', component: () => import('../views/Roles.vue'), meta: { title: '角色管理' } },
      { path: 'system/permissions', name: 'permissions', component: () => import('../views/Permissions.vue'), meta: { title: '权限管理' } },
      { path: 'system/menus', name: 'menus', component: () => import('../views/Menus.vue'), meta: { title: '菜单管理' } },
      { path: 'system/config-groups', name: 'config-groups', component: () => import('../views/ConfigGroups.vue'), meta: { title: '配置分组' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: () => (localStorage.getItem('token') ? '/' : '/login') }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth !== false && !token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
