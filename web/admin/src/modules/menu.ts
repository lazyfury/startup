import type { MenuItem as ApiMenuItem } from '../api/types'
import { MenuApi } from '../api/apis/menu'

export type MenuItem = {
  title: string
  path: string
  icon?: string
  children?: MenuItem[]
}

function mapMenu(items: ApiMenuItem[] = []): MenuItem[] {
  return (items || [])
    .filter((i) => !i.hidden)
    .map((i) => ({
      title: i.name || i.code || '',
      path: i.path || `/${i.code || ''}`,
      icon: i.icon || undefined,
      children: mapMenu(i.children || [])
    }))
}

function localMenus(): MenuItem[] {
  return [
    { title: '仪表盘', path: '/', icon: 'House' },
    {
      title: '系统管理',
      path: '/system',
      icon: 'Setting',
      children: [
        { title: '用户管理', path: '/system/users', icon: 'User' },
        { title: '角色管理', path: '/system/roles', icon: 'UserFilled' },
        { title: '权限管理', path: '/system/permissions', icon: 'Key' },
        { title: '菜单管理', path: '/system/menus', icon: 'Menu' },
        { title: '配置分组', path: '/system/config-groups', icon: 'Setting' },
        { title: '租户管理', path: '/system/tenants', icon: 'OfficeBuilding' },
        { title: '商户管理', path: '/system/merchants', icon: 'Shop' }
      ]
    }
  ]
}

function collectPaths(list: MenuItem[], set = new Set<string>()): Set<string> {
  for (const m of list || []) {
    if (m.path) set.add(m.path)
    if (m.children?.length) collectPaths(m.children, set)
  }
  return set
}

export async function fetchMenus(): Promise<MenuItem[]> {
  const json = await MenuApi.tree()
  const remote = mapMenu(json.data || [])
  const paths = collectPaths(remote)
  const locals = localMenus()
  const filteredLocals: MenuItem[] = []
  for (const m of locals) {
    if (paths.has(m.path)) continue
    if (m.children?.length) {
      const sub = m.children.filter((c) => !paths.has(c.path))
      if (sub.length) filteredLocals.push({ ...m, children: sub })
      else filteredLocals.push({ ...m, children: [] })
    } else {
      filteredLocals.push(m)
    }
  }
  return [...filteredLocals, ...remote]
}
