import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';
import { message, notification } from 'antd';
import { json } from 'express';
import { history } from 'umi';

export enum ErrorCode {
  //参数错误
  PARAMETER_ERROR = 100,
  //业务错误
  BUSINESS_ERROR = 200,
  //登录错误
  LOGIN_ERROR = 201
}

// 错误处理方案： 错误类型
enum ErrorShowType {
  SILENT = 0,
  WARN_MESSAGE = 1,
  ERROR_MESSAGE = 2,
  NOTIFICATION = 3,
  REDIRECT = 9,
}
// 与后端约定的响应数据格式
export interface ResponseStructure {
  success: boolean;
  data: any;
  code?: number;
  errorMessage?: Map<string, string>;
  showType?: ErrorShowType;
}

/**
 * @name 错误处理
 * pro 自带的错误处理， 可以在这里做自己的改动
 * @doc https://umijs.org/docs/max/request#配置
 */
export const errorConfig: RequestConfig = {
  // 错误处理： umi@3 的错误处理方案。
  errorConfig: {
    // 错误抛出
    errorThrower: (res) => {
      const { success, data, code, errorMessage, showType } =
        res as unknown as ResponseStructure;
      if (!success) {
        const error: any = new Error();
        error.name = 'BizError';
        error.info = { code, errorMessage, showType, data };
        throw error; // 抛出自制的错误
      }
    },
    // 错误接收及处理
    errorHandler: (error: any, opts: any) => {
      const errorInfo: ResponseStructure | undefined = error.info;
      if (opts?.skipErrorHandler) throw error;

      // 我们的 errorThrower 抛出的错误。
      if (error.name === 'BizError') {
        if (errorInfo) {
          const { errorMessage, code } = errorInfo;
          let errorMessageString = "";
          console.log("errorMessage" + JSON.stringify(errorMessage))
          errorMessageString = Object.entries(errorMessage)
            .map(([key, value]) => `${key}: ${value}`)
            .join('\n');
          console.log("错误信息：" + errorMessageString);
          console.log("code" + code);
          switch (code) {
            case ErrorCode.PARAMETER_ERROR://参数错误
              message.error(errorMessageString);
              break;
            case ErrorCode.BUSINESS_ERROR://业务错误
              message.error(errorMessageString);
              break;
            case ErrorCode.LOGIN_ERROR://业务错误
              history.push("/user/login");
              break;
            case ErrorShowType.SILENT:
              // do nothing
              break;
            case ErrorShowType.WARN_MESSAGE:
              message.warn(errorMessageString);
              break;
            case ErrorShowType.ERROR_MESSAGE:
              message.error(errorMessageString);
              break;
            case ErrorShowType.NOTIFICATION:
              notification.open({
                description: errorMessageString,
                message: code,
              });
              break;
            case ErrorShowType.REDIRECT:
              // TODO: redirect
              break;
            default:
              message.error(errorMessageString);
          }
        }
      } else if (error.response) {
        // Axios 的错误
        // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
        message.error(`Response status:${error.response.status}`);
      } else if (error.request) {
        // 请求已经成功发起，但没有收到响应
        // \`error.request\` 在浏览器中是 XMLHttpRequest 的实例，
        // 而在node.js中是 http.ClientRequest 的实例
        message.error('None response! Please retry.');
      } else {
        // 发送请求时出了点问题
        message.error('Request error, please retry.');
      }
    },
  },

  // 请求拦截器
  requestInterceptors: [
    (config: RequestOptions) => {
      // 拦截请求配置，进行个性化处理。
      const url = config?.url?.concat('');//?token = 123
      // 这是用于后端请求认证的，后端权限框架我是采用saToken
      let token = localStorage.getItem('token');
      let clientId = localStorage.getItem('clientId');

      if (token !== null && token !== undefined) {
        const tokenHeader = {
          'api-access-token': `${token}`,
          'client-id': `${clientId}`
        };
        config.headers = tokenHeader;
      }

      return { ...config, url };
    },
  ],

  // 响应拦截器
  responseInterceptors: [
    (response) => {
      // 拦截响应数据，进行个性化处理
      const { data } = response as unknown as ResponseStructure;

      if (data?.success === false) {
        let errorMessageString = "";
        console.log("errorMessage" + JSON.stringify(data?.errorMessage))
        errorMessageString = Object.entries(data?.errorMessage)
          .map(([key, value]) => `${key}: ${value}`)
          .join('\n');
        console.log("错误信息：" + errorMessageString);
        //message.error(errorMessageString);
      }
      return response;
    },
  ]
};
