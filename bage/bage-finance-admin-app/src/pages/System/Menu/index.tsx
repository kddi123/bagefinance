import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProTable, ProColumns } from '@ant-design/pro-components';
import { Tree, Button, Tabs, Card, Space, Row, Col, Tag, TreeSelect, message, Modal, Drawer } from 'antd';
import type { DataNode, TreeProps } from 'antd/es/tree';
import React, { useRef, useState, useEffect, useCallback } from 'react';
import { listTreeMenu, listTreeSelectMenu, getMenuById, createMenu, updateMenu, delMenu } from '@/services/ant-design-pro/menu';
import {
    ExclamationCircleOutlined,
    SettingOutlined,
    PlusOutlined,
    DeleteOutlined,
    RestOutlined,
    LinkOutlined, AccountBookOutlined, SmileOutlined, FileTextOutlined, PieChartOutlined, FileExcelOutlined, CreditCardOutlined, TransactionOutlined, UploadOutlined, FileProtectOutlined, HomeOutlined, BankOutlined, ReadOutlined, ToolOutlined, HistoryOutlined, EditOutlined
} from '@ant-design/icons';
import { FormattedMessage, history, SelectLang, useIntl, useModel } from '@umijs/max';
import { flushSync } from 'react-dom';
import { ResponseStructure } from '@/requestErrorConfig';
import { currentUser } from '@/services/ant-design-pro/api';
import { IconMap, loopMenuItem } from '@/services/common/iconMap'
import { Link } from 'umi';

const { TabPane } = Tabs;

