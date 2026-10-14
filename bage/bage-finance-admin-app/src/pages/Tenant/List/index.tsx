import { ExclamationCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns, ProFormDatePicker, ProFormSelect } from '@ant-design/pro-components';
import { ProTable, TableDropdown, ProFormText, ProForm } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Switch, Modal, message, Tag, Select, DatePicker, Drawer } from 'antd';

import React, { useState, useEffect, useRef } from 'react';
import request from 'umi-request';
import { list, bindRoleIds as bindRoleId, get, update } from '@/services/ant-design-pro/tenant';
import { searchMember } from '@/services/ant-design-pro/member';
import { listRole } from '@/services/ant-design-pro/role';

import { Link } from '@umijs/max';
const { RangePicker } = DatePicker;
import type { TableRowSelection } from 'antd/es/table/interface';
import moment from 'moment';

export default () => {
    const [membersOptions, setMembersOptions] = useState([]);
    const [selectedMemberId, setSelectedMemberId] = useState<string | undefined>(undefined);
    const [selectedTenantLevel, setSelectedTenantLevel] = useState<string | undefined>(undefined);
    const [openRole, setOpenRole] = useState(false);
    const [tenantId, setTenantId] = useState(Number);
    const [selectedRoleRowKeys, setSelectedRoleRowKeys] = useState([]);
    const [checkStrictly, setCheckStrictly] = useState(false);
    const [roleData, setRoleData] = useState([]);
    const [bindRoleIds, setBindRoleIds] = useState([]);
    const [unBindRoleIds, setUnBindRoleIds] = useState([]);
    const [copySelectedRoleRowKeys, setCopySelectedRoleRowKeys] = useState([]);
    const [openEditTenant, setOpenEditTenant] = useState(false);
    const [editForm] = ProForm.useForm();
    const actionRef = useRef<ActionType>();

    const onCloseTenant = async () => {
        setOpenEditTenant(false);
    }

    const onOpenEditTenant = async (id: number) => {
        setTenantId(id);
        setOpenEditTenant(true);
        await get({ id: id }).then((response) => {
            editForm.setFieldsValue(response.data);
        }).catch(() => {

        });
    }
    // 在这里调用你的接口获取 Tree 数据
    const onOpenRole = async (id: number) => {
        setTenantId(id);
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
            await bindRoleId({ id: tenantId, bindRoleIds: bindRoleIds, unBindRoleIds: unBindRoleIds }).then(response => {
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
    const onSearchMembers = (name: string) => {
        setMembersOptions([]);
        searchMember({ name: name }).then(response => {
            const dataArray = response.data.map(data => ({
                value: data.id,
                label: data.nickName + '(' + data.name + ')'
            }));
            setMembersOptions(dataArray);
        });
    }
    interface DataType {
        key: React.ReactNode;
        name: string;
        age: number;
        address: string;
        children?: DataType[];
    }
    const rowSelectionRole: TableRowSelection<DataType> = {
        selectedRowKeys: selectedRoleRowKeys,
        onChange: (selectedRowKeys, selectedRows) => {
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
        onSelect: (record, selected, selectedRows) => {
            console.log(record, selected, selectedRows);
        },
        onSelectAll: (selected, selectedRows, changeRows) => {
            console.log(selected, selectedRows, changeRows);
        },
    };
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
            title: '租户名称',
            dataIndex: 'name',
            filters: true,
            ellipsis: true,
            search: true
        },
        {
            disable: true,
            title: '租户管理员',
            dataIndex: 'nickName',
            filters: true,
            ellipsis: true,
            search: true,
            valueType: 'select',
            renderFormItem: (_, { type, defaultRender, ...rest }) => {
                return <Select
                    // mode="tags"
                    labelInValue
                    showSearch={true}
                    filterOption={(input, option) => {
                        //忽略大小写
                        return option.label.toLowerCase().includes(input.toLowerCase());
                    }}
                    onFocus={(value) => { onSearchMembers(""); }}
                    onSearch={(value) => { onSearchMembers(value); }}
                    onChange={(value) => { setSelectedMemberId(value.value); }} // 添加此处，更新选中的租户管理员的值
                    options={membersOptions}
                />;

            },
        },
        {
            disable: true,
            title: '租户等级',
            dataIndex: 'tenantLevelName',
            filters: true,
            ellipsis: true,
            search: true,
            valueType: 'select',
            renderFormItem: (_, { type, defaultRender, ...rest }) => {
                return <Select
                    // mode="tags"
                    labelInValue
                    showSearch={true}
                    filterOption={(input, option) => {
                        //忽略大小写
                        return option.label.toLowerCase().includes(input.toLowerCase());
                    }}
                    onChange={(value) => { setSelectedTenantLevel(value.value); }} // 添加此处，更新选中的租户管理员的值
                    options={[
                        {
                            value: '0',
                            label: '免费租户',
                        },
                        {
                            value: '1',
                            label: '1级租户',
                        },
                        {
                            value: '2',
                            label: '2级租户',
                        },
                        {
                            value: '3',
                            label: '3级租户',
                        },
                        {
                            value: 1000,
                            label: '自定义租户',
                        },
                    ]}
                />;

            },
        },
        {
            disable: true,
            title: '到期时间',
            dataIndex: 'expireTime',
            filters: true,
            ellipsis: true,
            search: true,
            renderFormItem: (_, { type, defaultRender, ...rest }) => {
                return <RangePicker />;

            },
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
                    <Switch disabled={record.defaultFlag} checkedChildren="是" unCheckedChildren="否" checked={!record.disable} onChange={(checked: boolean) => {
                        updateDefaultFlag({ id: record.id })
                            .then(response => {
                                action?.reload();
                            })
                            .catch(error => {
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
                                    .then(response => {
                                        if (response.data) {
                                            action?.reload();
                                        }
                                        else {
                                            message.error("删除失败");
                                        }
                                    })
                                    .catch(error => {
                                        console.log(error);
                                    });

                            }
                        });
                    }} style={{ color: 'red' }} rel="noopener noreferrer" key="del">
                        删除
                    </Link>
                )
                return [
                    <Link to={"#"} onClick={() => { onOpenEditTenant(record.id) }}>
                        编辑
                    </Link>,
                    (record.tenantLevel === 1000) && (<Link to={"#"} onClick={() => { onOpenRole(record.id) }}>
                        绑定角色
                    </Link>),
                    delLink
                    ,
                ]
            }
        },
    ];
    const onListRole = async (id: number) => {
        //查询当前角色已经绑定的资源
        get({ id: id }).then(response => {
            if (response.data.sysRoleIds) {
                let sysRoleIds = JSON.parse(response.data.sysRoleIds);
                setSelectedRoleRowKeys(sysRoleIds);
                setCopySelectedRoleRowKeys(sysRoleIds);
            }
            else {
                setSelectedRoleRowKeys([]);
                setCopySelectedRoleRowKeys([]);
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
                request={async (params = {}, sort, filter) => {
                    console.log(sort, filter);
                    let beginExpireTime;
                    let endExpireTime;
                    if (params.expireTime) {
                        beginExpireTime = moment(params.expireTime[0]).format('YYYY-MM-DD 00:00:00');
                        endExpireTime = moment(params.expireTime[1]).format('YYYY-MM-DD 23:59:59');
                    }
                    let data = await list({
                        ...params,
                        memberId: selectedMemberId,
                        tenantLevel: selectedTenantLevel,
                        expireTime: undefined,
                        pageNum: params.current,
                        beginExpireTime: beginExpireTime,
                        endExpireTime: endExpireTime
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
                    setSelectedMemberId(undefined);
                    setSelectedTenantLevel(undefined);
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
                    pagination={true}
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
            <Drawer title="编辑租户" placement="right" onClose={onCloseTenant} open={openEditTenant}>
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
                        render: (_, dom) => (
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
                        label="租户名称"
                    />
                    <ProFormSelect
                        width="md"
                        name="tenantLevel"
                        label="租户等级"
                        placeholder="请选择租户等级"
                        fieldProps={{
                            // size: 'large',
                        }}
                        options={[
                            {
                                value: 0,
                                label: '免费租户',
                            },
                            {
                                value: 1,
                                label: '1级租户',
                            },
                            {
                                value: 2,
                                label: '2级租户',
                            },
                            {
                                value: 3,
                                label: '3级租户',
                            },
                            {
                                value: 1000,
                                label: '自定义租户',
                            },
                        ]}
                        rules={[{ required: true }]}
                    />
                    <ProFormDatePicker
                        width="md"
                        name="expireTime"
                        label="过期时间"
                        placeholder="请输入过期时间"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true }]}
                    />
                </ProForm>
            </Drawer>
        </PageContainer>
    );
};