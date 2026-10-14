import { ExclamationCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns } from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Tag, Switch, Modal, message } from 'antd';
import { useRef, useState } from 'react';
import request from 'umi-request';
import { accountBookList, disableAccountBook, delAccountBook } from '@/services/ant-design-pro/accountBook';
import styles from './index.less';
import { Link } from 'umi';

type GithubIssueItem = {
    url: string;
    id: number;
    number: number;
    title: string;
    labels: {
        name: string;
        color: string;
    }[];
    state: string;
    comments: number;
    created_at: string;
    updated_at: string;
    closed_at?: string;
    disable?: boolean;
};
const AccountList: React.FC = () => {
    const columns: ProColumns<API.ListAccountBookVoItem>[] = [
        {
            title: <strong>编号</strong>,
            dataIndex: 'id',
            // valueType: 'number',
            width: 48,
            search: false
        },
        {
            title: <strong>公司名称</strong>,
            dataIndex: 'companyName',
            ellipsis: true,
            // tip: '标题过长会自动收缩',

        },
        {
            disable: true,
            title: <strong>增值税种类</strong>,
            dataIndex: 'valueAddedTaxCate',
            filters: true,
            ellipsis: true,
            search: false
        },
        {
            title: <strong>会计准则</strong>,
            key: 'showTime',
            dataIndex: 'accountingStandard',
            search: false
        },
        {
            title: <strong>启用时间</strong>,
            dataIndex: 'startTime',
            valueType: 'date',
            search: false
        },
        {
            title: <strong>创建时间</strong>,
            dataIndex: 'createTime',
            valueType: 'dateTime',
            search: false
        },
        {
            disable: true,
            title: <strong>启用凭证审核</strong>,
            dataIndex: 'enableVoucherVerify',
            search: false,
            renderFormItem: (_, { defaultRender }) => {
                return defaultRender(_);
            },
            render: (_, record) => (
                <Space>
                    {/* <Switch checkedChildren="审核" unCheckedChildren="不审核" defaultChecked={record.enableVoucherVerify} /> */}

                    <Tag color={record.enableVoucherVerify ? "red" : "blue"}>
                        {record.enableVoucherVerify ? "审核" : "不审核"}
                    </Tag>
                </Space>
            ),
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
            render: (text, record, _, action) => (
                <Space>
                    <Switch checkedChildren="启用" unCheckedChildren="禁用" checked={!record.disable} onChange={(checked: boolean) => {
                        console.log("禁用或启用：" + record.id);
                        const updatedDataSource = dataSource?.map(item => {
                            if (item.id === record.id) {
                                item.disable = !checked;
                            }
                            return item;
                        });
                        setDataSource(updatedDataSource);
                        disableAccountBook({ id: record.id, disable: !checked })
                            .then(response => {
                                if (!response.data) {
                                    message.error("操作失败");
                                    const updatedDataSource = dataSource?.map(item => {
                                        if (item.id === record.id) {
                                            item.disable = checked;
                                        }
                                        return item;
                                    });
                                    setDataSource(updatedDataSource);
                                }
                            })
                            .catch(error => {
                                console.log(error);
                                const updatedDataSource = dataSource?.map(item => {
                                    if (item.id === record.id) {
                                        item.disable = checked;
                                    }
                                    return item;
                                });
                                setDataSource(updatedDataSource);
                            });
                    }} />
                </Space>
            ),
        },
        {
            title: <strong>操作</strong>,
            valueType: 'option',
            key: 'option',
            align: 'center',
            render: (text, record, _, action) => [
                // <a
                //     key="editable"
                //     onClick={() => {
                //         action?.startEditable?.(record.id);
                //     }}
                // >
                //     编辑
                // </a>,
                <Link to={"/FinanceSetting/AccountList/AccountUpdate?id=" + record.id.toString()} rel="noopener noreferrer" key="editable">
                    编辑
                </Link>,
                <Link to={"/FinanceSetting/AccountList/AccountDetail?id=" + record.id.toString()} rel="noopener noreferrer" key="view">
                    查看
                </Link>,
                <a onClick={() => {
                    Modal.confirm({
                        title: <span style={{ fontWeight: 'bold' }}>删除账套</span>,
                        icon: <ExclamationCircleOutlined />,
                        content: '确认要删除"' + record.companyName + '"的账套吗？',
                        okText: '确认',
                        cancelText: '取消',
                        onOk: async () => {
                            console.log("删除：" + record.id);
                            await delAccountBook({ id: record.id })
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

                            //return { data: data.data?.list, total: data.data?.total };
                        }
                    });
                }} style={{ color: 'red' }} rel="noopener noreferrer" key="del">
                    删除
                </a>,
                // <TableDropdown
                //     key="actionGroup"
                //     onSelect={() => action?.reload()}
                //     menus={[
                //         { key: 'copy', name: '复制' },
                //         { key: 'delete', name: '删除' },
                //     ]}
                // />,
            ],
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
                    let data = await accountBookList({ ...params, pageNum: params.current });
                    setDataSource(data.data?.list);
                    return { data: data.data?.list, total: data.data?.total };
                    // return await request<{
                    //     data: API.ListAccountBookVoItem[];
                    // }>('https://proapi.azurewebsites.net/github/issues', {
                    //     params,
                    // });
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
                headerTitle="账套列表"
                toolBarRender={() => [
                    <Link to="/FinanceSetting/AccountList/AccountCreate" key="button">
                        <Button icon={<PlusOutlined />} type="primary">
                            新建
                        </Button>
                    </Link>,
                    // <Dropdown
                    //   key="menu"
                    //   menu={{
                    //     items: [
                    //       {
                    //         label: '1st item',
                    //         key: '1',
                    //       },
                    //       {
                    //         label: '2nd item',
                    //         key: '1',
                    //       },
                    //       {
                    //         label: '3rd item',
                    //         key: '1',
                    //       },
                    //     ],
                    //   }}
                    // >
                    //   <Button>
                    //     <EllipsisOutlined />
                    //   </Button>
                    // </Dropdown>,
                ]}
            />
        </PageContainer>
    );
};
export default AccountList;