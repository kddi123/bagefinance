import { Request, Response } from 'express';

const waitTime = (time: number = 100) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true);
    }, time);
  });
};

async function getFakeCaptcha(req: Request, res: Response) {
  await waitTime(2000);
  return res.json('captcha-xxx');
}

const { ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION } = process.env;

/**
 * 当前用户的权限，如果为空代表没登录
 * current user access， if is '', user need login
 * 如果是 pro 的预览，默认是有权限的
 */
let access = ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION === 'site' ? 'admin' : 'admin'; // 默认已登录

const getAccess = () => {
  return access;
};

// 代码中会兼容本地 service mock 以及部署站点的静态数据
export default {
  // 支持值为 Object 和 Array
  'GET /api/currentUser': (req: Request, res: Response) => {
    if (!getAccess()) {
      res.status(401).send({
        data: {
          isLogin: false,
        },
        errorCode: '401',
        errorMessage: '请先登录！',
        success: true,
      });
      return;
    }
    res.send({
      success: true,
      data: {
        name: 'Serati Ma',
        avatar: 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png',
        userid: '00000001',
        email: 'antdesign@alipay.com',
        signature: '海纳百川，有容乃大',
        title: '交互专家',
        group: '蚂蚁金服－某某某事业群－某某平台部－某某技术部－UED',
        tags: [
          {
            key: '0',
            label: '很有想法的',
          },
          {
            key: '1',
            label: '专注设计',
          },
          {
            key: '2',
            label: '辣~',
          },
          {
            key: '3',
            label: '大长腿',
          },
          {
            key: '4',
            label: '川妹子',
          },
          {
            key: '5',
            label: '海纳百川',
          },
        ],
        notifyCount: 12,
        unreadCount: 11,
        country: 'China',
        access: getAccess(),
        geographic: {
          province: {
            label: '浙江省',
            key: '330000',
          },
          city: {
            label: '杭州市',
            key: '330100',
          },
        },
        address: '西湖区工专路 77 号',
        phone: '0752-268888888',
      },
    });
  },
  // GET POST 可省略
  'GET /api/users': [
    {
      key: '1',
      name: 'John Brown',
      age: 32,
      address: 'New York No. 1 Lake Park',
    },
    {
      key: '2',
      name: 'Jim Green',
      age: 42,
      address: 'London No. 1 Lake Park',
    },
    {
      key: '3',
      name: 'Joe Black',
      age: 32,
      address: 'Sidney No. 1 Lake Park',
    },
  ],
  'GET /api/login/account': (req: Request, res: Response) => {
    const { username, password, type = 'account' } = req.query;

    console.log('[Mock GET /api/login/account]', { username, password, type });

    // 仅用于测试，生产环境不应使用 GET 登录（密码会暴露在 URL 中）
    if (password === 'ant.design' && username === 'admin') {
      res.send({
        success: true,
        status: 'ok',
        type,
        currentAuthority: 'admin',
        token: 'mock-admin-token-' + Date.now(),
        userId: '00000001',
        username: 'admin',
        nickname: 'Admin User',
        roles: ['admin', 'user'],
        permissions: ['*'],
        avatar: 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png',
      });
      access = 'admin';
      return;
    }

    if (password === 'ant.design' && username === 'user') {
      res.send({
        success: true,
        status: 'ok',
        type,
        currentAuthority: 'user',
        token: 'mock-user-token-' + Date.now(),
        userId: '00000002',
        username: 'user',
        nickname: 'Normal User',
        roles: ['user'],
        permissions: ['read', 'write'],
        avatar: 'https://gw.alipayobjects.com/zos/antfincdn/efFD%24IOql2/weixintupian_20170331104822.jpg',
      });
      access = 'user';
      return;
    }

    res.status(401).send({
      success: false,
      errorMessage: '用户名或密码错误（GET 方法仅用于测试，生产环境请使用 POST）',
    });
  },
  'POST /api/login/account': async (req: Request, res: Response) => {
    // 同时支持 Body 和 Query Params（生产环境只用 Body）
    const { password, username, type } = req.body.username ? req.body : req.query;
    console.log('[Mock POST /api/login/account]', { username, password, type, source: req.body.username ? 'body' : 'query' });
    // await waitTime(2000); // 暂时注释以便测试
    if (password === 'ant.design' && username === 'admin') {
      res.status(200).send({
        success: true,
        status: 'ok',
        type,
        currentAuthority: 'admin',
        token: 'mock-admin-token-' + new Date().toISOString(),  // ← Date.now() 就是时间戳数字
        userId: '00000001',
        username: 'admin',
        nickname: 'Admin User',
        roles: ['admin', 'user'],
        permissions: ['*'],
        avatar: 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png',
      });
      access = 'admin';
      return;
    }
    if (password === 'ant.design' && username === 'user') {
      res.send({
        success: true,
        status: 'ok',
        type,
        currentAuthority: 'user',
        token: 'mock-user-token-' + Date.now(),
        userId: '00000002',
        username: 'user',
        nickname: 'Normal User',
        roles: ['user'],
        permissions: ['read', 'write'],
        avatar: 'https://gw.alipayobjects.com/zos/antfincdn/efFD%24IOql2/weixintupian_20170331104822.jpg',
      });
      access = 'user';
      return;
    }
    if (type === 'mobile') {
      res.send({
        success: true,
        status: 'ok',
        type,
        currentAuthority: 'admin',
        token: 'mock-mobile-token-' + Date.now(),
        userId: '00000003',
        username: username,
        nickname: '手机用户',
        roles: ['admin'],
        permissions: ['*'],
        avatar: 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png',
      });
      access = 'admin';
      return;
    }

    res.send({
      status: 'error',
      type,
      currentAuthority: 'guest',
    });
    access = 'guest';
  },
  'POST /api/login/outLogin': (req: Request, res: Response) => {
    access = '';
    res.send({ data: {}, success: true });
  },
  'POST /api/register': (req: Request, res: Response) => {
    res.send({ status: 'ok', currentAuthority: 'user', success: true });
  },
  'GET /api/500': (req: Request, res: Response) => {
    res.status(500).send({
      timestamp: 1513932555104,
      status: 500,
      error: 'error',
      message: 'error',
      path: '/base/category/list',
    });
  },
  'GET /api/404': (req: Request, res: Response) => {
    res.status(404).send({
      timestamp: 1513932643431,
      status: 404,
      error: 'Not Found',
      message: 'No message available',
      path: '/base/category/list/2121212',
    });
  },
  'GET /api/403': (req: Request, res: Response) => {
    res.status(403).send({
      timestamp: 1513932555104,
      status: 403,
      error: 'Forbidden',
      message: 'Forbidden',
      path: '/base/category/list',
    });
  },
  'GET /api/401': (req: Request, res: Response) => {
    res.status(401).send({
      timestamp: 1513932555104,
      status: 401,
      error: 'Unauthorized',
      message: 'Unauthorized',
      path: '/base/category/list',
    });
  },

  'GET  /api/login/captcha': getFakeCaptcha,
};
