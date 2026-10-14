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
  return request('/adminapi/reportIncomeStatement/getReport', {
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
  return request('/adminapi/reportIncomeStatement/saveFormula', {
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
  return request('/adminapi/reportIncomeStatement/delFormula', {
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
  return request('/adminapi/reportIncomeStatement/listReportFormula', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}