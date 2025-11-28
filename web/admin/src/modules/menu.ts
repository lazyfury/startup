export type MenuItem = {
  title: string
  path: string
  icon?: string
  children?: MenuItem[]
}

export async function fetchMenus(): Promise<MenuItem[]> {
  await new Promise((r) => setTimeout(r, 200))
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
        { title: '配置分组', path: '/system/config-groups', icon: 'Setting' }
      ]
    }
  ]
}
