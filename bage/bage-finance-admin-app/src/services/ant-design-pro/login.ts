// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 发送验证码 POST /api/login/captcha */
export async function sendSmsCode(
  params: {
    // query
    /** 手机号 */
    clientId?: string;
    phone?: string;
    code?: string;
    smsCodeType?:string;
  },
  options?: { [key: string]: any },
) {
  return request<API.FakeCaptcha>('/adminapi/login/sendSmsCode', {
    method: 'GET',
    params: {
      ...params,
    },
    skipErrorHandler:false,
    ...(options || {}),
  });
}

/** 获取微信公众号二维码 GET /user/generateMpRegCode */
export async function generateMpRegCode(
  params: {
    // query
    /** 客户端id */
    clientId: string;
  },
  options?: { [key: string]: any }) {
  return request<API.GenerateMpRegCode>('/adminapi/reg/generateMpRegCode', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取图形验证码 GET /user/getBase64Code */
export async function generateBase64Code(
  params: {
    // query
    /** 客户端id */
    clientId: string;
  },
  options?: { [key: string]: any }) {
  return request<API.GenerateBase64Code>('/adminapi/login/getBase64Code', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取客户端id GET /user/getClientId */
export async function getClientId(options?: { [key: string]: any }) {
  return request<API.GetClientId>('/adminapi/login/getClientId', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取客户端token GET /user/getClientToken */
export async function getClientToken(
  params: {
    // query
    /** 客户端id */
    clientId: string;
  },
  options?: { [key: string]: any }) {
  return request<API.GetClientToken>('/adminapi/login/getClientToken', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}