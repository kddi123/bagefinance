// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 获取资源列表 POST /adminapi/sysResource/list */
export async function list(
  params: {
    name?: string;
    path?: string;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysResource/list', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 创建资源 POST /adminapi/sysResource/create */
export async function create(
  params: {

  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysResource/create', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 获取资源 GET /adminapi/sysResource/get */
export async function get(
  params: {
    id?: number;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysResource/get', {
    method: 'GET',
    params: params,
    ...(options || {}),
  });
}

/** 编辑资源 POST /adminapi/sysResource/update */
export async function update(
  params: {

  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysResource/update', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}


/** 删除资源 POST /adminapi/sysResource/del */
export async function del(
  params: {
    id?: number
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysResource/del', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}