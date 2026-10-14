// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 手机注册账号 POST /adminapi/reg/phoneReg */
export async function accountBookList(
  params: {
    // query
    /** 当前的页码 */
    pageNum?: number;
    /** 页面的容量 */
    pageSize?: number;
  },
  options?: { [key: string]: any }) {
  return request<API.ListAccountBookVo>('/adminapi/account_book/list', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function disableAccountBook(
  params: {
    /** 账套id */
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/account_book/disable', {
    method: 'POST',
    data: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function delAccountBook(
  params: {
    /** 账套id */
    id?: number;
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/account_book/del', {
    method: 'POST',
    data: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function addAccountBook(
  params: {
    /** 账套id */
    companyName?: string;
    unifiedSocialCreditCode?: string;
    industryId?: string;
    valueAddedTaxCate?: number;
    enableVoucherVerify?: boolean;
    startTime?: Date;
    accountingStandard?: number;
    enableFixedAssets?: boolean;
    enableCapital?: boolean;
    enablePsi?: boolean;
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/account_book/add', {
    method: 'POST',
    data: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function getAccountBook(
  params: {
    /** 账套id */
    id?: number;
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<API.GetAccountBookVo>>('/adminapi/account_book/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function updateAccountBook(
  params: {
    /** 账套id */
    id: number;
    companyName?: string;
    unifiedSocialCreditCode?: string;
    industryId?: string;
    valueAddedTaxCate: number;
    enableVoucherVerify?: boolean;
    startTime?: Date;
    accountingStandard: number;
    enableFixedAssets: boolean;
    enableCapital: boolean;
    enablePsi: boolean;
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/account_book/update', {
    method: 'POST',
    data: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function listHangYe(
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<API.DataDictionaryVo[]>>('/adminapi/DataDictionary/listHangYe', {
    method: 'GET',
    ...(options || {}),
  });
}

export async function listKuaiJiZhunZe(
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<API.DataDictionaryVo[]>>('/adminapi/DataDictionary/listKuaiJiZhunZe', {
    method: 'GET',
    ...(options || {}),
  });
}