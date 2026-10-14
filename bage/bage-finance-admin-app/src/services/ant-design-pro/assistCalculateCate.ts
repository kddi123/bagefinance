import { request } from '@umijs/max';

/** 获取辅助核算类别列表 GET /assistCalculateCate/list */
export async function listAssistCalculateCate(
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCate/list', {
    method: 'GET',
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取辅助核算类别明细 GET /assistCalculateCate/getById */
export async function getAssistCalculateCateDetail(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCate/getById', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助类别 POST /assistCalculateCate/create */
export async function create(
  params: {
    code?: string,
    name?: string,
    customerColumnConfigList?: []
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCate/create', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改辅助类别 POST /assistCalculateCate/update */
export async function update(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCate/update', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 删除辅助类别 POST /assistCalculateCate/del */
export async function del(
  params: {
    id:number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCate/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}