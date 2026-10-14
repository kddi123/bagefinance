import React, { useState, useEffect, useRef } from 'react';

import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProFormCheckbox, recordKeyToString } from '@ant-design/pro-components';
import { Avatar, List, Radio, Space, Card, Row, Col, Affix, Checkbox, Form, Button, Input, DatePicker, Select, InputNumber, message, Modal }
    from 'antd';
import { history } from '@umijs/max';
import { Link } from 'react-router-dom';
import { DownOutlined, ExclamationCircleOutlined } from '@ant-design/icons';

import { list, del } from '@/services/ant-design-pro/voucher';
import { list as listVoucherWordConfig } from '@/services/ant-design-pro/voucherWordConfig';
import { listByCateAndCodeAndName } from '@/services/ant-design-pro/subject';
import { listMember } from '@/services/ant-design-pro/member';



import moment from 'moment';

import { digitUppercase } from "pixiu-number-toolkit";

const { RangePicker } = DatePicker;

import { formatNumber, formatAmount } from '@/common';

import './index.less'; // 导入CSS样式

type PaginationPosition = 'top' | 'bottom' | 'both';

type PaginationAlign = 'start' | 'center' | 'end';

const positionOptions = ['top', 'bottom', 'both'];

const alignOptions = ['start', 'center', 'end'];

