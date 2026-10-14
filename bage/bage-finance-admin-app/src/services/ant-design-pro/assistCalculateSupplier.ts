// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取辅助核算列表 GET /assistCalculateSupplier/list */
export async function listAssistCalculateSupplier(
  params: {
    codeOrName?: string,
    pageNum?:number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSupplier/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}


/** 删除辅助核算 GET /assistCalculateSupplier/del */
export async function delAssistCalculateSupplier(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSupplier/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 GET /assistCalculateSupplier/create */
export async function create(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSupplier/create', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改辅助核算 GET /assistCalculateSupplier/update */
export async function update(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSupplier/update', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取辅助核算详情 GET /assistCalculateSupplier/get */
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSupplier/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 禁用或启用供应商辅助核算 POST /assistCalculateSupplier/updateDisable */
export async function updateAssistCalculateSupplierDisable(
  params: {
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSupplier/updateDisable', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}