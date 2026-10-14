import { PageContainer } from '@ant-design/pro-components';
import { Card, Button, Input, Switch, message, Space, Modal, Drawer } from 'antd';
import React, { useState, useEffect, useRef } from 'react';
import { PlusOutlined } from '@ant-design/icons';
const { Search } = Input;

import { listAssistCalculateCate, getAssistCalculateCateDetail, del as delAssistCalculateCate } from '@/services/ant-design-pro/assistCalculateCate';
import {
    del, updateDisable,
    listCustom, listCustomer, listDepartment, listEmployee, listInventory, listProject, listSupplier, listCashFlow


} from '@/services/ant-design-pro/assistCalculateSummary';


import { ProTable, ProColumns, ActionType } from '@ant-design/pro-components';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import { Link } from '@umijs/max';

import AssistCalculateCustomerCreate from '@/pages/AssistCalculateCustomer/Create';
import AssistCalculateCustomerDetail from '@/pages/AssistCalculateCustomer/Detail';

import AssistCalculateDepartmentCreate from '@/pages/AssistCalculateDepartment/Create';
import AssistCalculateDepartmentDetail from '@/pages/AssistCalculateDepartment/Detail';

import AssistCalculateEmployeeCreate from '@/pages/AssistCalculateEmployee/Create';
import AssistCalculateEmployeeDetail from '@/pages/AssistCalculateEmployee/Detail';


import AssistCalculateInventoryCreate from '@/pages/AssistCalculateInventory/Create';
import AssistCalculateInventoryDetail from '@/pages/AssistCalculateInventory/Detail';

import AssistCalculateProjectCreate from '@/pages/AssistCalculateProject/Create';
import AssistCalculateProjectDetail from '@/pages/AssistCalculateProject/Detail';

import AssistCalculateSupplierCreate from '@/pages/AssistCalculateSupplier/Create';
import AssistCalculateSupplierDetail from '@/pages/AssistCalculateSupplier/Detail';

import AssistCalculateCustomCreate from '@/pages/AssistCalculateCustom/Create';
import AssistCalculateCustomDetail from '@/pages/AssistCalculateCustom/Detail';

import AssistCalculateCateCreate from '@/pages/AssistCalculateCate/Create';
import AssistCalculateCateDetail from '@/pages/AssistCalculateCate/Detail';


