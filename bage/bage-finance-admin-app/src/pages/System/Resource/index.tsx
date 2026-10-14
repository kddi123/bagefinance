import { ExclamationCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns, ProForm, ProFormText } from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Tag, Switch, Modal, message, Drawer, Tree } from 'antd';
import { useRef, useState, useEffect, Key } from 'react';
import request from 'umi-request';
import { list, create, get, update, del } from '@/services/ant-design-pro/resource';
import { Link } from 'umi';


const Resource: React.FC = () => {
    const actionRef = useRef<ActionType>();
    const [openCreateResource, setOpenCreateResource] = useState(false);
    const [openUpdateResource, setOpenUpdateResource] = useState(false);
    const [formEdit] = ProForm.useForm();
    const [formCreate] = ProForm.useForm();
    const [createPid, setCreatePid] = useState<number>();
    const [id, setId] = useState<number>();


    // 在这里调用你的接口获取 Tree 数据
    const onOpenCreateResource = async (id: number, name: string) => {
        setCreatePid(id);
        setOpenCreateResource(true);
        formCreate.setFieldsValue({ pid: id, name: "", path: "", parentName: name });
    };

    const onCloseCreateResource = () => {
        setOpenCreateResource(false);
    };

    const onFinishCreate = async (record: any) => {
        const { parentName, ...submitValues } = record;
        submitValues.pid = createPid;
        // 替换成实际的接口地址
        await create(submitValues).then(response => {
            if (response.data) {
                setOpenCreateResource(false);
                message.success("创建成功");
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

    // 在这里调用你的接口获取 Tree 数据
    const onOpenUpdateResource = async (id: number) => {
        setId(id);
        setOpenUpdateResource(true);

        let data = await get({ id }).then(response => {
            return {
                ...response.data
            }
        });
        formEdit.setFieldsValue(data);
    };

    const onCloseUpdateResource = () => {
        setOpenUpdateResource(false);
    };

    const onFinishUpdate = async (record: any) => {

        const { parentName, ...submitValues } = record;
        // 替换成实际的接口地址
        await update(submitValues).then(response => {
            if (response.data) {
                message.success("修改成功");
                setOpenUpdateResource(false);
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

    const onDel = async (id: number, name: string) => {
        Modal.confirm({
            title: '确认删除资源',
            content: '确定要删除“' + name + '”吗？',
            okText: '确认',
            cancelText: '取消',
            onOk() {

                // 替换成实际的接口地址
                del({ id }).then(response => {
                    if (response.data) {
                        message.success("删除成功");
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
            title: <strong>资源名称</strong>,
            dataIndex: 'name',
            ellipsis: true,
            search: true,
            // tip: '标题过长会自动收缩',
            render: (text, record, _, action) => [
                <>[{record.id}] {record.name}</>
            ],

        },
        {
            title: <strong>资源路径</strong>,
            dataIndex: 'path',
            ellipsis: true,
            search: true
            // tip: '标题过长会自动收缩',

        },
        {
            title: <strong>操作</strong>,
            valueType: 'option',
            key: 'option',
            render: (text, record, _, action) => {
                const addLink = record.pid === 0 && (
                    <Link to='#' onClick={() => onOpenCreateResource(record.id, record.name)} key="editable">
                        新增
                    </Link>
                )
                const delLink = !record.children && (
                    <Link to="#" onClick={() => { onDel(record.id, record.name); }} style={{ color: 'red' }} rel="noopener noreferrer" key="del">
                        删除
                    </Link>
                )
                return [
                    addLink,
                    <Link to='#' onClick={() => onOpenUpdateResource(record.id)} key="editable">
                        编辑
                    </Link>,
                    delLink
                ]
            }
        },
    ];

    return (
        <PageContainer>
            <ProTable<API.ListRoleVo, API.PageParams>
                bordered={true}
                columns={columns}
                actionRef={actionRef}
                cardBordered
                request={async (params = {}, sort, filter) => {
                    console.log(sort, filter);
                    let data = await list({ ...params });
                    // setDataSource(data.data?.list);
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
                headerTitle="资源列表"
                toolBarRender={() => [
                    <Button key="button" icon={<PlusOutlined />} type="primary" onClick={() => { onOpenCreateResource(0, "无上级资源") }}>
                        新建
                    </Button>,
                ]}
            />
            <Drawer title="创建资源" placement="right" onClose={onCloseCreateResource} open={openCreateResource}>
                <ProForm
                    form={formCreate}
                    layout='horizontal' // 设置为垂直布局
                    onFinish={onFinishCreate}
                    labelCol={{ span: 6 }}   // 控制 label 的布局，可以调整 span 的值
                    wrapperCol={{ span: 14 }} // 控制包裹内容的布局，可以调整 span 的值
                    submitter={{
                        render: (_, dom) => (
                            <ProForm.Item
                                wrapperCol={{ offset: 6, span: 14 }}
                                colon={false}
                            >
                                <Button type="primary" onClick={() => formCreate?.submit?.()}>
                                    创建资源
                                </Button>
                            </ProForm.Item>
                        ),
                    }}
                >
                    <ProFormText
                        width="md"
                        name="parentName"
                        label="上级资源"
                        rules={[{ required: true }]}
                        disabled

                    />
                    <ProFormText
                        width="md"
                        name="name"
                        label="资源名称"
                        placeholder="请输入资源名称"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 50, message: '资源名称最大长度为50' }]}
                    />
                    <ProFormText
                        width="md"
                        name="path"
                        label="资源路径"
                        placeholder="请输入资源路径"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 200, message: '资源路径最大长度为200' }]}
                    />
                </ProForm>
            </Drawer>
            <Drawer title="编辑资源" placement="right" onClose={onCloseUpdateResource} open={openUpdateResource}>
                <ProForm
                    form={formEdit}
                    layout='horizontal' // 设置为垂直布局
                    onFinish={onFinishUpdate}
                    labelCol={{ span: 6 }}   // 控制 label 的布局，可以调整 span 的值
                    wrapperCol={{ span: 14 }} // 控制包裹内容的布局，可以调整 span 的值
                    submitter={{
                        render: (_, dom) => (
                            <ProForm.Item
                                wrapperCol={{ offset: 6, span: 14 }}
                                colon={false}
                            >
                                <Button type="primary" onClick={() => formEdit?.submit?.()}>
                                    编辑资源
                                </Button>
                            </ProForm.Item>
                        ),
                    }}
                >
                    <ProFormText
                        width="md"
                        name="id"
                        label="id"
                        rules={[{ required: true }]}
                        disabled

                    />
                    <ProFormText
                        width="md"
                        name="parentName"
                        label="上级资源"
                        rules={[{ required: true }]}
                        disabled

                    />
                    <ProFormText
                        width="md"
                        name="name"
                        label="资源名称"
                        placeholder="请输入资源名称"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 50, message: '资源名称最大长度为50' }]}
                    />
                    <ProFormText
                        width="md"
                        name="path"
                        label="资源路径"
                        placeholder="请输入资源路径"
                        fieldProps={{
                            // size: 'large',
                        }}
                        rules={[{ required: true },
                        { max: 200, message: '资源路径最大长度为200' }]}
                    />
                </ProForm>
            </Drawer>
        </PageContainer>
    );
};
export default Resource;