// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


/** 获取客户辅助核算列表 GET /assistCalculateSummary/list */
export async function list(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/list', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 禁用或启用客户辅助核算 POST /assistCalculateSummary/updateDisable */
export async function updateDisable(
  params: {
    id?: number;
    disable?: boolean;
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/updateDisable', {
    method: 'POST',
    data: {
      ...params,
    },
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 删除辅助核算 GET /assistCalculate/del */
export async function del(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/del', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

//***************************************************创建begin */
/** 创建辅助核算 POST /assistCalculateSummary/createCustom */
export async function createCustom(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/createCustom', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 POST /assistCalculateSummary/createCustomer */
export async function createCustomer(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/createCustomer', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 POST /assistCalculateSummary/createDepartment */
export async function createDepartment(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/createDepartment', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 POST /assistCalculateSummary/createEmployee */
export async function createEmployee(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/createEmployee', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 POST /assistCalculateSummary/createInventory */
export async function createInventory(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/createInventory', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 POST /assistCalculateSummary/createProject */
export async function createProject(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/createProject', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

/** 创建辅助核算 POST /assistCalculateSummary/createSupplier */
export async function createSupplier(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/createSupplier', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

//************************************创建end */

//********************************修改begin */
/** 修改辅助核算 post /assistCalculate/updateCustom */
export async function updateCustom(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/updateCustom', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function updateCustomer(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/updateCustomer', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}
export async function updateDepartment(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/updateDepartment', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}
export async function updateEmployee(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/updateEmployee', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}
export async function updateInventory(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/updateInventory', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}
export async function updateProject(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/updateProject', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}
export async function updateSupplier(
  params: {
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/updateSupplier', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}
//********************************修改end */


//********************************查询begin */

/** 获取客户辅助核算列表 GET /assistCalculate/listCustom */
export async function listCustom(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/listCustom', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function listCustomer(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/listCustomer', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function listDepartment(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/listDepartment', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function listEmployee(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/listEmployee', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function listInventory(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/listInventory', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function listProject(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/listProject', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function listSupplier(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/listSupplier', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function listCashFlow(
  params: {
    assistCalculateCateId?: number
    codeOrName?: string,
    pageNum?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/listCashFlow', {
    method: 'POST',
    data: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}
//********************************查询end */



//**************************************get begin */
/** 获取客户辅助核算明细 GET /assistCalculate/getCustom */
export async function getCustom(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/getCustom', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function getCustomer(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/getCustomer', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function getDepartment(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/getDepartment', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function getEmployee(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/getEmployee', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function getInventory(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/getInventory', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function getProject(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/getProject', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}

export async function getSupplier(
  params: {
    id?: number
  },
  options?: { [key: string]: any },
) {
  return request('/adminapi/assistCalculateSummary/getSupplier', {
    method: 'GET',
    params: params,
    skipErrorHandler: false,
    ...(options || {}),
  });
}
//**************************************get end */