const AssistCalulateList: React.FC = () => {
    const urlParams = new URL(window.location.href).searchParams;
    const urlParamMap: Record<string, string> = {};
    for (const [key, value] of urlParams.entries()) {
        urlParamMap[key] = value;
    }

    const [, setAssistCalculateCateColumnData] = useState<ProColumns[]>();
    const [searchText, setSearchText] = useState<string>('');


    const cateActionRef = useRef<ActionType>();
    const assistCalulateListActionRef = useRef<ActionType>();


    const [isOpenDataPanel, setIsOpenDataPanel] = useState<boolean>();
    const [cateCode, setCateCode] = useState<string>('');
    const [cateId, setCateId] = useState<number>(0);
    const [listData, setListData] = useState<React.ReactNode>(null);
    const [panelTitle, setPanelTitle] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalOpenData, setModalOpenData] = useState<React.ReactNode>(null);

    let onUpdateDisable: (record: any) => Promise<void>;
    let onEdit: (id: number) => void;
    let onCreate: () => void;
    let updateIsModalOpenState: (state: boolean) => void;


    const onGetAssistCalculateCateDetail = async (id: number) => {
        return await getAssistCalculateCateDetail({ id }).then(response => {
            if (response.data) {
                let columnData = response.data.customerColumnConfigList.map((column: any) => ({
                    title: column.columnAlias,
                    dataIndex: column.columnName
                }
                ));
                columnData = columnData.concat([
                    {
                        disable: true,
                        title: '是否启用',
                        dataIndex: 'disable',
                        filters: true,
                        ellipsis: true,
                        search: false,
                        render: (text: any, record: any) => (
                            <Space>
                                <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={() => onUpdateDisable(record)} />
                            </Space>
                        ),
                    },
                    {
                        title: '操作',
                        valueType: 'option',
                        key: 'option',
                        render: (text: any, record: any, _: any, action: any) => {
                            const delLink = (
                                <Link to='#' style={{ color: 'red' }} rel="noopener noreferrer" key="del"
                                    onClick={() => {
                                        Modal.confirm({
                                            title: <span style={{ fontWeight: 'bold' }}>删除辅助核算</span>,
                                            icon: <ExclamationCircleOutlined />,
                                            content: '确认要删除辅助核算“' + record.name + '”吗？',
                                            okText: '确认',
                                            cancelText: '取消',
                                            onOk: async () => {
                                                console.log("删除：" + record.id);
                                                await del({ id: record.id })
                                                    .then(() => {
                                                        action?.reload();
                                                    })
                                                    .catch(error => {
                                                        console.log(error);
                                                    });
                                            }
                                        });
                                    }}
                                >
                                    删除
                                </Link>
                            )
                            return [
                                <Link to={"#"} key="edit"
                                    onClick={() => { onEdit(record.id) }}
                                >
                                    编辑
                                </Link>,
                                delLink
                                ,
                            ]
                        }
                    }]);
                setAssistCalculateCateColumnData(columnData);
                console.log("assistCalculateCateColumnData：" + JSON.stringify(columnData));
                return columnData;
            }
        }).catch(error => {
            console.log(error);
        });
    }

    onUpdateDisable = async (record: any) => {
        try {
            // 替换成实际的接口地址
            await updateDisable({ id: record.id, disable: !record.disable }).then(response => {
                let op = "";
                if (!record.disable) {
                    op = "禁用";
                }
                else {
                    op = "启用";
                }
                if (response.data) {
                    message.success(op + "成功");
                }
                else {
                    message.error(op + "失败");
                }
            }).catch(error => {
                console.log(error);
            });
        } catch (error) {
            console.error(error);
        }
        finally {
            if (assistCalulateListActionRef.current) {
                assistCalulateListActionRef.current.reload();
            }
        }
    }

    //客户字段
    const customer = () => (
        <ProTable
            bordered
            columns={[
                {
                    title: '客户编码',
                    dataIndex: 'code',
                    ellipsis: true,
                    // tip: '标题过长会自动收缩',

                },
                {
                    disable: true,
                    title: '客户名称',
                    dataIndex: 'name',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '助记码',
                    dataIndex: 'mnemonicCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '客户类别',
                    dataIndex: 'customerCate',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '经营地址',
                    dataIndex: 'address',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '联系人',
                    dataIndex: 'contacts',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '手机',
                    dataIndex: 'phone',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '税号',
                    dataIndex: 'unifiedSocialCreditCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '是否启用',
                    dataIndex: 'disable',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    render: (text: any, record: any) => (
                        <Space>
                            <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={() => onUpdateDisable(record)} />
                        </Space>
                    ),
                },
                {
                    disable: true,
                    title: '备注',
                    dataIndex: 'notes',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    title: '操作',
                    valueType: 'option',
                    key: 'option',
                    render: (text: any, record: any, _: any, action: any) => {
                        const delLink = (
                            <Link to='#' style={{ color: 'red' }} rel="noopener noreferrer" key="del"
                                onClick={() => {
                                    Modal.confirm({
                                        title: <span style={{ fontWeight: 'bold' }}>删除辅助核算</span>,
                                        icon: <ExclamationCircleOutlined />,
                                        content: '确认要删除辅助核算“' + record.name + '”吗？',
                                        okText: '确认',
                                        cancelText: '取消',
                                        onOk: async () => {
                                            console.log("删除：" + record.id);
                                            await del({ id: record.id })
                                                .then(() => {
                                                    action?.reload();
                                                })
                                                .catch(error => {
                                                    console.log(error);
                                                });
                                        }
                                    });
                                }}
                            >
                                删除
                            </Link>
                        )
                        return [
                            <Link to={"#"} key="edit"
                                onClick={() => { onEdit(record.id) }}
                            >
                                编辑
                            </Link>,
                            delLink
                            ,
                        ]
                    }
                }
            ]}
            actionRef={assistCalulateListActionRef}
            // data={response.data?.list}
            // total={response.data?.total}
            request={async (params = {}) => {
                let data = await listCustomer({ ...params, assistCalculateCateId: cateId, codeOrName: searchText, pageNum: params.current });
                return { data: data.data?.list, total: data.data?.total };
            }}
            pagination={{
                pageSize: 20,
                onChange: (page) => console.log(page),
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
            search={false}

            form={{
                // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                syncToUrl: (values, type) => {
                    if (type === 'get') {
                        return {
                            ...values,
                        };
                    }
                    return values;
                },
            }}
            dateFormatter="string"
            headerTitle={
                <Search placeholder="请输入客户编码或名称" defaultValue={searchText} onSearch={(value) => {
                    setSearchText(value);
                    if (assistCalulateListActionRef.current) {
                        assistCalulateListActionRef.current.reload();
                    }
                }} enterButton />
            }
            toolBarRender={() => [
                <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => {
                    onCreate()
                }}>
                    新建
                </Button>
            ]}
        />
    )

    //供应商字段
    const supplier = () => (
        <ProTable
            bordered
            columns={[
                {
                    title: '供应商编码',
                    dataIndex: 'code',
                    ellipsis: true,
                    // tip: '标题过长会自动收缩',

                },
                {
                    disable: true,
                    title: '供应商名称',
                    dataIndex: 'name',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '助记码',
                    dataIndex: 'mnemonicCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '供应商类别',
                    dataIndex: 'supplierCate',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '经营地址',
                    dataIndex: 'address',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '联系人',
                    dataIndex: 'contacts',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '手机',
                    dataIndex: 'phone',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '税号',
                    dataIndex: 'unifiedSocialCreditCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '是否启用',
                    dataIndex: 'disable',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    render: (text: any, record: any) => (
                        <Space>
                            <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={() => onUpdateDisable(record)} />
                        </Space>
                    ),
                },
                {
                    disable: true,
                    title: '备注',
                    dataIndex: 'notes',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    title: '操作',
                    valueType: 'option',
                    key: 'option',
                    render: (text: any, record: any, _: any, action: any) => {
                        const delLink = (
                            <Link to='#' style={{ color: 'red' }} rel="noopener noreferrer" key="del"
                                onClick={() => {
                                    Modal.confirm({
                                        title: <span style={{ fontWeight: 'bold' }}>删除辅助核算</span>,
                                        icon: <ExclamationCircleOutlined />,
                                        content: '确认要删除辅助核算“' + record.name + '”吗？',
                                        okText: '确认',
                                        cancelText: '取消',
                                        onOk: async () => {
                                            console.log("删除：" + record.id);
                                            await del({ id: record.id })
                                                .then(() => {
                                                    action?.reload();
                                                })
                                                .catch(error => {
                                                    console.log(error);
                                                });
                                        }
                                    });
                                }}
                            >
                                删除
                            </Link>
                        )
                        return [
                            <Link to={"#"} key="edit"
                                onClick={() => { onEdit(record.id) }}
                            >
                                编辑
                            </Link>,
                            delLink
                            ,
                        ]
                    }
                }
            ]}
            actionRef={assistCalulateListActionRef}
            request={async (params = {}) => {
                let data = await listSupplier({ ...params, assistCalculateCateId: cateId, codeOrName: searchText, pageNum: params.current });
                return { data: data.data?.list, total: data.data?.total };
            }}
            pagination={{
                pageSize: 20,
                onChange: (page) => console.log(page),
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
            search={false}

            form={{
                // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                syncToUrl: (values, type) => {
                    if (type === 'get') {
                        return {
                            ...values,
                        };
                    }
                    return values;
                },
            }}
            dateFormatter="string"
            headerTitle={
                <Search placeholder="请输入供应商编码或名称" defaultValue={searchText} onSearch={(value) => {
                    setSearchText(value);
                    if (assistCalulateListActionRef.current) {
                        assistCalulateListActionRef.current.reload();
                    }
                }} enterButton />
            }
            toolBarRender={() => [
                <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => {
                    onCreate()
                }}>
                    新建
                </Button>
            ]}
        />
    )

    //职员字段
    const employee = () => (
        <ProTable
            bordered
            columns={[
                {
                    title: '职员编码',
                    dataIndex: 'code',
                    ellipsis: true,
                    // tip: '标题过长会自动收缩',

                },
                {
                    disable: true,
                    title: '职员名称',
                    dataIndex: 'name',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '助记码',
                    dataIndex: 'mnemonicCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '性别',
                    dataIndex: 'sex',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueEnum: {
                        1: '男性',
                        2: '女性',
                        0: '其他',
                    }
                },
                {
                    disable: true,
                    title: '部门编码',
                    dataIndex: 'departmentCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '部门名称',
                    dataIndex: 'departmentName',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '职务',
                    dataIndex: 'position',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '岗位',
                    dataIndex: 'job',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '手机',
                    dataIndex: 'phone',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '出生日期',
                    dataIndex: 'birthday',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '入职日期',
                    dataIndex: 'startDate',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '离职日期',
                    dataIndex: 'departureDate',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '是否启用',
                    dataIndex: 'disable',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    render: (text: any, record: any) => (
                        <Space>
                            <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={() => onUpdateDisable(record)} />
                        </Space>
                    ),
                },
                {
                    disable: true,
                    title: '备注',
                    dataIndex: 'notes',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    title: '操作',
                    valueType: 'option',
                    key: 'option',
                    render: (text: any, record: any, _: any, action: any) => {
                        const delLink = (
                            <Link to='#' style={{ color: 'red' }} rel="noopener noreferrer" key="del"
                                onClick={() => {
                                    Modal.confirm({
                                        title: <span style={{ fontWeight: 'bold' }}>删除辅助核算</span>,
                                        icon: <ExclamationCircleOutlined />,
                                        content: '确认要删除辅助核算“' + record.name + '”吗？',
                                        okText: '确认',
                                        cancelText: '取消',
                                        onOk: async () => {
                                            console.log("删除：" + record.id);
                                            await del({ id: record.id })
                                                .then(() => {
                                                    action?.reload();
                                                })
                                                .catch(error => {
                                                    console.log(error);
                                                });
                                        }
                                    });
                                }}
                            >
                                删除
                            </Link>
                        )
                        return [
                            <Link to={"#"} key="edit"
                                onClick={() => { onEdit(record.id) }}
                            >
                                编辑
                            </Link>,
                            delLink
                            ,
                        ]
                    }
                }
            ]}
            actionRef={assistCalulateListActionRef}
            request={async (params = {}) => {
                let data = await listEmployee({ ...params, assistCalculateCateId: cateId, codeOrName: searchText, pageNum: params.current });
                return { data: data.data?.list, total: data.data?.total };
            }}
            pagination={{
                pageSize: 20,
                onChange: (page) => console.log(page),
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
            search={false}

            form={{
                // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                syncToUrl: (values, type) => {
                    if (type === 'get') {
                        return {
                            ...values,
                        };
                    }
                    return values;
                },
            }}
            dateFormatter="string"
            headerTitle={
                <Search placeholder="请输入职员编码或名称" defaultValue={searchText} onSearch={(value) => {
                    setSearchText(value);
                    if (assistCalulateListActionRef.current) {
                        assistCalulateListActionRef.current.reload();
                    }
                }} enterButton />
            }
            toolBarRender={() => [
                <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => {
                    onCreate()
                }}>
                    新建
                </Button>
            ]}
        />
    )

    //部门字段
    const department = () => (
        <ProTable
            bordered
            columns={[
                {
                    title: '部门编码',
                    dataIndex: 'code',
                    ellipsis: true,
                    // tip: '标题过长会自动收缩',

                },
                {
                    disable: true,
                    title: '部门名称',
                    dataIndex: 'name',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '助记码',
                    dataIndex: 'mnemonicCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '负责人',
                    dataIndex: 'manager',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '手机',
                    dataIndex: 'phone',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '出生日期',
                    dataIndex: 'birthday',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '成立日期',
                    dataIndex: 'startDate',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '撤销日期',
                    dataIndex: 'revokeDate',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '是否启用',
                    dataIndex: 'disable',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    render: (text: any, record: any) => (
                        <Space>
                            <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={() => onUpdateDisable(record)} />
                        </Space>
                    ),
                },
                {
                    disable: true,
                    title: '备注',
                    dataIndex: 'notes',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    title: '操作',
                    valueType: 'option',
                    key: 'option',
                    render: (text: any, record: any, _: any, action: any) => {
                        const delLink = (
                            <Link to='#' style={{ color: 'red' }} rel="noopener noreferrer" key="del"
                                onClick={() => {
                                    Modal.confirm({
                                        title: <span style={{ fontWeight: 'bold' }}>删除辅助核算</span>,
                                        icon: <ExclamationCircleOutlined />,
                                        content: '确认要删除辅助核算“' + record.name + '”吗？',
                                        okText: '确认',
                                        cancelText: '取消',
                                        onOk: async () => {
                                            console.log("删除：" + record.id);
                                            await del({ id: record.id })
                                                .then(() => {
                                                    action?.reload();
                                                })
                                                .catch(error => {
                                                    console.log(error);
                                                });
                                        }
                                    });
                                }}
                            >
                                删除
                            </Link>
                        )
                        return [
                            <Link to={"#"} key="edit"
                                onClick={() => { onEdit(record.id) }}
                            >
                                编辑
                            </Link>,
                            delLink
                            ,
                        ]
                    }
                }
            ]}
            actionRef={assistCalulateListActionRef}
            request={async (params = {}) => {
                let data = await listDepartment({ ...params, assistCalculateCateId: cateId, codeOrName: searchText, pageNum: params.current });
                return { data: data.data?.list, total: data.data?.total };
            }}
            pagination={{
                pageSize: 20,
                onChange: (page) => console.log(page),
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
            search={false}

            form={{
                // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                syncToUrl: (values, type) => {
                    if (type === 'get') {
                        return {
                            ...values,
                        };
                    }
                    return values;
                },
            }}
            dateFormatter="string"
            headerTitle={
                <Search placeholder="请输入部门编码或名称" defaultValue={searchText} onSearch={(value) => {
                    setSearchText(value);
                    if (assistCalulateListActionRef.current) {
                        assistCalulateListActionRef.current.reload();
                    }
                }} enterButton />
            } toolBarRender={() => [
                <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => {
                    onCreate()
                }}>
                    新建
                </Button>
            ]}
        />
    )

    //项目字段
    const project = () => (
        <ProTable
            bordered
            columns={[
                {
                    title: '项目编码',
                    dataIndex: 'code',
                    ellipsis: true,
                    // tip: '标题过长会自动收缩',

                },
                {
                    disable: true,
                    title: '项目名称',
                    dataIndex: 'name',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '助记码',
                    dataIndex: 'mnemonicCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '负责部门',
                    dataIndex: 'responsibleDepartment',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '责任人',
                    dataIndex: 'responsiblePerson',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '手机',
                    dataIndex: 'phone',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '开始日期',
                    dataIndex: 'startDate',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '结束日期',
                    dataIndex: 'endDate',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '是否启用',
                    dataIndex: 'disable',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    render: (text: any, record: any) => (
                        <Space>
                            <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={() => onUpdateDisable(record)} />
                        </Space>
                    ),
                },
                {
                    disable: true,
                    title: '备注',
                    dataIndex: 'notes',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    title: '操作',
                    valueType: 'option',
                    key: 'option',
                    render: (text: any, record: any, _: any, action: any) => {
                        const delLink = (
                            <Link to='#' style={{ color: 'red' }} rel="noopener noreferrer" key="del"
                                onClick={() => {
                                    Modal.confirm({
                                        title: <span style={{ fontWeight: 'bold' }}>删除辅助核算</span>,
                                        icon: <ExclamationCircleOutlined />,
                                        content: '确认要删除辅助核算“' + record.name + '”吗？',
                                        okText: '确认',
                                        cancelText: '取消',
                                        onOk: async () => {
                                            console.log("删除：" + record.id);
                                            await del({ id: record.id })
                                                .then(() => {
                                                    action?.reload();
                                                })
                                                .catch(error => {
                                                    console.log(error);
                                                });
                                        }
                                    });
                                }}
                            >
                                删除
                            </Link>
                        )
                        return [
                            <Link to={"#"} key="edit"
                                onClick={() => { onEdit(record.id) }}
                            >
                                编辑
                            </Link>,
                            delLink
                            ,
                        ]
                    }
                }
            ]}
            actionRef={assistCalulateListActionRef}
            request={async (params = {}) => {
                let data = await listProject({ ...params, assistCalculateCateId: cateId, codeOrName: searchText, pageNum: params.current });
                return { data: data.data?.list, total: data.data?.total };
            }}
            pagination={{
                pageSize: 20,
                onChange: (page) => console.log(page),
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
            search={false}

            form={{
                // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                syncToUrl: (values, type) => {
                    if (type === 'get') {
                        return {
                            ...values,
                        };
                    }
                    return values;
                },
            }}
            dateFormatter="string"
            headerTitle={
                <Search placeholder="请输入项目编码或名称" defaultValue={searchText} onSearch={(value) => {
                    setSearchText(value);
                    if (assistCalulateListActionRef.current) {
                        assistCalulateListActionRef.current.reload();
                    }
                }} enterButton />
            }
            toolBarRender={() => [
                <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => {
                    onCreate()
                }}>
                    新建
                </Button>
            ]}
        />
    )

    //存货字段
    const inventory = () => (
        <ProTable
            bordered
            columns={[
                {
                    title: '存货编码',
                    dataIndex: 'code',
                    ellipsis: true,
                    search: true
                    // tip: '标题过长会自动收缩',

                },
                {
                    disable: true,
                    title: '存货名称',
                    dataIndex: 'name',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '助记码',
                    dataIndex: 'mnemonicCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '规格型号',
                    dataIndex: 'specifications',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '存货类别',
                    dataIndex: 'inventoryCate',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '计量单位',
                    dataIndex: 'units',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '启用日期',
                    dataIndex: 'startDate',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '停用日期',
                    dataIndex: 'endDate',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    valueType: 'date',
                },
                {
                    disable: true,
                    title: '是否启用',
                    dataIndex: 'disable',
                    filters: true,
                    ellipsis: true,
                    search: false,
                    render: (text: any, record: any) => (
                        <Space>
                            <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={() => onUpdateDisable(record)} />
                        </Space>
                    ),
                },
                {
                    disable: true,
                    title: '备注',
                    dataIndex: 'notes',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    title: '操作',
                    valueType: 'option',
                    key: 'option',
                    render: (text: any, record: any, _: any, action: any) => {
                        const delLink = (
                            <Link to='#' style={{ color: 'red' }} rel="noopener noreferrer" key="del"
                                onClick={() => {
                                    Modal.confirm({
                                        title: <span style={{ fontWeight: 'bold' }}>删除辅助核算</span>,
                                        icon: <ExclamationCircleOutlined />,
                                        content: '确认要删除辅助核算“' + record.name + '”吗？',
                                        okText: '确认',
                                        cancelText: '取消',
                                        onOk: async () => {
                                            console.log("删除：" + record.id);
                                            await del({ id: record.id })
                                                .then(() => {
                                                    action?.reload();
                                                })
                                                .catch(error => {
                                                    console.log(error);
                                                });
                                        }
                                    });
                                }}
                            >
                                删除
                            </Link>
                        )
                        return [
                            <Link to={"#"} key="edit"
                                onClick={() => { onEdit(record.id) }}
                            >
                                编辑
                            </Link>,
                            delLink
                            ,
                        ]
                    }
                }
            ]}
            actionRef={assistCalulateListActionRef}
            request={async (params = {}) => {
                let data = await listInventory({ ...params, assistCalculateCateId: cateId, codeOrName: searchText, pageNum: params.current });
                return { data: data.data?.list, total: data.data?.total };
            }}
            pagination={{
                pageSize: 20,
                onChange: (page) => console.log(page),
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
            search={false}

            form={{
                // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                syncToUrl: (values, type) => {
                    if (type === 'get') {
                        return {
                            ...values,
                        };
                    }
                    return values;
                },
            }}
            dateFormatter="string"
            headerTitle={
                <Search placeholder="请输入存货编码或名称" defaultValue={searchText} onSearch={(value) => {
                    setSearchText(value);
                    if (assistCalulateListActionRef.current) {
                        assistCalulateListActionRef.current.reload();
                    }
                }} enterButton />
            }
            toolBarRender={() => [
                <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => {
                    onCreate()
                }}>
                    新建
                </Button>
            ]}
        />
    )

    //现金流字段
    const cashFlow = () => (
        <ProTable
            bordered
            columns={[
                {
                    title: '现金流编码',
                    dataIndex: 'code',
                    ellipsis: true,
                    width: 100
                    // tip: '标题过长会自动收缩',

                },
                {
                    disable: true,
                    title: '现金流名称',
                    dataIndex: 'name',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '现金流类别',
                    dataIndex: 'cashFlowCate',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '助记码',
                    dataIndex: 'mnemonicCode',
                    filters: true,
                    ellipsis: true,
                    search: false
                },
                {
                    disable: true,
                    title: '备注',
                    dataIndex: 'notes',
                    filters: true,
                    ellipsis: true,
                    search: false
                }
            ]}
            actionRef={assistCalulateListActionRef}
            request={async (params = {}) => {
                let data = await listCashFlow({ ...params, assistCalculateCateId: cateId, codeOrName: searchText, pageNum: params.current });
                return { data: data.data?.list, total: data.data?.total };
            }}
            pagination={{
                pageSize: 20,
                onChange: (page) => console.log(page),
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
            search={false}

            form={{
                // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                syncToUrl: (values, type) => {
                    if (type === 'get') {
                        return {
                            ...values,
                        };
                    }
                    return values;
                },
            }}
            dateFormatter="string"
            headerTitle={
                <Search placeholder="请输入现金流编码或名称" defaultValue={searchText} onSearch={(value) => {
                    setSearchText(value);
                    if (assistCalulateListActionRef.current) {
                        assistCalulateListActionRef.current.reload();
                    }
                }} enterButton />
            }
            toolBarRender={() => [
                // <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => {
                //     onCreate()
                // }}>
                //     新建
                // </Button>
            ]}
        />
    )

    const custom = (column: ProColumns[]) => (
        <ProTable
            columns={column}
            bordered
            actionRef={assistCalulateListActionRef}
            request={async (params = {}) => {
                let data = await listCustom({ ...params, assistCalculateCateId: cateId, codeOrName: searchText, pageNum: params.current });
                return { data: data.data?.list, total: data.data?.total };
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
            search={false}

            form={{
                // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                syncToUrl: (values, type) => {
                    if (type === 'get') {
                        return {
                            ...values,
                        };
                    }
                    return values;
                },
            }}
            dateFormatter="string"
            headerTitle={
                <Search placeholder="请输入编码或名称" defaultValue={searchText} onSearch={(value) => {
                    setSearchText(value);
                    if (assistCalulateListActionRef.current) {
                        assistCalulateListActionRef.current.reload();
                    }
                }} enterButton />
            }
            toolBarRender={() => [
                <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => {
                    onCreate()
                }}>
                    新建
                </Button>
            ]}
        />
    )

    const handleOpenData = (id: number, cateCode: string, name: string) => {
        if (id !== cateId) {
            setSearchText('');
        }
        setIsOpenDataPanel(true);
        setCateCode(cateCode);
        setCateId(id);
        setPanelTitle(name);
    }
    const list = async () => {
        switch (cateCode) {
            case "CUSTOMER":
                setListData(customer());
                break;
            case "SUPPLIER":
                setListData(supplier());
                break;
            case "EMPLOYEE":
                setListData(employee());
                break;
            case "DEPARTMENT":
                setListData(department());
                break;
            case "PROJECT":
                setListData(project());
                break;
            case "INVENTORY":
                setListData(inventory());
                break;
            case "CASH_FLOW":
                setListData(cashFlow());
                break;
            // case "CUSTOM":
            //     const response = await onGetAssistCalculateCateDetail(cateId);
            //     setListData(custom(cateId, response));
            //     break;
            default: {
                const response = await onGetAssistCalculateCateDetail(cateId);
                setListData(custom(response));
                break;
            }
        }
        if (assistCalulateListActionRef.current) {
            assistCalulateListActionRef.current.reload();
        }
    }
    onCreate = () => {
        setIsModalOpen(true);
        switch (cateCode) {
            case "CUSTOMER":
                setModalOpenData(<AssistCalculateCustomerCreate
                    p={{ cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateCustomerCreate>);
                break;
            case "SUPPLIER":
                setModalOpenData(<AssistCalculateSupplierCreate
                    p={{ cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateSupplierCreate>);
                break;
            case "EMPLOYEE":
                setModalOpenData(<AssistCalculateEmployeeCreate
                    p={{ cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateEmployeeCreate>);
                break;
            case "DEPARTMENT":
                setModalOpenData(<AssistCalculateDepartmentCreate
                    p={{ cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateDepartmentCreate>);
                break;
            case "PROJECT":
                setModalOpenData(<AssistCalculateProjectCreate
                    p={{ cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateProjectCreate>);
                break;
            case "INVENTORY":
                setModalOpenData(<AssistCalculateInventoryCreate
                    p={{ cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateInventoryCreate>);
                break;
            // case "CASH_FLOW":
            //     setModalOpenData(<AssistCalculateCashFlowCreate
            //         p={{ id: id, cateId: cateId }}
            //         updateIsModalOpenState={updateIsModalOpenState}
            //     ></AssistCalculateCashFlowCreate>);
            //     break;
            // case "CUSTOM":
            //     const response = await onGetAssistCalculateCateCreate(cateId);
            //     setListData(custom(cateId, response));
            //     break;
            default:
                setModalOpenData(<AssistCalculateCustomCreate
                    p={{ cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateCustomCreate>);
                break;
        }
        if (assistCalulateListActionRef.current) {
            assistCalulateListActionRef.current.reload();
        }
    }
    updateIsModalOpenState = (state: boolean) => {
        setIsModalOpen(state);
        if (assistCalulateListActionRef.current) {
            assistCalulateListActionRef.current.reload();
        }
        if (cateActionRef.current) {
            cateActionRef.current.reload();
        }
    }

    onEdit = (id: number) => {
        setIsModalOpen(true);
        setModalOpenData(null); // 在设置新的 modalOpenData 之前先将其置为空
        switch (cateCode) {
            case "CUSTOMER":
                setModalOpenData(<AssistCalculateCustomerDetail
                    key={Math.random()}
                    p={{ id: id, cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateCustomerDetail>);
                break;
            case "SUPPLIER":
                setModalOpenData(<AssistCalculateSupplierDetail
                    key={Math.random()}
                    p={{ id: id, cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateSupplierDetail>);
                break;
            case "EMPLOYEE":
                setModalOpenData(<AssistCalculateEmployeeDetail
                    key={Math.random()}
                    p={{ id: id, cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateEmployeeDetail>);
                break;
            case "DEPARTMENT":
                setModalOpenData(<AssistCalculateDepartmentDetail
                    key={Math.random()}
                    p={{ id: id, cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateDepartmentDetail>);
                break;
            case "PROJECT":
                setModalOpenData(<AssistCalculateProjectDetail
                    key={Math.random()}
                    p={{ id: id, cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateProjectDetail>);
                break;
            case "INVENTORY":
                setModalOpenData(<AssistCalculateInventoryDetail
                    key={Math.random()}
                    p={{ id: id, cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateInventoryDetail>);
                break;
            // case "CASH_FLOW":
            //     setModalOpenData(<AssistCalculateCashFlowDetail
            //         p={{ id: id, cateId: cateId }}
            //         updateIsModalOpenState={updateIsModalOpenState}
            //     ></AssistCalculateCashFlowDetail>);
            //     break;
            // case "CUSTOM":
            //     const response = await onGetAssistCalculateCateDetail(cateId);
            //     setListData(custom(cateId, response));
            //     break;
            default:
                setModalOpenData(<AssistCalculateCustomDetail
                    key={Math.random()}
                    p={{ id: id, cateId: cateId }}
                    updateIsModalOpenState={updateIsModalOpenState}
                ></AssistCalculateCustomDetail>);
                break;
        }
        if (assistCalulateListActionRef.current) {
            assistCalulateListActionRef.current.reload();
        }
    }
    //init();
    useEffect(() => {
        if (isOpenDataPanel && cateId) {
            list();
        }
    }, [isOpenDataPanel, isModalOpen, cateId, cateCode, searchText, modalOpenData])
    return (
        <PageContainer>
            <Card>
                <ProTable
                    columns={[
                        {
                            title: '类别编号',
                            dataIndex: 'id',
                            ellipsis: true,
                            // tip: '标题过长会自动收缩',

                        },
                        {
                            disable: true,
                            title: '类别名称',
                            dataIndex: 'name',
                            filters: true,
                            ellipsis: true,
                            search: false
                        },
                        {
                            title: '操作',
                            valueType: 'option',
                            key: 'option',
                            render: (text: any, record: any, _: any, action: any) => {
                                const delLink = (record.level === 1) && (
                                    <Link to='#' style={{ color: 'red' }} rel="noopener noreferrer" key="del"
                                        onClick={() => {
                                            Modal.confirm({
                                                title: <span style={{ fontWeight: 'bold' }}>删除辅助核算类别</span>,
                                                icon: <ExclamationCircleOutlined />,
                                                content: '确认要删除辅助核算“' + record.name + '”吗？',
                                                okText: '确认',
                                                cancelText: '取消',
                                                onOk: async () => {
                                                    console.log("删除：" + record.id);
                                                    await delAssistCalculateCate({ id: record.id })
                                                        .then(() => {
                                                            action?.reload();
                                                        })
                                                        .catch(error => {
                                                            console.log(error);
                                                        });
                                                }
                                            });
                                        }}
                                    >
                                        删除
                                    </Link>
                                );
                                const editLink = (record.level === 1) && (
                                    <Link to={"#"} key="edit"
                                        onClick={() => {
                                            setIsModalOpen(true);
                                            setPanelTitle('修改' + record.name + '类别');
                                            setModalOpenData(<AssistCalculateCateDetail
                                                p={{ id: record.id }}
                                                updateIsModalOpenState={updateIsModalOpenState}
                                            ></AssistCalculateCateDetail>);
                                        }}
                                    >
                                        编辑
                                    </Link>);
                                return [
                                    <Link to='#'
                                        onClick={() => { handleOpenData(record.id, record.code, record.name) }}
                                        key="editable">
                                        查看数据
                                    </Link>,
                                    editLink,
                                    delLink
                                    ,
                                ]
                            }
                        }
                    ]}
                    bordered
                    actionRef={cateActionRef}
                    request={async (params = {}) => {
                        let data = await listAssistCalculateCate({ ...params });
                        return { data: data.data };
                    }}
                    pagination={{
                        pageSize: 20,
                        onChange: (page) => console.log(page),
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
                    search={false}

                    form={{
                        // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                        syncToUrl: (values, type) => {
                            if (type === 'get') {
                                return {
                                    ...values,
                                };
                            }
                            return values;
                        },
                    }}
                    dateFormatter="string"
                    toolBarRender={() => [
                        <Button key="button" icon={<PlusOutlined />} type="primary"
                            onClick={() => {
                                setIsModalOpen(true);
                                setPanelTitle('创建自定义辅助核算类别');
                                setModalOpenData(<AssistCalculateCateCreate
                                    updateIsModalOpenState={updateIsModalOpenState}
                                ></AssistCalculateCateCreate>);
                            }}
                        >
                            新建
                        </Button>
                    ]}
                />
                <Drawer
                    zIndex={100} // 设置 Modal 的层级为 1000

                    title={panelTitle} visible={isOpenDataPanel} width={'80%'} onClose={() => { setIsOpenDataPanel(false) }}>
                    {listData}
                </Drawer>
                <Modal
                    zIndex={101} // 设置 Modal 的层级为 1000
                    title={panelTitle}
                    open={isModalOpen}
                    footer={null} // 设置 footer 为 null，即不显示底部按钮
                    width={'50%'}
                    style={{ top: 20 }}
                    onOk={() => { setIsModalOpen(false) }}
                    onCancel={() => { setModalOpenData(null); setIsModalOpen(false); }}>
                    {modalOpenData}
                </Modal>
            </Card>
        </PageContainer >
    );
};

export default AssistCalulateList;