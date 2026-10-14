// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取部门辅助核算列表 GET /assistCalculateDepartment/list */
export async function listAssistCalculateDepartment(
  params: {
    codeOrName?: string,
    pageNum?:number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateDepartment/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}


/** 删除辅助核算 GET /assistCalculateDepartmentr/del */
export async function delAssistCalculateDepartment(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateDepartment/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 GET /assistCalculateDepartment/create */
export async function create(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateDepartment/create', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改辅助核算 GET /assistCalculateDepartment/update */
export async function update(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateDepartment/update', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取辅助核算详情 GET /assistCalculateDepartment/get */
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateDepartment/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 禁用或启用客户辅助核算 POST /assistCalculateDepartment/updateDisable */
export async function updateAssistCalculateDepartmentDisable(
  params: {
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateDepartment/updateDisable', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}