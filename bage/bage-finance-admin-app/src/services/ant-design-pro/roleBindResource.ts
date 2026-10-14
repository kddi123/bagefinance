// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 查询角色绑定的系统资源id列表 POST /adminapi/sysRoleBindResource/listBindResourceIdByRoleId */
export async function listBindResourceIdByRoleId(
  params: {
    roleId?: number;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysRoleBindResource/listBindResourceIdByRoleId', {
    method: 'GET',
    params: params,
    ...(options || {}),
  });
}