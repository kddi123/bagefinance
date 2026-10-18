import { ProForm, ProFormText, ProFormSwitch } from '@ant-design/pro-components';
import { Form, message } from 'antd';
import React, { useState, useEffect } from 'react';
import { getAssistCalculateCateDetail } from '@/services/ant-design-pro/assistCalculateCate';
import { createCustom } from '@/services/ant-design-pro/assistCalculateSummary';

const AssistCalculateCustomCreate: React.FC<any> = (props) => {
    const [form] = Form.useForm();
    const { p } = props;

    const [customColumn, setCustomColumn] = useState<Array<{ columnName: string; columnAlias: string }>>([]);
    const onFinish = async (values: any) => {
        let params = {
            ...values,
            disable: !form.getFieldValue("disable")
        };
        console.log(params);
        await createCustom({ ...params, assistCalculateCateId: p.cateId }).then(response => {
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
            layout="horizontal"
            request={async () => {
                return await getAssistCalculateCateDetail({ "id": p.cateId }).then(response => {
                    setCustomColumn(response.data?.customerColumnConfigList);
                    return customColumn;
                });
            }}
        >
            {customColumn.map((item) => (
                item.columnName !== "mnemonicCode" ? (
                    <ProForm.Group key={item.columnName}>
                        <ProFormText
                            name={item.columnName}
                            width="md"
                            label={item.columnAlias}
                            placeholder={"请输入" + item.columnAlias}
                            rules={[{ max: 50 }, { required: (item.columnName === 'code' || item.columnName === 'name') }]}
                        />
                    </ProForm.Group>
                ) : null
            ))}
            <ProForm.Group>
                <ProFormSwitch width="md" name="disable" label="是否启用" rules={[{ required: true }]} checkedChildren="启用" unCheckedChildren="停用" initialValue={true} />
            </ProForm.Group>
        </ProForm>
    );
};

export default AssistCalculateCustomCreate;