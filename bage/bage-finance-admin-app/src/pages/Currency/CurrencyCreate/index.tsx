import { PageContainer, ProForm, ProFormText } from '@ant-design/pro-components';
import { Card, Form, message } from 'antd';
import React, { useEffect } from 'react';
import { create } from '@/services/ant-design-pro/currencyConfig';
import { history } from '@umijs/max';

const CurrencyCreate: React.FC = () => {
    const [form] = Form.useForm();
    const onFinish = async (values: any) => {
        // console.log(values)
        let params = {
            ...values
        };
        console.log(params);
        await create(params).then(response => {
            if (response.data) {
                message.success("创建成功");
                history.push("/FinanceSetting/CurrencyList");
            }
            else {
                message.error("创建失败");
            }
        }).catch(() => {
            //console.log(error);
        });

    };

    // 在组件加载时调用接口
    useEffect(() => {

    }, []);
    return (
        <PageContainer>
            <Card>
                <ProForm
                    form={form}
                    onFinish={onFinish}
                    layout="horizontal"
                >

                    <ProFormText
                        width="md"
                        name="code"
                        label="币别"
                        tooltip="最长为 16 位"
                        placeholder="请输入币别"
                        rules={[{ required: true }]}
                    />
                    <ProFormText
                        width="md"
                        name="name"
                        label="名称"
                        tooltip="最长为 50 位"
                        placeholder="请输入币别名称"
                        rules={[{ required: true, max: 50 }]}
                    />
                    <ProFormText
                        width="md"
                        name="exchangeRate"
                        label="汇率"
                        tooltip="最长为 50 位"
                        placeholder="请输入汇率"
                        rules={[{ required: true }]}
                    />

                </ProForm>
            </Card>
        </PageContainer >
    );
};

export default CurrencyCreate;