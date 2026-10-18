import { ExclamationCircleOutlined, PlusOutlined, ChromeOutlined, IeOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns, ProFormSelect } from '@ant-design/pro-components';
import { ProTable, ProFormText, ProForm } from '@ant-design/pro-components';
import { Button, Space, Switch, Modal, message, Tag, Select, DatePicker, Drawer, Tooltip, Table } from 'antd';

import React, { useState, useRef } from 'react';
import { update, listByName } from '@/services/ant-design-pro/tenant';
import { searchMemberManage, updateDisable, bindRoleIds as bindRoleId, getMemberInfo } from '@/services/ant-design-pro/member';
import { listRole } from '@/services/ant-design-pro/role';
import { del } from '@/services/ant-design-pro/voucherWordConfig';

import { Link } from '@umijs/max';
const { RangePicker } = DatePicker;
import type { TableRowSelection } from 'antd/es/table/interface';
import moment from 'moment';

export default () => {
    const [tenantOptions, setTenantOptions] = useState<any[]>([]);
    const [selectedTenantId, setSelectedTenantId] = useState<string | undefined>(undefined);
    const [openRole, setOpenRole] = useState(false);
    const [memberId, setMemberId] = useState(Number);
    const [selectedRoleRowKeys, setSelectedRoleRowKeys] = useState<any[]>([]);
    const [checkStrictly] = useState(false);
    const [roleData, setRoleData] = useState<any[]>([]);
    const [bindRoleIds, setBindRoleIds] = useState<any[]>([]);
    const [unBindRoleIds, setUnBindRoleIds] = useState<any[]>([]);
    const [copySelectedRoleRowKeys, setCopySelectedRoleRowKeys] = useState<any[]>([]);
    const [openEditMember, setOpenEditMember] = useState(false);
    const [editForm] = ProForm.useForm();
    const actionRef = useRef<ActionType>();
    const [tenantSysRoleIds, setTenantSysRoleIds] = useState<any[]>([]);


    const onCloseTenant = async () => {
        setOpenEditMember(false);
    }
    let onListRole: (id: number) => Promise<void>;
    const onOpenEditMember = async (id: number) => {
        setMemberId(id);
        setOpenEditMember(true);
        await getMemberInfo({ id: id }).then((response) => {
            editForm.setFieldsValue(response.data);
        }).catch(() => {

        });
    }
    // 在这里调用你的接口获取 Tree 数据
    const onOpenRole = async (id: number) => {
        setMemberId(id);
        setOpenRole(true);
        await onListRole(id);
        try {
            // 替换成实际的接口地址
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };
    const onCloseRole = () => {
        setOpenRole(false);
    };
    const onBindRole = async () => {
        try {
            // 替换成实际的接口地址
            await bindRoleId({ id: memberId, bindRoleIds: bindRoleIds, unBindRoleIds: unBindRoleIds }).then(response => {
                if (response.data) {
                    message.success("保存成功");
                }
                else {
                    message.error("保存失败");
                }
                setOpenRole(false);
            }).catch(error => {
                console.log(error);
            });
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };
    //搜索辅助核算
    const onSearchTenant = (name: string) => {
        setTenantOptions([]);
        listByName({ name: name }).then(response => {
            const dataArray = response.data.map((data: any) => ({
                value: data.id,
                label: data.name
            }));
            setTenantOptions(dataArray);
        });
    }
    interface _DataType {
        key: React.ReactNode;
        name: string;
        age: number;
        address: string;
        children?: _DataType[];
    }
    const isDisabled = (record: any) => {
        return tenantSysRoleIds.includes(record.id) ? false : true;
    }
    const rowSelectionRole: TableRowSelection<any> = {
        selectedRowKeys: selectedRoleRowKeys,
        onChange: (selectedRowKeys, _selectedRows) => {
            setSelectedRoleRowKeys(selectedRowKeys);
            // //新绑定的菜单
            let bindRoleId = selectedRowKeys.filter(p => !copySelectedRoleRowKeys || !copySelectedRoleRowKeys.includes(p));
            // //需要解绑的菜单
            let unBindRoleId = copySelectedRoleRowKeys.filter(p => !selectedRowKeys || !selectedRowKeys.includes(p));
            setBindRoleIds(bindRoleId);
            setUnBindRoleIds(unBindRoleId);
            // console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
            // console.log("bindMenuId" + bindMenuId, "unBindMenuId" + unBindMenuId);
        },
        getCheckboxProps: (record: any) => ({
            disabled: isDisabled(record) // 根据函数返回的值来判断是否禁用复选框
        }),
        onSelect: (record, selected, selectedRows) => {
            console.log(record, selected, selectedRows);
        },
        onSelectAll: (selected, selectedRows, changeRows) => {
            console.log(selected, selectedRows, changeRows);
        },
    };
    const getBrowserType = (userAgent: string) => {
        if (userAgent.indexOf("Firefox") !== -1) {
            return "Firefox";
        } else if (userAgent.indexOf("Opera") !== -1 || userAgent.indexOf("OPR") !== -1) {
            return "Opera";
        } else if (userAgent.indexOf("Trident") !== -1) {
            return <IeOutlined style={{color:'red', fontSize: '20px', fontWeight: 'bold'}} />;
        } else if (userAgent.indexOf("Edg") !== -1) {
            return <IeOutlined style={{color:'blue', fontSize: '20px', fontWeight: 'bold'}}/>;
        } else if (userAgent.indexOf("Chrome") !== -1) {
            return <ChromeOutlined style={{color:'green', fontSize: '20px', fontWeight: 'bold'}}/>;
        } else if (userAgent.indexOf("Safari") !== -1) {
            return "Safari";
        } else {
            return "unknown";
        }
    }

    const deviceColumns = [
        {
            title: '',
            dataIndex: 'clientName',
            key: 'clientIcon',
            render: (_text: any, record: any) => {
                return getBrowserType(record.clientName);
            }
        },
        {
            title: '设备ID',
            dataIndex: 'clientId',
            key: 'clientId',
        },
        {
            title: '设备名称',
            dataIndex: 'clientName',
            key: 'clientName',
        },
    ];

    const columns: ProColumns[] = [
        {
            title: 'ID',
            dataIndex: 'id',
            ellipsis: true,
            search: false
            // tip: '标题过长会自动收缩',

        },
        {
            disable: true,
            title: '名称',
            dataIndex: 'name',
            filters: true,
            ellipsis: true,
            search: true
        },
        {
            disable: true,
            title: '昵称',
            dataIndex: 'nickName',
            filters: true,
            ellipsis: true,
            search: true
        },
        {
            disable: true,
            title: '手机',
            dataIndex: 'phone',
            filters: true,
            ellipsis: true,
            search: true
        },
        {
            disable: true,
            title: '租户',
            dataIndex: 'tenantName',
            filters: true,
            ellipsis: true,
            search: true,
            valueType: 'select',
            renderFormItem: (_, { ..._rest }) => {
                return <Select
                    // mode="tags"
                    labelInValue
                    showSearch={true}
                    filterOption={(input, option: any) => {
                        //忽略大小写
                        return option.label.toLowerCase().includes(input.toLowerCase());
                    }}
                    onFocus={() => { onSearchTenant(""); }}
                    onSearch={(value) => { onSearchTenant(value); }}
                    onChange={(value: any) => { setSelectedTenantId(value.value); }} // 添加此处，更新选中的租户管理员的值
                    options={tenantOptions}
                />;

            },
        },
        {
            disable: true,
            title: '注册时间',
            dataIndex: 'createTime',
            filters: true,
            ellipsis: true,
            search: true,
            renderFormItem: () => {
                return <RangePicker />;

            },
        },
        {
            disable: true,
            title: '在线状态',
            dataIndex: 'online',
            filters: true,
            ellipsis: true,
            search: false,
            render: (_text: any, record: any) => (
                <Tooltip placement="left"
                    overlayStyle={{ maxWidth: '600px', wordWrap: 'break-word' }}
                    title={() => {
                        if (record.online) {
                            return (
                                <Table
                                    columns={deviceColumns}
                                    dataSource={record.deviceInfoVoList}
                                    pagination={false}
                                    size="small"
                                    rowKey="clientId"
                                />
                            );
                        }
                        return "没有在线设备";
                    }}
                >
                    <Tag color={record.online ? "green" : "red"}>
                        {record.online ? "在线" : "离线"}
                    </Tag>
                </Tooltip>
            ),
        },
        {
            title: <strong>是否启用</strong>,
            dataIndex: 'disable',
            valueType: 'select',
            valueEnum: {
                false: { text: '是' },
                true: { text: '否' },
            },
            search: false,
            render: (text, record, _, action) => (
                <Space>
                    <Switch checkedChildren="是" unCheckedChildren="否" checked={!record.disable} onChange={() => {
                        updateDisable({ id: record.id, disable: !record.disable })
                            .then(() => {
                                action?.reload();
                            })
                            .catch(() => {
                                action?.reload();
                            });
                    }} />
                </Space>
            ),
        },
        {
            title: '操作',
            valueType: 'option',
            key: 'option',
            render: (text, record, _, action) => {
                const delLink = !record.defaultFlag && record.useCount <= 0 && (
                    <Link to='#' onClick={() => {
                        Modal.confirm({
                            title: <span style={{ fontWeight: 'bold' }}>删除凭证字</span>,
                            icon: <ExclamationCircleOutlined />,
                            content: '确认要删除凭证字"' + record.voucherWord + '"吗？',
                            okText: '确认',
                            cancelText: '取消',
                            onOk: async () => {
                                console.log("删除：" + record.id);
                                await del({ id: record.id })
                                    .then((response: any) => {
                                        if (response.data) {
                                            action?.reload();
                                        }
                                        else {
                                            message.error("删除失败");
                                        }
                                    })
                                    .catch((error: any) => {
                                        console.log(error);
                                    });

                            }
                        });
                    }} style={{ color: 'red' }} rel="noopener noreferrer" key="del">
                        删除
                    </Link>
                )
                return [
                    <Link to={"#"} key="edit" onClick={() => { onOpenEditMember(record.id) }}>
                        编辑
                    </Link>,
                    <Link to={"#"} key="bindRole" onClick={() => { onOpenRole(record.id) }}>
                        绑定角色
                    </Link>,
                    delLink
                    ,
                ]
            }
        },
    ];
    onListRole = async (id: number) => {
        //查询当前角色已经绑定的资源
        getMemberInfo({ id: id }).then(response => {
            if (response.data.sysRoleIds) {
                let sysRoleIds = JSON.parse(response.data.sysRoleIds);
                setSelectedRoleRowKeys(sysRoleIds);
                setCopySelectedRoleRowKeys(sysRoleIds);
            }
            else {
                setSelectedRoleRowKeys([]);
                setCopySelectedRoleRowKeys([]);
            }
            if (response.data.tenantSysRoleIds) {
                let sysRoleIds = JSON.parse(response.data.tenantSysRoleIds);
                setTenantSysRoleIds(sysRoleIds);
            }
            else {
                setTenantSysRoleIds([]);
            }
        }).catch(error => {
            console.log(error);
        });
        //查询当前角色已经绑定的资源
        listRole({}).then(response => {
            if (response.data) {
                setRoleData(response.data.list);
            }
            else {
                setRoleData(response.data.list);

            }
        }).catch(error => {
            console.log(error);
        });
    }
    const onUpdateTenant = async (record: API.ListRoleVo) => {
        try {
            // 替换成实际的接口地址
            await update({ ...record }).then(response => {
                if (response.data) {
                    message.success("修改成功");
                }
                else {
                    message.error("修改失败");
                }
                onCloseTenant();
            }).catch(error => {
                console.log(error);
            });
        } catch (error) {
            console.error(error);
        }
        finally {
            if (actionRef.current) {
                actionRef.current.reload();
            }
        }
    }
    return (
        <PageContainer>
            <ProTable<API.ListAccountBookVoItem, API.PageParams>
                columns={columns}
                actionRef={actionRef}
                cardBordered
                bordered
                request={async (params: any = {}, sort, filter) => {
                    console.log(sort, filter);
                    let beginCreateTime;
                    let endCreateTime;
                    if (params.createTime) {
                        beginCreateTime = moment(params.createTime[0]).format('YYYY-MM-DD 00:00:00');
                        endCreateTime = moment(params.createTime[1]).format('YYYY-MM-DD 23:59:59');
                    }
                    let data = await searchMemberManage({
                        ...params,
                        tenantId: selectedTenantId,
                        createTime: undefined,
                        pageNum: params.current,
                        beginCreateTime: beginCreateTime,
                        endCreateTime: endCreateTime
                    });
                    return { data: data.data.list };
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
                // form={{
                //     // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
                //     syncToUrl: (values, type) => {
                //         if (type === 'get') {
                //             return {
                //                 ...values,
                //             };
                //         }
                //         return values;
                //     },
                // }}
                dateFormatter="string"
                headerTitle="租户列表"
                pagination={{
                    pageSize: 20,
                    onChange: (page) => console.log(page),
                }}
                onReset={() => {
                    setSelectedTenantId(undefined);
                    // setSelectedTenantLevel(undefined);
                }}
                toolBarRender={() => [
                    <Button key="button" icon={<PlusOutlined />} type="primary" href='/FinanceSetting/VoucherWordConfig/Create'>
                        新建
                    </Button>
                ]}
            />

            <Drawer title="绑定角色" placement="right" onClose={onCloseRole} open={openRole} width={800}>
                <ProTable<API.ListRoleVo, API.PageParams>
                    // expandable={{
                    //     defaultExpandAllRows: true, // 展开所有行
                    // }}
                    rowSelection={{ ...rowSelectionRole, checkStrictly }}
                    bordered={true}
                    columns={[
                        {
                            title: <strong>ID</strong>,
                            dataIndex: 'id',
                            ellipsis: true
                        },
                        {
                            title: <strong>角色名称</strong>,
                            dataIndex: 'roleName',
                            search: false,
                            width: 200
                        },
                        {
                            title: <strong>菜单路径</strong>,
                            dataIndex: 'path',
                            ellipsis: true,
                        }
                    ]}
                    actionRef={actionRef}
                    dataSource={roleData}
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
                    pagination={{
                        pageSize: 20,
                    }}
                    search={false} // 关闭搜索功能
                    options={{
                        density: false, // 不显示密度调整按钮
                        fullScreen: false, // 不显示全屏按钮
                        reload: false, // 不显示刷新按钮
                        setting: false, // 不显示列设置按钮
                    }}
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
                    headerTitle="角色列表"
                />
                <Space style={{ marginTop: 20, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                    <Button type='primary' icon={<PlusOutlined />} onClick={onBindRole}>绑定角色</Button>
                </Space>
            </Drawer>
            <Drawer title="编辑用户" placement="right" onClose={onCloseTenant} open={openEditMember}>
                <ProForm
                    form={editForm}
                    initialValues={{
                        hideInMenu: false,
                        layout: true
                    }}
                    layout='horizontal' // 设置为垂直布局
                    onFinish={onUpdateTenant}
                    labelCol={{ span: 6 }}   // 控制 label 的布局，可以调整 span 的值
                    wrapperCol={{ span: 14 }} // 控制包裹内容的布局，可以调整 span 的值
                    submitter={{
                        render: (_, _dom) => (
                            <ProForm.Item
                                wrapperCol={{ offset: 6, span: 14 }}
                                colon={false}
                            >
                                <Button type="primary" onClick={() => editForm?.submit?.()}>
                                    确定
                                </Button>
                            </ProForm.Item>
                        ),
                    }}
                >
                    <ProFormText
                        width="md"
                        name="id"
                        label="ID"
                        disabled

                    />
                    <ProFormText
                        width="md"
                        name="name"
                        label="名称"
                    />
                    <ProFormText
                        width="md"
                        name="nickName"
                        label="昵称"
                    />
                    <ProFormSelect
                        width="md"
                        name="tenantName"
                        label="租户"
                        placeholder="请选择租户"
                        fieldProps={{
                            labelInValue: true,
                            showSearch: true,
                            filterOption: (input: any, option: any) => {
                                //忽略大小写
                                return option.label.toLowerCase().includes(input.toLowerCase());
                            },
                            onFocus: () => { onSearchTenant(""); },
                            onSearch: (value: any) => { onSearchTenant(value); },
                            onChange: (value: any) => { setSelectedTenantId(value ? value.value : ""); },
                        }}
                        rules={[{ required: true }]}
                        options={tenantOptions}
                    />
                </ProForm>
            </Drawer>
        </PageContainer>
    );
};