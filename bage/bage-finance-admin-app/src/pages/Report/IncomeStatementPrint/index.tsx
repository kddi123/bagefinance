import { ExclamationCircleOutlined, PlusOutlined, SearchOutlined, InfoCircleOutlined, PrinterOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns, ProFormDatePicker, ProFormSelect, ProFormItem } from '@ant-design/pro-components';
import { ProTable, TableDropdown, ProFormText, ProForm } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Switch, Modal, message, Tag, Select, DatePicker, Drawer, ConfigProvider, Affix } from 'antd';

import React, { useState, useEffect, useRef } from 'react';
import request from 'umi-request';
import { getReport, saveFormula, delFormula, listReportFormula } from '@/services/ant-design-pro/reportIncomeStatement';
import { listByCateAndCodeAndName } from '@/services/ant-design-pro/subject';

import { Link } from '@umijs/max';
import './index.less'

const { RangePicker } = DatePicker;
import type { TableRowSelection } from 'antd/es/table/interface';
import moment from 'moment';

export default () => {
    const urlParams = new URL(window.location.href).searchParams;
    const urlParamMap = {};
    for (const [key, value] of urlParams.entries()) {
        urlParamMap[key] = value;
    }
    const actionRef = useRef<ActionType>();

    // 获取当前系统时间，并精确到月份的第一天
    const [defaultDate, setDefaultDate] = useState(urlParamMap["reportDate"]);
    const [isShowToolBar, setIsShowToolBar] = useState(true);
    const [top, setTop] = React.useState<number>(0);

    interface DataType {
        key: React.ReactNode;
        name: string;
        age: number;
        address: string;
        children?: DataType[];
    }
    const columns: ProColumns[] = [
        {
            title: '项目',
            dataIndex: 'project',
            ellipsis: true,
            width: 500,
            // tip: '标题过长会自动收缩',
            render: (text, record, _, action) => (
                <Space>
                    {record.fontBold ?
                        <span style={{ fontWeight: 'bold' }}>{record.project}</span>
                        :
                        <span style={{ marginLeft: record.tabCount * 20 }}> {record.project}</span>}
                </Space>
            ),
            search: false
        },
        {
            disable: true,
            title: '行次',
            dataIndex: 'rowNo',
            filters: true,
            ellipsis: true,
            search: false,
            render: (text, record, _, action) => (
                record.rowNo ? record.rowNo : ""
            ),
        },
        {
            disable: true,
            title: '本期余额',
            dataIndex: 'termEndBalance',
            filters: true,
            ellipsis: true,
            search: false,
            render: (text, record, _, action) => (
                <Space>
                    {
                        record.currentMonthTotalAmount ?
                            parseFloat(record.currentMonthTotalAmount).toFixed(2).toLocaleString() :
                            ""
                    }
                </Space>
            ), // 保留两位小数并格式化数字
        },
        {
            disable: true,
            title: '本年累计金额',
            dataIndex: 'currentYearTotalAmount',
            filters: true,
            ellipsis: true,
            search: false,
            render: (text, record, _, action) => (
                <Space>
                    {
                        record.currentYearTotalAmount ?
                            parseFloat(record.currentYearTotalAmount).toFixed(2).toLocaleString() :
                            ""
                    }
                </Space>
            ), // 保留两位小数并格式化数字
        },
    ];

    const rowClassName = (record, index) => {
        return index % 2 === 0 ? 'even-row' : 'odd-row';
    };
    const print = () => {
        setIsShowToolBar(false);
        setTimeout(() => {
            window.print();
        }, 100);
    }
    useEffect(() => {
        const handleBeforePrint = () => {
            setIsShowToolBar(false);
        };

        const handleAfterPrint = () => {
            setIsShowToolBar(true);
        };

        window.addEventListener('beforeprint', handleBeforePrint);
        window.addEventListener('afterprint', handleAfterPrint);

        return () => {
            window.removeEventListener('beforeprint', handleBeforePrint);
            window.removeEventListener('afterprint', handleAfterPrint);
        };
    }, []);
    return (
        <div>
            {isShowToolBar && (
                <Affix offsetTop={top}>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', backgroundColor: 'rgb(50,54,57)', height: 40 }}>
                        <Button icon={<PrinterOutlined />} onClick={() => print()} style={{ marginRight: 10 }}>
                            打印
                        </Button>
                    </div>
                </Affix>
            )}
            <div style={{ marginBottom: 10 }}>
                <Space direction="vertical">
                    <div style={{ width: '100%', height: 50 }}>
                        <div style={{ fontSize: 35, textAlign: 'center' }}>净利润表</div>
                        <div style={{ fontSize: 14, float: 'right' }}>会小企02表</div>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', height: 20, fontSize: 14 }}>
                        <div>单位：字节跳动</div>
                        <div>日期：{moment(defaultDate).format('YYYY-MM-DD')}</div>
                        <div>单位：元</div>
                    </div>
                    <ConfigProvider
                        componentSize="small" // 设置表格的尺寸为小尺寸
                        theme={{
                            components: {
                                Table: {
                                    // footerColor: 'white',
                                    // headerBorderRadius: 0,
                                    // cellFontSize: 5,
                                    // cellPaddingBlock: 5,
                                    // headerBg: '#bae0ff',
                                    // borderColor: 'black',
                                    // rowHoverBg: '#bae0ff'
                                },
                            },
                        }}
                    >
                        <ProTable<API.ListAccountBookVoItem, API.PageParams>
                            size="small" // 设置表格的尺寸为小尺寸
                            search={false}
                            columns={columns}
                            actionRef={actionRef}
                            rowClassName={rowClassName}
                            cardBordered
                            bordered
                            request={async (params = {}, sort, filter) => {
                                let data = await getReport({ reportDate: moment(defaultDate).format('YYYY-MM-DD 00:00:00') });
                                return { data: data.data.balanceRows };
                            }}
                            editable={{
                                type: 'multiple',
                            }}
                            columnsState={{
                                persistenceKey: 'pro-table-singe-demos',
                                persistenceType: 'localStorage',
                                onChange(value) {
                                    console.log('value: ', value);
                                },
                            }}
                            rowKey="rowId"
                            options={{
                                setting: {
                                    listsHeight: 400,
                                },
                            }}
                            dateFormatter="string"
                            headerTitle="净利润表"
                            pagination={false}
                            onReset={() => {

                            }}
                            toolBarRender={false}
                            expandable={{ defaultExpandAllRows: true }}
                        />
                    </ConfigProvider>
                </Space>
            </div>
        </div>
    );
};