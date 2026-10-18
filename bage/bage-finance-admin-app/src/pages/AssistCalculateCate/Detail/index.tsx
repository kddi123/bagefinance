import { ProForm, ProFormText } from '@ant-design/pro-components';
import { Form, Button, message } from 'antd';
import React, { useState, useEffect } from 'react';
import { update, getAssistCalculateCateDetail } from '@/services/ant-design-pro/assistCalculateCate';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';

const AssistCalculateCateUpdate: React.FC<any> = (props) => {
    const [form] = Form.useForm();
    const { p } = props;

    const [customColumnCount, setCustomColumnCount] = useState(0);

    const handleAddCustomColumn = () => {
        if (customColumnCount < 8) {
            setCustomColumnCount((prevCount) => prevCount + 1);
        }
    };
    const handleDeleteCustomColumn = () => {
        // 删除当前自定义列文本框
        setCustomColumnCount((prevCount) => prevCount - 1);
    };

    const onFinish = async (values: any) => {
        let params = {
            ...values,
        };
        console.log(params);
        const allValues: Record<string, string> = form.getFieldsValue();
        // 从 allValues 中提取自定义列的值
        const customColumnValues = Object.keys(allValues)
            .filter((key) => key.startsWith('c'))
            .reduce((acc: Record<string, string>, key) => {
                if (key === "c_code" || key === "c_name" || key === "c_notes" || key === "c_mnemonicCode") {
                    const modifiedKey = key.slice(2); // 移除 'c_' 前缀
                    acc[modifiedKey] = allValues[key];
                }
                else {
                    acc[key] = allValues[key];
                }
                return acc;
            }, {});
        console.log('Custom Column Values:', customColumnValues);
        const customColumnData = Object.entries(customColumnValues).map(([key, value]) => ({ "columnName": key, "columnAlias": value }));

        await update({ id: p.id, code: params.code, name: params.name, customerColumnConfigList: customColumnData }).then(response => {
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
                return await getAssistCalculateCateDetail({ id: p.id }).then(response => {
                    // 遍历 customerColumnConfigList，组合成对象
                    console.log("aaaaa" + response.data.customerColumnConfigList.length)
                    let columnCount = 0;
                    const combinedObject = response.data.customerColumnConfigList.reduce((acc: Record<string, string>, { columnName, columnAlias }: { columnName: string; columnAlias: string }) => {
                        // 添加前缀，如果是 "code" 则加上 "c_code" 前缀，如果是 "name" 则加上 "c_name" 前缀
                        let prefixedColumnName = columnName;
                        if (columnName === "code" || columnName === "name" || columnName === "notes" || columnName === "mnemonicCode") {
                            prefixedColumnName = "c_" + columnName;
                        }
                        else {
                            ++columnCount;
                        }
                        acc[prefixedColumnName] = columnAlias;
                        return acc;
                    }, { name: response.data.name });
                    setCustomColumnCount(columnCount);
                    return combinedObject;
                });
            }}
        >
            <ProForm.Group>
                <ProFormText
                    width="md"
                    name="name"
                    label="辅助核算类别"
                    placeholder="请输入辅助核算类别"
                    rules={[{ required: true }]}
                />
            </ProForm.Group>
            <ProForm.Group>
                <ProFormText
                    name="c_code"
                    label="默认列"
                    placeholder="请输入编码"
                    rules={[{ required: true }]}
                />
                <ProFormText
                    name="c_name"
                    placeholder="请输入名称"
                    rules={[{ max: 50 }, { required: true }]}
                />
                <ProFormText
                    name="c_mnemonicCode"
                    placeholder="请输入注记码"
                    rules={[{ max: 50 }, { required: true }]}
                />
                <ProFormText
                    name="c_notes"
                    placeholder="请输入备注"
                    rules={[{ max: 50 }, { required: true }]}
                />
            </ProForm.Group>
            <ProForm.Group>
                <ProForm.Item
                    label="自定义列"
                />
                {[...Array(customColumnCount).keys()].map((index) => (

                    <ProFormText
                        width={200}
                        name={`c${index + 1}`}
                        placeholder={`自定义列 ${index + 1}`}
                        rules={[{ max: 50 }, { required: true }]}
                        key={`customColumn${index}`}
                    />

                ))}
                {customColumnCount < 8 && (
                    <ProForm.Item>
                        <Button
                            icon={<PlusOutlined />}
                            type="primary"
                            block
                            onClick={handleAddCustomColumn}
                        >
                            添加自定义列
                        </Button>
                    </ProForm.Item>
                )}
                {customColumnCount > 0 && (
                    <ProForm.Item>
                        <Button
                            icon={<DeleteOutlined />}
                            danger
                            type="primary"
                            block
                            onClick={handleDeleteCustomColumn}
                        >
                            删除自定义列
                        </Button>
                    </ProForm.Item>
                )}
            </ProForm.Group>
        </ProForm>
    );
};

export default AssistCalculateCateUpdate;