// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取存货辅助核算列表 GET /assistCalculateInventory/list */
export async function listAssistCalculateInventory(
  params: {
    codeOrName?: string,
    pageNum?:number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateInventory/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}


/** 删除辅助核算 GET /assistCalculateInventory/del */
export async function delAssistCalculateInventory(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateInventory/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 GET /assistCalculateInventory/create */
export async function create(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateInventory/create', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改辅助核算 GET /assistCalculateInventory/update */
export async function update(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateInventory/update', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取辅助核算详情 GET /assistCalculateInventory/get */
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateInventory/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 禁用或启用存货辅助核算 POST /assistCalculateInventory/updateDisable */
export async function updateAssistCalculateInventoryDisable(
  params: {
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateInventory/updateDisable', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}