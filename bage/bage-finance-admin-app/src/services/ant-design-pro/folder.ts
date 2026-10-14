// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 币别列表 GET /currencyConfig/list */
export async function list(
  params: {
    pid?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/folder/list', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

//创建文件夹
export async function create(
  params: {
    pid?: number,
    name?: string,
    sort?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/folder/create', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

//修改文件夹
export async function update(
  params: {
    id?: number,
    name?: string,
    sort?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/folder/update', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

//删除文件夹
export async function del(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/folder/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

//获取文件夹详情
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/folder/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}