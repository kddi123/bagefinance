// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 凭证字列表 GET /currencyConfig/list */
export async function list(
  options?: { [key: string]: any },
) {
  return request('/adminapi/voucherWordConfig/list', {
    method: 'GET',
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 凭证字详情 GET /voucherWordConfig/get */
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/voucherWordConfig/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改凭证字 POST /voucherWordConfig/update */
export async function update(
  params: {
    id?: number
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/voucherWordConfig/update', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 创建凭证字 POST /voucherWordConfig/create */
export async function create(
  params: {

  },
  options?: { [key: string]: any }) {
  return request('/adminapi/voucherWordConfig/create', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 删除凭证字 POST /voucherWordConfig/del */
export async function del(
  params: {
    id?: number
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/voucherWordConfig/del', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 修改凭证字默认值(改为默认值) POST /voucherWordConfig/updateDefaultFlag */
export async function updateDefaultFlag(
  params: {
    id?: number
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/voucherWordConfig/updateDefaultFlag', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}