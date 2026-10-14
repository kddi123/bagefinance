import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProFormCheckbox } from '@ant-design/pro-components';
import { Card, Form, Row, Col, Button, Input, Select, Radio, Switch, DatePicker, message, Checkbox, Space } from 'antd';
import React, { useState, useEffect } from 'react';
import { getSubject, createSubject } from '@/services/ant-design-pro/subject';
import { listAssistCalculateCate } from '@/services/ant-design-pro/assistCalculateCate';
import { list } from '@/services/ant-design-pro/currencyConfig';
import { history } from 'umi';
const CheckboxGroup = Checkbox.Group;

const SubjectCreate: React.FC = () => {
    const [form] = Form.useForm();
    const urlParams = new URL(window.location.href).searchParams;
    const urlParamMap = {};
    for (const [key, value] of urlParams.entries()) {
        urlParamMap[key] = value;
    }
    // 用于存储从接口获取的选项数据
    const [assistCalculateCateOptions, setAssistCalculateCateOptions] = useState([]);
    const [assistCalculateCateRequired, setAssistCalculateCateRequired] = useState([]);
    const [assistCurrencyConfigOptions, setAssistCurrencyConfigOptions] = useState([]);
    const [enableForeignCurrencyConfig, setEnableForeignCurrencyConfig] = useState(false);
    const [enableAssistCalculateConfigs, setEnableAssistCalculateConfigs] = useState(false);
    const [enableNumberCalculateConfig, setEnableNumberCalculateConfig] = useState(false);

    const [parentEnableForeignCurrencyConfig, setParentEnableForeignCurrencyConfig] = useState(false);
    const [parentEnableAssistCalculateConfigs, setParentEnableAssistCalculateConfigs] = useState(false);
    const [parentEnableNumberCalculateConfig, setParentEnableNumberCalculateConfig] = useState(false);

    type AssistCalculateConfig = {
        assistCalculateId: number;
        requiredFlag: boolean;
    };

    function mergeArrays(a: number[], b: number[]): AssistCalculateConfig[] {
        a = a ? a : [];
        b = b ? b : [];
        const mergedArray: AssistCalculateConfig[] = [];

        // 处理 a 数组
        for (const idA of a) {
            mergedArray.push({
                assistCalculateId: idA,
                requiredFlag: b.includes(idA),
            });
        }

        return mergedArray;
    }
    const onFinish = async (values: any) => {
        let assistCalculateConfigs = mergeArrays(values.assistCalculateCate, values.assistCalculateCateRequired);
        let params = {
            ...values,
            disable: !form.getFieldValue("disable"),
            pid: urlParamMap["pid"],
            //核算配置
            subjectCalculateConfigForm: {
                //数量核算配置
                numberCalculateConfig: {
                    unitOfMeasurement: values.unitOfMeasurement
                },
                //辅助核算配置
                assistCalculateConfigs: assistCalculateConfigs,
                //外币核算配置
                foreignCurrencyConfig: {
                    //是否启用期末调汇
                    endOfYearCurrencyRevaluationFlag: values.endOfYearCurrencyRevaluationFlag ? values.endOfYearCurrencyRevaluationFlag : false,
                    //币别id列表
                    currencyConfigIds: values.assistCheckboxGroup
                },
                enableAssistCalculateConfigs: values.enableAssistCalculateConfigs ? values.enableAssistCalculateConfigs : false,
                enableForeignCurrencyConfig: values.enableForeignCurrencyConfig ? values.enableForeignCurrencyConfig : false,
                enableNumberCalculateConfig: values.enableNumberCalculateConfig ? values.enableNumberCalculateConfig : false
            }
        };
        console.log(params);
        await createSubject(params).then(response => {
            if (response.data) {
                message.success("创建成功");
                history.push("/FinanceSetting/SubjectList");
            }
            else {
                message.error("创建失败");
            }
        }).catch(error => {
            //console.log(error);
        });

    };
    const fetchAssistCalculateCateOptions = async (defaultCheckedValues: []) => {
        await listAssistCalculateCate().then(response => {
            setAssistCalculateCateOptions(response.data.map(item => ({ label: item.name, value: item.id.toString() })));
            setAssistCalculateCateRequired(response.data
                .filter(item => defaultCheckedValues.map(checkedItem => checkedItem.assistCalculateId).includes(item.id))
                .map(item => ({ label: item.name, value: item.id.toString() })));
            form.setFieldsValue({ assistCalculateCate: defaultCheckedValues.map(item => (item.assistCalculateId.toString())) });


        }).catch(error => {
            //console.log(error);
        });
    };
    const fetchCurrencyConfigOptions = async (defaultCheckedValues: []) => {
        const defaultCheckedStrings = defaultCheckedValues?defaultCheckedValues.map(value => value.toString()):[];
        await list().then(response => {
            setAssistCurrencyConfigOptions(response.data.map(item => ({
                label: item.name,
                value: item.id.toString(),
                disabled: defaultCheckedStrings.includes(item.id.toString()),
            })));
            form.setFieldsValue({ assistCheckboxGroup: defaultCheckedValues.map(value => value.toString()) });
        }).catch(error => {
            //console.log(error);
        });
    };
    const handleSwitchEnableForeignCurrencyConfig = (checked: boolean) => {
        setEnableForeignCurrencyConfig(checked);
        // 在这里处理开关状态变化，可以执行其他逻辑
    };
    const handleSwitchEnableAssistCalculateConfigs = (checked: boolean) => {
        setEnableAssistCalculateConfigs(checked);
        // 在这里处理开关状态变化，可以执行其他逻辑
    };
    const handleSwitchEnableNumberCalculateConfig = (checked: boolean) => {
        setEnableNumberCalculateConfig(checked);
        // 在这里处理开关状态变化，可以执行其他逻辑
    };
    const onAssistCalculateCateChange = () => {
        setAssistCalculateCateRequired(assistCalculateCateOptions
            .filter(item => form.getFieldsValue().assistCalculateCate.includes(item.value)));
    }
    // 在组件加载时调用接口
    useEffect(() => {

    }, []);
    return (
        <PageContainer>
            <Card>
                <ProForm
                    form={form}
                    onFinish={onFinish}
                    layout="vertical"
                    request={async (params = {}) => {
                        console.log(urlParamMap)
                        return await getSubject({ id: urlParamMap["id"], pid: urlParamMap["pid"] }).then(response => {
                            fetchAssistCalculateCateOptions(response.data.subjectCalculateConfigVo.assistCalculateConfigs);
                            fetchCurrencyConfigOptions(response.data.subjectCalculateConfigVo.foreignCurrencyConfig.currencyConfigIds);
                            setEnableNumberCalculateConfig(response.data.subjectCalculateConfigVo.enableNumberCalculateConfig);
                            setEnableAssistCalculateConfigs(response.data.subjectCalculateConfigVo.enableAssistCalculateConfigs);
                            setEnableForeignCurrencyConfig(response.data.subjectCalculateConfigVo.enableForeignCurrencyConfig);

                            setParentEnableNumberCalculateConfig(response.data.subjectCalculateConfigVo.extendParentNumberCalculateConfigFlag);
                            setParentEnableAssistCalculateConfigs(response.data.subjectCalculateConfigVo.extendParentAssistCalculateConfigsFlag);
                            setParentEnableForeignCurrencyConfig(response.data.subjectCalculateConfigVo.extendParentForeignCurrencyConfigFlag);

                            return {
                                ...response.data,
                                disable: !response.data.disable,
                                unitOfMeasurement: response.data.subjectCalculateConfigVo.numberCalculateConfig.unitOfMeasurement,
                                valueAddedTaxCate: response.data.valueAddedTaxCate?.toString(), // 将整数转换为字符串
                                accountingStandard: response.data.accountingStandard?.toString(),
                                //是否启用数量核算配置
                                enableNumberCalculateConfig: response.data.subjectCalculateConfigVo.enableNumberCalculateConfig,
                                //是否启用辅助核算配置
                                enableAssistCalculateConfigs: response.data.subjectCalculateConfigVo.enableAssistCalculateConfigs,
                                //外币核算配置
                                enableForeignCurrencyConfig: response.data.subjectCalculateConfigVo.enableForeignCurrencyConfig,
                                //是否启用期末调汇
                                endOfYearCurrencyRevaluationFlag: response.data.subjectCalculateConfigVo.foreignCurrencyConfig.endOfYearCurrencyRevaluationFlag
                            }
                        });
                    }}
                >
                    <ProForm.Group>
                        <ProFormText
                            width="md"
                            name="code"
                            label="科目编码"
                            tooltip="最长为 16 位"
                            placeholder="请输入科目编码"
                            rules={[{ required: true }]}
                        />
                        <ProFormText
                            width="md"
                            name="name"
                            label="科目名称"
                            tooltip="最长为 50 位"
                            placeholder="请输入科目名称"
                            rules={[{ required: true, max: 50 }]}
                        />

                    </ProForm.Group>
                    <ProForm.Group>
                        <ProFormSelect width="md" name="parentName" label="上级科目" disabled rules={[{ required: true }]}>
                        </ProFormSelect >
                        <ProFormSelect width="md" name="subjectCateName" disabled label="科目类别" rules={[{ required: true }]}>
                        </ProFormSelect>
                    </ProForm.Group>
                    <ProForm.Group>
                        <ProFormRadio.Group width="md" name="balanceDirection" label="余额方向" radioType="radio" rules={[{ required: true, message: "请选择余额方向" }]}
                            options={[
                                {
                                    label: '借',
                                    value: 1
                                },
                                {
                                    label: '贷',
                                    value: 2
                                }
                            ]}>
                        </ProFormRadio.Group>
                        <ProFormSwitch width="md" name="disable" label="科目状态" rules={[{ required: true }]} checkedChildren="启用" unCheckedChildren="停用" initialValue={true} />
                    </ProForm.Group>
                    <ProForm.Group>
                        <ProFormSwitch
                            name="enableNumberCalculateConfig"
                            fieldProps={{
                                checkedChildren: '启用数量核算',
                                unCheckedChildren: '禁用数量核算',
                                defaultChecked: false, // 设置默认选中状态
                            }}
                            onChange={handleSwitchEnableNumberCalculateConfig}
                            disabled={parentEnableNumberCalculateConfig}
                        />
                        <ProFormText
                            width="md"
                            name="unitOfMeasurement"
                            label="计量单位"
                            tooltip="最长为 10 位"
                            placeholder="请输入计量单位"
                            rules={[{ required: enableNumberCalculateConfig, max: 10 }]}
                            disabled={!enableNumberCalculateConfig || parentEnableNumberCalculateConfig}
                            hidden={!enableNumberCalculateConfig}
                        />
                    </ProForm.Group>

                    <ProForm.Group>
                        <ProFormSwitch
                            name="enableAssistCalculateConfigs"
                            fieldProps={{
                                checkedChildren: '启用辅助核算',
                                unCheckedChildren: '禁用辅助核算',
                                defaultChecked: false, // 设置默认选中状态
                            }}
                            onChange={handleSwitchEnableAssistCalculateConfigs}
                            disabled={parentEnableAssistCalculateConfigs}
                        />
                        <ProForm.Item style={{ marginTop: 2 }} hidden={!enableAssistCalculateConfigs} name="assistCalculateCate">
                            <CheckboxGroup options={assistCalculateCateOptions}
                                disabled={!enableAssistCalculateConfigs || parentEnableAssistCalculateConfigs}
                                onChange={onAssistCalculateCateChange} />
                        </ProForm.Item>
                    </ProForm.Group>

                    <ProForm.Group>
                        <ProForm.Item hidden={!enableAssistCalculateConfigs}>
                            <div>设置非必录项</div>
                        </ProForm.Item>
                        <ProForm.Item style={{ marginTop: 2 }} hidden={!enableAssistCalculateConfigs} name="assistCalculateCateRequired">
                            <CheckboxGroup options={assistCalculateCateRequired}
                                disabled={!enableAssistCalculateConfigs} />
                        </ProForm.Item>
                    </ProForm.Group>
                    <ProForm.Group>
                        <ProFormSwitch
                            name="enableForeignCurrencyConfig"
                            fieldProps={{
                                checkedChildren: '启用外币核算',
                                unCheckedChildren: '禁用外币核算',
                                defaultChecked: false, // 设置默认选中状态
                            }}
                            onChange={handleSwitchEnableForeignCurrencyConfig}
                            disabled={parentEnableForeignCurrencyConfig}
                        />
                        <ProForm.Item style={{ marginTop: 2 }} name="assistCheckboxGroup" hidden={!enableForeignCurrencyConfig}>
                            <CheckboxGroup options={assistCurrencyConfigOptions} />
                        </ProForm.Item>
                    </ProForm.Group>
                    <ProForm.Group>
                        <ProFormSwitch
                            name="endOfYearCurrencyRevaluationFlag"
                            fieldProps={{
                                checkedChildren: '启用期末调汇',
                                unCheckedChildren: '禁用期末调汇',
                                defaultChecked: false, // 设置默认选中状态
                            }}
                            disabled={!enableForeignCurrencyConfig || parentEnableForeignCurrencyConfig}
                            hidden={!enableForeignCurrencyConfig}
                        />
                    </ProForm.Group>
                </ProForm>
            </Card>
        </PageContainer >
    );
};

export default SubjectCreate;