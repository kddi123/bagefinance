// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 币别列表 GET /currencyConfig/list */
export async function list(
  options?: { [key: string]: any },
) {
  return request('/adminapi/currencyConfig/list', {
    method: 'GET',
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 币别详情 GET /currencyConfig/get */
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/currencyConfig/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改币别 POST /currencyConfig/update */
export async function update(
  params: {
    id?: number
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/currencyConfig/update', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 创建币别 POST /currencyConfig/create */
export async function create(
  params: {
    
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/currencyConfig/create', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 删除币别 POST /currencyConfig/del */
export async function del(
  params: {
    id?: number
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/currencyConfig/del', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}