import Footer from '@/components/Footer';
import { ResponseStructure, ErrorCode } from '@/requestErrorConfig';
import { phonePasswordLogin, phoneSmsCodeLogin } from '@/services/ant-design-pro/api';
import { sendSmsCode, generateMpRegCode, generateBase64Code, getClientId } from '@/services/ant-design-pro/login';

import {
  BarcodeOutlined,
  LockOutlined,
  MobileOutlined,
  UserOutlined,
  WechatOutlined,
} from '@ant-design/icons';
import {
  ProFormCaptcha,
  ProFormCheckbox,
  ProFormText,
} from '@ant-design/pro-components';
import { FormattedMessage, history, useModel, Link } from '@umijs/max';
import { Alert, message, Tabs, Image, Space, Form, Button } from 'antd';
import React, { useState, useEffect } from 'react';
import { flushSync } from 'react-dom';
import styles from './index.less';

const Login: React.FC = () => {
  const [form] = Form.useForm();
  const [type, setType] = useState<string>('account');
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

  const getClientIdMethod = async () => {
    try {
      const clientIdResult = await getClientId();
      if (clientIdResult.data !== undefined) {
        const clientId = clientIdResult.data;
        setLocalClientId(clientId);  // 假设这个函数用于设置本地状态
        localStorage.setItem("clientId", clientId);  // 将 clientId 存储到本地存储
        console.log(clientId);
        return clientId;  // 返回 clientId
      }
    } catch (e) {
      console.error("Error fetching clientId:", e);
      throw e; // 如果有错误，向上层抛出异常
    }
    return undefined;
  };
  const generateMpRegCodeMethod = async (clientId: string) => {
    await generateMpRegCode({ clientId: clientId }).then(response => {
      setGenerateMpRegCodeData(response);
    }).catch(error => {
      console.log(error);
    });
  }
  const getBase64CodeMethod = async (clientId: string) => {
    if (clientId === undefined) {
      return;
    }
    const result = await generateBase64Code({ clientId: clientId });
    setBase64Code(result);
    console.log(result);
  }

  const clickGetBase64CodeMethod = async () => {
    if (localClientId === undefined) {
      return;
    }
    await getBase64CodeMethod(localClientId)
  };

  const handleSubmit = async (values: API.PhonePasswordLoginParams | API.PhoneSmsCodeLoginParams) => {
    let validateFields;
    try {
      let clientId = localStorage.getItem("clientId");
      if (clientId === null) {
        message.error("获取clientId失败");
        return;
      }
      // 登录
      let msg;
      if (type === 'account') {
        let v: API.PhonePasswordLoginParams = values;
        msg = await phonePasswordLogin({ ...v, clientId });
        validateFields = ['phone', 'password', 'code']
      }
      else {
        let v: API.PhoneSmsCodeLoginParams = values;
        msg = await phoneSmsCodeLogin({ ...v, clientId });
        validateFields = ['phone', 'smsCode']
      }
      if (msg.success) {
        message.success('登录成功！');
        localStorage.setItem('token', msg.data?.token || '');
        await fetchUserInfo();
        const urlParams = new URL(window.location.href).searchParams;
        history.push(urlParams.get('redirect') || '/');
        return;
      }

    } catch (error: any) {
      const response = error.info as API.LoginResult;
      if (response && !response.success) {
        setCustomValidate({
          ...customValidate,
          validateStatus: false,
          errors: response.errorMessage as unknown as Record<string, string>,
          code: response.code,
          codeMessage: response.codeMessage,
          formValue: form.getFieldsValue(),
          validateFields: validateFields
        })
      }
    }
    finally {
      clickGetBase64CodeMethod();
    }
  };

  const init = async () => {
    localStorage.removeItem('token');
    localStorage.removeItem('clientId');
    const clientId = await getClientIdMethod();
    if (!clientId) {
      return;
    }
    await generateMpRegCodeMethod(clientId);

    await getBase64CodeMethod(clientId);
  }
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
          smsCodeType: 'LOGIN'
        });
      }
      catch (error: any) {
        fieldCheckStatus = false;
        let response = error.info as ResponseStructure;
        if (!response.success) {
          setCustomValidate({
            ...customValidate,
            validateStatus: false,
            errors: response.errorMessage as unknown as Record<string, string>,
            code: response.code,
            formValue: form.getFieldsValue(),
            validateFields: ['phone', 'code']
          })
        }
      }
      finally {
        clickGetBase64CodeMethod();
      }
    });
    if (!fieldCheckStatus) {
      throw new Error("参数错误");
    }
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
    init();
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

            <Form
              form={form}
              className={styles.login_form}
              initialValues={{
                autoLogin: true,
              }}
            >
              <Space direction="vertical" align="center">
                <Space className={styles.logo}>
                  <img alt="logo" src="/logo.svg" style={{ width: '44px', height: '44px' }} /><span>麦云财税</span>
                </Space>
                <div className={styles.subTitle}>一款免费的财税软件 有10万家企业选择麦云财税
                </div>
              </Space>

              <Tabs
                activeKey={type}
                onChange={setType}
                centered={false}
                items={[
                  {
                    key: 'account',
                    label: (
                      <span>
                        <UserOutlined />
                        密码登录
                      </span>
                    ),
                  },
                  {
                    key: 'phone',
                    label: (
                      <span>
                        <MobileOutlined />
                        短信登录
                      </span>
                    ),
                  },
                ]}
              />
              {/* 
                {userLoginState.code === 202 && type === 'account' && userLoginState.errorMessage && (
                  <LoginMessage
                    content='{userLoginState.errorMessage.entries().next().value[1]}'
                  />
                )} */}

              {type === 'account' && (
                <>
                  <ProFormText
                    name="phone"
                    fieldProps={{
                      size: 'large',
                      prefix: <MobileOutlined className={styles.prefixIcon} />,
                      autoComplete: "off"
                    }}
                    placeholder="请输入手机号"
                    rules={[
                      {
                        required: true,
                        message: '请输入手机号！',
                      },
                      {
                        pattern: /^1\d{10}$/,
                        message: '手机号格式错误！',
                      }
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
                        message: '图形验证码格式不正确',
                      },
                      () => ({
                        validator(_rule, value) {
                          // if (customValidate.code === 100 && customValidate.validateStatus === false && value === customValidate.formValue.code) {
                          //   return Promise.reject(new Error(customValidate.msg));
                          // }
                          console.log(customValidate)
                          if (customValidate.code === ErrorCode.PARAMETER_ERROR && customValidate.errors?.code && value === customValidate.formValue.code) {
                            return Promise.reject(new Error(customValidate.errors?.code));
                          }
                          return Promise.resolve();
                        },
                      }),
                    ]}
                  />
                </>
              )}

              {type === 'phone' && (
                <>
                  <ProFormText
                    fieldProps={{
                      size: 'large',
                      prefix: <MobileOutlined className={styles.prefixIcon} />,
                      autoComplete: "off"
                    }}
                    name="phone"
                    placeholder='请输入手机号'
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
                        validator(_rule, value) {
                          if (customValidate.code === ErrorCode.PARAMETER_ERROR && customValidate.errors?.phone && value === customValidate.formValue.phone) {
                            return Promise.reject(new Error(customValidate.errors?.phone));
                          }
                          return Promise.resolve();
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
                        message: '图形验证码格式不正确',
                      },
                      () => ({
                        validator(_rule, value) {
                          if (customValidate.code === ErrorCode.PARAMETER_ERROR && customValidate.errors?.code && value === customValidate.formValue.code) {
                            return Promise.reject(new Error(customValidate.errors?.code));
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
                      },
                      () => ({
                        validator(_rule, value) {
                          if (customValidate.code === ErrorCode.PARAMETER_ERROR && customValidate.errors?.smsCode && value === customValidate.formValue.smsCode) {
                            return Promise.reject(new Error(customValidate.errors?.smsCode));
                          }
                          return Promise.resolve();
                        },
                      }),
                    ]}
                    onGetCaptcha={getFakeCaptchaMethod}
                  />
                </>
              )}
              <div
                style={{
                  marginBottom: 24,
                }}
              >
                <ProFormCheckbox noStyle name="autoLogin">
                  <FormattedMessage id="pages.login.rememberMe" defaultMessage="自动登录" />
                </ProFormCheckbox>
                {type === 'account' && (
                  <>
                    <a
                      style={{
                        float: 'right',
                      }}
                    >
                      <FormattedMessage id="pages.login.forgotPassword" defaultMessage="忘记密码" />
                    </a>
                  </>
                )}
              </div>
              <Button type="primary" block size='large' onClick={async () => {
                let validateFields;
                if (type === 'account') {
                  validateFields = ['phone', 'password', 'code']
                }
                else {
                  validateFields = ['phone', 'smsCode']
                }

                await form.validateFields(validateFields).then(async (values) => {
                  await handleSubmit(values);
                }).catch((errorInfo) => {
                  console.error(errorInfo);
                });
              }}
              >登录</Button>
            </Form>
            <Link to='/user/register' className={styles.go_to_register}>
              <span>注册</span>
            </Link>
          </Space>
        </Space>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
