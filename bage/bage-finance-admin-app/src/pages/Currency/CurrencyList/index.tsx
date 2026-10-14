import { ExclamationCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns } from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Tag, Switch, Modal, message } from 'antd';
import { useRef, useState } from 'react';
import request from 'umi-request';
import { list, del } from '@/services/ant-design-pro/currencyConfig';
import { Link } from 'umi';

const CurrencyList: React.FC = () => {
    const columns: ProColumns[] = [
        {
            title: <strong>编码</strong>,
            dataIndex: 'code',
            // valueType: 'number',
            search: false
        },
        {
            title: <strong>名称</strong>,
            dataIndex: 'name',
            ellipsis: true,
            search: false
            // tip: '标题过长会自动收缩',

        },
        {
            disable: true,
            title: <strong>汇率</strong>,
            dataIndex: 'exchangeRate',
            filters: true,
            search: false
        },
        {
            title: <strong>是否本位币</strong>,
            dataIndex: 'baseCurrencyFlag',
            valueType: 'text', // 设置 valueType 为 'text'，将布尔值显示为文本
            search: false,
            render: (text, record) => (
                <Tag color={record.baseCurrencyFlag ? 'green' : 'red'}>
                    {record.baseCurrencyFlag ? '是' : '否'}
                </Tag>
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
            title: <strong>操作</strong>,
            valueType: 'option',
            key: 'option',
            render: (text, record, _, action) => {
                const editLink = !record.baseCurrencyFlag && (
                    <Link to={"/FinanceSetting/CurrencyList/CurrencyDetail?id=" + record.id?.toString()} key="editable">
                        编辑
                    </Link>
                );
                const deleteLink = !record.baseCurrencyFlag && record.useCount <= 0 && (
                    <Link to='#' onClick={() => {
                        Modal.confirm({
                            title: <span style={{ fontWeight: 'bold' }}>删除币别</span>,
                            icon: <ExclamationCircleOutlined />,
                            content: '确认要删除币别"' + record.name + '"吗？',
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
                );
                return [
                    editLink,
                    deleteLink
                ]
            }
        },
    ];
    const actionRef = useRef<ActionType>();
    const [dataSource, setDataSource] = useState<API.ListAccountBookVoItem[]>();

    return (
        <PageContainer>
            <ProTable<API.ListAccountBookVoItem, API.PageParams>
                bordered={true}
                columns={columns}
                actionRef={actionRef}
                cardBordered
                request={async (params = {}, sort, filter) => {
                    console.log(sort, filter);
                    let data = await list({ ...params, pageNum: params.current });
                    setDataSource(data.data);
                    return { data: data.data };
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
                pagination={false}
                dateFormatter="string"
                headerTitle="币别列表"
                toolBarRender={() => [
                    <Button key="button" icon={<PlusOutlined />} type="primary" href='/FinanceSetting/CurrencyList/CurrencyCreate'>
                        新建
                    </Button>,
                ]}
            />
        </PageContainer>
    );
};
export default CurrencyList;