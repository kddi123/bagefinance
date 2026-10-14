import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProFormDatePicker } from '@ant-design/pro-components';
import { Card, Form, Row, Col, Button, Input, Select, Radio, Switch, DatePicker, message } from 'antd';
import React from 'react';
import { addAccountBook, listHangYe, listKuaiJiZhunZe } from '@/services/ant-design-pro/accountBook';
import { history } from 'umi';

const AccountCreate: React.FC = () => {
    const [form] = Form.useForm();
    const onFinish = async (values: any) => {
        await addAccountBook({ ...values, startTime: values.startTime + " 00:00:00" }).then(response => {
            if (response.data) {
                message.success("创建成功");
                history.push("/Account/AccountList");
            }
            else {
                message.error("创建失败");
            }
        }).catch(error => { 
            //console.log(error);
        });

    };
    return (
        <PageContainer>
            <Card>
                <ProForm
                    form={form}
                    onFinish={onFinish}
                >
                    <ProForm.Group>
                        <ProFormText
                            width="md"
                            name="companyName"
                            label="公司名称"
                            tooltip="最长为 24 位"
                            placeholder="请输入公司名称"
                            rules={[{ required: true }]}
                        />
                        <ProFormText
                            width="md"
                            name="unifiedSocialCreditCode"
                            label="统一社会信用代码"
                            tooltip="最长为 24 位"
                            placeholder="请输入统一社会信用代码"
                            rules={[{ required: true }]}
                        />

                    </ProForm.Group>
                    <ProForm.Group>
                        <ProFormSelect width="md" name="industryId" label="行业"
                            request={async (params = {}) => {
                                return await listHangYe().then(response => {
                                    return response.data?.map(item => ({
                                        label: item.dataValue,
                                        value: item.dataCode
                                    }));
                                });
                            }}
                        >
                        </ProFormSelect >
                        <ProFormRadio.Group width="md" name="valueAddedTaxCate" label="增值税种类" radioType="radio" rules={[{ required: true, message: "请选择增值税种类" }]}
                            options={[
                                {
                                    label: '小规模纳税人',
                                    value: '1',
                                },
                                {
                                    label: '一般纳税人',
                                    value: '2',
                                }
                            ]}>
                        </ProFormRadio.Group>
                    </ProForm.Group>
                    <ProForm.Group>
                        <ProFormDatePicker
                            width="md"
                            name="startTime"
                            label="账套启用年月"
                            placeholder="请选择账套启用年月"
                            rules={[{ required: true }]}
                            picker="month" />
                        <ProFormSelect
                            width="md"
                            name="accountingStandard"
                            placeholder="请选择会计准则"
                            label="会计准则"
                            rules={[{ required: true }]}
                            request={async (params = {}) => {
                                return await listKuaiJiZhunZe().then(response => {
                                    return response.data?.map(item => ({
                                        label: item.dataValue,
                                        value: item.dataCode
                                    }));
                                });
                            }}>
                        </ProFormSelect>
                    </ProForm.Group>
                    <ProForm.Group>
                        <ProFormSwitch width="lg" name="enableVoucherVerify" label="凭证审核" rules={[{ required: true }]} checkedChildren="审核" unCheckedChildren="不审核" initialValue={true} />
                        <ProFormSwitch width="lg" name="enableFixedAssets" label="固定资产模块" rules={[{ required: true }]} checkedChildren="启用" unCheckedChildren="不启用" initialValue={true} />
                        <ProFormSwitch width="lg" name="enableCapital" label="资金模块" rules={[{ required: true }]} checkedChildren="启用" unCheckedChildren="不启用" initialValue={true} />
                    </ProForm.Group>
                </ProForm>
            </Card>
        </PageContainer >
    );
};

export default AccountCreate;