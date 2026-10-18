import { ProForm, ProFormText, ProFormSwitch } from '@ant-design/pro-components';
import { Form, message } from 'antd';
import React, { useState, useEffect } from 'react';
import { getAssistCalculateCateDetail } from '@/services/ant-design-pro/assistCalculateCate';
import { updateCustom, getCustom } from '@/services/ant-design-pro/assistCalculateSummary';

const AssistCalculateCustomDetail: React.FC<any> = (props) => {
    const [form] = Form.useForm();
    const { p } = props;
    const [customColumn, setCustomColumn] = useState<Array<{ columnName: string; columnAlias: string }>>([]);

    const onFinish = async (values: any) => {
        let params = {
            ...values,
            id: p.id
        };
        console.log(params);
        await updateCustom({ ...params, assistCalculateCateId: p.cateId }).then(response => {
            if (response.data) {
                message.success("修改成功");
                props.updateIsModalOpenState(false);
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
        <ProForm
            form={form}
            onFinish={onFinish}
            layout="horizontal"
            request={async () => {
               // message.success(2)

                return await getAssistCalculateCateDetail({ "id": p.cateId }).then(response => {
                    setCustomColumn(response.data?.customerColumnConfigList);
                    return getCustom({ "id": p.id }).then(detailResponse => {
                        // 遍历 customerColumnConfigList，组合成对象
                        const combinedObject = response.data.customerColumnConfigList.reduce((acc: Record<string, string>, { columnName }: { columnName: string; columnAlias: string }) => {
                            acc[columnName] = detailResponse.data[columnName] || ""; // 使用详情接口返回的值填充文本框
                            return acc;
                        }, { name: detailResponse.data?.name, code: detailResponse.data?.code, notes: detailResponse.data?.notes });
                        return combinedObject;
                    });
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

export default AssistCalculateCustomDetail;