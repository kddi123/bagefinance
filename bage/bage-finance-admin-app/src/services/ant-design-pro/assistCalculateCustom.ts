// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取客户辅助核算列表 GET /assistCalculate/list */
export async function listAssistCalculate(
  params: {
    assistCalculateCateId?: string
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustom/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,  
    ...(options || {}),
  });
}


/** 删除辅助核算 GET /assistCalculate/del */
export async function delAssistCalculate(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustom/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 GET /assistCalculate/create */
export async function createAssistCalculate(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustom/create', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改辅助核算 post /assistCalculate/update */
export async function updateAssistCalculate(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustom/update', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取客户辅助核算明细 GET /assistCalculate/get */
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustom/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 禁用或启用客户辅助核算 POST /assistCalculateCustom/updateDisable */
export async function updateAssistCalculateCustomDisable(
  params: {
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCustom/updateDisable', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}