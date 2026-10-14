/**
 * @name umi 的路由配置
 * @description 只支持 path,component,routes,redirect,wrappers,title 的配置
 * @param path  path 只支持两种占位符配置，第一种是动态参数 :id 的形式，第二种是 * 通配符，通配符只能出现路由字符串的最后。
 * @param component 配置 location 和 path 匹配后用于渲染的 React 组件路径。可以是绝对路径，也可以是相对路径，如果是相对路径，会从 src/pages 开始找起。
 * @param routes 配置子路由，通常在需要为多个路径增加 layout 组件时使用。
 * @param redirect 配置路由跳转
 * @param wrappers 配置路由组件的包装组件，通过包装组件可以为当前的路由组件组合进更多的功能。 比如，可以用于路由级别的权限校验
 * @doc https://umijs.org/docs/guides/routes
 */
export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        name: 'login',
        path: '/user/login',
        component: './User/Login'
      }
    ]
  },
  {
    path: '/user',
    layout: false,
    access: "",
    routes: [
      {
        name: 'register',
        path: '/user/register',
        component: './User/Register',
      }
    ]
  },
  {
    path: '/welcome',
    name: '欢迎',
    icon: 'smile',
    component: './Welcome'
  },
  {
    path: '/admin',
    name: 'admin',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {
        path: '/admin',
        redirect: '/admin/sub-page'
      },
      {
        path: '/admin/sub-page',
        name: 'sub-page',
        component: './Admin'
      }
    ]
  },
  {
    path: '/',
    redirect: '/welcome'
  },
  {
    path: '*',
    layout: false,
    component: './404'
  },
  {
    name: '凭证',
    icon: 'FileTextOutlined',
    path: '/Voucher',
    routes: [
      {
        name: '新增凭证',
        path: '/Voucher/VoucherCreate',
        component: './Voucher/VoucherCreate'
      },
      {
        name: '查看凭证',
        path: '/Voucher/VoucherList',
        component: './Voucher/VoucherList'
      },
      {
        name: '修改凭证',
        path: '/Voucher/VoucherUpdate',
        component: './Voucher/VoucherUpdate'
      },
      {
        name: '会计电子档案',
        path: '/Voucher/File/List',
        component: './File/List'
      },
      {
        name: '打印',
        icon: 'FileTextOutlined',
        path: '/Voucher/VoucherPrint',
        component: './Voucher/VoucherPrint',
      },
    ]
  },
  {
    name: '账套',
    icon: 'AccountBookOutlined',
    path: '/Account',
    routes: [
      {
        name: '创建账套',
        path: '/Account/AccountCreate',
        component: './Account/AccountCreate'
      },
      {
        name: '查看账套',
        path: '/Account/AccountList',
        component: './Account/AccountList'
      },
      {
        name: '编辑账套',
        path: '/Account/AccountUpdate',
        component: './Account/AccountUpdate',
        hideInMenu: true
      },
      {
        name: '查看账套',
        path: '/Account/AccountDetail',
        component: './Account/AccountDetail',
        hideInMenu: true
      }
    ]
  },
  {
    name: '数据字典',
    icon: 'AccountBookOutlined',
    path: '/DataDictionary',
    hideInMenu: true,
    routes: [
      {
        name: '查询行业数据列表',
        path: '/DataDictionary/listHangYe',
        hideInMenu: true
      },
      {
        name: '查询会计准则数据列表',
        path: '/DataDictionary/listKuaiJiZhunZe',
        hideInMenu: true
      }
    ]
  },
  {
    name: '财务设置',
    icon: 'AccountBookOutlined',
    path: '/FinanceSetting',
    routes: [
      {
        name: '账套',
        path: '/FinanceSetting/AccountList',
        component: './Account/AccountList'
      },
      {
        name: '创建账套',
        path: '/FinanceSetting/AccountList/AccountCreate',
        component: './Account/AccountCreate'
      },
      {
        name: '编辑账套',
        path: '/FinanceSetting/AccountList/AccountUpdate',
        component: './Account/AccountUpdate',
        hideInMenu: true
      },
      {
        name: '查看账套明细',
        path: '/FinanceSetting/AccountList/AccountDetail',
        component: './Account/AccountDetail',
        hideInMenu: true
      },
      {
        name: '科目',
        path: '/FinanceSetting/SubjectList',
        component: './Subject/SubjectList'
      },
      {
        name: '科目明细',
        path: '/FinanceSetting/SubjectList/SubjectDetail',
        component: './Subject/SubjectDetail'
      },
      {
        name: '创建科目',
        path: '/FinanceSetting/SubjectList/SubjectCreate',
        component: './Subject/SubjectCreate'
      },
      {
        name: '币别',
        path: '/FinanceSetting/CurrencyList',
        component: './Currency/CurrencyList'
      },
      {
        name: '创建币别',
        path: '/FinanceSetting/CurrencyList/CurrencyCreate',
        component: './Currency/CurrencyCreate'
      },
      {
        name: '币别明细',
        path: '/FinanceSetting/CurrencyList/CurrencyDetail',
        component: './Currency/CurrencyDetail'
      },
      {
        name: '凭证',
        path: '/FinanceSetting/VoucherList',
        component: './Voucher/VoucherList'
      },
      {
        name: '凭证详情',
        path: '/FinanceSetting/VoucherList/VoucherDetail',
        component: './Voucher/VoucherDetail'
      },
      {
        name: '创建凭证',
        path: '/FinanceSetting/VoucherList/VoucherCreate',
        component: './Voucher/VoucherCreate'
      },
      {
        name: '辅助核算',
        path: '/FinanceSetting/AssistCalulateList',
        component: './AssistCalculate/AssistCalulateList'
      },
      {
        name: '创建客户辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateCustomer/Create',
        component: './AssistCalculateCustomer/Create'
      },
      {
        name: '编辑客户辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateCustomer/Detail',
        component: './AssistCalculateCustomer/Detail'
      },
      {
        name: '创建供应商辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateSupplier/Create',
        component: './AssistCalculateSupplier/Create'
      },
      {
        name: '编辑供应商辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateSupplier/Detail',
        component: './AssistCalculateSupplier/Detail'
      },
      {
        name: '创建职员辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateEmployee/Create',
        component: './AssistCalculateEmployee/Create'
      },
      {
        name: '编辑职员辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateEmployee/Detail',
        component: './AssistCalculateEmployee/Detail'
      },
      {
        name: '创建部门辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateDepartment/Create',
        component: './AssistCalculateDepartment/Create'
      },
      {
        name: '编辑部门辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateDepartment/Detail',
        component: './AssistCalculateDepartment/Detail'
      },
      {
        name: '创建项目辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateProject/Create',
        component: './AssistCalculateProject/Create'
      },
      {
        name: '编辑项目辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateProject/Detail',
        component: './AssistCalculateProject/Detail'
      },
      {
        name: '创建存货辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateInventory/Create',
        component: './AssistCalculateInventory/Create'
      },
      {
        name: '编辑存货辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateInventory/Detail',
        component: './AssistCalculateInventory/Detail'
      },
      {
        name: '创建辅助核算类别',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateCate/Create',
        component: './AssistCalculateCate/Create'
      },
      {
        name: '编辑辅助核算类别',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateCate/Detail',
        component: './AssistCalculateCate/Detail'
      },
      {
        name: '创建自定义辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateCustom/Create',
        component: './AssistCalculateCustom/Create'
      },
      {
        name: '编辑自定义辅助核算',
        path: '/FinanceSetting/AssistCalulateList/AssistCalculateCustom/Detail',
        component: './AssistCalculateCustom/Detail'
      },
      {
        name: '凭证字',
        path: '/FinanceSetting/VoucherWordConfig',
        component: './VoucherWordConfig/List'
      },
      {
        name: '凭证字详情',
        path: '/FinanceSetting/VoucherWordConfig/Detail',
        component: './VoucherWordConfig/Detail'
      },
      {
        name: '创建凭证字',
        path: '/FinanceSetting/VoucherWordConfig/Create',
        component: './VoucherWordConfig/Create'
      }
    ]
  },
  {
    name: '运营',
    icon: 'AccountBookOutlined',
    path: '/Operate',
    routes: [
      {
        name: '租户',
        path: '/Operate/TenantList',
        component: './Tenant/List'
      },
      {
        name: '用户',
        path: '/Operate/MemberManageList',
        component: './MemberManage/List'
      }
    ]
  },
  {
    name: '系统设置',
    icon: 'AccountBookOutlined',
    path: '/System',
    routes: [
      {
        name: '个人',
        path: '/System/PersonalInfo',
        component: './System/PersonalInfo'
      },
      {
        name: '菜单管理',
        path: '/System/Menu',
        component: './System/Menu'
      },
      {
        name: '角色管理',
        path: '/System/RoleList',
        component: './System/RoleList'
      },
      {
        name: '资源管理',
        path: '/System/Resource',
        component: './System/Resource'
      }
    ]
  },
  {
    name: '报表',
    icon: 'AccountBookOutlined',
    path: '/Report',
    routes: [
      {
        name: '资产负债表',
        path: '/Report/BalanceSheet',
        component: './Report/BalanceSheet'
      },
      {
        name: '利润表',
        path: '/Report/IncomeStatement',
        component: './Report/IncomeStatement'
      },
      {
        name: '现金流表',
        path: '/Report/CashFlowStatement',
        component: './Report/CashFlowStatement'
      },
      {
        name: '打印资产负债表',
        path: '/Report/BalanceSheetPrint',
        component: './Report/BalanceSheetPrint',
      },
      {
        name: '打印利润表',
        path: '/Report/IncomeStatementPrint',
        component: './Report/IncomeStatementPrint',
      },
      {
        name: '打印现金流表',
        path: '/Report/CashFlowStatementPrint',
        component: './Report/CashFlowStatementPrint',
      },
    ]
  },
  {
    name: '打印报表',
    icon: 'AccountBookOutlined',
    path: '/Report',
    layout: false,
    routes: [
      {
        name: '打印资产负债表',
        path: '/Report/BalanceSheetPrint',
        component: './Report/BalanceSheetPrint',
      },
      {
        name: '打印利润表',
        path: '/Report/IncomeStatementPrint',
        component: './Report/IncomeStatementPrint',
      },
      {
        name: '打印现金流表',
        path: '/Report/CashFlowStatementPrint',
        component: './Report/CashFlowStatementPrint',
      },
    ]
  },
  {
    name: '打印凭证',
    icon: 'AccountBookOutlined',
    path: '/Voucher',
    layout: false,
    routes: [
      {
        name: '打印凭证',
        icon: 'FileTextOutlined',
        path: '/Voucher/VoucherPrint',
        component: './Voucher/VoucherPrint',
      },
    ]
  },
];
