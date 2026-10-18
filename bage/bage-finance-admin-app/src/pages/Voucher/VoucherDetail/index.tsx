import { PageContainer, ProForm, ProFormText } from '@ant-design/pro-components';
import { Card, Form, message } from 'antd';
import React, { useEffect } from 'react';
import { update, get } from '@/services/ant-design-pro/voucherWordConfig';
import { history } from '@umijs/max';

const VoucherDetail: React.FC = () => {
    const [form] = Form.useForm();
    const urlParams = new URL(window.location.href).searchParams;
    const urlParamMap: Record<string, string> = {};
    for (const [key, value] of urlParams.entries()) {
        urlParamMap[key] = value;
    }

    const onFinish = async (values: any) => {
        // console.log(values)
        let params = {
            ...values,
            id: urlParamMap["id"],
        };
        console.log(params);
        await update(params).then(response => {
            if (response.data) {
                message.success("修改成功");
                history.push("/FinanceSetting/VoucherList");
            }
            else {
                message.error("修改失败");
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
                    request={async (_params = {}) => {
                        return await get({ id: urlParamMap["id"] }).then(response => {
                            return {
                                ...response.data,
                            }
                        });
                    }}
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

export default VoucherDetail;
