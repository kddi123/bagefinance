import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProFormCheckbox } from '@ant-design/pro-components';
import {
    Card, Form, Row, Col, Button, Input, Select, Radio, Switch, DatePicker, message,
    Checkbox, Table, Tag, Space, ConfigProvider, Popconfirm, InputNumber, Tabs, List, Flex,
    Upload, UploadProps, Divider, Modal
} from 'antd';
import { UploadOutlined, EditOutlined } from '@ant-design/icons';
import React, { useState, useEffect, useRef } from 'react';
import { list as listVoucherWordConfig } from '@/services/ant-design-pro/voucherWordConfig';
import { save, get } from '@/services/ant-design-pro/voucher';
import { listByCateAndCodeAndName, getSubjectDetail } from '@/services/ant-design-pro/subject';
import { history, Link } from 'umi';
import { digitUppercase } from "pixiu-number-toolkit";
import { list as listCurrencyConfig } from '@/services/ant-design-pro/currencyConfig';

import { list as listAssistCalculateSummary } from '@/services/ant-design-pro/assistCalculateSummary';
import { currentUser } from '@/services/ant-design-pro/api';
import FileIndex from '@/components/File/index';

import _ from 'lodash';
import dayjs from 'dayjs';
import type { Dayjs } from 'dayjs';
import moment from 'moment'; // 引入moment库用于处理日期
import { useLocation } from 'react-router-dom';


import './index.less'; // 导入CSS样式
import { Item } from 'rc-menu';
import { response } from 'express';
const { TextArea } = Input;
const { TabPane } = Tabs;

interface DataType {
    key: number;
    summary: string;
    subject: any;
    debitAmount: number;
    creditAmount: number;
    subjectBalance: number;
    newSubjectBalance: number;
}
const dateFormat = 'YYYY-MM-DD 00:00:00';

