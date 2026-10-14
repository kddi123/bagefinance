// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取项目辅助核算列表 GET /assistCalculateProject/list */
export async function listAssistCalculateProject(
  params: {
    codeOrName?: string,
    pageNum?:number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateProject/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}


/** 删除辅助核算 GET /assistCalculateProject/del */
export async function delAssistCalculateProject(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateProject/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 GET /assistCalculateProject/create */
export async function create(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateProject/create', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改辅助核算 GET /assistCalculateProject/update */
export async function update(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateProject/update', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取辅助核算详情 GET /assistCalculateProject/get */
export async function get(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateProject/get', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 禁用或启用项目辅助核算 POST /assistCalculateProject/updateDisable */
export async function updateAssistCalculateProjectDisable(
  params: {
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateProject/updateDisable', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}