const VoucherList: React.FC = () => {
    const [position, setPosition] = useState<PaginationPosition>('bottom');
    const [align, setAlign] = useState<PaginationAlign>('center');
    const [form] = Form.useForm();
    const [expand, setExpand] = useState(false);
    const [data, setData] = useState([]); // 原始的备注值
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
    const [voucherWordConfigOptions, setVoucherWordConfigOptions] = useState([]);
    const [defaultSelectVoucherWordConfigValue, setDefaultSelectVoucherWordConfigValue] = useState();
    const [subjectData, setSubjectData] = useState([]);
    const [selectSubjectData, setSelectSubjectData] = useState();
    const [memberData, setMemberData] = useState([]);
    const [selectMemberData, setSelectMemberData] = useState();


    //查询科目列表
    const onSearchVoucher = async (params?: any) => {
        console.log(params)
        params.pageNum = params.pageNum ? params.pageNum : 1;
        return await list({ ...params }).then(response => {
            setData(response.data.list);
            setPagination({
                ...pagination,
                current: params.pageNum,
                total: response.data.total,
            });
        }).catch(() => {

        });
    }
    const handlePaginationChange = (page: number) => {
        const params = { ...form.getFieldsValue(), pageNum: page };
        onSearchVoucher(params);
    };
    //获取凭证字列表
    const fetchVoucherWordConfigData = async () => {
        try {
            await listVoucherWordConfig().then(response => {
                if (response.data) {
                    const dataArray = response.data.map(data => ({
                        value: data.id,
                        label: data.voucherWord
                    }));
                    setVoucherWordConfigOptions(dataArray);
                    if (dataArray.length > 0) {
                        setDefaultSelectVoucherWordConfigValue(dataArray[0])
                    }
                }
            }).catch(error => {
            });
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };
    //获取科目列表
    const fetchSubjectListData = async () => {
        try {
            await listByCateAndCodeAndName({}).then(response => {
                if (response.data) {
                    const dataArray = response.data.map(data => ({
                        value: data.id,
                        label: data.code + " " + data.fullName
                    }));
                    setSubjectData(dataArray);
                }
            }).catch(error => {
            });
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };
    //获取用户列表
    const fetchMemberListData = async () => {
        try {
            await listMember({}).then(response => {
                if (response.data) {
                    const dataArray = response.data.map(data => ({
                        value: data.id,
                        label: data.nickName
                    }));
                    setMemberData(dataArray);
                }
            }).catch(error => {
            });
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };
    useEffect(() => {
        onSearchVoucher({});
        fetchVoucherWordConfigData();
        fetchSubjectListData();
        fetchMemberListData();
    }, []);
    return (
        <PageContainer >
            <Card style={{ marginBottom: 20 }}>
                <Form form={form} name="advanced_search"
                    onFinish={onSearchVoucher}
                >
                    <Row gutter={24}>
                        <Col>
                            <Select
                                defaultValue="0"
                                style={{ width: 120 }}
                                options={[
                                    { value: '0', label: '会计期间' },
                                    { value: '1', label: '凭证日期' }
                                ]}
                            />
                        </Col>
                        <Col>
                            <Form.Item
                                name={'voucherDate'}
                            >
                                <RangePicker picker="month" />
                                {/* <RangePicker /> */}
                            </Form.Item>
                        </Col>
                        <Col>
                            <Form.Item
                                name={'voucherWordConfigId'}
                                label='凭证字'
                            >
                                <Select
                                    style={{ width: 70 }}
                                    options={voucherWordConfigOptions}
                                    onChange={(value) => setDefaultSelectVoucherWordConfigValue(value)}
                                />
                            </Form.Item>
                        </Col>
                        <Col>
                            <Form.Item
                                label='制单人'
                                name={"memberId"}
                            >
                                <Select
                                    style={{ width: 120 }}
                                    options={memberData}
                                    onChange={(value) => setSelectMemberData(value)}
                                />
                            </Form.Item>
                        </Col>
                        <Col>
                            <Form.Item
                                label='摘要'
                                name={'summary'}
                            >
                                <Input />
                            </Form.Item>
                        </Col>
                    </Row>

                    {expand ? (
                        <>
                            <Row gutter={24}>
                                <Col>
                                    <Form.Item
                                        label='备注'
                                        name={'notes'}
                                    >
                                        <Input />
                                    </Form.Item>
                                </Col>
                                <Col>
                                    <Form.Item
                                        label='科目'
                                        name={'subjectId'}
                                    >
                                        <Select
                                            style={{ width: 150 }}
                                            options={subjectData}
                                            onChange={(value) => setSelectSubjectData(value)}
                                        />
                                    </Form.Item>
                                </Col>
                                <Col>
                                    <Space>
                                        <Form.Item
                                            label='金额'
                                            name='beginTotalAmount'
                                        >
                                            <InputNumber precision={2} prefix="￥" />
                                        </Form.Item>
                                        <label style={{ height: '46px', display: 'inline-block' }}>至</label>
                                        <Form.Item
                                            name='endTotalAmount'
                                        >
                                            <InputNumber precision={2} prefix="￥" />
                                        </Form.Item>
                                    </Space>
                                </Col>
                                <Col>
                                    <Space>
                                        <Form.Item
                                            label='凭证号'
                                            name={'beginVoucherNumber'}
                                        >
                                            <InputNumber min={1} />
                                        </Form.Item>
                                        <label style={{ height: '46px', display: 'inline-block' }}>至</label>
                                        <Form.Item
                                            name={'endVoucherNumber'}
                                        >
                                            <InputNumber min={1} />
                                        </Form.Item>
                                    </Space>

                                </Col>
                            </Row>
                            <Row gutter={24}>
                                <Col>
                                    <Form.Item
                                        label='排序'
                                        name={'sortRule'}
                                    >
                                        <Select
                                            defaultValue="凭证号升序↑"
                                            style={{ width: 150 }}
                                            options={[
                                                { value: '0', label: '凭证号升序↑' },
                                                { value: '1', label: '凭证号降序↓' },
                                                { value: '2', label: '凭证日期升序↑' },
                                                { value: '3', label: '凭证日期降序↓' },
                                            ]}
                                        />
                                    </Form.Item>
                                </Col>
                            </Row>
                        </>
                    ) : <></>
                    }

                    <div style={{ textAlign: 'right' }}>
                        <Space size="small">
                            <Button type="primary" htmlType="submit">
                                查询
                            </Button>
                            <Button
                                onClick={() => {
                                    form.resetFields();
                                }}
                            >
                                重置
                            </Button>
                            <a
                                style={{ fontSize: 12 }}
                                onClick={() => {
                                    setExpand(!expand);
                                }}
                            >
                                <DownOutlined rotate={expand ? 180 : 0} /> {expand ? '折叠' : '展开'}
                            </a>
                        </Space>
                    </div>
                </Form>
            </Card>
            <Card>
                <Row style={{ marginBottom: 15 }}>
                    <Col span={24} style={{ textAlign: 'right' }}>
                        <Space>
                            <Button>打印</Button>
                            <Button>导出凭证</Button>
                            <Button>导入凭证</Button>
                            <Button>整理凭证</Button>
                            <Button>回收站</Button>
                        </Space>
                    </Col>
                </Row>
                <Affix offsetTop={50}>
                    <Row className="custom-head">
                        <Col span={5}><Checkbox>摘要</Checkbox></Col>
                        <Col span={8}>科目</Col>
                        <Col span={1}>数量</Col>
                        <Col span={1}>单价</Col>
                        <Col span={2}>币别</Col>
                        <Col span={2}>原币金额</Col>
                        <Col span={1}>汇率</Col>
                        <Col span={2}>借方金额</Col>
                        <Col span={2}>贷方金额</Col>
                    </Row>
                </Affix>
                <List
                    pagination={{
                        ...pagination,
                        showQuickJumper: true,
                        onChange: handlePaginationChange,
                    }} bordered={false}
                    dataSource={data}
                    renderItem={(item, index) => (
                        <List.Item>
                            <List.Item.Meta
                                // title={ }
                                description={
                                    <>
                                        <div className="row-container">
                                            <Row className="title">
                                                <Col span={12}>
                                                    <Space style={{ border: 0, margin: 0, padding: 0, lineHeight: '40px' }} size={10}>
                                                        <Checkbox></Checkbox>
                                                        <span>日期：{moment(item.voucherDate).format('YYYY-MM-DD')}</span><span>凭证字号：{item.voucherWord} - {item.voucherNumber} </span><a>附件：{item.documentNum}</a>
                                                    </Space>
                                                </Col>
                                                <Col span={12} style={{ textAlign: 'right' }}>
                                                    <Space style={{ border: 0, margin: 0, padding: 0, lineHeight: '40px' }} size={10}>
                                                        <Link to={`/Voucher/VoucherUpdate?id=${item.id}`}>修改</Link>
                                                        <Link to={`/Voucher/VoucherCreate?copyId=${item.id}`}>复制</Link>
                                                        <Link to='#' onClick={() => {
                                                            Modal.confirm({
                                                                title: <span style={{ fontWeight: 'bold' }}>删除凭证字</span>,
                                                                icon: <ExclamationCircleOutlined />,
                                                                content: '确认要删除凭证"' + item.voucherNumber + '"吗？',
                                                                okText: '确认',
                                                                cancelText: '取消',
                                                                onOk: async () => {
                                                                    await del({ id: item.id, voucherNumber: item.voucherNumber })
                                                                        .then(response => {
                                                                            if (response.data) {
                                                                                // action?.reload();
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
                                                        <Link to={'#'}>插入</Link>
                                                        <Link to={'#'}>红冲</Link>
                                                        <Link to={'/Voucher/VoucherPrint?id=' + item.id} target='_blank'>打印</Link>
                                                    </Space>
                                                </Col>
                                            </Row>
                                            {/* 遍历 voucherSubjectDetailVoList */}
                                            {
                                                item.voucherSubjectDetailVoList.map((detailItem, detailIndex) => (
                                                    <Row className="custom-row" key={detailIndex}>
                                                        <Col span={5}>{detailItem.summary}</Col>
                                                        <Col span={8}>{detailItem.showSubjectName}</Col>
                                                        <Col span={1}>{detailItem.subjectNum}</Col>
                                                        <Col span={1}>{detailItem.subjectUnitPrice}</Col>
                                                        <Col span={2}>{detailItem.currencyConfigName}</Col>
                                                        <Col span={2}>{formatAmount(detailItem.originalCurrency)}</Col>
                                                        <Col span={1}>{detailItem.exchangeRate}</Col>
                                                        <Col span={2}>{formatAmount(detailItem.debitAmount)}</Col>
                                                        <Col span={2}>{formatAmount(detailItem.creditAmount)}</Col>
                                                    </Row>
                                                ))}

                                            <Row className="custom-row">
                                                <Col span={5}>合计</Col>
                                                <Col span={8}>{digitUppercase(item.totalAmount)}</Col>
                                                <Col span={1}></Col>
                                                <Col span={1}></Col>
                                                <Col span={2}></Col>
                                                <Col span={2}></Col>
                                                <Col span={1}></Col>
                                                <Col span={2}>{formatAmount(item.totalAmount)}</Col>
                                                <Col span={2}>{formatAmount(item.totalAmount)}</Col>
                                            </Row>
                                        </div>
                                    </>
                                }
                            />
                        </List.Item>
                    )}
                />
            </Card>
        </PageContainer >
    );
};

export default VoucherList;