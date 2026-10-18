import { PageContainer, ProForm, ProFormText } from '@ant-design/pro-components';
import { Card, Form, message } from 'antd';
import React, { useEffect } from 'react';
import { create } from '@/services/ant-design-pro/voucherWordConfig';
import { history } from '@umijs/max';

const Create: React.FC = () => {
    const [form] = Form.useForm();

    const onFinish = async (values: any) => {
        // console.log(values)
        let params = {
            ...values,
        };
        console.log(params);
        await create(params).then(response => {
            if (response.data) {
                message.success("创建成功");
                history.push("/FinanceSetting/VoucherWordConfig");
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
                        name="voucherWord"
                        label="凭证字"
                        tooltip="最长为 16 位"
                        placeholder="请输入币别"
                        rules={[{ required: true }]}
                    />
                    <ProFormText
                        width="md"
                        name="printTitle"
                        label="显示标题"
                        tooltip="最长为 50 位"
                        placeholder="请输入币别名称"
                        rules={[{ required: true, max: 50 }]}
                    />

                </ProForm>
            </Card>
        </PageContainer >
    );
};

export default Create;
