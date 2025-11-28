export const API = {
  auth: {
    login: '/api/auth/login',
    profile: '/api/auth/profile',
    createAdminDebug: '/api/auth/debug/create-admin'
  },
  configGroup: {
    list: '/config-group',
    item: (id: number) => `/config-group/${id}`
  },
  menu: {
    list: '/menu',
    tree: '/menu/tree',
    item: (id: number) => `/menu/${id}`,
    permissions: (id: number) => `/menu/${id}/permissions`,
    autoPermissions: (id: number) => `/menu/${id}/permissions/auto`
  },
  users: {
    list: '/user',
    item: (id: number) => `/user/${id}`
  },
  roles: {
    list: '/role',
    item: (id: number) => `/role/${id}`
  },
  permissions: {
    list: '/permission',
    item: (id: number) => `/permission/${id}`,
    tags: '/permission/tags',
    searchByTag: '/permission/search-by-tag'
    ,tree: '/permission/tree'
  },
  acl: {
    rolePermissions: (roleId: number) => `/acl/role/${roleId}/permissions`,
    userRoles: (userId: number) => `/acl/user/${userId}/roles`
  }
}
