// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 租户列表 GET /tenant/get */
export async function get(
  params: {
    /** 当前的页码 */
    id?: number;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/tenant/get', {
    method: 'GET',
    params: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}


/** 租户列表 GET /tenant/list */
export async function list(
  params: {
    // query
    /** 当前的页码 */
    pageNum?: number;
    /** 页面的容量 */
    pageSize?: number;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/tenant/list', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 租户列表 GET /tenant/bindRoleIds */
export async function bindRoleIds(
  params: {
    tenantId?:number;
    // query
    /** 当前的页码 */
    bindRoleIds?: [];
    /** 页面的容量 */
    unBindRoleIds?: [];
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/tenant/bindRoleIds', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改租户 GET /tenant/update */
export async function update(
  params: {

  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/tenant/update', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 租户列表 GET /tenant/listByName */
export async function listByName(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/tenant/listByName', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}