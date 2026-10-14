// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 租户列表 GET /tenant/get */
export async function getReport(
  params: {
    /** 当前的页码 */
    id?: number;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/reportCashFlowStatement/getReport', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function saveFormula(
  params: {

  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/reportCashFlowStatement/saveFormula', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function delFormula(
  params: {

  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/reportCashFlowStatement/delFormula', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function listReportFormula(
  params: {

  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/reportCashFlowStatement/listReportFormula', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}