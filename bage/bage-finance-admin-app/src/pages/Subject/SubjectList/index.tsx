import { ExclamationCircleOutlined, PlusOutlined, ExportOutlined } from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns } from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Tag, Switch, Modal, message, Tabs } from 'antd';
import { useRef, useState } from 'react';
import request from 'umi-request';
import { list, disable, del, download } from '@/services/ant-design-pro/subject';
import { Link } from 'umi';

const { TabPane } = Tabs;

const SubjectList: React.FC = () => {
    const [activeKey, setActiveKey] = useState('1');

    const columns: ProColumns[] = [
        {
            title: <strong>序号</strong>,
            dataIndex: 'id',
            // valueType: 'number',
            width: 48,
            search: false
        },
        {
            title: <strong>科目编码</strong>,
            dataIndex: 'code',
            search: false,
            width: 200,
            render: (text, record) => (
                <span>
                    {/* 在这里可以自定义字段内容 */}
                    <div style={{ paddingLeft: record.nodeDepth * 20 }}>{text}</div>
                </span>
            ),
        },
        {
            disable: true,
            title: <strong>科目名称</strong>,
            dataIndex: 'name',
            filters: true,
            ellipsis: true,
            search: false,
            width: 200
        },
        {
            title: <strong>助记码</strong>,
            key: 'showTime',
            dataIndex: 'mnemonicCode',
            search: false
        },
        {
            title: <strong>余额方向</strong>,
            dataIndex: 'balanceDirectionText',
            // valueEnum: {
            //     false: { text: '启用' },
            //     true: { text: '禁用' },
            // },
            search: false
        },
        {
            title: <strong>数量核算</strong>,
            dataIndex: 'unitOfMeasurement',
            search: false
        },
        {
            title: <strong>辅助核算</strong>,
            dataIndex: 'assistCalculateText',
            search: false
        },
        {
            title: <strong>外币核算</strong>,
            dataIndex: 'currencyConfigText',
            search: false
        },
        {
            title: <strong>是否禁用</strong>,
            dataIndex: 'disable',
            valueType: 'select',
            valueEnum: {
                false: { text: '启用' },
                true: { text: '禁用' },
            },
            search: false,
            render: (text, record, _, action) => (
                <Space>
                    <Switch checkedChildren="启用" unCheckedChildren="禁用" disabled={record.parentSubjectDisable} checked={!record.disable} onChange={(checked: boolean) => {
                        console.log("禁用或启用：" + record.id);
                        disable({ id: record.id, disable: !checked })
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
            title: <strong>操作</strong>,
            valueType: 'option',
            key: 'option',
            width: 150,
            render: (text, record, _, action) => {
                const deleteLink = record.subjectType === 1 && (
                    <Link to="#" onClick={() => {
                        Modal.confirm({
                            title: <span style={{ fontWeight: 'bold' }}>删除账套</span>,
                            icon: <ExclamationCircleOutlined />,
                            content: '确认要删除科目“' + record.name + '”吗？',
                            okText: '确认',
                            cancelText: '取消',
                            onOk: async () => {
                                console.log("删除：" + record.id);
                                await del({ id: record.id })
                                    .then(response => {
                                        action?.reload();
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
                const addLink = (!record.disable && record.nodeDepth < 3) && (
                    <Link to={"/FinanceSetting/SubjectList/SubjectCreate?pid=" + record.id?.toString()} key="editable">
                        新增
                    </Link>
                );
                return [
                    addLink
                    ,
                    <Link to={"/FinanceSetting/SubjectList/SubjectDetail?id=" + record.id.toString()} rel="noopener noreferrer" key="view">
                        编辑
                    </Link>,
                    deleteLink
                ]
            }
        },
    ];
    const actionRef = useRef<ActionType>();
    const [dataSource, setDataSource] = useState<API.ListAccountBookVoItem[]>();
    const handleTabChange = (key: string) => {
        // 在这里可以获取到当前激活的 tab 的 key
        console.log('当前激活的 tab 的 key:', key);
        setActiveKey(key);
        if (actionRef.current) {
            actionRef.current.reload();
        }
    };
    // 点击按钮触发下载
    const onDownload = async () => {
        try {
            const response = await download();
        } catch (error) {
            console.error('下载失败:', error);
            message.error('下载失败');
        }
    };
       return (
        <PageContainer>
            <ProTable<API.ListAccountBookVoItem, API.PageParams>
                search={false}
                bordered={true}
                columns={columns}
                actionRef={actionRef}
                cardBordered
                request={async (params = {}, sort, filter) => {
                    console.log(sort, filter);
                    let data = await list({ ...params, subjectCate: activeKey, pageNum: params.current });
                    setDataSource(data.data);
                    return { data: data.data, total: data.data?.total };
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
                headerTitle={<Tabs defaultActiveKey="1" style={{ float: 'left', marginRight: 0 }} type="line" activeKey={activeKey} onChange={handleTabChange}>
                    <TabPane
                        tab={
                            <span>
                                资产
                            </span>
                        }
                        key="1"
                    ></TabPane>
                    <TabPane
                        tab={
                            <span>
                                负债
                            </span>
                        }
                        key="2"
                    ></TabPane>
                    <TabPane
                        tab={
                            <span>
                                权益
                            </span>
                        }
                        key="3"
                    ></TabPane>
                    <TabPane
                        tab={
                            <span>
                                成本
                            </span>
                        }
                        key="4"
                    ></TabPane>
                    <TabPane
                        tab={
                            <span>
                                损益
                            </span>
                        }
                        key="5"
                    ></TabPane>
                </Tabs>}
                toolBarRender={() => [
                    <Button key="button" icon={<ExportOutlined />} type="primary" onClick={onDownload}>
                        导出
                    </Button>
                ]}
            />
        </PageContainer>
    );
};
export default SubjectList;