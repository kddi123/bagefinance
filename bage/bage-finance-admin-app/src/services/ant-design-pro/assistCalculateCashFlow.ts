// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取现金流辅助核算列表 GET /assistCalculateCashFlow/list */
export async function listAssistCalculateCashFlow(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number,
    pageSize?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateCashFlow/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}