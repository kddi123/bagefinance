// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 手机注册账号 POST /adminapi/reg/phoneReg */
export async function phoneReg(
  body: API.PhoneRegisterRequest,
  options?: { [key: string]: any }) {
  return request<API.PhoneRegisterResponse>('/adminapi/reg/phoneReg', {
    method: 'POST',
    data: body,
    ...(options || {}),
  });
}