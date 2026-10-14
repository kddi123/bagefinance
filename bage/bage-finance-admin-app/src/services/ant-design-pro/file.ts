// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 币别列表 GET /file/list */
export async function list(
  params: {
    folderId?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/file/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 文件地址 GET /file/getPicUrl */
export async function getPicUrl(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/file/getPicUrl', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 文件地址 POST /file/del */
export async function del(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/file/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}


/** 币别列表 GET /file/listByIds */
export async function listByIds(
  params: {
    ids?: []
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/file/listByIds', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 合并上传的文件分片 /file/composeFile */
export async function composeFile(
  params: {
    folderId?: number,
    totalChunks?: number,
    fileName?: string,
    uid?: string
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/file/composeFile', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 合并上传的文件分片 /file/listFileIds */
export async function listFileIds(
  params: {
    fileRefType?: number,
    fileRefId?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/file/listFileIds', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}