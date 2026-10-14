// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取客户辅助核算列表 GET /assistCalculate/list */
export async function listAssistCalculate(
  params: {
    assistCalculateCateId?: string
    codeOrName?: string
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculate/list', {
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
  return request('/adminapi/assistCalculate/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}