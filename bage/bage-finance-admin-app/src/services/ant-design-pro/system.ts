// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 修改用户姓名和邮箱 POST /adminapi/user/updateEmailAndName */
export async function updateEmailAndName(
  params: {
    email?: string;
    name?: string;
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/user/updateEmailAndName', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 修改密码 POST /adminapi/user/updatePassword */
export async function updatePassword(
  params: {
    oldPassword?: string;
    password?: string;
    confirmPassword?: string;
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/user/updatePassword', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 获取图形验证码 GET /user/getBase64Code */
export async function generateBase64Code(
  options?: { [key: string]: any }) {
  return request<API.GenerateBase64Code>('/adminapi/user/getBase64Code', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 发送短信验证码 GET /user/sendSmsCode */
export async function sendSmsCode(
  params: {
    // query
    /** 手机号 */
    phone?: string;
    code?: string;
    smsCodeType?: string;
  },
  options?: { [key: string]: any },
) {
  return request<API.FakeCaptcha>('/adminapi/user/sendSmsCode', {
    method: 'GET',
    params: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 修改手机号 GET /user/updatePhone */
export async function updatePhone(
  params: {
    // query
    /** 手机号 */
    phone?: string;
    smsCode?: string;
  },
  options?: { [key: string]: any },
) {
  return request<boolean>('/adminapi/user/updatePhone', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}


/** 获取省市区撒胡椒 GET /sysRegion/listAllByCache */
export async function listRegion(
  options?: { [key: string]: any }) {
  return request('/adminapi/sysRegion/listAllByCache', {
    method: 'GET',
    ...(options || {}),
  });
}