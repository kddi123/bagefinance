// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取客户辅助核算列表 GET /assistCalculateCustomer/list */
export async function listAssistCalculateCustomer(
  params: {
    codeOrName?: string,
    pageNum?:number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustomer/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}


/** 删除辅助核算 GET /assistCalculateCustomer/del */
export async function delAssistCalculateCustomer(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustomer/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 GET /assistCalculateCustomer/create */
export async function create(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustomer/create', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改辅助核算 GET /assistCalculateCustomer/update */
export async function update(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustomer/update', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取辅助核算详情 GET /assistCalculateCustomer/get */
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustomer/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 禁用或启用客户辅助核算 POST /assistCalculateCustomer/updateDisable */
export async function updateAssistCalculateCustomerDisable(
  params: {
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustomer/updateDisable', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}