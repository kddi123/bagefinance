// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 查询角色绑定的系统资源id列表 POST /adminapi/sysRoleBindMenu/listBindMenuIdByRoleId */
export async function listBindMenuIdByRoleId(
  params: {
    roleId?: number;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/sysRoleBindMenu/listBindMenuIdByRoleId', {
    method: 'GET',
    params: params,
    ...(options || {}),
  });
}