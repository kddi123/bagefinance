// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 获取树形菜单 GET /sysMenu/listTreeMenu */
export async function listTreeMenu(
  params: {
    title?: string;
  },
  options?: { [key: string]: any },
) {
  return request<API.ApiResponse<API.ListTreeMenuVo[]>>('/adminapi/sysMenu/listTreeMenu', {
    method: 'GET',
    params:params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取树形选择菜单 GET /SysMenu/ListTreeSelectMenuVo */
export async function listTreeSelectMenu(
  options?: { [key: string]: any },
) {
  return request<API.ApiResponse<API.ListTreeSelectMenuVo[]>>('/adminapi/sysMenu/listTreeSelectMenu', {
    method: 'GET',
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取树形菜单明细 GET /sysMenu/getMenuById */
export async function getMenuById(
  params: {
    id: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.ApiResponse<API.ListTreeSelectMenuVo>>('/adminapi/sysMenu/getMenuById', {
    method: 'GET',
    params: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建菜单 POST /sysMenu/create */
export async function createMenu(
  params: {
    pid: number;
    name?: string;
    path?: string;
    component?: string;
    icon?: string;
    layout?: boolean;
    hideInMenu?: boolean;
    redirect?: string;
    sort?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.ApiResponse<API.ListTreeSelectMenuVo>>('/adminapi/sysMenu/create', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建菜单 POST /sysMenu/update */
export async function updateMenu(
  params: {
    id: number;
    pid: number;
    name?: string;
    path?: string;
    component?: string;
    icon?: string;
    layout?: boolean;
    hideInMenu?: boolean;
    redirect?: string;
    sort?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.ApiResponse<API.ListTreeSelectMenuVo>>('/adminapi/sysMenu/update', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 删除菜单 POST /sysMenu/del */
export async function delMenu(
  params: {
    id?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.ApiResponse<API.ListTreeSelectMenuVo>>('/adminapi/sysMenu/del', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}