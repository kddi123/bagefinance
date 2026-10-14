// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 租户列表 GET /tenant/get */
export async function getBalanceSheet(
  params: {
    /** 当前的页码 */
    id?: number;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/report/getBalanceSheet', {
    method: 'POST',
    params: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}