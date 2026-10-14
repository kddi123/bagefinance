import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import { useIntl, history, useModel } from '@umijs/max';
import React, { useEffect, useState, useRef } from 'react';
import { message } from 'antd';
import { getClientToken } from '@/services/ant-design-pro/login';
import { response } from 'express';
import { flushSync } from 'react-dom';
import { AlertOutlined } from '@ant-design/icons';

const Footer: React.FC = () => {
  const { initialState, setInitialState } = useModel('@@initialState');

  const [socket, setSocket] = useState<WebSocket | null>(null);
  const [iconColor, setIconColor] = useState('inherit'); // State for icon color

  const intl = useIntl();
  const defaultMessage = intl.formatMessage({
    id: 'app.copyright.produced',
    defaultMessage: '麦云财税出品',
  });

  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();
    if (userInfo) {
      flushSync(() => {
        setInitialState((s) => ({
          ...s,
          currentUser: userInfo,
        }));
      });
    }
  };

  const currentYear = new Date().getFullYear();
  const socketRef = useRef<WebSocket | null>(null);
  const heartbeatTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const retryTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  const sendHeartbeat = () => {
    if (heartbeatTimeoutRef.current) {
      clearTimeout(heartbeatTimeoutRef.current);
    }

    const clientId = localStorage.getItem("clientId");
    const token = localStorage.getItem("token");
    if (clientId && socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify({
        clientId: clientId,
        cmd: 0,
        token: token ? token : null,
        clientName: navigator.userAgent
      }));
    }

    heartbeatTimeoutRef.current = setTimeout(sendHeartbeat, 3000);
  };


  let autoChangeInterval: string | number | NodeJS.Timeout | undefined;  //自动切换定时器

  const autoGetClientToken = () => {  //自动轮播函数
    clearTimeout(autoChangeInterval);
    autoChangeInterval = setTimeout(() => {
      const clientId = localStorage.getItem("clientId");
      getClientToken({ clientId: clientId }).then(response => {
        if (response.data && response.data) {
          loginSuccess(response.data);
        }
        else {
          autoGetClientToken();
        }
      });
    }, 5000)
  };

  const loginSuccess = async (data: any) => {
    clearTimeout(autoChangeInterval);

    localStorage.setItem('token', data.token);
    const urlParams = new URL(window.location.href).searchParams;
    message.success('登录成功！');
    await fetchUserInfo();
    history.push(urlParams.get('redirect') || '/');
    return;

  }

  const connectServer = () => {
    if (socketRef.current && (socketRef.current.readyState === WebSocket.OPEN || socketRef.current.readyState === WebSocket.CONNECTING)) {
      return;
    }

    const ws = new WebSocket('ws://localhost:8098/ws');

    ws.onopen = () => {
      // message.success('WebSocket connection established');
      socketRef.current = ws;
      setIconColor('green'); // Set icon color to green on connection success
      clearTimeout(autoChangeInterval);

      sendHeartbeat();
    };

    ws.onmessage = (event) => {
      //ok表示心跳
      if (event.data === "ok") {
        return;
      }
      if (event.data === "close") {
        message.error("连接超时，等待重新连接...")
        return;
      }
      const data = JSON.parse(event.data);
      //扫码登录
      if (data.cmd === 'SCAN_LOGIN') {
        loginSuccess(data.msg);
      }
      else {
        message.success(event.data)
      }
    };

    ws.onclose = () => {
      // message.info('WebSocket connection closed');
      setIconColor('inherit'); // Reset icon color on error
      if (heartbeatTimeoutRef.current) {
        clearTimeout(heartbeatTimeoutRef.current);
      }
      retryTimeoutRef.current = setTimeout(connectServer, 5000);
    };

    ws.onerror = (error) => {
      // message.error('WebSocket error: ' + error.message);
      setIconColor('inherit'); // Reset icon color on error
      if (heartbeatTimeoutRef.current) {
        clearTimeout(heartbeatTimeoutRef.current);
      }
    };

    setSocket(ws);
  };

  useEffect(() => {
    connectServer();
    autoGetClientToken();

    return () => {
      // if (socketRef.current) {
      //   socketRef.current.close();
      // }
      if (heartbeatTimeoutRef.current) {
        clearTimeout(heartbeatTimeoutRef.current);
      }
      if (retryTimeoutRef.current) {
        clearTimeout(retryTimeoutRef.current);
      }
    };
  }, []);
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={
        <span>
          {`${currentYear} ${defaultMessage} `}
          <AlertOutlined style={{ color: iconColor, fontSize: '20px', fontWeight: 'bold' }} />
        </span>
      }
      links={[
        {
          key: 'Ant Design Pro',
          title: 'Ant Design Pro',
          href: 'https://pro.ant.design',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/ant-design/ant-design-pro',
          blankTarget: true,
        },
        {
          key: 'Ant Design',
          title: 'Ant Design',
          href: 'https://ant.design',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
