import Footer from '@/components/Footer';
import { ResponseStructure } from '@/requestErrorConfig';
import { sendSmsCode, generateMpRegCode, generateBase64Code, getClientId, getClientToken } from '@/services/ant-design-pro/login';
import { phoneReg } from '@/services/ant-design-pro/reg';
import {
  BarcodeOutlined,
  LockOutlined,
  MobileOutlined,
  WechatOutlined,
} from '@ant-design/icons';
import {
  ProFormCaptcha,
  ProFormText,
} from '@ant-design/pro-components';
import { history, useModel, Link } from '@umijs/max';
import { Alert, message, Tabs, Image, Space, Button, Form } from 'antd';
import React, { useState, useEffect } from 'react';
import { flushSync } from 'react-dom';
import styles from './index.less';

const Login: React.FC = () => {
  const [form] = Form.useForm();
  const [type, setType] = useState<string>('phone');
  const { initialState, setInitialState } = useModel('@@initialState');
  const [generateMpRegCodeData, setGenerateMpRegCodeData] = useState<API.GenerateMpRegCode>({});
  const [base64Code, setBase64Code] = useState<API.GenerateBase64Code>({ data: "iVBORw0KGgoAAAANSUhEUgAAAA0AAAALCAIAAAAr0JA2AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAUSURBVChTY/hPHBhVhx0MjLr//wFSjatj+k9ANwAAAABJRU5ErkJggg==" });
  const [localClientId, setLocalClientId] = useState<string>();
  const [customValidate, setCustomValidate] = useState<API.CustomValidate>({ validateStatus: true, code: 0, msg: '', formValue: {}, validateFields: [] });

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

  const handleSubmit = async (values: API.PhoneRegisterRequest) => {
    try {
      let clientId = localStorage.getItem("clientId");
      if (clientId === null) {
        message.error("获取clientId失败");
        return;
      }
      // 注册
      const response = await phoneReg({ ...values, clientId });
      console.log(response);
      if (response.success) {
        const defaultLoginSuccessMessage = '注册成功！';
        message.success(defaultLoginSuccessMessage);
        await fetchUserInfo();
        const urlParams = new URL(window.location.href).searchParams;
        history.push(urlParams.get('redirect') || '/');
        return;
      }

    } catch (error: any) {
      let response = error.info as ResponseStructure;
      if (!response.success) {
        setCustomValidate({
          ...customValidate,
          validateStatus: false,
          msg: response.errorMessage ? [...response.errorMessage.values()][0] : undefined,
          code: response.code,
          formValue: form.getFieldsValue(),
          validateFields: ['phone', 'smsCode']
        })
        throw new Error("参数错误");
      }
    }
  };

  const getClientIdMethod = async () => {
    let clientId = localStorage.getItem("clientId");
    if (clientId === null) {
      let clientIdResult = await getClientId();
      console.log(clientIdResult.data);
      if (clientIdResult.data !== undefined) {
        setLocalClientId(clientIdResult.data);
        clientId = clientIdResult.data;
        localStorage.setItem("clientId", clientIdResult.data);
      }
    }
    else {
      setLocalClientId(clientId);
    }
    return clientId;
  }
  const generateMpRegCodeMethod = async (clientId: string) => {
    await generateMpRegCode({ clientId: clientId })
      .then((response) => {
        setGenerateMpRegCodeData(response);
        console.log(response.data?.qrCodeUrl);
      }).catch((e) => {
        console.error(e);
      });
  }
  const getBase64CodeMethod = async (clientId: string) => {
    if (clientId === undefined) {
      return;
    }
    await generateBase64Code({ clientId: clientId })
      .then((response) => {
        setBase64Code(response);
      }).catch((e) => {
        console.error(e);
      });
  }
  const getClientTokenMethod = async (clientId: string) => {
    console.log("获取token...")
    const result = await getClientToken({ clientId: clientId });
    console.log("result:" + JSON.stringify(result));
    if (result.data !== undefined && result.data !== null) {
      localStorage.setItem("token", JSON.stringify(result));
    }
  }

  let autoChangeInterval: ReturnType<typeof setTimeout> | undefined;  //自动切换定时器

  const autoGetClientToken = (clientId: string) => {  //自动轮播函数
    clearTimeout(autoChangeInterval);
    autoChangeInterval = setTimeout(() => {
      getClientTokenMethod(clientId);
      autoGetClientToken(clientId);
    }, 30000)
  };

  const init = async () => {
    const clientId = await getClientIdMethod();
    if (!clientId) {
      return;
    }
    await generateMpRegCodeMethod(clientId);
    await getBase64CodeMethod(clientId);
    autoGetClientToken(clientId);
  }

  const clickGetBase64CodeMethod = async () => {
    if (localClientId === undefined) {
      return;
    }
    await getBase64CodeMethod(localClientId)
  };
  const getFakeCaptchaMethod = async () => {
    if (localClientId === undefined) {
      message.error("clientId不能为空");
      throw new Error("参数错误");
    }
    let fieldCheckStatus = true;
    await form.validateFields(['phone', 'code']).then(async () => {
      fieldCheckStatus = true;
      let phone = form.getFieldValue('phone');
      let code = form.getFieldValue('code');
      console.log(form.getFieldsValue());
      try {
        await sendSmsCode({
          clientId: localClientId,
          phone,
          code,
          smsCodeType: 'REG'
        });
      }
      catch (error: any) {
        console.error(error);
        fieldCheckStatus = false;
        // let response = error.info as ResponseStructure;
        // if (!response.success) {
        //   setCustomValidate({
        //     ...customValidate,
        //     validateStatus: false,
        //     errors: response.errorMessage,
        //     code: response.code,
        //     formValue: form.getFieldsValue(),
        //     validateFields: ['phone', 'code']
        //   })
        // }
      }
      finally {
        clickGetBase64CodeMethod();
      }
    });
    if (!fieldCheckStatus) {
      throw new Error("参数错误");
    }
  }

  const formButtonClick = async () => {
    await form.validateFields(['phone', 'password', 'confirmPassword', 'smsCode']).then(async (values) => {
      await handleSubmit(values as API.PhoneRegisterRequest);
    }).catch(() => {
    });

  }

  const CodeImg = (
    <img
      src={"data:image/jpg;base64," + base64Code.data}
      onClick={clickGetBase64CodeMethod}
      title='点击刷新'
      style={{ cursor: "pointer", height: 40, width: 90 }}
    />
  )



  //init();
  useEffect(() => {
    init()
  }, [])
  useEffect(() => {
    if (!customValidate.validateStatus) {
      form.validateFields(customValidate.validateFields);
    }
  }, [customValidate])
  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <Space>
          <Image
            preview={false}
            src="https://img.alicdn.com/imgextra/i2/O1CN01vP61xq1m3n77lRGgR_!!6000000004899-2-tps-780-1036.png"
          />
          <Space className={styles.login_content}>
            <Space align="center" className={styles.mp_code}>
              <Space direction="vertical" align="center">
                <span className={styles.mp_code_title}>微信扫码登录</span>
                <Image
                  preview={false}
                  height={210}
                  width={200}
                  className={styles.mp_code_img}
                  src={generateMpRegCodeData.data?.qrCodeUrl}
                />

                <Alert
                  // message={(<span>微信扫码<span>关注公众号</span></span>)}
                  description={(<div><span>微信扫码<span className={styles.mp_tips}>关注公众号</span></span><br />登录更快更安全</div>)}
                  // type="success"
                  showIcon={true}
                  className={styles.alert}
                  icon={(<WechatOutlined />)}
                />
              </Space>
            </Space>

            <Form form={form}
              className={styles.login_form}
            >

              <Tabs
                activeKey={type}
                onChange={setType}
                centered={false}
                items={[
                  {
                    key: 'phone',
                    label: (
                      <span>
                        <MobileOutlined />
                        手机号注册
                      </span>
                    ),
                  },
                ]}
              />

              <>
                <ProFormText
                  fieldProps={{
                    size: 'large',
                    prefix: <MobileOutlined className={styles.prefixIcon} />,
                    autoComplete: 'off'
                  }}
                  name="phone"
                  placeholder='请输入手机号！'
                  rules={[
                    {
                      required: true,
                      message: '请输入手机号！',
                    },
                    {
                      pattern: /^1\d{10}$/,
                      message: '手机号格式错误！',
                    },
                    () => ({
                      validator(_, value) {
                        console.log("phoneValid", customValidate.code === 100002, customValidate.validateStatus === false, value === customValidate.formValue.phone)
                        if (customValidate.code === 100002 && customValidate.validateStatus === false && value === customValidate.formValue.phone) {
                          return Promise.reject(new Error(customValidate.msg));
                        }
                        return Promise.resolve();
                      },
                    }),
                  ]}
                />

                <ProFormText.Password
                  name="password"
                  fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined className={styles.prefixIcon} />,
                  }}
                  placeholder="请输入密码"
                  rules={[
                    {
                      required: true,
                      message: '请输入密码！',
                    },
                    {
                      pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,18}$/,
                      message: '密码长度需在6~18位字符，且必须包含字母和数字！'
                    }
                  ]}
                />

                <ProFormText.Password
                  name="confirmPassword"
                  dependencies={['password']}
                  fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined className={styles.prefixIcon} />,
                  }}
                  placeholder="请再次确认密码"
                  rules={[
                    {
                      required: true,
                      message: '请再次确认密码！',
                    },
                    ({ getFieldValue }) => ({
                      validator(_, value) {
                        if (!value || getFieldValue('password') === value) {
                          return Promise.resolve();
                        }
                        return Promise.reject(new Error('两次输入的密码不一致!'));
                      },
                    }),
                  ]}
                />

                <ProFormText
                  addonAfter={CodeImg}
                  name="code"
                  fieldProps={{
                    size: 'large',
                    prefix: <BarcodeOutlined className={styles.prefixIcon} />,
                    autoComplete: "off"
                  }}
                  placeholder="请输入图形验证码"
                  rules={[
                    {
                      required: true,
                      message: '请输入图形验证码！',
                    },
                    {
                      pattern: /^[a-zA-Z0-9]{5}$/,
                      message: '图形验证码格式不正确!',
                    },
                    () => ({
                      validator(_, value) {
                        // if (!value || value.count == 0) {
                        //   //setCustomValidate({validateStatus:''});
                        //   return Promise.reject(new Error("请输入图形验证码！"));
                        // }
                        // if (!/^[a-zA-Z0-9]{5}$/.test(value)) {
                        //   //setCustomValidate({validateStatus:''});
                        //   return Promise.reject(new Error("图形验证码格式不正确"));
                        // }
                        if (customValidate.code === 100001 && customValidate.validateStatus === false && value === customValidate.formValue.code) {
                          return Promise.reject(new Error(customValidate.msg));
                        }
                        return Promise.resolve();
                      },
                    }),
                  ]}
                />

                <ProFormCaptcha
                  fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined className={styles.prefixIcon} />,
                    autoComplete: "off"
                  }}
                  captchaProps={{
                    size: 'large',
                  }}
                  placeholder='请输入短信验证码'
                  captchaTextRender={(timing, count) => {
                    if (timing) {
                      return `${count}后可重新获取`;
                    }
                    return '获取短信验证码';
                  }}
                  name="smsCode"
                  rules={[
                    {
                      required: true,
                      message: '请输入短信验证码！',
                    },
                    {
                      pattern: /^[0-9]{6}$/,
                      message: '短信验证码格式不正确',
                    }
                  ]}
                  onGetCaptcha={getFakeCaptchaMethod}
                />
              </>
              <Button type="primary" block size='large' onClick={formButtonClick}
              >注册</Button>
            </Form>
            <Link to='/user/login' className={styles.go_to_register}>
              <span>登录</span>
            </Link>
          </Space>
        </Space>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
