import React, { useRef, useState, useEffect, useLayoutEffect } from 'react';
import { Button, Card, Modal, Table, Space, Row, Col, Affix, ConfigProvider, message } from 'antd';
import {
    PrinterOutlined,
} from '@ant-design/icons';
import { get } from '@/services/ant-design-pro/voucher';
import moment from 'moment';
import { digitUppercase } from "pixiu-number-toolkit";

import './index.less'
interface DataType {
    key: number;
    summary: string;
    subject: any;
    debitAmount: number;
    creditAmount: number;
    subjectBalance: number;
    newSubjectBalance: number;
}
const VoucherPrint: React.FC = () => {
    const urlParams = new URL(window.location.href).searchParams;
    const urlParamMap = {};
    for (const [key, value] of urlParams.entries()) {
        urlParamMap[key] = value;
    }
    const tableRef = useRef(null); // 创建一个ref
    const sharedOnCell = (_: DataType, index: number) => {
        // 判断是否是最后一行
        if (_.key === 0) {
            return { colSpan: 0 };
        }

        return {};
    };
    const columns = [
        {
            title: '摘要',
            dataIndex: 'summary',
            key: 'summary',
            width: 200,
            onCell: (_, index) => ({
                // 判断是否是最后一行
                colSpan: _.key === 0 ? 2 : 1,
            }),
        },
        {
            title: '科目',
            dataIndex: 'showSubjectName',
            key: 'showSubjectName',
            width: 300,
            onCell: sharedOnCell,
        },
        {
            title: '借方金额',
            dataIndex: 'debitAmount',
            key: 'debitAmount',
            width: 150,
            render: (text: string) => parseFloat(text).toFixed(2).toLocaleString(), // 保留两位小数并格式化数字
        },
        {
            title: '贷方金额',
            dataIndex: 'creditAmount',
            key: 'creditAmount',
            width: 150,
            render: (text: string) => parseFloat(text).toFixed(2).toLocaleString(), // 保留两位小数并格式化数字
        },
    ];
    const [top, setTop] = React.useState<number>(0);
    const [isShowToolBar, setIsShowToolBar] = useState(true);
    const [data, setData] = useState();
    const [subjectData, setSubjectData] = useState([]);
    const [subjectTempData, setSubjectTempData] = useState([]);
    const [isEnd, setIsEnd] = useState(false);
    const divRefs = useRef([]); // 创建一个 ref 数组来存储 div 的 ref
    const [currentPage, setCurrentPage] = useState(0);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [currentRow, setCurrentRow] = useState(0);
    const [isShowPrintButton, setIsShowPrintButton] = useState(false);


    const print = () => {
        setIsShowToolBar(false);
        setTimeout(() => {
            window.print();
        }, 100);
    }
    // 每个打印页面多大
    //const pageSize = 300;
    useEffect(() => {
        const handleBeforePrint = () => {
            setIsShowToolBar(false);
        };

        const handleAfterPrint = () => {
            setIsShowToolBar(true);
        };

        window.addEventListener('beforeprint', handleBeforePrint);
        window.addEventListener('afterprint', handleAfterPrint);
        if (subjectTempData.length === 0) {
            get({ id: urlParamMap["id"] }).then((response) => {
                let temp = [];
                response.data.voucherSubjectDetailVoList.forEach((content, index) => {
                    temp.push(content);
                    temp.push(content);
                    temp.push(content);
                })
                setData(response.data);
                //setSubjectTempData(response.data.voucherSubjectDetailVoList);
                setSubjectTempData(temp);
            });
        } else {
            if (isEnd) {
                return;
            }
            const pageSize = 980; // 打印页面的高度
            let newSubjectData = [];
            let tArray = [];
            subjectTempData.forEach((content, index) => {
                newSubjectData.push(content);
            });

            subjectData.forEach((content, index) => {
                tArray.push(content);
            })
            if (currentPage < newSubjectData.length && currentRow < newSubjectData.length) {
                tArray[currentPage] = newSubjectData.slice(currentIndex, currentRow + 1);
                tArray[currentPage].push({ "key": 0, "summary": "合计：" + digitUppercase(data.totalAmount), "showSubjectName": '', "debitAmount": parseFloat(data.totalAmount).toFixed(2).toLocaleString(), "creditAmount": parseFloat(data.totalAmount).toFixed(2).toLocaleString() });
                setCurrentRow(currentRow + 1);
            }
            else {
                setIsEnd(true);
                setIsShowPrintButton(true);
            }
            if (!divRefs.current[currentPage]) {
                divRefs.current[currentPage] = React.createRef();
            }
            if (divRefs.current[currentPage].current) {
                if (divRefs.current[currentPage].current.clientHeight > pageSize) {
                    setCurrentIndex(currentRow + 1);
                    setCurrentPage(currentPage + 1);
                }
            }
            setSubjectData(tArray);
        }

        return () => {
            window.removeEventListener('beforeprint', handleBeforePrint);
            window.removeEventListener('afterprint', handleAfterPrint);
        };
    }, [subjectTempData, subjectData]);
    return (
        <div>
            {isShowToolBar && (
                <Affix offsetTop={top}>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', backgroundColor: 'rgb(50,54,57)', height: 40 }}>
                        {isShowPrintButton ? (
                            <Button icon={<PrinterOutlined />} onClick={() => print()} style={{ marginRight: 10 }}>
                                打印
                            </Button>
                        ) : <div style={{ color: 'white', marginRight: 10 }}>正在生成凭证...</div>}
                    </div>
                </Affix>
            )}
            <div ref={tableRef} className='print_voucher'>
                {data && subjectData.map((content, index) => (
                    content ? (
                        <div ref={divRefs.current[index]} key={index} style={{ marginBottom: 10 }}>
                            <Space direction="vertical">
                                <div style={{ width: '100%', position: 'relative', height: 50 }}>
                                    <div style={{ position: 'absolute', left: '50%', transform: 'translateX(-50%)', fontSize: 35 }}>{data.voucherWord}</div>
                                    <div style={{ position: 'absolute', right: 0, top: 25, fontSize: 13 }}>附单据数：{data.documentNum}</div>
                                </div>
                                <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', height: 30, fontSize: 13 }}>
                                    <div>单位：字节跳动</div>
                                    <div>日期：{moment(data.voucherDate).format('YYYY-MM-DD')}</div>
                                    <div>凭证号：{data.voucherWord}-{data.voucherNumber}
                                        {subjectData.length > 1 && (
                                            <>（{index + 1}/{subjectData.length}）</>
                                        )}
                                    </div>
                                </div>

                                <ConfigProvider
                                    theme={{
                                        components: {
                                            Table: {
                                                footerColor: 'white',
                                                headerBorderRadius: 0,
                                                // cellFontSize: 5,
                                                // cellPaddingBlock: 5,
                                                headerBg: 'white',
                                                borderColor: 'black',
                                            },
                                        },
                                    }}
                                >
                                    <div>
                                        <Table
                                            size='small'
                                            bordered
                                            style={{ width: 'auto' }}
                                            pagination={false}
                                            dataSource={subjectData[index]}
                                            columns={columns}
                                        />
                                    </div>
                                </ConfigProvider>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 10 }}>
                                    <div>主管：</div>
                                    <div>记账：</div>
                                    <div>审核： </div>
                                    <div>出纳： </div>
                                    <div>制单： {data.memberName}</div>
                                </div>
                            </Space>
                        </div>
                    ) : <br key={index} />
                ))}
            </div>
        </div >
    );

}

export default VoucherPrint;