const Menu: React.FC = () => {
    const [open, setOpen] = useState(false);
    const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
        console.log('selected', selectedKeys, info);
        setSelectedKeys(selectedKeys);
        setEdit(selectedKeys.length === 0);
        handleLoadFormData(selectedKeys[0]);
    };

    const onCheck: TreeProps['onCheck'] = (checkedKeys, info) => {
        console.log('onCheck', checkedKeys, info);
    };
    const [treeData, setTreeData] = useState();
    const [treeSelectData, setTreeSelectData] = useState([]);
    const [selectedKeys, setSelectedKeys] = useState([]);
    const [isEdit, setEdit] = useState(true);
    const [form] = ProForm.useForm();

    const handleLoadFormData = async (id?: number) => {
        try {
            setOpen(true);
            setEdit(true);
            if (id === undefined || id === 0) {
                handleResetMenu();
                return;
            }
            // 这里调用接口或其他异步操作获取数据
            const formData = await getMenuById({ id: id });
            // 将获取到的数据设置到表单中
            form.setFieldsValue(formData.data);
        } catch (error) {
            console.error('加载表单数据失败', error);
        }
    };
    //映射菜单对应的图标
    const loopMenuItem = (menus: API.ListTreeMenuVo[], type: number): API.ListTreeMenuVo[] => menus.map(({ id, icon, children, title, level, ...item }) => ({
        ...item,
        // icon:{IconMap[icon]},
        title: (<div>{IconMap[icon]}<span style={{ marginLeft: 5 }}>{title}</span>
            {
                type === 0 && (
                    <>
                        <Button type="primary" shape="circle" danger style={{ float: 'right' }} size='small' onClick={() => del(id, title)} icon={<DeleteOutlined />}></Button>
                        <Button type="primary" shape="circle" style={{ float: 'right', marginRight: 10 }} size='small' onClick={() => handleLoadFormData(id)} icon={<EditOutlined />}></Button>
                        <Button type="primary" shape="circle" hidden={level === 3} ghost={level === 2} title={(level < 2) ? "创建菜单" : "创建接口"} style={{ float: 'right', marginRight: 10 }} size='small' onClick={() => clickCreateChildMenu(id)} icon={<PlusOutlined />}></Button>
                    </>
                )
            }
        </div >),
        children: children && loopMenuItem(children, type)
    }));
    const handleResetMenu = () => {
        setSelectedKeys([]); // 将选中的节点重置为空数组
        form.setFieldsValue({
            pid: 0,
            name: "",
            path: "",
            component: "",
            icon: "",
            layout: true,
            hideInMenu: false,
            redirect: "",
            sort: 0
        });
    };
    const clickCreateChildMenu = (id?: number) => {
        setOpen(true);
        setEdit(false);
        handleResetMenu();
        form.setFieldsValue({
            pid: id,
        });

    }
    // 在这里调用你的接口获取 Tree 数据
    const listTreeMenuData = async () => {
        try {
            return await listTreeMenu({});
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };

    const listTreeSelectMenuData = async () => {
        try {
            // 替换成实际的接口地址
            const response = await listTreeSelectMenu();
            if (response) {
                setTreeSelectData(loopMenuItem(response.data, 1)); // 设置获取的数据到 state
            }
        } catch (error) {
            console.error('Error fetching tree data:', error);
        }
    };
    useEffect(() => {
        listTreeMenuData();
        listTreeSelectMenuData();
    }, []); // 空数组表示仅在组件加载时调用一次
    const onFinish1 = async (values: any) => {
        if (isEdit) {
            await updateMenu(values).then(response => {
                if (response.data) {
                    setOpen(false);
                    message.success("修改成功");
                    // listTreeMenuData();
                    // listTreeSelectMenuData();
                }
                else {
                    message.error("修改失败");
                }
            }).catch(error => {
                console.log(error);
            }).finally(() => {
                if (actionRef.current) {
                    actionRef.current.reload();
                }
            });
        }
        else {
            await createMenu(values).then(response => {
                if (response.data) {
                    setOpen(false);
                    message.success("创建成功");
                    // listTreeMenuData();
                    // listTreeSelectMenuData();
                }
                else {
                    message.error("创建失败");
                }
            }).catch(error => {
                console.log(error);
            }).finally(() => {
                if (actionRef.current) {
                    actionRef.current.reload();
                }
            });
        }
    }
    const del = async (id?: number, name?: string) => {
        Modal.confirm({
            title: '确认删除菜单',
            content: '确定要删除菜单“' + name + '”吗？',
            okText: '确认',
            cancelText: '取消',
            onOk() {
                delMenu({ id: id }).then(response => {
                    if (response.data) {
                        message.success("删除成功");
                        // listTreeMenuData();
                        // listTreeSelectMenuData();
                    }
                    else {
                        message.error("删除失败");
                    }
                }).catch(error => {
                    console.log(error);
                }).finally(() => {
                    if (actionRef.current) {
                        actionRef.current.reload();
                    }
                });
            }
        })
    }

    const columns: ProColumns[] = [

        {
            title: <strong>菜单名称</strong>,
            dataIndex: 'title',
            search: true,
            render: (text, record) => (
                <span style={{ color: record.hideInMenu ? '#888888' : 'inherit' }}>
                    {/* 在这里可以自定义字段内容 */}
                    <div>{IconMap[record.icon]} {text}</div>
                </span>
            ),
        },
        {
            title: <strong>是否隐藏</strong>,
            dataIndex: 'hideInMenu',
            search: false,
            width: 100,
            render: (text, record) => (
                <>
                    {record.hideInMenu ? <Tag color="default">隐藏</Tag> : <Tag color="success">显示</Tag>}
                </>
            ),
        },
        {
            title: <strong>顺序</strong>,
            dataIndex: 'sort',
            search: false,
            width: 50
        },
        {
            title: <strong>路由</strong>,
            dataIndex: 'path',
            search: false,
            ellipsis:true
        },
        {
            title: <strong>操作</strong>,
            valueType: 'option',
            key: 'option',
            width: 150,
            render: (text, record, _, action) => {
                const deleteLink = (!record.children) && (
                    <Link to="#" onClick={async () => {
                        await del(record.id, record.title);
                    }} style={{ color: 'red' }} rel="noopener noreferrer" key="del">
                        删除
                    </Link>
                );
                const addLink = (record.pid === 0) && (
                    <Link to="#" onClick={() => clickCreateChildMenu(record.id)} key="editable">
                        新增
                    </Link>
                );
                return [
                    addLink
                    ,
                    <Link to="#" onClick={() => handleLoadFormData(record.id)} rel="noopener noreferrer" key="view">
                        编辑
                    </Link>,
                    deleteLink
                ]
            }
        },
    ];
    const actionRef = useRef<ActionType>();
    return (
        <PageContainer>
            <ProTable<API.ListTreeMenuVo[], API.PageParams>
                search={true}
                bordered={true}
                columns={columns}
                actionRef={actionRef}
                cardBordered
                request={async (params, sort, filter) => {
                    let data = await listTreeMenu({ ...params });
                    data.data.map((item) => {
                        if (item.children) {
                            let preChildrenItem;
                            item.children.map((childrenItem) => {
                                if (childrenItem.hideInMenu && preChildrenItem && childrenItem.path.startsWith(preChildrenItem.path)) {
                                    childrenItem.title = "-------" + childrenItem.title;
                                }
                                if (!childrenItem.hideInMenu) {
                                    preChildrenItem = childrenItem;
                                }
                            })
                        }
                    })
                    return { data: data.data };
                }}
                // dataSource={dataSource}
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
                // search={{
                //     labelWidth: 'auto',
                // }}
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
                // headerTitle={}
                toolBarRender={() => [
                    <Button type='primary' icon={<PlusOutlined />} onClick={() => clickCreateChildMenu(0)}>创建一级菜单</Button>
                    // <Button key="button" icon={<ExportOutlined />} type="primary" onClick={onDownload}>
                    //     导出
                    // </Button>
                ]}
            />
            {/* <Card>
                <Tree
                    defaultExpandAll={true}
                    // onSelect={onSelect}
                    // onCheck={onCheck}
                    treeData={treeData}
                    // selectedKeys={selectedKeys}
                    blockNode
                />
                <Space style={{ marginTop: 20, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                    <Button type='primary' icon={<PlusOutlined />} onClick={handleResetMenu}>创建一级菜单</Button>
                </Space>
            </Card> */}

            <Drawer title={isEdit ? '编辑菜单' : '创建菜单'} placement="right" onClose={() => { setOpen(false) }} open={open} width={600}>
                <ProForm
                    form={form}
                    initialValues={{
                        hideInMenu: false,
                        layout: true
                    }}
                    layout='horizontal' // 设置为垂直布局
                    onFinish={onFinish1}
                    labelCol={{ span: 7 }}   // 控制 label 的布局，可以调整 span 的值
                    wrapperCol={{ span: 17 }} // 控制包裹内容的布局，可以调整 span 的值
                    submitter={{
                        render: (_, dom) => (
                            <ProForm.Item
                                wrapperCol={{ offset: 7, span: 17 }}
                                colon={false}
                            >
                                <Button type="primary" onClick={() => form?.submit?.()}>
                                    {isEdit ? '编辑菜单' : '创建菜单'}
                                </Button>
                            </ProForm.Item>
                        ),
                    }}
                >
                    {isEdit && (
                        <ProFormText
                            width="md"
                            name="id"
                            label="ID"
                            disabled

                        />
                    )}
                    <ProForm.Item name="pid" label="父级菜单"
                        rules={[{ required: true }]}>
                        <TreeSelect
                            showSearch
                            allowClear
                            treeDefaultExpandAll
                            treeData={treeSelectData}
                            placeholder="请选择父节点，不选择表示一级菜单"
                            disabled
                        />
                    </ProForm.Item>
                    <ProFormText
                        width="md"
                        name="name"
                        label="菜单名称"
                        placeholder="请输入菜单名称"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 50, message: '菜单名称最大长度为50' }]}
                    />
                    <ProFormText
                        width="md"
                        name="path"
                        label="菜单路由"
                        placeholder="请输入菜单路由"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 200, message: '菜单路径最大长度为200' }]}
                    />
                    <ProFormText
                        width="md"
                        name="component"
                        label="菜单组件"
                        placeholder="请输入菜单组件"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[
                            { max: 200, message: '菜单组件最大长度为200' }]}
                    />
                    <ProFormText
                        width="md"
                        name="redirect"
                        label="重定向地址"
                        placeholder="请输入重定向地址"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[
                            { max: 200, message: '重定向地址最大长度为200' }]}
                    />
                    <ProFormText
                        width="md"
                        name="icon"
                        label="图标"
                        placeholder="请输入图标"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[
                            { max: 20, message: '菜单组件最大长度为20' }]}
                    />
                    <ProFormText
                        width="md"
                        name="sort"
                        label="顺序"
                        placeholder="请输入顺序"
                        fieldProps={{
                            type: 'number', // 只允许输入数字
                        }}
                        rules={[
                            { required: true }]}
                    />
                    <ProFormSwitch
                        name="hideInMenu"
                        label="是否隐藏菜单"
                        fieldProps={{
                            checkedChildren: '隐藏',
                            unCheckedChildren: '显示',
                            defaultChecked: false, // 设置默认选中状态
                        }}
                    />
                    <ProFormSwitch
                        name="layout"
                        label="是否使用布局"
                        fieldProps={{
                            checkedChildren: '使用',
                            unCheckedChildren: '不使用',
                            defaultChecked: true, // 设置默认选中状态
                        }}
                    />
                </ProForm>
            </Drawer>

        </PageContainer >
    );
};

export default Menu;