import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProFormCheckbox } from '@ant-design/pro-components';
import { Card, Form, Row, Col, Button, Input, Select, Radio, Switch, DatePicker, message, Checkbox, Cascader, Tooltip, Popconfirm } from 'antd';
import React, { useState, useEffect } from 'react';
import { getAssistCalculateCateDetail } from '@/services/ant-design-pro/assistCalculateCate';
import { updateCustom, getCustom } from '@/services/ant-design-pro/assistCalculateSummary';

import { listRegion } from '@/services/ant-design-pro/system';
import { history } from 'umi';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';

const AssistCalculateCustomDetail: React.FC<any> = (props) => {
    const [form] = Form.useForm();
    const { p } = props;
    const [regionData, setRegionData] = useState([]);
    const [selectedRegionValues, setSelectedRegionValues] = useState([]);

    const [customColumnCount, setCustomColumnCount] = useState(0);

    const [cateName, setCateName] = useState<string>('');
    const [customColumn, setCustomColumn] = useState([]);

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
            layout="horizontal"
            request={async (params = {}) => {
               // message.success(2)

                return await getAssistCalculateCateDetail({ "id": p.cateId }).then(response => {
                    setCustomColumn(response.data?.customerColumnConfigList);
                    return getCustom({ "id": p.id }).then(detailResponse => {
                        // 遍历 customerColumnConfigList，组合成对象
                        setCateName(response.data?.name);
                        const combinedObject = response.data.customerColumnConfigList.reduce((acc, { columnName, columnAlias }) => {
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