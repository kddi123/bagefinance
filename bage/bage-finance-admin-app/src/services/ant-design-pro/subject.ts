// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 获取科目列表 POST /adminapi/subject/list */
export async function list(
  params: {
    subjectCate: string;
    pageNum?: number;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/subject/list', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}


/** 获取科目列表 POST /adminapi/subject/listByCateAndCodeAndName */
export async function listByCateAndCodeAndName(
  params: {
    subjectCate?: number;
    codeAndName?: string;
    pageNum?: number;
  },
  options?: { [key: string]: any }) {
  return request('/adminapi/subject/listByCateAndCodeAndName', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 获取科目明细 GET /subject/get */
export async function getSubject(
  params: {
    id: number;
    pid: number;
  },
  options?: { [key: string]: any },
) {
  const { id, pid } = params;
  let endpoint = '';

  if (id && id > 0) {
    endpoint = '/adminapi/subject/get';  // 替换为实际的带有 id 参数的接口路径
  } else if (pid && pid > 0) {
    endpoint = '/adminapi/subject/getByPid';  // 替换为实际的带有 pid 参数的接口路径
  } else {
    // 如果既没有 id 也没有 pid，可能需要进行错误处理或者返回默认值
    return Promise.reject(new Error('Invalid parameters. Either id or pid should be greater than 0.'));
  }

  return request(endpoint, {
    method: 'GET',
    params: {
      ...params
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 获取科目明细 GET /subject/getDetail */
export async function getSubjectDetail(
  params: {
    id: number;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/subject/getDetail', {
    method: 'GET',
    params: {
      ...params
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建科目 POST /adminapi/subject/create */
export async function createSubject(
  params: {
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/subject/create', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}


/** 编辑科目 POST /adminapi/subject/update */
export async function updateSubject(
  params: {
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/subject/update', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 禁用或启用科目 POST /subject/disable */
export async function disable(
  params: {
    id?: number,
    disable?: boolean
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/subject/disable', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 删除科目 POST /subject/del */
export async function del(
  params: {
    id?: number
  },
  options?: { [key: string]: any }) {
  return request<API.ApiResponse<boolean>>('/adminapi/subject/del', {
    method: 'POST',
    data: params,
    ...(options || {}),
  });
}

/** 导出科目 POST /subject/download */
export async function download(
  options?: { [key: string]: any }) {
  let token = localStorage.getItem('token');
  let clientId = localStorage.getItem('clientId');

  const response = await fetch('/adminapi/subject/download', {
    method: 'GET',
    headers: {
      'api-access-token': `${token}`,  // 设置 Authorization 头，Bearer 是一种常见的身份验证机制
      'client-id': `${clientId}`
    },
    // responseType: 'blob', // 设置响应类型为 blob
    ...(options || {}),
  });
  // 处理文件下载
  // 从响应头中获取文件名
  const contentDisposition = response.headers.get('content-disposition');
  const match = contentDisposition && contentDisposition.match(/filename="(.+)"/);
  const fileName = match ? match[1] : '科目.xlsx';

  const blob = await response.blob();
  console.log(blob);
  const url = window.URL.createObjectURL(blob);
  console.log(url);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', fileName);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
}