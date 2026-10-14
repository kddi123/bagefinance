import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProFormCheckbox, ProFormDatePicker } from '@ant-design/pro-components';
import { Card, Form, Row, Col, Button, Input, Select, Radio, Switch, DatePicker, message, Checkbox, Cascader } from 'antd';
import React, { useState, useEffect } from 'react';
import { createProject, updateProject } from '@/services/ant-design-pro/assistCalculateSummary';
import { history } from 'umi';

const AssistCalculateProjectCreate: React.FC<any> = (props) => {
    const [form] = Form.useForm();
    const { p } = props;

    const onFinish = async (values: any) => {
        let params = {
            ...values,
            disable: !form.getFieldValue("disable"),
            assistCalculateCateId: p.cateId
        };
        console.log(params);
        await createProject(params).then(response => {
            if (response.data) {
                message.success("创建成功");
                props.updateIsModalOpenState(false);
            }
            else {
                message.error("创建失败");
            }
        }).catch(error => {
            //console.log(error);
        });

    };

    // 在组件加载时调用接口
    useEffect(() => {

    }, []);
    return (
        <ProForm
            form={form}
            onFinish={onFinish}
            layout="vertical"
        // request={async (params = {}) => {
        // }}
        >
            <ProForm.Group>
                <ProFormText
                    width="md"
                    name="code"
                    label="项目编码"
                    placeholder="请输入项目编码"
                    rules={[{ required: true }]}
                />
                <ProFormText
                    width="md"
                    name="name"
                    label="项目名称"
                    placeholder="请输入项目名称"
                    rules={[{ required: true, max: 50 }]}
                />

            </ProForm.Group>
            <ProForm.Group>
                <ProFormText
                    width="md"
                    name="responsibleDepartment"
                    label="负责部门"
                    placeholder="请输入负责部门"
                    rules={[{ max: 50 }]}
                />

                <ProFormText
                    width="md"
                    name="responsiblePerson"
                    label="责任人"
                    placeholder="请输入责任人"
                    rules={[{ max: 50 }]}
                />

            </ProForm.Group>
            <ProForm.Group>
                <ProFormText
                    width="md"
                    name="phone"
                    label="手机"
                    placeholder="请输入手机"
                    rules={[{ max: 50 }, {
                        pattern: /^1\d{10}$/,
                        message: '手机号格式错误！',
                    }]}
                />
                <ProFormDatePicker
                    width="md"
                    name="startDate"
                    label="开始日期"
                    placeholder="请输入开始日期"
                />
            </ProForm.Group>
            <ProForm.Group>
                <ProFormDatePicker
                    width="md"
                    name="endDate"
                    label="结束日期"
                    placeholder="请输入结束日期"
                />
            </ProForm.Group>

            <ProFormText
                width="md"
                name="notes"
                label="备注"
                placeholder="请输入备注"
                rules={[{ max: 500 }]}
                fieldProps={{ style: { width: '61%' } }}
            />

            <ProForm.Group>
                <ProFormSwitch width="md" name="disable" label="是否启用" rules={[{ required: true }]} checkedChildren="启用" unCheckedChildren="停用" initialValue={true} />
            </ProForm.Group>
        </ProForm>
    );
};

export default AssistCalculateProjectCreate;