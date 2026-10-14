import { ExclamationCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns } from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Switch, Modal, message, Tag } from 'antd';
import { useRef } from 'react';
import request from 'umi-request';
import { list, del, updateDefaultFlag } from '@/services/ant-design-pro/voucherWordConfig';
import { Link } from '@umijs/max';

const columns: ProColumns[] = [
    {
        title: '凭证字',
        dataIndex: 'voucherWord',
        ellipsis: true,
        // tip: '标题过长会自动收缩',

    },
    {
        disable: true,
        title: '打印标题',
        dataIndex: 'printTitle',
        filters: true,
        ellipsis: true,
        search: false
    },
    {
        title: <strong>是否默认</strong>,
        dataIndex: 'defaultFlag',
        valueType: 'select',
        valueEnum: {
            false: { text: '是' },
            true: { text: '否' },
        },
        search: false,
        render: (text, record, _, action) => (
            <Space>
                <Switch disabled={record.defaultFlag} checkedChildren="是" unCheckedChildren="否" checked={record.defaultFlag} onChange={(checked: boolean) => {
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
        title: <strong>状态</strong>,
        dataIndex: 'useCount',
        valueType: 'text', // 设置 valueType 为 'text'，将布尔值显示为文本
        search: false,
        render: (text, record) => (
            <Tag color={record.useCount > 0 ? 'red' : 'green'}>
                {record.useCount > 0 ? '已使用' : '未使用'}
            </Tag>
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
                <Link to={"/FinanceSetting/VoucherWordConfig/Detail?id=" + record.id?.toString()} key="editable">
                    编辑
                </Link>,
                delLink
                ,
            ]
        }
    },
];

export default () => {
    const actionRef = useRef<ActionType>();
    return (
        <PageContainer>
            <ProTable<API.ListAccountBookVoItem, API.PageParams>
                columns={columns}
                actionRef={actionRef}
                cardBordered
                bordered
                request={async (params = {}, sort, filter) => {
                    console.log(sort, filter);
                    let data = await list({ ...params, pageNum: params.current });
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
                search={false}
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
                dateFormatter="string"
                headerTitle="凭证字列表"
                toolBarRender={() => [
                    <Button key="button" icon={<PlusOutlined />} type="primary" href='/FinanceSetting/VoucherWordConfig/Create'>
                        新建
                    </Button>
                ]}
            />
        </PageContainer>
    );
};