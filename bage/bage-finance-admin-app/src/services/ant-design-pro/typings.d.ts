// @ts-ignore
/* eslint-disable */

declare namespace API {
  type ApiResponse<T> = {
    success?: boolean;
    code?: number;
    codeMessage?: string;
    errorMessage?: Map<string, string>;
    showType?: ErrorShowType;
    data: T
  }
  type CurrentUser = {
    name?: string;
    avatar?: string;
    userid?: string;
    email?: string;
    signature?: string;
    title?: string;
    group?: string;
    tags?: { key?: string; label?: string }[];
    notifyCount?: number;
    unreadCount?: number;
    country?: string;
    access?: string;
    geographic?: {
      province?: { label?: string; key?: string };
      city?: { label?: string; key?: string };
    };
    address?: string;
    phone?: string;
  };

  type LoginResult = ResponseStructure & {
    data?: {
      token?: string;
      expireDateTime?: number;
      expire?: number;
      timeUnit?: any;
    }
  };

  type PageParams = {
    pageNum?: number;
    pageSize?: number;
  };

  type RuleListItem = {
    key?: number;
    disabled?: boolean;
    href?: string;
    avatar?: string;
    name?: string;
    owner?: string;
    desc?: string;
    callNo?: number;
    status?: number;
    updatedAt?: string;
    createdAt?: string;
    progress?: number;
  };

  type RuleList = {
    data?: RuleListItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };
  interface ResponseStructure {
    success?: boolean;
    code?: number;
    codeMessage?: string;
    errorMessage?: Map<string, string>;
    showType?: ErrorShowType;
  }
  type FakeCaptcha = ResponseStructure & {
    data?: boolean;
  };

  type PhonePasswordLoginParams = {
    clientId?: string;
    phone?: string;
    password?: string;
    code?: string;
    autoLogin?: boolean;
  };

  type PhoneSmsCodeLoginParams = {
    clientId?: string;
    phone?: string;
    smsCode?: string;
    autoLogin?: boolean;
  };

  type ErrorResponse = {
    /** 业务约定的错误码 */
    errorCode: string;
    /** 业务上的错误信息 */
    errorMessage?: Map<string, string>;
    /** 业务上的请求是否成功 */
    success?: boolean;
  };

  type NoticeIconList = {
    data?: NoticeIconItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type NoticeIconItemType = 'notification' | 'message' | 'event';

  type NoticeIconItem = {
    id?: string;
    extra?: string;
    key?: string;
    read?: boolean;
    avatar?: string;
    title?: string;
    status?: string;
    datetime?: string;
    description?: string;
    type?: NoticeIconItemType;
  };

  type GenerateMpRegCode = {
    data?: {
      regCode?: string;
      qrCodeUrl?: string;
      expireSeconds?: number;
      ticket?: string;
      url?: string;
    };
  }

  type GetClientId = {
    data?: string;
  }

  type GenerateBase64Code = {
    data?: string;
  }

  type GetClientToken = {
    data?: {
      token?: string;
      expireDateTime?: number;
      expire?: number;
    };
  }

  type PhoneRegisterRequest = {
    clientId?: string;
    phone?: string;
    password?: string;
    confirmPassword?: string;
    smsCode?: number;
  }

  type PhoneRegisterResponse = ResponseStructure & {
    data?: number;
  }

  type CustomValidate = {
    validateStatus?: boolean;
    msg?: string;
    formValue?: any;
    code?: number;
    codeMessage?: string;
    errors?: Record<string, string>;
    validateFields?: string[];
  }

  type ListAccountBookVo = ResponseStructure & {
    data?: {
      total?: number;
      list?: ListAccountBookVoItem[]
    }
  }
  type ListAccountBookVoItem = {
    id: number;
    companyName?: string;
    valueAddedTaxCate?: string;
    accountingStandard?: string;
    startTime?: Date;
    createTime?: Date;
    enableVoucherVerify?: boolean;
    disable?: boolean;
  }
  type GetAccountBookVo = {
    id?: number;
    companyName?: string;
    unifiedSocialCreditCode?: string;
    industryId?: number;
    valueAddedTaxCate?: number;
    enableVoucherVerify?: boolean;
    startTime?: Date;
    accountingStandard?: number;
    enableFixedAssets?: boolean;
    enableCapital?: boolean;
    enablePsi?: boolean;
  }
  type DataDictionaryVo = {
    dataCode?: string;
    dataValue?: string;
  }

  type ListTreeMenuVo = {
    id?: number;
    key?: string;
    title?: any;
    icon?: string;
    checked?: boolean;
    children?: ListTreeMenuVo[];
    level?: number;
  };

  type ListTreeSelectMenuVo = {
    id?: number;
    value?: string;
    title?: any;
    children?: ListTreeMenuVo[];
  }

  type ListRoleVo = {
    id: number;
    roleName?: string;
    disable?: boolean;
  }

  type ListSubject = {
    id: number;
    pid?: number;
    valueAddedTaxCate?: string;
    accountingStandard?: string;
    startTime?: Date;
    createTime?: Date;
    enableVoucherVerify?: boolean;
    disable?: boolean;
  }
}