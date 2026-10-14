import { ExclamationCircleOutlined, PlusOutlined, SearchOutlined, InfoCircleOutlined, PrinterOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns, ProFormDatePicker, ProFormSelect, ProFormItem } from '@ant-design/pro-components';
import { ProTable, TableDropdown, ProFormText, ProForm } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Switch, Modal, message, Tag, Select, DatePicker, Drawer, ConfigProvider } from 'antd';

import React, { useState, useEffect, useRef } from 'react';
import request from 'umi-request';
import { getReport, saveFormula, delFormula, listReportFormula } from '@/services/ant-design-pro/reportCashFlowStatement';
import { listByCateAndCodeAndName } from '@/services/ant-design-pro/subject';

import { Link } from '@umijs/max';
import './index.less'

const { RangePicker } = DatePicker;
import type { TableRowSelection } from 'antd/es/table/interface';
import moment from 'moment';

export default () => {
    const [openEditFormula, setOpenEditFormula] = useState(false);
    const [editRowName, setEditRowName] = useState("");
    const [editRowNo, setEditRowNo] = useState(0);
    const [editForm] = ProForm.useForm();
    const actionRef = useRef<ActionType>();
    const formulaRef = useRef<ActionType>();

    // 获取当前系统时间，并精确到月份的第一天
    const [defaultDate, setDefaultDate] = useState(moment().startOf('month'));
    const [mouseOverRowId, setMouseOverRowId] = useState(0);
    const [subjectOptions, setSubjectOptions] = useState([]);
    const [selectedSubjectId, setSelectedSubjectId] = useState(0);

    const onCloseFormula = async () => {
        setOpenEditFormula(false);
    }

    //搜索科目
    const onSearchSubject = (name: string) => {
        setSubjectOptions([]);
        listByCateAndCodeAndName({ codeAndName: name }).then(response => {
            const dataArray = response.data.map(data => ({
                value: data.id,
                label: data.code + "-" + data.fullName
            }));
            setSubjectOptions(dataArray);
        });
    }
    const handleSaveCashFlowStatementFormula = (record: any) => {
        saveFormula({ ...record, rowNo: editRowNo }).then(response => {
            message.success("保存成功");
            formulaRef.current?.reload();
            actionRef.current?.reload();
        }).catch((e) => {
            console.log(e);
        });
    }
    const handleDelCashFlowStatementFormula = (record: any) => {
        delFormula({ ...record, rowNo: editRowNo }).then(response => {
            message.success("保存成功");
            formulaRef.current?.reload();
            actionRef.current?.reload();
        }).catch((e) => {
            console.log(e);
        });
    }

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
                    {
                        record.updateFormulaFlag && record.rowId === mouseOverRowId ?
                            <Link to='#' onClick={() => {
                                setOpenEditFormula(true);
                                setEditRowName(record.project);
                                setEditRowNo(record.rowNo);
                                onSearchSubject("");
                                formulaRef.current?.reload();
                            }}>编辑公式</Link>
                            : ""
                    }
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
                    {
                        record.rowId === mouseOverRowId ?
                            <InfoCircleOutlined style={{ color: 'green', cursor: 'pointer', float: 'right' }} />
                            : ""
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
                    {
                        record.rowId === mouseOverRowId ?
                            <InfoCircleOutlined style={{ color: 'green', cursor: 'pointer', float: 'right' }} />
                            : ""
                    }
                </Space>
            ), // 保留两位小数并格式化数字
        },
    ];

    const formulaColumns: ProColumns[] = [
        {
            title: '科目名称',
            dataIndex: 'subjectName',
            ellipsis: true,
        },
        {
            title: '运算符号',
            dataIndex: 'operationalSymbol',
            ellipsis: true,
        },
        {
            title: '取数规则',
            dataIndex: 'readDataRule',
            ellipsis: true,
            // tip: '标题过长会自动收缩',
            render: (text, record, _, action) => (
                <Space>
                    {record.assetFontBold ?
                        <span style={{ fontWeight: 'bold' }}>{record.asset}</span>
                        :
                        <span style={{ marginLeft: record.assetTabCount * 20 }}> {record.asset}</span>}
                    {
                        record.assetType >= 0 && record.rowId === mouseOverRowId ?
                            <Link to='#' onClick={() => {
                                setOpenEditFormula(true);
                                setEditRowName(record.asset);
                                setEditRowNo(record.assetRowNo);
                                onSearchSubject("");
                                formulaRef.current?.reload();
                            }}>编辑公式</Link>
                            : ""
                    }
                </Space>
            ),
            search: false
        },
        {
            title: '本月累计',
            dataIndex: 'termEndBalance',
            ellipsis: true,
            // tip: '标题过长会自动收缩',
            render: (text, record, _, action) => (
                record.termEndBalance ?
                    parseFloat(record.termEndBalance).toFixed(2).toLocaleString() :
                    ""
            ),
            search: false
        },
        {
            title: '本年累计',
            dataIndex: 'yearBeginBalance',
            ellipsis: true,
            // tip: '标题过长会自动收缩',
            render: (text, record, _, action) => (
                record.yearBeginBalance ?
                    parseFloat(record.yearBeginBalance).toFixed(2).toLocaleString() :
                    ""
            ),
            search: false
        }
        ,
        {
            title: '操作',
            // dataIndex: 'yearBeginBalance',
            ellipsis: true,
            // tip: '标题过长会自动收缩',
            render: (text, record, _, action) => {
                const deleteLink =
                    <Link to="#" onClick={() => {
                        Modal.confirm({
                            title: <span style={{ fontWeight: 'bold' }}>删除账套</span>,
                            icon: <ExclamationCircleOutlined />,
                            content: '确认要删除科目“' + record.subjectName + '”吗？',
                            okText: '确认',
                            cancelText: '取消',
                            onOk: async () => {
                                await delFormula({ rowNo: editRowNo, subjectId: record.subjectId })
                                    .then(response => {
                                        formulaRef.current?.reload();
                                    })
                                    .catch(error => {
                                        console.log(error);
                                    });
                            }
                        });
                    }} style={{ color: 'red' }} rel="noopener noreferrer" key="del">
                        删除
                    </Link>

                return [
                    deleteLink
                ]
            },
            search: false
        }
    ];

    const rowClassName = (record, index) => {
        return index % 2 === 0 ? 'even-row' : 'odd-row';
    };
    const handleSearch = async (value) => {
        message.success(value)
    };

    return (
        <PageContainer>
            <ConfigProvider
                theme={{
                    components: {
                        Table: {
                            // footerColor: 'white',
                            // headerBorderRadius: 0,
                            // cellFontSize: 5,
                            // cellPaddingBlock: 5,
                            headerBg: '#bae0ff',
                            // borderColor: 'black',
                            rowHoverBg: '#bae0ff'
                        },
                    },
                }}
            >
                <ProTable<API.ListAccountBookVoItem, API.PageParams>
                    size={'small'}
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
                    headerTitle="现金流表"
                    pagination={false}
                    onReset={() => {

                    }}
                    toolBarRender={() => [
                        <DatePicker defaultValue={defaultDate} picker="month" onChange={(d) => { setDefaultDate(d); }} />,
                        <Button key="button" icon={<SearchOutlined />} type="primary" onClick={() => { actionRef.current?.reload() }}>
                            查看报表
                        </Button>,
                        <Link key="button" to={"/report/CashFlowStatementPrint?reportDate=" + moment(defaultDate).format('YYYY-MM-DD')}
                            target="_blank">
                            打印
                        </Link>
                    ]}
                    onRow={(record) => {
                        return {
                            onClick: (event) => { }, // 点击行
                            onDoubleClick: (event) => { },
                            onContextMenu: (event) => { },
                            onMouseEnter: (event) => {
                                setMouseOverRowId(record.rowId)
                            }, // 鼠标移入行
                            onMouseLeave: (event) => {
                                setMouseOverRowId(0);
                            },// 鼠标移出
                        };
                    }}
                />
            </ConfigProvider>
            <Drawer title={"编辑公式-" + editRowName} placement="right" onClose={onCloseFormula} open={openEditFormula} width={1000}>
                <ProForm
                    form={editForm}
                    initialValues={{
                        hideInMenu: false,
                        layout: "horizontal"
                    }}
                    layout='horizontal' // 设置为垂直布局
                    onFinish={handleSaveCashFlowStatementFormula}
                    // labelCol={{ span: 6 }}   // 控制 label 的布局，可以调整 span 的值
                    // wrapperCol={{ span: 6 }} // 控制包裹内容的布局，可以调整 span 的值
                    submitter={false}
                >
                    <Space>
                        <ProFormItem
                            label="科目名称"
                            width="md"
                            name="subjectId" // 确保与 Select 组件的 name 属性匹配
                            rules={[{ required: true, message: '请选择科目' }]} // 添加必填规则
                        >
                            <Select
                                style={{ width: 200 }}
                                placeholder="请选择科目"
                                showSearch={true}
                                filterOption={false}
                                onFocus={() => { onSearchSubject(""); }}
                                onSearch={(value: string) => { onSearchSubject(value); }}
                                onChange={(value) => { setSelectedSubjectId(value ? value.value : ""); }} // 添加此处，更新选中的租户管理员的值
                                options={subjectOptions}
                            />
                        </ProFormItem>

                        <ProFormSelect
                            width="sm"
                            name="operationalSymbol"
                            label="运算符"
                            placeholder="请选择取数规则"
                            fieldProps={{
                                // size: 'large',
                            }}
                            options={[
                                {
                                    value: "+",
                                    label: '+',
                                },
                                {
                                    value: "-",
                                    label: '-',
                                }
                            ]}
                            rules={[{ required: true }]}
                        />
                        <ProFormSelect
                            width="sm"
                            name="readDataRule"
                            label="取数规则"
                            placeholder="请选择取数规则"
                            fieldProps={{
                                // size: 'large',
                            }}
                            options={[
                                {
                                    value: 0,
                                    label: '发生额',
                                },
                            ]}
                            rules={[{ required: true }]}
                        />

                        <Button type="primary" onClick={() => editForm?.submit?.()} style={{ marginBottom: 25 }}>
                            添加
                        </Button>
                    </Space>
                </ProForm>
                <ProTable<API.ListAccountBookVoItem, API.PageParams>
                    size={'small'}
                    search={false}
                    columns={formulaColumns}
                    actionRef={formulaRef}
                    rowClassName={rowClassName}
                    cardBordered
                    bordered
                    request={async (params = {}, sort, filter) => {
                        let data = await listReportFormula({ reportDate: moment(defaultDate).format('YYYY-MM-DD 00:00:00'), rowNo: editRowNo });
                        return { data: data.data };
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
                    rowKey="id"
                    options={{
                        setting: {
                            listsHeight: 400,
                        },
                    }}
                    dateFormatter="string"
                    headerTitle="资产负债表"
                    pagination={false}
                    onReset={() => {

                    }}
                    toolBarRender={false}
                    onRow={(record) => {
                        return {
                            onClick: (event) => { }, // 点击行
                            onDoubleClick: (event) => { },
                            onContextMenu: (event) => { },
                            onMouseEnter: (event) => {
                                setMouseOverRowId(record.rowId)
                            }, // 鼠标移入行
                            onMouseLeave: (event) => {
                                setMouseOverRowId(0);
                            },// 鼠标移出
                        };
                    }}
                />
            </Drawer>
        </PageContainer >
    );
};