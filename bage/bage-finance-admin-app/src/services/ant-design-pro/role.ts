// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 查询角色列表 POST /sysMenu/listRole */
export async function listRole(
  params: {
    // roleName: string;
    // disable: boolean;
    // pageNum?: number;
    // /** 页面的容量 */
    // pageSize?: number;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/sysRole/list', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 删除角色 POST /sysRole/del */
export async function delSysRole(
  params: {
    id?: number;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/sysRole/del', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 禁用或启用角色 POST /sysRole/updateDisable */
export async function updateRoleDisable(
  params: {
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/sysRole/updateDisable', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取角色明细 POST /sysRole/getById */
export async function getRoleById(
  params: {
    id?: number;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/sysRole/getById', {
    method: 'GET',
    params: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改角色 POST /sysRole/update */
export async function updateRole(
  params: {
    id?: number;
    roleName?: string;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/sysRole/update', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改角色 POST /sysRole/update */
export async function createRole(
  params: {
    roleNo?: string;
    roleName?: string;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/sysRole/create', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 绑定角色菜单 POST /adminapi/sysRole/roleBindMenu */
export async function roleBindMenu(
  params: {
    roleId?: number;
    bindMenuIds?: [];
    unBindMenuIds?: []
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysRole/roleBindMenu', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 绑定角色资源 POST /adminapi/sysRole/listBindResourceIdByRoleId */
export async function roleBindResource(
  params: {
    roleId?: number;
    bindResourceIds?: [];
    unBindResourceIds?: []
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysRole/roleBindResource', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}