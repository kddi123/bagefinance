// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 创建凭证字 POST /voucher/save */
export async function save(
  params: {

  },
  options?: { [key: string]: any }) {
  return request('/adminapi/voucher/save', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 创建凭证字 POST /voucher/list */
export async function list(
  params: {

  },
  options?: { [key: string]: any }) {
  return request('/adminapi/voucher/list', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 获取凭证明细 GET /voucher/list */
export async function get(
  params: {
    id: number
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/voucher/get', {
    method: 'GET',
    params: params,
    ...(options || {}),
  });
}

// 删除凭证
export async function del(
  params: {
    id: number,
    voucherNum: number
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/voucher/del', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}