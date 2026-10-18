import { ProForm, ProFormText, ProFormSelect, ProFormSwitch, ProFormDatePicker } from '@ant-design/pro-components';
import { Form, message } from 'antd';
import React, { useEffect } from 'react';
import { createEmployee } from '@/services/ant-design-pro/assistCalculateSummary';

const AssistCalculateEmployeeCreate: React.FC<any> = (props) => {
    const [form] = Form.useForm();
    const { p } = props;

    const onFinish = async (values: any) => {
        let params = {
            ...values,
            disable: !form.getFieldValue("disable"),
            assistCalculateCateId: p.cateId
        };
        console.log(params);
        await createEmployee(params).then(response => {
            if (response.data) {
                message.success("创建成功");
                props.updateIsModalOpenState(false);
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
                    label="职员编码"
                    placeholder="请输入职员编码"
                    rules={[{ required: true }]}
                />
                <ProFormText
                    width="md"
                    name="name"
                    label="职员名称"
                    placeholder="请输入职员名称"
                    rules={[{ required: true, max: 50 }]}
                />

            </ProForm.Group>
            <ProForm.Group>
                <ProFormSelect
                    width="md"
                    name="sex"
                    label="性别"
                    placeholder="选择性别"
                    // onChange={handleGenderChange}
                    options={[
                        { label: '男性', value: 1 },
                        { label: '女性', value: 2 },
                        { label: '其他', value: 0 },
                    ]}
                    valueEnum={{
                        1: '男性',
                        2: '女性',
                        0: '其他',
                    }}
                >
                </ProFormSelect>

                <ProFormText
                    width="md"
                    name="departmentCode"
                    label="部门编码"
                    placeholder="请输入部门编码"
                    rules={[{ max: 50 }]}
                />

            </ProForm.Group>
            <ProForm.Group>
                <ProFormText
                    width="md"
                    name="departmentName"
                    label="部门名称"
                    placeholder="请输入部门名称"
                    rules={[{ max: 50 }]}
                />

                <ProFormText
                    width="md"
                    name="position"
                    label="职务"
                    placeholder="请输入职务"
                    rules={[{ max: 50 }]}
                />

            </ProForm.Group>
            <ProForm.Group>
                <ProFormText
                    width="md"
                    name="job"
                    label="岗位"
                    placeholder="请输入岗位"
                    rules={[{ max: 50 }]}
                />
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
            </ProForm.Group>
            <ProForm.Group>
                <ProFormDatePicker
                    width="md"
                    name="birthday"
                    label="出生日期"
                    placeholder="请输入出生日期"
                />
                <ProFormDatePicker
                    width="md"
                    name="startDate"
                    label="入职日期"
                    placeholder="请输入入职日期"
                />
            </ProForm.Group>
            <ProForm.Group>
                <ProFormDatePicker
                    width="md"
                    name="departureDate"
                    label="离职日期"
                    placeholder="请输入离职日期"
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

export default AssistCalculateEmployeeCreate;