
import { request } from '@umijs/max';
import { message } from 'antd';

/** 获取用户列表 GET /user/listMember */
export async function listMember(
  options?: { [key: string]: any }) {
  return request('/adminapi/user/listMember', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取用户列表 GET /user/searchMember */
export async function searchMember(
  params: {
    name?: string;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/user/searchMember', {
    method: 'POST',
    data: {
      ...params
    },
    ...(options || {}),
  });
}

/** 退出登录 GET /user/loginOut */
export async function loginOut(
  options?: { [key: string]: any }) {
  return request('/adminapi/user/loginOut', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 获取用户列表 GET /searchMemberManage/searchMemberManage */
export async function searchMemberManage(
  params: {
    name?: string;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/memberManage/searchMemberManage', {
    method: 'POST',
    data: {
      ...params
    },
    ...(options || {}),
  });
}

/** 获取用户列表 POST /searchMemberManage/updateDisable */
export async function updateDisable(
  params: {
    name?: string;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/memberManage/updateDisable', {
    method: 'POST',
    data: {
      ...params
    },
    ...(options || {}),
  });
}

/** 绑定角色 POST /memberManage/bindRoleIds */
export async function bindRoleIds(
  params: {
    id?:number;
    // query
    /** 当前的页码 */
    bindRoleIds?: [];
    /** 页面的容量 */
    unBindRoleIds?: [];
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/memberManage/bindRoleIds', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取用户列表 GET /searchMemberManage/getMemberInfo */
export async function getMemberInfo(
  params: {
    id?: number;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/memberManage/getMemberInfo', {
    method: 'GET',
    params: {
      ...params
    },
    ...(options || {}),
  });
}