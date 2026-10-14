import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProFormCaptcha } from '@ant-design/pro-components';
import { Card, Form, Row, Col, Button, Input, Select, Radio, Switch, DatePicker, message, Tabs, TabsProps, Image, Space, Alert } from 'antd';
import React, { useState, useEffect, useCallback } from 'react';
import { updateEmailAndName, updatePassword, generateBase64Code, sendSmsCode, updatePhone } from '@/services/ant-design-pro/system';
import {
    BarcodeOutlined,
    LockOutlined,
    MailFilled,
    MobileFilled,
    SettingOutlined,
    UserOutlined
} from '@ant-design/icons';
import { FormattedMessage, history, SelectLang, useIntl, useModel } from '@umijs/max';
import { flushSync } from 'react-dom';
import { ResponseStructure } from '@/requestErrorConfig';
import { currentUser } from '@/services/ant-design-pro/api';
const { TabPane } = Tabs;


const PersonalInfo: React.FC = () => {
    const [form] = Form.useForm();
    const [base64Code, setBase64Code] = useState<API.GenerateBase64Code>({ data: "iVBORw0KGgoAAAANSUhEUgAAAA0AAAALCAIAAAAr0JA2AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAUSURBVChTY/hPHBhVhx0MjLr//wFSjatj+k9ANwAAAABJRU5ErkJggg==" });
    const urlParams = new URL(window.location.href).searchParams;
    const urlParamMap = {};
    for (const [key, value] of urlParams.entries()) {
        urlParamMap[key] = value;
    }
    const onFinish1 = async (values: any) => {
        await updateEmailAndName(values).then(response => {
            if (response.data) {
                message.success("保存成功");
            }
            else {
                message.error("保存失败");
            }
        }).catch(error => {
            console.log(error);
        });

    };
    const onFinish2 = async (values: any) => {
        await updatePassword(values).then(response => {
            if (response.data) {
                message.success("保存成功");
            }
            else {
                message.error("保存失败");
            }
        }).catch(error => {
            console.log(error);
        });

    };
    const onFinish3 = async (values: any) => {
        await updatePhone(values).then(response => {
            if (response.data) {
                message.success("保存成功");
            }
            else {
                message.error("保存失败");
            }
        }).catch(error => {
            console.log(error);
        });

    };

    const init = async () => {
        await getBase64CodeMethod();
    }
    const getBase64CodeMethod = async () => {
        const result = await generateBase64Code();
        setBase64Code(result);
        console.log(result);
    }

    const clickGetBase64CodeMethod = async () => {
        await getBase64CodeMethod();
    };
    const getFakeCaptchaMethod = async () => {
        await form.validateFields(['phone', 'code']).then(async (values) => {
            let phone = form.getFieldValue('phone');
            let code = form.getFieldValue('code');
            try {
                await sendSmsCode({
                    phone,
                    code,
                    smsCodeType: 'UPDATE_PHONE'
                });
            }
            catch (error: any) {
                console.log(error);
                throw new Error(error);
            }
            finally {
                clickGetBase64CodeMethod();
            }
        });
    }

    const CodeImg = (
        <img
            src={"data:image/jpg;base64," + base64Code.data}
            onClick={clickGetBase64CodeMethod}
            title='点击刷新'
            style={{ cursor: "pointer", height: 40, width: 100 }}
        />
    )



    //init();
    useEffect(() => {
        init()
    }, [])
    // useEffect(() => {
    //     if (!customValidate.validateStatus) {
    //         form.validateFields(customValidate.validateFields);
    //     }
    // }, [customValidate])
    return (
        <PageContainer>
            <Card>
                <Tabs defaultActiveKey="1">
                    <TabPane
                        tab={
                            <span>
                                <UserOutlined />
                                个人资料
                            </span>
                        }
                        key="1"
                    >
                        <ProForm
                            layout='vertical' // 设置为垂直布局
                            onFinish={onFinish1}
                            labelCol={{ span: 2 }}   // 控制 label 的布局，可以调整 span 的值
                            wrapperCol={{ span: 30 }} // 控制包裹内容的布局，可以调整 span 的值
                            request={async (params = {}) => {
                                console.log(urlParamMap)
                                return await currentUser().then(response => {
                                    return {
                                        ...response.data
                                    }
                                });
                            }}
                        >
                            <ProFormText
                                width="md"
                                name="email"
                                label="邮箱"
                                placeholder="请输入邮箱"
                                fieldProps={{
                                    // size: 'large',
                                    prefix: <MailFilled />,
                                    autoComplete: "off"
                                }}
                                rules={[{ required: true },
                                { type: 'email', message: '请输入有效的邮箱地址' }]}
                            />
                            <ProFormText
                                width="md"
                                name="name"
                                label="姓名"
                                placeholder="请输入姓名"
                                fieldProps={{
                                    // size: 'large',
                                    prefix: <UserOutlined />,
                                    autoComplete: "off"
                                }}
                                rules={[{ required: true },
                                { max: 50, message: '姓名最大长度为50' }]}
                            />

                        </ProForm>
                    </TabPane>
                    <TabPane
                        tab={
                            <span>
                                <LockOutlined />
                                修改密码
                            </span>
                        }
                        key="2"
                    >
                        <ProForm
                            layout='vertical' // 设置为垂直布局
                            form={form}
                            onFinish={onFinish2}
                            labelCol={{ span: 3 }}   // 控制 label 的布局，可以调整 span 的值
                            wrapperCol={{ span: 8 }} // 控制包裹内容的布局，可以调整 span 的值
                        >
                            <ProFormText.Password
                                name="oldPassword"
                                label="旧密码"
                                placeholder="请输入旧密码"
                                fieldProps={{
                                    // size: 'large',
                                    prefix: <LockOutlined />,
                                    autoComplete: "off"
                                }}
                                rules={[{ required: true }, {
                                    pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,18}$/,
                                    message: '密码长度需在6~18位字符，且必须包含字母和数字！',
                                }]}
                            />
                            <ProFormText.Password
                                name="password"
                                label="新密码"
                                placeholder="请输入新密码"
                                fieldProps={{
                                    // size: 'large',
                                    prefix: <LockOutlined />,
                                    autoComplete: "off"
                                }}
                                rules={[{ required: true }, {
                                    pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,18}$/,
                                    message: '密码长度需在6~18位字符，且必须包含字母和数字！',
                                }]}
                            />
                            <ProFormText.Password
                                name="confirmPassword"
                                dependencies={['password']}
                                label="确认新密码"
                                placeholder="请再次输入新密码"
                                fieldProps={{
                                    // size: 'large',
                                    prefix: <LockOutlined />,
                                    autoComplete: "off"
                                }}
                                rules={[{ required: true },
                                ({ getFieldValue }) => ({
                                    validator(_, value) {
                                        if (!value || getFieldValue('password') === value) {
                                            return Promise.resolve();
                                        }
                                        return Promise.reject(new Error('两次输入的密码不一致!'));
                                    },
                                }),]}
                            />
                        </ProForm>
                    </TabPane>
                    <TabPane tab={
                        <span>
                            <MobileFilled />
                            修改手机
                        </span>
                    }
                        key="3">
                        <ProForm
                            layout='vertical' // 设置为垂直布局
                            form={form}
                            onFinish={onFinish3}
                            labelCol={{ span: 3 }}   // 控制 label 的布局，可以调整 span 的值
                            wrapperCol={{ span: 7 }} // 控制包裹内容的布局，可以调整 span 的值
                        >
                            <ProFormText
                                // width="md"
                                name="phone"
                                label="手机号"
                                placeholder="请输入要绑定的手机号"
                                fieldProps={{
                                    // size: 'large',
                                    prefix: <MobileFilled />,
                                    autoComplete: "off"
                                }}
                                rules={[
                                    {
                                        required: true,
                                        message: '请输入手机号！',
                                    },
                                    {
                                        pattern: /^1\d{10}$/,
                                        message: '手机号格式错误！',
                                    },]}
                            />
                            <ProFormText
                                label="验证码"
                                // width="md"
                                addonAfter={CodeImg}
                                name="code"
                                fieldProps={{
                                    // size: 'large',
                                    prefix: <BarcodeOutlined />,
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
                                    }
                                ]}
                            />

                            <ProFormCaptcha
                                label="短信验证码"
                                fieldProps={{
                                    prefix: <LockOutlined />,
                                    autoComplete: "off"
                                }}
                                captchaProps={{
                                    // size: 'large',
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
                        </ProForm>

                    </TabPane>
                </Tabs>
            </Card>
        </PageContainer >
    );
};

export default PersonalInfo;