const Voucher: React.FC<any> = (props) => {
    const { p } = props;
    const [form] = Form.useForm();
    const inputRef = useRef();

    const [isOperate, setIsOperate] = useState(false);

    const [editingKey, setEditingKey] = useState<String>('');
    const [editingDataIndex, setEditingDataIndex] = useState<String>('');
    const [subjectData, setSubjectData] = useState([]);
    const [voucherWordConfigOptions, setVoucherWordConfigOptions] = useState([]);
    const [currencyConfigOptions, setCurrencyConfigOptions] = useState([]);
    const [defaultSelectVoucherWordConfigValue, setDefaultSelectVoucherWordConfigValue] = useState();
    const [assistCalculateOptions, setAssistCalculateOptions] = useState([]);
    const [voucherNumberValue, setVoucherNumberValue] = useState<String>();
    const [voucherDateValue, setVoucherDateValue] = useState<String>();
    const [documentNumValue, setDocumentNumValue] = useState<Number>(0);
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
    const [notesValule, setNotesValule] = useState<string>('');
    const [originalNotesValue, setOriginalNotesValue] = useState(''); // 原始的备注值
    const [isSubmit, setIsSubmit] = useState<boolean>(false);
    const [nickName, setNickName] = useState<string>('');
    const [isFileModalOpen, setIsFileModalOpen] = useState(false);
    const fileIndexRef = useRef();
    const [selectFileIds, setSelectFileIds] = useState<number[]>([]);
    const [selectTempFileIds, setSelectTempFileIds] = useState<number[]>([]);


    const sharedOnCell = (_: DataType, index: number) => {
        // 判断是否是最后一行
        if (_.key === 0) {
            return { colSpan: 0 };
        }

        return {};
    };
    //查询科目列表
    const onListSubject = async (subjectCate: string, codeAndName: string) => {
        let params = {};
        if (subjectCate !== "0") {
            params.subjectCate = subjectCate;
        }
        if (codeAndName) {
            params.codeAndName = codeAndName;
        }
        return await listByCateAndCodeAndName(params).then(response => {
            setSubjectData(response.data);
        });
    }
    //选择科目事件
    const onSelectSubject = (record, item) => {
        // if (record.subject.id && item.id === record.subject.id) {
        //     totalDataSource(dataSourceRow);
        //     // setIsOperate(false);
        //     // cancelEdit(record);
        //     return;
        // }
        getSubjectDetail({ "id": item.id }).then(response => {
            dataSourceRow.map((row) => {
                if (row.key === record.key) {
                    row.subject.id = item.id;
                    row.subject.code = item.code;
                    row.subject.name = item.name;
                    row.subject.fullName = item.fullName;
                    row.subject.showName = item.code + " " + item.fullName;
                    row.subject.tmpShowName = row.subject.showName;
                    row.subjectBalance = response.data.subjectBalance;
                    row.newSubjectBalance = response.data.subjectBalance;
                    row.subject.subjectCalculateConfigVo.enableNumberCalculateConfig = response.data.subjectCalculateConfigVo.enableNumberCalculateConfig;
                    row.subject.subjectCalculateConfigVo.enableForeignCurrencyConfig = response.data.subjectCalculateConfigVo.enableForeignCurrencyConfig;
                    row.subject.subjectCalculateConfigVo.enableAssistCalculateConfigs = response.data.subjectCalculateConfigVo.enableAssistCalculateConfigs;
                    row.subject.subjectCalculateConfigVo.assistCalculateConfigs = response.data.subjectCalculateConfigVo.assistCalculateConfigs;
                    row.subject.subjectCalculateConfigVo.foreignCurrencyConfig = response.data.subjectCalculateConfigVo.foreignCurrencyConfig;
                    if (response.data.subjectCalculateConfigVo.enableForeignCurrencyConfig) {
                        row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig = response.data.subjectCalculateConfigVo.foreignCurrencyConfig[0];
                        row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency = 0;
                        row.debitAmount = 0;
                        row.creditAmount = 0;
                    }
                    if (response.data.subjectCalculateConfigVo.enableNumberCalculateConfig) {
                        row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig = {
                            num: 0, // 数量
                            price: 0, //单价
                            totalPrice: 0//总价
                        }
                        row.debitAmount = 0;
                        row.creditAmount = 0;
                    }
                    //是否启用辅助核算
                    row.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs = [];
                    row.subject.subjectCalculateConfigVo.selectAssistCalculateOptions = [];
                    if (response.data.subjectCalculateConfigVo.enableAssistCalculateConfigs) {
                        row.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs = record.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs;
                        row.subject.subjectCalculateConfigVo.assistCalculateConfigs.map((item1, index1) => {
                            // let option = { label: '', value: '' };
                            row.subject.subjectCalculateConfigVo.selectAssistCalculateOptions.push(null);
                        });
                    }
                }
            });
            totalDataSource(dataSourceRow);
            if (record.subject.subjectCalculateConfigVo.enableAssistCalculateConfigs) {
                setIsOperate(true);
            }
            else {
                totalDataSource(dataSourceRow);
            }
        });
    }
    //确认辅助核算
    const onSelectAssistCalculate = (record, values) => {
        const showName = record.subject.code + " " + record.subject.fullName;
        dataSourceRow.map((row) => {
            if (row.key === record.key) {
                row.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs = [];
                row.subject.showName = showName;
                let index = 0;
                Object.keys(values).forEach((key) => {
                    const value = values[key];
                    if (value && value.label) {
                        row.subject.showName = row.subject.showName + "_" + value.label;
                        row.subject.tmpShowName = row.subject.showName;
                        const assistCalculateCateId = parseInt(key.split('_')[2], 10)
                        row.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs.push({
                            subjectId: row.subject.id,
                            assistCalculateCateId: assistCalculateCateId,//辅助核算类别id
                            assistCalculateId: value.value,//辅助核算id
                            assistCalculateName: value.label,//辅助核算名称
                        });
                    }
                    index++;
                });
            }
        });
        totalDataSource(dataSourceRow);
        setIsOperate(false);
        cancelEdit(record);
    };

    //搜索辅助核算
    const onSearchAssistCalculate = (assistCalculateCateId: number, codeOrName: string) => {
        setAssistCalculateOptions([]);
        listAssistCalculateSummary({ assistCalculateCateId: assistCalculateCateId, codeOrName: codeOrName }).then(response => {
            const dataArray = response.data.list.map(data => ({
                value: data.id,
                label: data.code + ' ' + data.name
            }));
            setAssistCalculateOptions(dataArray);
        });
    }
    const onValueChange = (record: any, changeField: string) => {
        dataSourceRow.map((row) => {
            if (row.key === record.key) {
                row = record;
                //如果数量或单价变化则修改借方金额或贷方金额
                if (changeField === "num" || changeField === "price") {
                    if (row.creditAmount) {
                        row.creditAmount = row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num * row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price;
                        row.creditAmount = Number(Number(row.creditAmount).toFixed(2));
                    }
                    else {
                        row.debitAmount = row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num * row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price;
                        row.debitAmount = Number(Number(row.debitAmount).toFixed(2));
                    }
                    if (changeField === "price") {
                        //是否启用了外币核算
                        if (row.subject.subjectCalculateConfigVo.enableForeignCurrencyConfig) {
                            //如果是本位币则修改原币
                            if (row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.baseCurrencyFlag) {
                                row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency = row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price / row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate;
                                row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency = Number(row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency).toFixed(2);
                            }
                            else {
                                //否则修改汇率
                                row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate = row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price / row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency;
                                row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate = Number(row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate).toFixed(2);
                            }
                        }
                    }
                }

                //修改币种，汇率或原币
                if (changeField === "foreignCurrencyConfigId" || changeField === "exchangeRate" || changeField === "originalCurrency") {
                    //如果是启用了数量核算
                    if (row.subject.subjectCalculateConfigVo.enableNumberCalculateConfig) {
                        row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price = row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency * row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate;
                        row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price = Number(row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price).toFixed(2);
                        if (row.creditAmount) {
                            row.creditAmount = row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num * row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price;
                            row.creditAmount = Number(Number(row.creditAmount).toFixed(2));
                        }
                        else {
                            row.debitAmount = row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num * row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price;
                            row.debitAmount = Number(Number(row.debitAmount).toFixed(2));
                        }
                    }
                    else {
                        //如果未启用数量核算 金额则是汇率*原币
                        const amount = row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency * row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate;
                        if (row.creditAmount) {
                            row.creditAmount = amount;
                            row.creditAmount = Number(Number(row.creditAmount).toFixed(2));
                        }
                        else {
                            row.debitAmount = amount;
                            row.debitAmount = Number(Number(row.debitAmount).toFixed(2));
                        }
                    }
                }

                //修改借款金额或贷款金额
                if (changeField === "debitAmount" || changeField === "creditAmount") {
                    if (row.subject.subjectCalculateConfigVo.enableNumberCalculateConfig) {
                        if (!row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num) {
                            row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num = 1;
                        }
                        if (row.creditAmount) {
                            row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price = row.creditAmount / row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num;
                        }
                        else {
                            row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price = row.debitAmount / row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num;
                        }
                        row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price = Number(row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price).toFixed(2);
                    }
                    //是否启用了外币核算
                    if (row.subject.subjectCalculateConfigVo.enableForeignCurrencyConfig) {
                        let price = row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency;
                        if (row.subject.subjectCalculateConfigVo.enableNumberCalculateConfig) {
                            price = row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price;
                        }
                        else {
                            if (row.debitAmount) {
                                price = row.debitAmount;
                            }
                            else {
                                price = row.creditAmount;
                            }
                        }
                        if (!row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate) {
                            row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate = 1;
                        }
                        if (!row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency) {
                            row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency = price;
                        }
                        //如果是本位币或者原币是0则修改原币
                        if (row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.baseCurrencyFlag) {
                            row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency = price / row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate;
                            row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency = Number(row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency).toFixed(2);
                        }
                        else {
                            //否则修改汇率
                            row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate = price / row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency;
                            row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate = Number(row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate).toFixed(2);
                        }
                    }
                }
                //设置余额
                if (row.debitAmount) {
                    row.newSubjectBalance = row.subjectBalance + row.debitAmount;
                    row.newSubjectBalance = Number(row.newSubjectBalance).toFixed(2);
                }
                else {
                    row.newSubjectBalance = row.subjectBalance - row.creditAmount;
                    row.newSubjectBalance = Number(row.newSubjectBalance).toFixed(2);
                }
            }
        });
        totalDataSource(dataSourceRow);
    }

    const columns: TableProps<DataType>['columns'] = [
        {
            title: () => (<div className='pz_title'>摘要</div>),
            dataIndex: 'summary',
            key: 'summary',
            width: 200,
            // ellipsis: true, // Enable ellipsis for this column
            render: (text, record, _, action) => {
                const isEditing = (record.key === editingKey && "summary" === editingDataIndex);
                if (record.key === 0) {
                    return (<><div className='heji'>
                        合计：{text}
                    </div></>)
                }
                return (
                    <>
                        {isEditing ? (
                            <div onClick={() => handleCellClick(record, 'summary')} className='zy editing'>
                                <TextArea autoFocus
                                    onFocus={(e) => {
                                        // 将光标移到最后一个位置
                                        const value = e.target.value;
                                        e.target.setSelectionRange(value.length, value.length);
                                    }}
                                    defaultValue={text}
                                    onChange={(e) => {
                                        record.summary = e.target.value;
                                    }}
                                ></TextArea>
                            </div>
                        ) : (
                            <div onClick={() => handleCellClick(record, 'summary')} className='zy show'>
                                {text}
                            </div>
                        )}
                    </>
                );
            },
            onCell: (_, index) => ({
                // 判断是否是最后一行
                colSpan: _.key === 0 ? 2 : 1,
            }),
            // render: (text, record) => (
            //     <div onClick={() => handleCellClick(record, 'name')} className='zy'>
            //         {text}
            //     </div>
            // ),
        },
        {
            title: () => (<div className='pz_title'>会计科目</div>),
            dataIndex: 'subject',
            key: 'subject',
            width: 400,
            render: (text, record, _, action) => {
                const isEditing = (record.key === editingKey && "subject" === editingDataIndex);
                let height = 70;
                //是否有余额
                let isYe = false;
                //是否启用外币核算
                let isWb = false;
                //是否启用数量核算
                let isSl = false;
                if (record.subject) {
                    if (record.subject.id) {
                        isYe = true;
                    }
                    isWb = record.subject.subjectCalculateConfigVo.enableForeignCurrencyConfig;
                    isSl = record.subject.subjectCalculateConfigVo.enableNumberCalculateConfig;
                }
                // if (isYe) {
                //     height = height + 10;
                // }
                // if (isWb) {
                //     height = height + 10;
                // }
                // if (isSl) {
                //     height = height + 10;
                // }
                return (
                    <>
                        {isEditing ? (
                            <Form onFinish={(values) => {
                                onSelectAssistCalculate(record, values);
                            }}>
                                <div onClick={() => handleCellClick(record, 'subject')} className='km editing' >
                                    <Form.Item>
                                        <TextArea autoFocus
                                            autoSize={{ minRows: 2, maxRows: 4 }}
                                            onFocus={(e) => {
                                                if (record.subject.id) {
                                                    record.subject.showName = record.subject.code + " " + record.subject.name;
                                                }
                                                // 将光标移到最后一个位置
                                                const value = e.target.value;
                                                e.target.setSelectionRange(value.length, value.length);
                                                onListSubject("0", "");
                                            }}
                                            onChange={(e) => {
                                                record.subject.showName = e.target.value;
                                                // 将光标移到最后一个位置
                                                const value = e.target.value;
                                                onListSubject("0", value);
                                            }}
                                            placeholder={'请输入科目名称/科目编码/科目名称拼音首字母快速搜索'}
                                            defaultValue={record.subject.showName}
                                            value={record.subject.showName}
                                        // onBlur={() => { cancelEdit(record) }}
                                        >
                                        </TextArea>
                                    </Form.Item>
                                    {
                                        (isWb || isYe || isSl) && (
                                            <div style={{ height: height }}>
                                                <div className='km_right'
                                                >
                                                    {isWb && (
                                                        <Space className='hl_yb'>
                                                            <Select
                                                                size='small'
                                                                style={{ padding: 0, marginRight: 5, width: 100, textAlign: 'left' }}
                                                                value={record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.id}
                                                                // options={currencyConfigOptions}
                                                                options={
                                                                    record.subject.subjectCalculateConfigVo.foreignCurrencyConfig.map(d => ({
                                                                        value: d.id,
                                                                        label: d.name
                                                                    }))
                                                                }
                                                                onChange={(e) => {
                                                                    record.subject.subjectCalculateConfigVo.foreignCurrencyConfig.map(d => {
                                                                        if (d.id === e) {
                                                                            const originalCurrency = record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency;
                                                                            record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig = d;
                                                                            record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency = originalCurrency;
                                                                        }
                                                                    });
                                                                    onValueChange(record, "foreignCurrencyConfigId");
                                                                }}
                                                            />
                                                            汇率：<Input
                                                                //defaultValue={record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate}
                                                                value={record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate}

                                                                size='small'
                                                                disabled={record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.baseCurrencyFlag}
                                                                style={{ width: 70 }}
                                                                onChange={(e) => {
                                                                    record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate = e.target.value;
                                                                    onValueChange(record, "exchangeRate");
                                                                }}
                                                            ></Input>
                                                            原币：
                                                            <Input
                                                                value={record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency}

                                                                size='small'
                                                                style={{ width: 70 }}
                                                                onChange={(e) => {
                                                                    record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency = e.target.value;
                                                                    onValueChange(record, "originalCurrency");
                                                                }}
                                                            ></Input>
                                                        </Space>
                                                    )}
                                                    {isSl && (
                                                        <Space className='sl_dj'>
                                                            数量：<Input
                                                                value={record.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num}

                                                                size='small'
                                                                style={{ width: 70 }}
                                                                onChange={(e) => {
                                                                    record.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num = e.target.value;
                                                                    onValueChange(record, "num");
                                                                }}
                                                            ></Input>
                                                            单价：<Input
                                                                value={record.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price}

                                                                size='small'
                                                                style={{ width: 70 }}
                                                                onChange={(e) => {
                                                                    record.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price = e.target.value;
                                                                    onValueChange(record, "price");
                                                                }}
                                                            ></Input>
                                                        </Space>
                                                    )}

                                                </div>
                                                {isYe && (
                                                    <div className='ye'><Link to={'#'}
                                                        onClick={(e) => {
                                                            e.stopPropagation(); // 阻止点击事件冒泡
                                                            // 处理点击事件的逻辑
                                                        }}
                                                    >余额：{record.newSubjectBalance}</Link></div>
                                                )}
                                            </div>
                                        )
                                    }


                                    <div className='km_select_panel'
                                    >
                                        <Tabs defaultActiveKey="0"
                                            onChange={(activeKey) => {
                                                onListSubject(activeKey, "");
                                            }}
                                        >
                                            <TabPane
                                                tab={
                                                    <span>
                                                        全部
                                                    </span>
                                                }
                                                key="0"
                                            ></TabPane>
                                            <TabPane
                                                tab={
                                                    <span>
                                                        资产
                                                    </span>
                                                }
                                                key="1"
                                            ></TabPane>
                                            <TabPane
                                                tab={
                                                    <span>
                                                        负债
                                                    </span>
                                                }
                                                key="2"
                                            ></TabPane>
                                            <TabPane
                                                tab={
                                                    <span>
                                                        权益
                                                    </span>
                                                }
                                                key="3"
                                            ></TabPane>
                                            <TabPane
                                                tab={
                                                    <span>
                                                        成本
                                                    </span>
                                                }
                                                key="4"
                                            ></TabPane>
                                            <TabPane
                                                tab={
                                                    <span>
                                                        损益
                                                    </span>
                                                }
                                                key="5"
                                            ></TabPane>
                                        </Tabs>
                                        <div
                                            id="scrollableDiv"
                                            style={{
                                                height: record.subject.subjectCalculateConfigVo.enableAssistCalculateConfigs && record.subject.subjectCalculateConfigVo.assistCalculateConfigs ? 300 : 258,
                                                overflow: 'auto',
                                                backgroundColor: 'white',
                                                borderTop: 0
                                            }}
                                        >
                                            <List
                                                size="small"
                                                // bordered
                                                dataSource={subjectData}
                                                renderItem={(item) => <List.Item
                                                    onClick={() => onSelectSubject(record, item)}
                                                    className={record.subject.id === item.id ? 'active' : ''} //
                                                >{item.code} <span style={{ float: 'right' }}>{item.fullName}</span>
                                                </List.Item>}
                                            />
                                            {!(record.subject.subjectCalculateConfigVo.enableAssistCalculateConfigs && record.subject.subjectCalculateConfigVo.assistCalculateConfigs) && (

                                                <Button type="primary" style={{ float: 'right', margin: 5, position: 'absolute', bottom: 0, right: 0 }}
                                                    disabled={record.subject.id ? false : true}
                                                    onClick={() => {
                                                        if (!record.subject.id) {
                                                            message.error("请确认科目");
                                                            return;
                                                        }
                                                        cancelEdit(record);
                                                    }}
                                                >
                                                    确定
                                                </Button>
                                            )}
                                            {record.subject.subjectCalculateConfigVo.enableAssistCalculateConfigs && record.subject.subjectCalculateConfigVo.assistCalculateConfigs && (
                                                <div style={{ position: 'absolute', top: 0, right: -400, width: 400 }}>
                                                    <Card size="small" title=' → 辅助核算' style={{
                                                        borderRadius: 0,
                                                        borderTop: '1px solid rgba(140, 140, 140, 0.35)',
                                                        borderRight: '1px solid rgba(140, 140, 140, 0.35)',
                                                        borderBottom: '1px solid rgba(140, 140, 140, 0.35)',
                                                        borderLeft: '3px solid #44b449'
                                                    }} headStyle={{ height: 20 }}>
                                                        {record.subject.subjectCalculateConfigVo.assistCalculateConfigs.map((item, index) => {
                                                            let selectAssistCalculateOptions;
                                                            if (record.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs) {
                                                                record.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs.map((item2, index2) => {
                                                                    if (record.subject.id === item2.subjectId && item.id === item2.assistCalculateCateId) {
                                                                        selectAssistCalculateOptions = {
                                                                            assistCalculateCateId: item2.assistCalculateCateId,
                                                                            value: item2.assistCalculateId,//辅助核算id
                                                                            label: item2.assistCalculateName,//辅助核算名称
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                            return (
                                                                <Form.Item
                                                                    labelCol={{ span: 6 }}  // 根据需要调整标签所占列数
                                                                    wrapperCol={{ span: 18 }} // 根据需要调整表单项所占列数
                                                                    // key={record.rKey + "_" + item.id}
                                                                    label={item.name}
                                                                    name={"a_" + record.subject.id + "_" + item.id}
                                                                    rules={[
                                                                        {
                                                                            required: item.requiredFlag,
                                                                            message: `请选择 ${item.name}!`,
                                                                        },
                                                                    ]}
                                                                    initialValue={selectAssistCalculateOptions}
                                                                >
                                                                    <Select
                                                                        // mode="tags"
                                                                        labelInValue
                                                                        showSearch={true}
                                                                        filterOption={(input, option) => {
                                                                            //忽略大小写
                                                                            return option.label.toLowerCase().includes(input.toLowerCase());
                                                                        }}
                                                                        onFocus={() => { onSearchAssistCalculate(item.id, item.assistCalculateName); }}
                                                                        onSearch={(value) => { onSearchAssistCalculate(item.id, value); }}
                                                                        options={assistCalculateOptions}
                                                                    />
                                                                </Form.Item>
                                                            )
                                                        })}
                                                        <Form.Item wrapperCol={{ offset: 6, span: 18 }}>
                                                            <Button type="primary" htmlType='submit'>
                                                                确定
                                                            </Button>
                                                        </Form.Item>
                                                    </Card>
                                                </div>)
                                            }

                                        </div>
                                    </div>
                                </div>
                            </Form>
                        ) : (
                            <div onClick={() => handleCellClick(record, 'subject')} className='km show'>
                                <div className='km_content'>
                                    {record.subject.showName}
                                </div>
                                <div style={{ height: height }}>
                                    <div className='km_right'>
                                        {isWb && (
                                            <>
                                                <Space>
                                                    <Select
                                                        size='small'
                                                        value={record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.name}
                                                        disabled
                                                        style={{ padding: 0, marginRight: 5, width: 100, textAlign: 'left' }}
                                                    />
                                                    <span className='hl_yb'>汇率：{record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate}</span>
                                                    <span>原币：{record.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency}</span>
                                                </Space>
                                                <br></br>
                                            </>
                                        )}
                                        {isSl && (
                                            <Space>
                                                <span className='sl_dj'>数量：{record.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num}</span>
                                                <span>单价：{record.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price}</span>
                                            </Space>
                                        )}
                                    </div>
                                    {isYe && (
                                        <div className='ye'><Link to={'#'}
                                            onClick={(e) => {
                                                e.stopPropagation(); // 阻止点击事件冒泡
                                                // 处理点击事件的逻辑
                                            }}
                                        >余额：{record.newSubjectBalance}</Link></div>
                                    )}
                                </div>
                            </div>
                        )
                        }
                    </>
                );
            },
            onCell: sharedOnCell,
        },
        {
            title: () => (<div className='pz_title'>借方金额</div>),
            dataIndex: 'debitAmount',
            key: 'debitAmount',
            align: 'center', // 设置表头居中
            width: 220,
            children: [
                {
                    height: 100,
                    width: 220,
                    title: (<div className='custom-border th'>
                        <span>亿</span>
                        <span>千</span>
                        <span className='b'>百</span>
                        <span>十</span>
                        <span>万</span>
                        <span className='q'>千</span>
                        <span>百</span>
                        <span>十</span>
                        <span className='y'>元</span>
                        <span>角</span>
                        <span>分</span>
                    </div>),
                    dataIndex: 'debitAmount',
                    key: 'debitAmount',
                    align: 'center', // 设置表头居中
                    render: (text, record, _, action) => {
                        const isEditing = (record.key === editingKey && "debitAmount" === editingDataIndex);
                        const amount = formatAmount(record.debitAmount >= 0 ? record.debitAmount : -record.debitAmount);

                        return (
                            <>
                                {record.key > 0 && isEditing ? (
                                    <div onClick={() => handleCellClick(record, 'debitAmount')} className='jfje editing'>
                                        <InputNumber autoFocus
                                            onFocus={(e) => {
                                                // 将光标移到最后一个位置
                                                const value = e.target.value;
                                                e.target.setSelectionRange(0, value.length);
                                            }}
                                            placeholder='请输入借方金额'
                                            formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                                            parser={(value) => value!.replace(/,/g, '')}
                                            // formatter={handleFormatter}
                                            // parser={handleParser}
                                            defaultValue={Number(text).toFixed(2)} style={{ fontSize: 25 }}
                                            // onBlur={() => { cancelEdit(record) }}
                                            onChange={(e) => {
                                                record.creditAmount = 0;
                                                record.debitAmount = e;
                                                onValueChange(record, "debitAmount");
                                            }}
                                        ></InputNumber>
                                    </div>
                                ) : (
                                    <div onClick={() => handleCellClick(record, 'debitAmount')} className={record.debitAmount >= 0 ? 'custom-border td' : 'custom-border td red'}>
                                        <span>{amount[0]}</span>
                                        <span>{amount[1]}</span>
                                        <span className='b'>{amount[2]}</span>
                                        <span>{amount[3]}</span>
                                        <span>{amount[4]}</span>
                                        <span className='q'>{amount[5]}</span>
                                        <span>{amount[6]}</span>
                                        <span>{amount[7]}</span>
                                        <span className='y'>{amount[8]}</span>
                                        <span>{amount[9]}</span>
                                        <span>{amount[10]}</span>
                                    </div>
                                )}
                            </>
                        );
                    },
                }]

        },
        {
            title: () => (<div className='pz_title'>贷方金额</div>),
            key: 'creditAmount',
            dataIndex: 'creditAmount',
            align: 'center', // 设置表头居中
            width: 220,
            children: [
                {
                    width: 220,
                    height: 100,
                    title: (<div className='custom-border th'>
                        <span>亿</span>
                        <span>千</span>
                        <span className='b'>百</span>
                        <span>十</span>
                        <span>万</span>
                        <span className='q'>千</span>
                        <span>百</span>
                        <span>十</span>
                        <span className='y'>元</span>
                        <span>角</span>
                        <span>分</span>
                    </div>),
                    dataIndex: 'creditAmount',
                    key: 'creditAmount',
                    align: 'center', // 设置表头居中
                    render: (text, record, _, action) => {
                        const isEditing = (record.key === editingKey && "creditAmount" === editingDataIndex);
                        const amount = formatAmount(record.creditAmount >= 0 ? record.creditAmount : -record.creditAmount);

                        return (
                            <>
                                {record.key > 0 && isEditing ? (
                                    <div onClick={() => handleCellClick(record, 'creditAmount')} className='dfje editing'>
                                        <InputNumber autoFocus
                                            ref={inputRef}
                                            onFocus={(e) => {
                                                // 将光标移到最后一个位置
                                                const value = e.target.value;
                                                e.target.setSelectionRange(0, value.length);
                                            }}
                                            placeholder='请输入贷方金额'
                                            formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                                            parser={(value) => value!.replace(/,/g, '')}
                                            defaultValue={Number(text).toFixed(2)}
                                            style={{ fontSize: 25 }}
                                            onChange={(e) => {
                                                record.debitAmount = 0;
                                                record.creditAmount = e;
                                                onValueChange(record, "creditAmount");
                                            }}
                                        ></InputNumber>
                                    </div>
                                ) : (
                                    <div onClick={() => handleCellClick(record, 'creditAmount')} className={record.creditAmount >= 0 ? 'custom-border td' : 'custom-border td red'}>
                                        <span>{amount[0]}</span>
                                        <span>{amount[1]}</span>
                                        <span className='b'>{amount[2]}</span>
                                        <span>{amount[3]}</span>
                                        <span>{amount[4]}</span>
                                        <span className='q'>{amount[5]}</span>
                                        <span>{amount[6]}</span>
                                        <span>{amount[7]}</span>
                                        <span className='y'>{amount[8]}</span>
                                        <span>{amount[9]}</span>
                                        <span>{amount[10]}</span>
                                    </div>
                                )}
                            </>
                        );
                    },
                    width: 190,
                }]
        },
        {
            title: <strong>操作</strong>,
            valueType: 'option',
            key: 'option',
            width: 80,
            render: (text, record, _, action) => {
                const isLastRow = _ === dataSource.length - 1;
                if (isLastRow) {
                    return;
                }
                return <div style={{ textAlign: 'center' }}>
                    <a rel="noopener noreferrer" key="editable" onClick={() => { handleAdd(record.key) }}>
                        新增
                    </a>
                    {record.key >= 3 && (
                        <span style={{ margin: '0 8px' }}>|</span>
                    )}
                    {record.key >= 3 && (
                        <Popconfirm title="确定要删除该科目？" onConfirm={() => handleDelete(record.key)}>
                            <a>删除</a>
                        </Popconfirm>
                    )}
                </div>
            }
        },
    ];

    const [dataSource, setDataSource] = useState<DataType[]>([

    ]);

    let [dataSourceRow, setDataSourceRow] = useState<DataType[]>([
        {
            key: 1,
            summary: '',
            subject: {
                showName: '',
                subjectCalculateConfigVo: {
                    enableNumberCalculateConfig: false,//是否启用数量核算
                    enableForeignCurrencyConfig: false,//是否启用外币核算
                    enableAssistCalculateConfigs: false,//是否启用辅助核算
                    selectAssistCalculateConfigs: []
                }
            },
            debitAmount: 0,
            creditAmount: 0,
            subjectBalance: 0,
            newSubjectBalance: 0
        },
        {
            key: 2,
            summary: '',
            subject: {
                showName: '',
                subjectCalculateConfigVo: {
                    enableNumberCalculateConfig: false,//是否启用数量核算
                    enableForeignCurrencyConfig: false,//是否启用外币核算
                    enableAssistCalculateConfigs: false,//是否启用辅助核算
                    selectAssistCalculateConfigs: []
                }
            },
            debitAmount: 0,
            creditAmount: 0,
            subjectBalance: 0,
            newSubjectBalance: 0
        }
    ]);

    // 在需要深拷贝的地方使用 cloneDeep 函数
    const clonedDataSourceRow = _.cloneDeep(dataSourceRow);

    const [dataSourceFootRow, setDataSourceFootRow] = useState<DataType[]>([
    ]);


    const totalDataSource = (dataRow: any) => {
        const totalDebitAmount = dataRow.reduce((sum, item) => sum + (Number(item.debitAmount) || 0), 0);
        const totalCreditAmount = dataRow.reduce((sum, item) => sum + (Number(item.creditAmount) || 0), 0);
        const chineseSummary = digitUppercase(totalDebitAmount);
        const footDataRow = {
            key: 0,
            summary: chineseSummary,
            subject: {
                id: 1,//科目id
                rKey: 0,
                code: '',//科目编码
                name: '',//科目名称
                showName: '库存现金',//显示的科目名称
                tmpShowName: '',//修改前的显示的科目名称

                subjectCalculateConfigVo: {
                    enableNumberCalculateConfig: true,//是否启用数量核算
                    enableForeignCurrencyConfig: true,//是否启用外币核算
                    enableAssistCalculateConfigs: true,//是否启用辅助核算
                    selectForeignCurrencyConfig: {
                        id: 1,
                        name: '美金',
                        exchangeRate: 6.8,
                        originalCurrency: 0
                    },
                    selectNumberCalculateConfig: {
                        num: 1, // 数量
                        price: 88, //单价
                        totalPrice: 88//总价
                    },
                    selectAssistCalculateConfigs: [
                    ],
                    foreignCurrencyConfig: //外币核算配置
                        [{
                            id: 1,
                            name: '美金',
                            exchangeRate: 6.8
                        }]
                    ,
                    assistCalculateConfigs: [{
                        id: 1,//辅助核算类别id
                        name: '供应商',//辅助核算名称
                        requiredFlag: true//是否必填
                    },
                    {
                        id: 2,//辅助核算类别id
                        name: '职员',//辅助核算名称
                        requiredFlag: true//是否必填
                    }]
                }
            },
            debitAmount: totalDebitAmount,
            creditAmount: totalCreditAmount
        };
        setDataSourceFootRow(footDataRow);
        setDataSource([...dataRow, footDataRow]);
    }
    //新增凭证明细
    const handleAdd = (targetKey: number) => {
        const index = dataSourceRow.findIndex(item => item.key === targetKey);
        if (index !== -1) {
            const newData = [...dataSourceRow];
            const newKey = Number(targetKey) + 1;

            // 更新插入位置后的所有行的key
            for (let i = index + 1; i < newData.length; i++) {
                newData[i].key = Number(newData[i].key) + 1;
            }

            newData.splice(index + 1, 0, {
                key: newKey,
                rKey: 0,
                summary: '',
                subject: {
                    subjectCalculateConfigVo: {
                        enableNumberCalculateConfig: false,//是否启用数量核算
                        enableForeignCurrencyConfig: false,//是否启用外币核算
                        enableAssistCalculateConfigs: false,//是否启用辅助核算
                        selectAssistCalculateConfigs: []
                    }
                },
                debitAmount: 0,
                creditAmount: 0
            });
            setDataSourceRow(newData);
            totalDataSource(newData);
        }
    };

    const handleDelete = (key: React.Key) => {
        const newData = dataSourceRow.filter((item) => item.key !== key);
        if (key <= 2) {
            message.error("不能删除");
            return;
        }
        // 更新插入位置后的所有行的key
        for (let i = 0; i < newData.length; i++) {
            newData[i].key = i + 1;
        }
        setDataSourceRow(newData);
        totalDataSource(newData);
    };
    const handleCellClick = (record: DataType, dataIndex: string) => {
        if (record.key === editingKey && dataIndex === editingDataIndex) {
            return;
        }
        if (isOperate) {
            message.success("请确认会计科目");
            return;
        }
        if (record.key === 0) {
            return;
        }
        if (dataIndex === "subject") {
            setIsOperate(true);
            if (record.subject.id) {
                getSubjectDetail({ "id": record.subject.id }).then(response => {
                    dataSourceRow.map((item, index) => {
                        if (record.subject.id === item.subject.id) {
                            item.subject.subjectCalculateConfigVo.foreignCurrencyConfig = response.data.subjectCalculateConfigVo.foreignCurrencyConfig;
                            item.subject.subjectCalculateConfigVo.assistCalculateConfigs = response.data.subjectCalculateConfigVo.assistCalculateConfigs;
                        }
                    });
                    totalDataSource(dataSourceRow);
                });
            }
        }

        setEditingKey(record.key);
        setEditingDataIndex(dataIndex);
    };
    // 取消编辑的处理函数
    const cancelEdit = (record: any) => {
        setEditingKey('');
        setEditingDataIndex('');
        setIsOperate(false);
        totalDataSource(dataSourceRow);
    };
    const saveEditContent = (key, dataIndex, value) => {
        // 在这里执行保存逻辑，例如更新数据源等操作
        const newData = dataSourceRow.map((item) => {
            if (item.key === key) {
                return { ...item, [dataIndex]: value };
            }
            return item;
        });
        setDataSourceRow(newData);
        totalDataSource(newData);
    };
    //创建凭证
    const createVoucher = async (type: number) => {
        if (isOperate) {
            message.error("请确认科目");
            return;
        }
        let params = {
            id: p.id,
            voucherWordConfigId: defaultSelectVoucherWordConfigValue.value,//凭证字
            voucherNumber: voucherNumberValue,//凭证号
            voucherDate: voucherDateValue,//凭证日期
            documentNum: documentNumValue,//单据数量
            notes: notesValule,//备注
            totalAmount: dataSourceFootRow.debitAmount,//总金额
            voucherSubjectDetailFormList: [],//科目明细
            voucherSubjectAssistCalculateDetailFormList: [],//辅助核算明细
            fileIds: selectFileIds
        };
        if (!defaultSelectVoucherWordConfigValue.value) {
            message.error("请选择凭证字");
            return;
        }
        if (!voucherNumberValue) {
            message.error("请输入凭证号");
            return;
        }
        if (!voucherDateValue) {
            message.error("请输入凭证日期");
            return;
        }
        try {
            dataSourceRow.forEach((item, index) => {
                const rowNo = index + 1;
                if (!item.summary) {
                    throw new Error("第" + rowNo + "行，请输入摘要！");
                }
                if (!item.subject.id) {
                    throw new Error("第" + rowNo + "行，请选择会计科目！");
                }
                if (!item.debitAmount && !item.creditAmount) {
                    throw new Error("第" + rowNo + "行，请输入金额！");
                }
                let amount = 0;
                //如果启用了数量核算
                if (item.subject.subjectCalculateConfigVo.enableNumberCalculateConfig) {
                    if (!item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num ||
                        Number(item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num) <= 0) {
                        throw new Error("第" + rowNo + "行，请输入数量且不能小于或等于0！");
                    }
                    if (!item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price ||
                        Number(item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price) === 0) {
                        throw new Error("第" + rowNo + "行，请输入单价且不能等于0！");
                    }
                    amount = Number(item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price) *
                        Number(item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num);
                    if (item.debitAmount && item.debitAmount !== amount) {
                        throw new Error("第" + rowNo + "行，数量*单价和金额不匹配！");
                    }
                    if (item.creditAmount && item.creditAmount !== amount) {
                        throw new Error("第" + rowNo + "行，数量*单价和金额不匹配！");
                    }
                    //如果启用了外币核算
                    if (item.subject.subjectCalculateConfigVo.enableForeignCurrencyConfig) {
                        if (item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate * item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency
                            !== Number(item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price)) {
                            throw new Error("第" + rowNo + "行，汇率*原币和单价不匹配！");
                        }
                    }
                }
                else {
                    item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig = {
                        num: null,
                        price: null
                    }
                }

                //如果启用了外币核算
                if (item.subject.subjectCalculateConfigVo.enableForeignCurrencyConfig) {
                    if (!item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate ||
                        Number(item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate) <= 0) {
                        throw new Error("第" + rowNo + "行，请输入汇率且不能小于或等于0！");
                    }
                    if (item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.baseCurrencyFlag
                        && Number(item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate) !== 1) {
                        throw new Error("第" + rowNo + "行，本位币汇率必须为1！");
                    }
                    //如果启用了数量核算
                    if (item.subject.subjectCalculateConfigVo.enableNumberCalculateConfig) {
                        if (item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate * item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency
                            !== Number(item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price)) {
                            throw new Error("第" + rowNo + "行，汇率*原币和单价不匹配！");
                        }
                    }
                    else {
                        amount = Number(item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate) *
                            Number(item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency);
                        if (item.debitAmount && item.debitAmount !== amount) {
                            throw new Error("第" + rowNo + "行，汇率*原币和金额不匹配！");
                        }
                        if (item.creditAmount && item.creditAmount !== amount) {
                            throw new Error("第" + rowNo + "行，汇率*原币和金额不匹配！");
                        }
                    }
                }
                else {
                    item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig = {
                        id: null,
                        exchangeRate: null,
                        originalCurrency: null
                    }
                }
                const voucherSubjectDetailForm = {
                    rowNo: index + 1,
                    subjectId: item.subject.id,//科目id
                    summary: item.summary,//摘要
                    currencyConfigId: item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.id,//币别id
                    exchangeRate: item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.exchangeRate,//汇率
                    originalCurrency: item.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig.originalCurrency,//原币
                    subjectNum: item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.num,//科目对应的数量
                    subjectUnitPrice: item.subject.subjectCalculateConfigVo.selectNumberCalculateConfig.price,//科目对应的单价数量
                    balance: item.subject.balance,//余额
                    debitAmount: item.debitAmount,//借方金额
                    creditAmount: item.creditAmount,//贷方金额
                };

                params.voucherSubjectDetailFormList.push(voucherSubjectDetailForm);

                //设置辅助核算明细
                if (item.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs) {
                    item.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs.map((item2, index2) => {
                        if (item2) {
                            const voucherSubjectAssistCalculateDetailForm = {
                                rowNo: voucherSubjectDetailForm.rowNo,
                                subjectId: item.subject.id,//科目id
                                assistCalculateCateId: item2.assistCalculateCateId,//辅助核算类别id
                                assistCalculateId: item2.assistCalculateId,//辅助核算id
                            }
                            params.voucherSubjectAssistCalculateDetailFormList.push(voucherSubjectAssistCalculateDetailForm);
                        }
                    });
                }

            });

            if (dataSourceFootRow.debitAmount !== dataSourceFootRow.creditAmount) {
                message.error("借代不平衡");
                return;
            }

            await save(params).then(response => {
                setIsSubmit(true);
                if (response.data) {
                    message.success("保存成功");
                    history.push("/Voucher/VoucherList");
                    // //保存并新增
                    // if (type === 0) {
                    //     setTimeout(() => {
                    //     }, 1000); // 延迟1秒后刷新页面
                    // }
                }
                else {
                    message.error("创建失败");
                }
            }).catch(error => {
            }).finally(() => {
                setIsSubmit(false);
            });
        } catch (error) {
            console.log(error)
            message.error(error.message);
        }
    };
    const handleFormatter = (value: string) => {
        if (value === '') {
            return '';
        }

        const numericValue = Number(value);

        if (isNaN(numericValue)) {
            return '';
        }

        return numericValue.toFixed(2);
    };

    const handleParser = (value) => {
        // 移除非数字字符
        const numericValue = value.replace(/[^0-9.]/g, '');

        // 转换为数字
        return parseFloat(numericValue);
    };

    const formatAmount = (amount: number) => {
        if (amount === 0 || amount === "") {
            return ["", "", "", "", "", "", "", "", "", "", ""]
        }
        const numericAmount = Number(amount);
        if (isNaN(numericAmount)) {
            console.error("Invalid amount. Please provide a valid number.");
            return ["", "", "", "", "", "", "", "", "", "", ""]
        }
        const formattedAmount = numericAmount.toFixed(2); // 保留两位小数
        const amountArray = formattedAmount.replace(".", "").split('');
        const newAmountArray = Array(11).fill('');

        // 从最后一个元素开始填充
        for (let i = amountArray.length - 1; i >= 0; i--) {
            newAmountArray[newAmountArray.length - amountArray.length + i] = amountArray[i];
        }

        // 遍历索引
        return newAmountArray;
    };

    //获取凭证字列表
    const fetchVoucherWordConfigData = async () => {
        try {
            await listVoucherWordConfig().then(response => {
                if (response.data) {
                    const dataArray = response.data.map(data => ({
                        value: data.id,
                        label: data.voucherWord
                    }));
                    setVoucherWordConfigOptions(dataArray);
                    if (dataArray.length > 0) {
                        setDefaultSelectVoucherWordConfigValue(dataArray[0])
                    }
                }
            }).catch(error => {
            });
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };
    //获取凭证字列表
    const fetchCurrencyConfigData = async () => {
        try {
            await listCurrencyConfig().then(response => {
                if (response.data) {
                    const dataArray = response.data.map(data => ({
                        value: data.code,
                        label: data.name
                    }));
                    setCurrencyConfigOptions(dataArray);
                    if (dataArray.length > 0) {
                        // setDefaultSelectVoucherWordConfigValue(dataArray[0].label)
                    }
                }
            }).catch(error => {
            });
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };
    const handleOkSetNotes = () => {
        setOriginalNotesValue(notesValule);
        setIsModalOpen(false);
    }
    const handleCancelSetNotes = () => {
        // 点击取消按钮时恢复原来的值
        setNotesValule(originalNotesValue);
        setIsModalOpen(false);
    }
    const getNickName = async () => {
        await currentUser().then((response) => {
            setNickName(response.data.name);
        }).catch(e => {
            console.log(e);
        })
    }
    const onOpenFileModal = () => {
        setIsFileModalOpen(true);
    }
    const onCloseFileModal = () => {
        setIsFileModalOpen(false);
    }
    const onOkFileModal = () => {
        // 从 FileIndex 获取参数
        setIsFileModalOpen(false);
        setSelectFileIds(selectTempFileIds);
        console.log('FileIndex params:', selectTempFileIds);
    }
    const handleFileIndexChange = (params: any) => {
        setSelectTempFileIds(params);
    };
    // 在组件加载时调用接口
    useEffect(() => {
        if (p.id === 0 && p.copyId === 0) {
            let newDataSourceRow: any = [];
            newDataSourceRow.push([
                {
                    key: 1,
                    summary: '',
                    subject: {
                        subjectCalculateConfigVo: {
                            enableNumberCalculateConfig: false,//是否启用数量核算
                            enableForeignCurrencyConfig: false,//是否启用外币核算
                            enableAssistCalculateConfigs: false,//是否启用辅助核算
                            selectAssistCalculateConfigs: []
                        },
                        showName: '',
                    },
                    debitAmount: 0,
                    creditAmount: 0
                },
                {
                    key: 2,
                    summary: '',
                    subject: {
                        subjectCalculateConfigVo: {
                            enableNumberCalculateConfig: false,//是否启用数量核算
                            enableForeignCurrencyConfig: false,//是否启用外币核算
                            enableAssistCalculateConfigs: false,//是否启用辅助核算
                            selectAssistCalculateConfigs: []
                        },
                        showName: '',
                    },
                    debitAmount: 0,
                    creditAmount: 0
                }
            ]);
            //setDataSourceRow(newDataSourceRow);
            totalDataSource(dataSourceRow);
            fetchVoucherWordConfigData();
            fetchCurrencyConfigData();
            getNickName();
            return;
        }
        else {
            const id = p.id > 0 ? p.id : p.copyId;
            get({ id: id }).then((response) => {
                let newDataSourceRow: any = [];
                response.data.voucherSubjectDetailVoList.map((item, index) => {
                    let row = {
                        key: index + 1,
                        summary: item.summary,//摘要
                        subject: {
                            id: item.subjectId,
                            code: item.subjectCode,
                            name: item.subjectName,
                            fullName: item.subjectFullName,
                            showName: item.showSubjectName,
                            tmpShowName: item.showSubjectName,
                            //辅助核算配置
                            subjectCalculateConfigVo: {
                                enableNumberCalculateConfig: item.enableNumberCalculateConfig,//是否启用数量核算
                                enableForeignCurrencyConfig: item.enableForeignCurrencyConfig,//是否启用外币核算
                                enableAssistCalculateConfigs: item.enableAssistCalculateConfigs,//是否启用辅助核算
                                selectAssistCalculateConfigs: item.assistCalculateConfigs//选择的辅助核算
                            }
                        },
                        subjectBalance: item.balance,
                        newSubjectBalance: item.balance,
                        debitAmount: item.debitAmount,
                        creditAmount: item.creditAmount
                    };
                    if (item.enableForeignCurrencyConfig) {
                        row.subject.subjectCalculateConfigVo.foreignCurrencyConfig = item.foreignCurrencyConfig;
                        row.subject.subjectCalculateConfigVo.selectForeignCurrencyConfig = {
                            id: item.currencyConfigId,
                            name: item.currencyConfigName,
                            originalCurrency: item.originalCurrency,
                            exchangeRate: item.exchangeRate,
                            baseCurrencyFlag: item.foreignCurrencyConfig[0].baseCurrencyFlag
                        };
                    }

                    if (item.enableNumberCalculateConfig) {
                        row.subject.subjectCalculateConfigVo.selectNumberCalculateConfig = {
                            num: item.subjectNum, // 数量
                            price: item.subjectUnitPrice, //单价
                            totalPrice: item.debitAmount ? item.debitAmount : item.creditAmount//总价
                        }
                    }


                    // row.subject.subjectCalculateConfigVo.selectAssistCalculateOptions
                    // // if (row.subject.subjectCalculateConfigVo.enableAssistCalculateConfigs) {
                    // //     row.subject.subjectCalculateConfigVo.selectAssistCalculateConfigs = item.assistCalculateConfigs;
                    // // }
                    setOriginalNotesValue(response.data.notes);
                    setNotesValule(response.data.notes);
                    newDataSourceRow.push(row);
                })
                setNickName(response.data.memberName);
                //设置凭证字
                setDefaultSelectVoucherWordConfigValue({
                    value: response.data.voucherWordConfigId,
                    label: response.data.voucherWord
                });
                //设置凭证号
                setVoucherNumberValue(response.data.voucherNumber);
                // 将时间戳转换为日期字符串
                setVoucherDateValue(response.data.voucherDate);
                setDocumentNumValue(response.data.documentNum);
                setSelectFileIds(response.data.fileIds);
                setDataSourceRow(newDataSourceRow);
                totalDataSource(newDataSourceRow);
                fetchVoucherWordConfigData();
                fetchCurrencyConfigData();
            });
        }
    }, []);
    return (
        <Card title={
            <Space>
                <Button type='primary'
                    disabled={isSubmit}
                    onClick={() => {
                        createVoucher(0);
                    }}>
                    {p.id ? '修改凭证' : '创建凭证'}
                </Button>
            </Space>
        }>

            <ConfigProvider
                theme={{
                    components: {
                        Table: {
                            cellFontSize: 5,
                            cellPaddingBlock: 5,
                            borderColor: '#c0c0c0'
                        },
                    },
                }}
            >
                <Table
                    className="voucher"
                    columns={columns}
                    dataSource={dataSource}
                    bordered size="small"
                    pagination={false}
                    title={() => {
                        return <>
                            <Flex gap="middle">
                                <Form
                                    initialValues={{ layout: 'inline' }}
                                    layout='inline'
                                >

                                    <Form.Item>
                                        <Select
                                            labelInValue
                                            style={{ width: 70 }}
                                            value={defaultSelectVoucherWordConfigValue}
                                            options={voucherWordConfigOptions}
                                            onChange={(value) => setDefaultSelectVoucherWordConfigValue(value)}
                                        />
                                    </Form.Item>

                                    <Form.Item>
                                        <Input style={{ width: 100 }}
                                            value={voucherNumberValue}
                                            onChange={(event) => setVoucherNumberValue(event.target.value)}
                                            addonAfter="号"
                                        ></Input><div>
                                        </div>
                                    </Form.Item>
                                    <Form.Item
                                        label='日期'>
                                        <DatePicker style={{ width: 150 }}
                                            value={voucherDateValue ? moment(voucherDateValue, dateFormat) : null}
                                            onChange={(date) => setVoucherDateValue(date.format(dateFormat))}
                                        ></DatePicker>
                                    </Form.Item>
                                    <Form.Item
                                        label='附单据'>
                                        <Input style={{ width: 100 }}
                                            value={documentNumValue}
                                            addonAfter="张"
                                            onChange={(event) => setDocumentNumValue(event.target.value)}
                                        ></Input>
                                    </Form.Item>
                                    <Space>

                                        <Button icon={<UploadOutlined />} type="dashed" danger onClick={onOpenFileModal}>添加附件</Button>

                                        <Button icon={<EditOutlined />} onClick={() => {
                                            setIsModalOpen(true);
                                        }}>备注</Button>
                                        <Modal title="备注" open={isModalOpen} onOk={handleOkSetNotes} onCancel={handleCancelSetNotes}>
                                            <TextArea style={{ width: '100%' }}
                                                value={notesValule}
                                                onChange={(event) => setNotesValule(event.target.value)}
                                            ></TextArea>
                                        </Modal>
                                    </Space>
                                </Form>
                            </Flex>

                        </>
                    }}
                    footer={() => {
                        return <Flex>
                            <Space>制单人：{nickName}<Button icon={<EditOutlined />} title='修改制单人' onClick={() => { history.push('/System/PersonalInfo') }}></Button></Space>
                        </Flex>
                    }}
                />

            </ConfigProvider>

            <Modal title="选择文件" open={isFileModalOpen} onOk={onOkFileModal} onCancel={onCloseFileModal} width={'80%'} style={{ top: 20 }}>
                <FileIndex
                    p={{ fileRefType: 0, fileRefId: Number(p.id), fileIds: selectFileIds, onParamsChange: handleFileIndexChange }}
                // onParamsChange={handleFileIndexChange}
                />
            </Modal>
        </Card>
    );
};

export default Voucher;