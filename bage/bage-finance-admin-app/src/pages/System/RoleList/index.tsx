import { ExclamationCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns, ProForm, ProFormSwitch, ProFormText } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';
import { Button, Space, Tag, Switch, Modal, message, Drawer } from 'antd';
import { useRef, useState } from 'react';
import { listRole, delSysRole, updateRoleDisable, getRoleById, updateRole, createRole, roleBindMenu, roleBindResource } from '@/services/ant-design-pro/role';
import { listTreeMenu } from '@/services/ant-design-pro/menu';
import { list } from '@/services/ant-design-pro/resource';
import { listBindMenuIdByRoleId } from '@/services/ant-design-pro/roleBindMenu';
import { listBindResourceIdByRoleId } from '@/services/ant-design-pro/roleBindResource';
import { IconMap } from '@/services/common/iconMap';
import { Link } from '@umijs/max';
import type { TableRowSelection } from 'antd/es/table/interface';


const RoleList: React.FC = () => {
    const actionRef = useRef<ActionType>();
    const roleActionRef = useRef<ActionType>();

    const [dataSource, setDataSource] = useState<API.ListRoleVo[]>();
    const [openMenu, setOpenMenu] = useState(false);
    const [openCreateRole, setOpenCreateRole] = useState(false);
    const [openEditRole, setOpenEditRole] = useState(false);
    const [openResource, setOpenResource] = useState(false);
    const [roleId, setRoleId] = useState<number>();
    const [editForm] = ProForm.useForm();
    const [createForm] = ProForm.useForm();


    // const [checkStrictly, setCheckStrictly] = useState(false);
    const [copySelectedMenuRowKeys, setCopySelectedMenuRowKeys] = useState<any[]>([]);
    const [selectedMenuRowKeys, setSelectedMenuRowKeys] = useState<any[]>([]);
    const [menuDataSource, setMenuDataSource] = useState<any>();
    const [bindMenuIds, setBindMenuIds] = useState<any[]>([]);
    const [unBindMenuIds, setUnBindMenuIds] = useState<any[]>([]);

    const [checkStrictly] = useState(false);
    const [copySelectedResourceRowKeys, setCopySelectedResourceRowKeys] = useState<any[]>([]);
    const [selectedResourceRowKeys, setSelectedResourceRowKeys] = useState<any[]>([]);
    const [resourceDataSource, setResourceDataSource] = useState<any>();
    const [bindResourceIds, setBindResourceIds] = useState<any[]>([]);
    const [unBindResourceIds, setUnBindResourceIds] = useState<any[]>([]);

    let listMenu: (roleId: number) => Promise<any>;
    let listResource: (roleId: number) => Promise<any>;


    //创建角色面板
    const onOpenCreateRole = () => {
        setOpenCreateRole(true);
    };
    // 在这里调用你的接口获取 Tree 数据
    const onOpenEditRole = async (id: number) => {
        setRoleId(id);
        setOpenEditRole(true);
        try {
            // 替换成实际的接口地址
            const response = await getRoleById({ id: id });
            editForm.setFieldsValue(response.data);
            console.log(response.data);
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };

    const onCloseRole = () => {
        setOpenCreateRole(false);
        setOpenEditRole(false);
    };
    const onDelSysRole = async (record: API.ListRoleVo) => {
        Modal.confirm({
            title: <span style={{ fontWeight: 'bold' }}>删除角色</span>,
            icon: <ExclamationCircleOutlined />,
            content: '确认要删除"' + record.roleName + '"的角色吗？',
            okText: '确认',
            cancelText: '取消',
            onOk: async () => {
                try {
                    // 替换成实际的接口地址
                    await delSysRole({ id: record.id }).then(response => {
                        if (response.data) {
                            message.success("删除成功");
                        }
                        else {
                            message.error("删除失败");
                        }
                    }).catch(error => {
                        console.log(error);
                    });
                } catch (error) {
                    console.error(error);
                }
                finally {
                    if (roleActionRef.current) {
                        roleActionRef.current.reload();
                    }
                }
            }
        });
    };

    const onUpdateRoleDisable = async (record: API.ListRoleVo) => {
        try {
            // 替换成实际的接口地址
            await updateRoleDisable({ id: record.id, disable: !record.disable }).then(response => {
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
            if (roleActionRef.current) {
                roleActionRef.current.reload();
            }
        }
    }
    const onUpdateRole = async (record: API.ListRoleVo) => {
        try {
            // 替换成实际的接口地址
            await updateRole({ id: record.id, roleName: record.roleName }).then(response => {
                if (response.data) {
                    message.success("修改成功");
                }
                else {
                    message.error("修改失败");
                }
                onCloseRole();
            }).catch(error => {
                console.log(error);
            });
        } catch (error) {
            console.error(error);
        }
        finally {
            if (roleActionRef.current) {
                roleActionRef.current.reload();
            }
        }
    }
    const onCreateRole = async (record: any) => {
        try {
            // 替换成实际的接口地址
            await createRole({ roleNo: record.roleNo, roleName: record.roleName, disable: !record.disable }).then(response => {
                if (response.data) {
                    message.success("创建成功");
                }
                else {
                    message.error("创建失败");
                }
                onCloseRole();
            }).catch(error => {
                console.log(error);
            });
        } catch (error) {
            console.error(error);
        }
        finally {
            if (roleActionRef.current) {
                roleActionRef.current.reload();
            }
        }
    }

    //菜单
    // 在这里调用你的接口获取 Tree 数据
    const onOpenMenu = async (id: number) => {
        setRoleId(id);
        setOpenMenu(true);
        try {
            listMenu(id);
            // // 替换成实际的接口地址
            // const response = await listBindRoleMenu({ id });
            // if (response) {
            //     setTreeData(loopMenuItem(response.data)); // 设置获取的数据到 state
            //     console.log(getCheckedIds(response.data))
            //     setDefaultCheckedKeys(getCheckedIds(response.data));
            // }
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };
    listMenu = async (roleId: number) => {
        //查询当前角色已经绑定的资源
        listBindMenuIdByRoleId({ roleId: roleId }).then(response => {
            if (response.data) {
                setSelectedMenuRowKeys(response.data);
                setCopySelectedMenuRowKeys(response.data);
            }
            else {
                setSelectedMenuRowKeys([]);
                setCopySelectedMenuRowKeys([]);
            }
        }).catch(error => {
            console.log(error);
        });
        // 替换成实际的接口地址
        return await listTreeMenu({}).then(response => {
            if (response.data) {
                response.data.forEach((item: any) => {
                    if (item.children) {
                        let preChildrenItem: any = undefined;
                        item.children.forEach((childrenItem: any) => {
                            if (childrenItem.hideInMenu && preChildrenItem && childrenItem.path.startsWith(preChildrenItem.path)) {
                                childrenItem.title = "-------" + childrenItem.title;
                            }
                            if (!childrenItem.hideInMenu) {
                                preChildrenItem = childrenItem;
                            }
                        })
                    }
                })
                setMenuDataSource(response.data);
                return response.data;
            }
        }).catch(error => {
            console.log(error);
        });
    }
    const onBindRoleMenu = async () => {
        setOpenMenu(true);
        try {
            // 替换成实际的接口地址
            await roleBindMenu({ roleId: roleId, bindMenuIds: bindMenuIds, unBindMenuIds: unBindMenuIds }).then(response => {
                if (response.data) {
                    message.success("保存成功");
                }
                else {
                    message.error("保存失败");
                }
                setOpenMenu(false);
            }).catch(error => {
                console.log(error);
            });
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };
    const onCloseMenu = () => {
        setOpenMenu(false);
    };
    const rowSelectionMenu: TableRowSelection<any> = {
        selectedRowKeys: selectedMenuRowKeys,
        onChange: (selectedRowKeys, selectedRows) => {
            setSelectedMenuRowKeys(selectedRowKeys);
            //新绑定的菜单
            let bindMenuId = selectedRowKeys.filter(p => !copySelectedMenuRowKeys || !copySelectedMenuRowKeys.includes(p));
            //需要解绑的菜单
            let unBindMenuId = copySelectedMenuRowKeys.filter(p => !selectedRowKeys || !selectedRowKeys.includes(p));
            setBindMenuIds(bindMenuId);
            setUnBindMenuIds(unBindMenuId);
            console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
            console.log("bindMenuId" + bindMenuId, "unBindMenuId" + unBindMenuId);
        },
        onSelect: (record, selected, selectedRows) => {
            console.log(record, selected, selectedRows);
        },
        onSelectAll: (selected, selectedRows, changeRows) => {
            console.log(selected, selectedRows, changeRows);
        },
    };

    // 资源
    const onOpenResource = async (id: number) => {
        setRoleId(id);
        setOpenResource(true);
        try {
            // 替换成实际的接口地址
            // const response = await listBindRoleMenu({ id });
            listResource(id);
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };
    const onCloseResource = () => {
        setOpenResource(false);
    }
    listResource = async (roleId: number) => {
        //查询当前角色已经绑定的资源
        listBindResourceIdByRoleId({ roleId: roleId }).then(response => {
            if (response.data) {
                setSelectedResourceRowKeys(response.data);
                setCopySelectedResourceRowKeys(response.data);
            }
            else {
                setSelectedResourceRowKeys([]);
                setCopySelectedResourceRowKeys([]);
            }
        }).catch(error => {
            console.log(error);
        });
        // 替换成实际的接口地址
        return await list({}).then(response => {
            if (response.data) {
                setResourceDataSource(response.data);
                return response.data;
            }
        }).catch(error => {
            console.log(error);
        });
    }
    const columns: ProColumns<API.ListRoleVo>[] = [
        {
            title: <strong>编号</strong>,
            dataIndex: 'id',
            // valueType: 'number',
            width: 48,
            search: false
        },
        {
            title: <strong>角色名称</strong>,
            dataIndex: 'roleName',
            ellipsis: true,
            // tip: '标题过长会自动收缩',

        },
        {
            title: <strong>角色编号</strong>,
            dataIndex: 'roleNo',
            ellipsis: true,
            // tip: '标题过长会自动收缩',

        },
        {
            title: <strong>是否禁用</strong>,
            dataIndex: 'disable',
            valueType: 'select',
            valueEnum: {
                false: { text: '启用' },
                true: { text: '禁用' },
            },
            search: true,
            render: (_text, record) => (
                <Space>
                    <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={() => onUpdateRoleDisable(record)} />
                </Space>
            ),
        },
        {
            title: <strong>操作</strong>,
            valueType: 'option',
            key: 'option',
            render: (_text, record, _index, _action) => [
                // <a
                //     key="editable"
                //     onClick={() => {
                //         action?.startEditable?.(record.id);
                //     }}
                // >
                //     编辑
                // </a>,
                <Link to='#' onClick={() => onOpenEditRole(record.id)} key="editable">
                    编辑
                </Link>,
                <Link to='#' onClick={() => onOpenMenu(record.id)} key="bindMenu">
                    绑定菜单
                </Link>,
                <Link to='#' onClick={() => onOpenResource(record.id)} key="bindResource">
                    绑定资源
                </Link>,
                <a onClick={() => { onDelSysRole(record); }} style={{ color: 'red' }} rel="noopener noreferrer" key="del">
                    删除
                </a>
            ],
        },
    ];

    const menuColumns: ProColumns[] = [
        {
            title: <strong>菜单名称</strong>,
            dataIndex: 'title',
            ellipsis: true,
            render: (text, record) => (
                <span style={{ color: record.hideInMenu ? '#888888' : 'inherit' }}>
                    {/* 在这里可以自定义字段内容 */}
                    <div>{record.icon ? IconMap[record.icon] : null} {record.title}</div>
                </span>
            ),
        },
        {
            title: <strong>是否隐藏</strong>,
            dataIndex: 'hideInMenu',
            search: false,
            width: 200,
            render: (text, record) => (
                <>
                    {record.hideInMenu ? <Tag color="default">隐藏</Tag> : <Tag color="success">显示</Tag>}
                </>
            ),
        },
        {
            title: <strong>菜单路径</strong>,
            dataIndex: 'path',
            ellipsis: true,
        }
    ];

    const resourceColumns: ProColumns[] = [
        {
            title: <strong>资源名称</strong>,
            dataIndex: 'name',
            ellipsis: true,
        },
        {
            title: <strong>资源路径</strong>,
            dataIndex: 'path',
            ellipsis: true,
            // tip: '标题过长会自动收缩',

        }
    ];
    const rowSelectionResource: TableRowSelection<any> = {
        selectedRowKeys: selectedResourceRowKeys,
        onChange: (selectedRowKeys, selectedRows) => {
            setSelectedResourceRowKeys(selectedRowKeys);
            //新绑定的资源
            let bindResourceId = selectedRowKeys.filter(p => !copySelectedResourceRowKeys || !copySelectedResourceRowKeys.includes(p));
            //需要解绑的资源
            let unBindResourceId = copySelectedResourceRowKeys.filter(p => !selectedRowKeys || !selectedRowKeys.includes(p));
            setBindResourceIds(bindResourceId);
            setUnBindResourceIds(unBindResourceId);
            console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
            console.log("bindResourceId" + bindResourceId, "unBindResourceId" + unBindResourceId);
        },
        onSelect: (record, selected, selectedRows) => {
            console.log(record, selected, selectedRows);
        },
        onSelectAll: (selected, selectedRows, changeRows) => {
            console.log(selected, selectedRows, changeRows);
        },
    };

    const onBindRoleResource = async () => {
        try {
            // 替换成实际的接口地址
            await roleBindResource({ roleId: roleId, bindResourceIds: bindResourceIds, unBindResourceIds: unBindResourceIds }).then(response => {
                if (response.data) {
                    message.success("保存成功");
                }
                else {
                    message.error("保存失败");
                }
                setOpenResource(false);
            }).catch(error => {
                console.log(error);
            });
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };

    return (
        <PageContainer>
            <ProTable<API.ListRoleVo, API.PageParams>
                bordered={true}
                columns={columns}
                actionRef={roleActionRef}
                cardBordered
                request={async (params = {}, sort, filter) => {
                    console.log(sort, filter);
                    let data = await listRole({ ...params, pageNum: params.current });
                    setDataSource(data.data?.list);
                    return { data: data.data?.list, total: data.data?.total };
                }}
                dataSource={dataSource}
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
                search={{
                    labelWidth: 'auto',
                }}
                options={{
                    setting: {
                        listsHeight: 400,
                    },
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
                pagination={{
                    pageSize: 20,
                    onChange: (page) => console.log(page),
                }}
                dateFormatter="string"
                headerTitle="角色列表"
                toolBarRender={() => [
                    <Button key="button" icon={<PlusOutlined />} type="primary" onClick={onOpenCreateRole}>
                        新建
                    </Button>,
                ]}
            />
            <Drawer title="绑定菜单" placement="right" onClose={onCloseMenu} open={openMenu} width={800}>
                <ProTable<API.ListRoleVo, API.PageParams>
                    // expandable={{
                    //     defaultExpandAllRows: true, // 展开所有行
                    // }}
                    rowSelection={{ ...rowSelectionMenu, checkStrictly }}
                    bordered={true}
                    columns={menuColumns}
                    actionRef={actionRef}
                    dataSource={menuDataSource}
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
                    pagination={false}
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
                // headerTitle="资源列表"
                />
                <Space style={{ marginTop: 20, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                    <Button type='primary' icon={<PlusOutlined />} onClick={onBindRoleMenu}>绑定菜单</Button>
                </Space>
            </Drawer>
            <Drawer title="绑定资源" placement="right" onClose={onCloseResource} open={openResource} width={800}>
                <ProTable<API.ListRoleVo, API.PageParams>
                    expandable={{
                        defaultExpandAllRows: true, // 展开所有行
                    }}
                    rowSelection={{ ...rowSelectionResource, checkStrictly }}
                    bordered={true}
                    columns={resourceColumns}
                    actionRef={actionRef}
                    dataSource={resourceDataSource}
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
                    pagination={false}
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
                // headerTitle="资源列表"
                />
                <Space style={{ marginTop: 20, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                    <Button type='primary' icon={<PlusOutlined />} onClick={onBindRoleResource}>绑定资源</Button>
                </Space>
            </Drawer>
            <Drawer title="创建角色" placement="right" onClose={onCloseRole} open={openCreateRole}>
                <ProForm
                    form={createForm}
                    initialValues={{
                        disable: true
                    }}
                    layout='horizontal' // 设置为垂直布局
                    onFinish={onCreateRole}
                    labelCol={{ span: 6 }}   // 控制 label 的布局，可以调整 span 的值
                    wrapperCol={{ span: 14 }} // 控制包裹内容的布局，可以调整 span 的值
                    submitter={{
                        render: (_: any) => (
                            <ProForm.Item
                                wrapperCol={{ offset: 6, span: 14 }}
                                colon={false}
                            >
                                <Button type="primary" onClick={() => createForm?.submit?.()}>
                                    创建角色
                                </Button>
                            </ProForm.Item>
                        ),
                    }}
                >
                    <ProFormText
                        width="md"
                        name="roleNo"
                        label="角色编号"
                        placeholder="请输入角色编号"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 50, message: '角色编号最大长度为50' }]}
                    />
                    <ProFormText
                        width="md"
                        name="roleName"
                        label="角色名称"
                        placeholder="请输入角色名称"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 50, message: '角色名称最大长度为50' }]}
                    />
                    <ProFormSwitch
                        width="md"
                        name="disable"
                        label="是否启用"
                        fieldProps={{
                            // size: 'large',
                        }}
                        checkedChildren="启用" unCheckedChildren="禁用"
                        rules={[{ required: true }]}
                    >

                    </ProFormSwitch>
                </ProForm>
            </Drawer>
            <Drawer title="编辑角色" placement="right" onClose={onCloseRole} open={openEditRole}>
                <ProForm
                    form={editForm}
                    initialValues={{
                        hideInMenu: false,
                        layout: true
                    }}
                    layout='horizontal' // 设置为垂直布局
                    onFinish={onUpdateRole}
                    labelCol={{ span: 6 }}   // 控制 label 的布局，可以调整 span 的值
                    wrapperCol={{ span: 14 }} // 控制包裹内容的布局，可以调整 span 的值
                    submitter={{
                        render: (_: any) => (
                            <ProForm.Item
                                wrapperCol={{ offset: 6, span: 14 }}
                                colon={false}
                            >
                                <Button type="primary" onClick={() => editForm?.submit?.()}>
                                    修改角色
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
                        name="roleNo"
                        label="角色编号"
                        disabled
                    />
                    <ProFormText
                        width="md"
                        name="roleName"
                        label="角色名称"
                        placeholder="请输入角色名称"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 50, message: '角色名称最大长度为50' }]}
                    />
                </ProForm>
            </Drawer>
        </PageContainer>
    );
};
export default RoleList;