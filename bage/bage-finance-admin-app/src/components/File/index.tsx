import {
    ExclamationCircleOutlined, PlusOutlined, FolderOutlined, FolderOpenFilled, InboxOutlined, EyeOutlined, OrderedListOutlined, PictureOutlined,
    CheckOutlined,
    DeleteOutlined,
    VideoCameraOutlined,
    EditOutlined
} from '@ant-design/icons';
import { ActionType, PageContainer, ProColumns } from '@ant-design/pro-components';
import { ProTable, TableDropdown, ProForm, ProFormText } from '@ant-design/pro-components';
import type { UploadProps } from 'antd';
import { Button, Dropdown, Space, Tag, Switch, Modal, message, Tree, Card, Row, Col, Form, DatePicker, Input, Upload, List, Affix, Badge, Drawer, Pagination } from 'antd';
import { useRef, useState, useEffect, useImperativeHandle, forwardRef } from 'react';
import request from 'umi-request';
import { list, create, del as delFolder, update as updateFolder, get as getFolder } from '@/services/ant-design-pro/folder';
import { list as listFile, getPicUrl, del, listByIds, composeFile, listFileIds } from '@/services/ant-design-pro/file';
import './index.less'
import { Link } from 'umi';
import {
    FolderOpenOutlined,
    FileTextOutlined, FilePdfOutlined, FileImageOutlined, FileExcelOutlined, FileWordOutlined, FilePptOutlined
} from '@ant-design/icons';
import moment from 'moment';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';
import { response } from 'express';
const iconMap = {
    '.txt': <FileTextOutlined />,
    '.pdf': <FilePdfOutlined />,
    '.png': <FileImageOutlined />,
    '.jpg': <FileImageOutlined />,
    '.jpeg': <FileImageOutlined />,
    '.xls': <FileExcelOutlined />,
    '.xlsx': <FileExcelOutlined />,
    '.csv': <FileExcelOutlined />,
    '.word': <FileWordOutlined />,
    '.ppt': <FilePptOutlined />,
    '.mp4': <VideoCameraOutlined />
};
const getFileIcon = (extension: string) => {
    return iconMap[extension] || <div>extension</div>; // 如果没有匹配项，显示未知文件类型  
}
const { RangePicker } = DatePicker;
const { Dragger } = Upload;

interface DataNode {
    title: string;
    key: string;
    isLeaf?: boolean;
    children?: DataNode[];
}

const initTreeData: DataNode[] = [
    // { title: 'Expand to load', key: '0' },
    // { title: 'Expand to load', key: '1' },
    // { title: 'Tree Node', key: '2', isLeaf: true },
];



const FileIndex: React.FC = (props1, onParamsChange) => {
    const { p } = props1;
    const [fileList, setFileList] = useState([]);
    const [treeData, setTreeData] = useState(initTreeData);
    const actionRef = useRef<ActionType>();
    const [dataSource, setDataSource] = useState<API.ListAccountBookVoItem[]>();
    const [form] = Form.useForm();
    const [formCreateFolder] = Form.useForm();
    const [formUpdateFolder] = Form.useForm();

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isModalOpenPrePic, setIsModalOpenPrePir] = useState(false);
    const [folderId, setFolderId] = useState(0);
    const [parentFolderId, setParentFolderId] = useState(0);
    const [picUrl, setPicUrl] = useState('');
    const [picTitle, setPicTitle] = useState('');
    const [extension, setExtension] = useState('');
    const [isOpenSelectFile, setIsOpenSelectFile] = useState(false);
    const [isOpenCreateFolder, setIsOpenCreateFolder] = useState(false);
    const [isOpenUpdateFolder, setIsOpenUpdateFolder] = useState(false);
    const [isShowList, setIsShowList] = useState(false);
    const [total, setTotal] = useState<number>(0);
    const [current, setCurrent] = useState<Number>(1);
    const [searchPar, setSearchPar] = useState({ pageNum: 1, pageSize: 10 });
    const [selectFileIds, setSelectFileIds] = useState<number[]>([]);
    // 通过 useImperativeHandle 向父组件暴露方法
    // useImperativeHandle(ref, () => ({
    //     getSelectFileIds: () => {
    //         return selectFileIds;
    //     }
    // }));
    const [selectDataSource, setSelectDataSource] = useState<API.ListAccountBookVoItem[]>();

    // 状态来控制展开的节点
    const [expandedKeys, setExpandedKeys] = useState<string[]>(['0']);
    const videoRef = useRef(null);

    let token = localStorage.getItem('token');
    let clientId = localStorage.getItem('clientId');

    const CHUNK_SIZE = 5 * 1024 * 1024; // 5MB
    const MAX_CONCURRENT_UPLOADS = 10; //最多同时上传10个文件
    const customRequest = async ({ file, onProgress, onSuccess, onError }) => {
        const fileName = file.name;
        const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
        const uuid = uuidv4();

        let uploadIndex = 0;
        const uploadQueue = [];
        const activeUploads = new Set();

        const uploadChunk = async (chunkIndex) => {
            const start = chunkIndex * CHUNK_SIZE;
            const end = Math.min(start + CHUNK_SIZE, file.size);
            const blob = file.slice(start, end);

            const formData = new FormData();
            formData.append('file', blob);
            formData.append('chunk', chunkIndex);
            formData.append('totalChunks', totalChunks);
            formData.append('fileName', fileName);
            formData.append('folderId', folderId);
            formData.append('uid', uuid);

            try {
                await axios.post('/adminapi/file/uploadPart', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        'Api-Access-Token': token,
                        'client-id': clientId
                    },
                });
                uploadIndex = uploadIndex + 1;
                if (onProgress) {
                    if (totalChunks > 1) {
                        onProgress({ percent: ((uploadIndex) / (totalChunks + 1)) * 100 });
                    } else {
                        onProgress({ percent: ((uploadIndex) / totalChunks) * 100 });
                    }
                }
            } catch (error) {
                throw error;
            } finally {
                activeUploads.delete(chunkIndex);
                if (uploadQueue.length > 0) {
                    const nextChunkIndex = uploadQueue.shift();
                    activeUploads.add(nextChunkIndex);
                    uploadChunk(nextChunkIndex).catch(onError);
                }
            }
        };

        try {
            for (let chunk = 0; chunk < totalChunks; chunk++) {
                if (activeUploads.size < MAX_CONCURRENT_UPLOADS) {
                    activeUploads.add(chunk);
                    uploadChunk(chunk).catch(onError);
                } else {
                    uploadQueue.push(chunk);
                }
            }

            while (activeUploads.size > 0) {
                await new Promise(resolve => setTimeout(resolve, 100));
            }

            if (totalChunks > 1) {
                await composeFile({
                    totalChunks: totalChunks,
                    folderId: folderId,
                    fileName: fileName,
                    uid: uuid,
                });
            }

            if (onProgress) {
                onProgress({ percent: 100 });
            }

            onSuccess('File uploaded successfully');
        } catch (error) {
            onError(error);
            message.error(`${file.name} 上传失败！`);
        }
    };
    const props: UploadProps = {
        name: 'file',
        multiple: true,
        maxCount: 20,
        fileList,
        // action: '/adminapi/file/upload',
        // headers: {
        //     "Api-Access-Token": `${token}`,
        // },
        customRequest,
        // data: { folderId: folderId }, // 将 foldId 作为参数发送
        onChange(info) {
            const { status } = info.file;
            setFileList(info.fileList);

            if (status !== 'uploading') {
                console.log(info.file, info.fileList);
            }
            if (status === 'done') {
                message.success(`${info.file.name} 上传成功！`);
                getDataSource(searchPar);
            } else if (status === 'error') {
                message.error(`${info.file.name} 上传失败！`);
            }
        },
        onDrop(e) {
            console.log('Dropped files', e.dataTransfer.files);
        },
    };

    const updateTreeData = (list: DataNode[], key: React.Key, children: DataNode[]): DataNode[] => {
        const data = list.map((node) => {
            if (node.key === key) {
                return {
                    ...node,
                    children,
                };
            }
            if (node.children) {
                return {
                    ...node,
                    children: updateTreeData(node.children, key, children),
                };
            }
            return node;
        });
        return data;
    }
    const onLoadData = ({ key, children }: any) =>
        new Promise<void>((resolve) => {
            if (children) {
                resolve();
                return;
            }
            list({ pid: key }).then(response => {
                const newData = response.data.map((item, index) => ({
                    title: item.name,
                    pid: `${item.pid}`,
                    key: `${item.id}`, // 这里需要根据实际情况组合 key
                    sort: `${item.sort}`,
                    isLeaf: item.childCount ? false : true
                    // 其他可能的属性...
                }));
                setTreeData((origin) =>
                    updateTreeData(origin, key, newData),
                );
                resolve();
            }).catch(() => {

            });
        });
    const deleteTreeNode = (list: DataNode[], key: React.Key): DataNode[] => {
        return list
            .map(node => ({ ...node }))
            .filter(node => {
                if (node.children) {
                    node.children = deleteTreeNode(node.children, key);
                    if (node.children.length === 0) {
                        node.isLeaf = true;
                        node.children = undefined;
                    }
                }
                return node.key !== key;
            });
    };
    const updateTreeNode = (list: DataNode[], key: React.Key, newTitle: string, newOrder?: number): DataNode[] => {
        return list.map(node => {
            if (node.key === key) {
                return {
                    ...node,
                    title: newTitle,
                    ...(newOrder !== undefined && { sortOrder: newOrder })
                };
            }
            if (node.children) {
                node.children = updateTreeNode(node.children, key, newTitle, newOrder);
                // If we have sortOrder, we need to sort the children array based on the sortOrder
                if (newOrder !== undefined) {
                    node.children.sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0));
                }
            }
            return node;
        });
    };
    const addTreeNodeAndExpand = (list: DataNode[], parentKey: React.Key, newChild: DataNode): DataNode[] => {
        let shouldExpandParent = false;
        const newList = list.map(node => {
            if (node.key === parentKey) {
                if (!node.children) {
                    node.children = [];
                }
                node.isLeaf = false;
                shouldExpandParent = true;
                node.children.push(newChild);
                node.children.sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0)); // 根据sort排序子节点
                node.expanded = true; // 展开父节点
            } else if (node.children) {
                // 递归调用该函数为每个子节点
                node.children = addTreeNodeAndExpand(node.children, parentKey, newChild);
            }
            return node;
        });
        if (shouldExpandParent) {
            setExpandedKeys([...expandedKeys, parentKey.toString()]);
        }

        return newList;
    };


    const columns: ProColumns[] = [
        {
            title: <strong>ID</strong>,
            dataIndex: 'id',
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
            title: <strong>文件类型</strong>,
            dataIndex: 'extension',
            filters: true,
            search: false,
            render: (text, record) => (
                <Space>
                    {getFileIcon(record.extension)}
                    <div>{record.extension}</div>
                </Space>
            ),
        },
        {
            title: <strong>上传日期</strong>,
            dataIndex: 'createTime',
            valueType: 'date', // 设置 valueType 为 'text'，将布尔值显示为文本
            search: false,
        },
        {
            title: <strong>操作</strong>,
            valueType: 'option',
            key: 'option',
            render: (text, record, _, action) => {
                return [
                    <Link to={"#"} key="editable"
                        onClick={() => {
                            Modal.confirm({
                                title: '确认删除文件',
                                content: '确定要删除文件“' + name + '”吗？',
                                okText: '确认',
                                cancelText: '取消',
                                onOk() {
                                    del({ id: record.id }).then((response) => {
                                        if (response.data) {
                                            message.success("删除成功");
                                            // listTreeMenuData();
                                            // listTreeSelectMenuData();
                                        }
                                        else {
                                            message.error("删除失败");
                                        }
                                    }).catch((e) => {
                                        console.log(e);
                                    }).finally(() => {
                                        getDataSource(searchPar);
                                    });
                                }
                            })
                        }}
                    >
                        删除
                    </Link>
                    ,
                    <a href='#' onClick={() => {
                        getPicUrl({ id: record.id }).then((response) => {
                            setPicTitle(record.name);
                            setIsModalOpenPrePir(true);
                            setPicUrl(response.data);
                            setExtension(record.extension);
                        }).catch((e) => {
                            console.log(e);
                        })
                    }}>
                        查看
                    </a>
                ]
            }
        },
    ];

    const showModal = () => {
        token = localStorage.getItem('token');
        setFileList([]);
        setIsModalOpen(true);
    };

    const handleOk = () => {
        setIsModalOpen(false);
    };

    const handleCancel = () => {
        setIsModalOpen(false);
    };

    const showModalPrePic = () => {
        setIsModalOpenPrePir(true);
    };

    const handlePrePicOk = () => {
        setIsModalOpenPrePir(false);
    };

    const handlePrePicCancel = () => {
        if (videoRef.current) {
            videoRef.current.pause();
        }
        setIsModalOpenPrePir(false);
    };
    //查询文件列表
    const onSearchFileList = async (params?: any) => {
        console.log(params)
        params.folderId = folderId;
        params.pageNum = params.pageNum ? params.pageNum : 1;
        if (params.createDate) {
            params.beginDate = moment(params.createDate[0]).format('YYYY-MM-DD 00:00:00');
            params.endDate = moment(params.createDate[1]).format('YYYY-MM-DD 23:59:59');
        }
        params.pageNum = current;
        params.pageSize = searchPar.pageSize;
        setSearchPar(params);
        //await getDataSource(params);
        // return await listFile({ ...params }).then(response => {
        //     setDataSource(response.data.list);
        //     // setPagination({
        //     //     ...pagination,
        //     //     current: params.pageNum,
        //     //     total: response.data.total,
        //     // });
        // }).catch(() => {

        // });
    }


    const rowSelection = {
        selectedRowKeys: selectFileIds, // 设置默认选中的行
        onChange: (selectedRowKeys, selectedRows) => {
            setSelectFileIds(selectedRowKeys);
            console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
        },
        getCheckboxProps: record => ({
            disabled: record.name === 'Disabled User', // Column configuration not to be checked
            name: record.name,
        }),
    };
    const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg', 'webp'];

    const isImage = (fileName) => {
        const extension = fileName.split('.').pop().toLowerCase();
        return imageExtensions.includes(extension);
    };

    const videoExtensions = ['mp4'];

    const isVideo = (fileName) => {
        const extension = fileName.split('.').pop().toLowerCase();
        return videoExtensions.includes(extension);
    };
    const onOpenSelectFile = async () => {
        setIsOpenSelectFile(true);
        if (selectFileIds.length === 0) {
            setSelectDataSource([]);
            return;
        }
        await listByIds({ ids: selectFileIds }).then((response) => {
            setSelectDataSource(response.data);
        }).catch((e) => {
            console.log(e);
        });
    }
    const onCloseSelectFile = () => {
        setIsOpenSelectFile(false);
    }
    const getDataSource = async (params: any) => {
        let response = await listFile({ ...params }).then((response) => {
            setDataSource(response.data.list);
            setTotal(response.data.total);
        }).catch((e) => {
            console.log(e);
        });
    }
    const handleListPageChange = (page: number, pageSize?: number) => {
        setSearchPar({ ...searchPar, pageNum: page, pageSize });
    };
    const toggleFileId = (fileId: number) => {
        setSelectFileIds((prevSelectFileIds) => {
            if (prevSelectFileIds.includes(fileId)) {
                // 如果ID存在，则删除它
                return prevSelectFileIds.filter((id) => id !== fileId);
            } else {
                // 如果ID不存在，则添加它
                return [...prevSelectFileIds, fileId];
            }
        });
    };
    const fetchFolderData = async () => {
        if (folderId === 0) {
            formCreateFolder.setFieldsValue({ parentName: "根目录" });
            return;
        }
        await getFolder({ id: folderId }).then((response) => {
            formUpdateFolder.setFieldsValue(response.data);
            formCreateFolder.setFieldsValue({ parentName: response.data.name });

        }).catch((e) => {
            console.log(e);
        }); // 替换为你的API端点
    }

    useEffect(() => {
        if (treeData.length === 0) {
            setExpandedKeys(['0']);
            const newData = [{
                title: "所有文件",
                expand: true,
                key: '0'
            }];
            setTreeData(newData);
            if (p) {
                if (p.fileIds) {
                    setSelectFileIds(p.fileIds);
                }
                else if (p.fileRefType >= 0) {
                    listFileIds({ ...p }).then((response) => {
                        if (response.data) {
                            setSelectFileIds(response.data);
                        }
                    }).catch((e) => {
                        console.log(e);
                    });
                }
            }
        }

        if (isOpenSelectFile) {
            onOpenSelectFile();
        }
        getDataSource(searchPar);
        if (p.onParamsChange) {
            p.onParamsChange(selectFileIds);
        }
        if (isOpenUpdateFolder || isOpenCreateFolder) {
            fetchFolderData();
        }
        //, folderId, parentFolderId
    }, [searchPar, selectFileIds, isOpenUpdateFolder, isOpenCreateFolder]); // 空数组表示仅在组件加载时调用一次
    return (
        <div className={'file_components'}>
            <Card style={{ marginBottom: 20 }} size='small'>
                <Form form={form} name="advanced_search"
                    onFinish={onSearchFileList}
                >
                    <Row gutter={24}>
                        <Col>
                            <Form.Item
                                label='上传日期'
                                name={'createDate'}
                            >
                                <RangePicker />
                                {/* <RangePicker /> */}
                            </Form.Item>
                        </Col>
                        <Col>
                            <Form.Item
                                label='文件名'
                                name={'name'}
                            >
                                <Input />
                            </Form.Item>
                        </Col>

                        <Col>
                            <Space>
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
                            </Space>

                        </Col>
                    </Row>
                </Form>
            </Card>
            <Row gutter={[16, 16]}>
                <Col span={6}>
                    <Card title="文件夹" style={{ overflow: 'auto' }} size='small'
                        extra={
                            <Space>
                                <Button icon={<PlusOutlined />} onClick={() => {
                                    setIsOpenCreateFolder(true);
                                }}>
                                </Button>
                                {
                                    folderId !== 0 && (<>
                                        <Button icon={<EditOutlined />} onClick={() => { setIsOpenUpdateFolder(true) }}>

                                        </Button>
                                        <Button icon={<DeleteOutlined />} onClick={() => {
                                            Modal.confirm({
                                                title: '确认删除文件夹',
                                                content: '确定要删除文件夹吗？',
                                                okText: '确认',
                                                cancelText: '取消',
                                                onOk() {
                                                    delFolder({ id: folderId })
                                                        .then((response) => {
                                                            message.success("删除成功");
                                                            const newTreeData = deleteTreeNode(treeData, folderId.toString());
                                                            setTreeData(newTreeData);
                                                        })
                                                        .catch((e) => {
                                                            console.log(e);
                                                        });
                                                }
                                            })
                                        }}>
                                        </Button>
                                    </>)
                                }
                            </Space>
                        }
                    >
                        <Tree loadData={onLoadData} treeData={treeData}
                            showLine
                            expandedKeys={expandedKeys} // 使用受控属性 expandedKeys
                            onExpand={(keys) => setExpandedKeys(keys)} // 更新 expandedKeys 状态
                            // onExpand={async (expandedKeys, { expanded, node }) => {
                            //     if (!expanded) {
                            //         const keysToRemove = node.children.map(child => child.key);
                            //         setExpandedKeys(expandedKeys.filter(key => !keysToRemove.includes(key)));
                            //     } else {
                            //         setExpandedKeys(expandedKeys);
                            //         await onLoadData({ key: node.key });
                            //     }
                            // }}
                            icon={({ expanded }) => (expanded ? <FolderOpenFilled /> : <FolderOutlined />)}
                            // onSelect={(key) => {
                            //     setFolderId(Number(key[0]));
                            //     form.submit();
                            //     //actionRef.current?.reload();
                            // }}
                            onSelect={(selectedKeys, e) => {
                                if (selectedKeys.length === 0) {
                                    setFolderId(null); // 清空 folderId
                                } else {
                                    setFolderId(Number(selectedKeys[0]));
                                    setParentFolderId(e.node.pid);
                                    form.submit();
                                }
                            }}
                        // switcherIcon={customSwitcherIcon} // 设置自定义的展开和关闭图标
                        />
                    </Card>
                </Col>
                <Col span={18}>
                    <Card
                        title={
                            <Space>
                                <div>文件列表</div>
                                <Button
                                    disabled={!isShowList}
                                    key="button" icon={<PictureOutlined />} onClick={() => {
                                        setIsShowList(false);
                                        getDataSource(searchPar);
                                    }} title={'缩略图'}>

                                </Button>
                                <Button key="button" icon={<OrderedListOutlined />}
                                    disabled={isShowList}
                                    onClick={() => {
                                        setIsShowList(true);
                                        getDataSource(searchPar);
                                    }} title={'列表'}>

                                </Button>
                            </Space>
                        }
                        extra={
                            <Space>
                                <Button key="button" icon={<PlusOutlined />} type="primary" onClick={showModal}>
                                    上传文件
                                </Button>
                                {selectFileIds.length ? (<Badge count={selectFileIds.length}>
                                    <Button key="button" icon={<EyeOutlined />} type="primary" onClick={onOpenSelectFile}>
                                        已选文件
                                    </Button>
                                </Badge>) : ""
                                }
                            </Space>
                        }
                    >

                        {!isShowList && (<List
                            grid={{
                                gutter: 16,
                                column: 5, // 固定列数为5
                                xs: 1,
                                sm: 2,
                                md: 4,
                                lg: 4,
                                xl: 5,
                                xxl: 7,
                            }}
                            pagination={false}
                            dataSource={dataSource}
                            renderItem={item => (
                                <List.Item>
                                    <div style={{ position: 'relative', width: 150 }}
                                    >
                                        {selectFileIds.includes(item.id) && (
                                            <div style={{ position: 'absolute', right: 8, top: 5, background: '#1890ff', padding: 5 }}>
                                                <CheckOutlined style={{ fontSize: 20, fontWeight: 'bold', color: 'white' }} />
                                            </div>
                                        )}
                                        <Space direction="vertical" align="center"
                                        >
                                            {isImage(item.extension) ?
                                                (<div style={{ border: '3px solid rgb(243,244,244)', padding: 5 }}><img src={item.picUrl} style={{ width: 150, height: 150, objectFit: 'contain' }} alt={item.name}
                                                    onClick={() => {
                                                        toggleFileId(item.id);
                                                    }}
                                                ></img></div>)
                                                :
                                                <div
                                                    onClick={() => {
                                                        toggleFileId(item.id);
                                                    }}
                                                    className='list_file'
                                                    style={{
                                                        display: 'flex',
                                                        alignItems: 'center',
                                                        justifyContent: 'center',
                                                        textAlign: 'center',
                                                        border: '3px solid rgb(243,244,244)',
                                                        padding: 5
                                                    }} >
                                                    {getFileIcon(item.extension)}
                                                </div>
                                            }
                                            <div style={{
                                                width: 150,
                                                textAlign: 'center',
                                                whiteSpace: 'nowrap',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                                cursor: 'pointer'
                                            }}
                                                onClick={() => {
                                                    getPicUrl({ id: item.id }).then((response) => {
                                                        setPicTitle(item.name);
                                                        setIsModalOpenPrePir(true);
                                                        setPicUrl(response.data);
                                                    }).catch((e) => {
                                                        console.log(e);
                                                    })
                                                }}
                                            >{item.name}</div>
                                            <Space>
                                                <Link to='#'
                                                    onClick={() => {
                                                        getPicUrl({ id: item.id }).then((response) => {
                                                            setPicTitle(item.name);
                                                            setIsModalOpenPrePir(true);
                                                            setPicUrl(item.picUrl);
                                                            setExtension(item.extension);
                                                        }).catch((e) => {
                                                            console.log(e);
                                                        })
                                                    }}
                                                ><EyeOutlined /> 查看</Link>
                                                <Link to='#' style={{ color: 'red' }}
                                                    onClick={() => {
                                                        Modal.confirm({
                                                            title: '确认删除文件',
                                                            content: '确定要删除文件“' + name + '”吗？',
                                                            okText: '确认',
                                                            cancelText: '取消',
                                                            onOk() {
                                                                del({ id: item.id }).then((response) => {
                                                                    if (response.data) {
                                                                        message.success("删除成功");
                                                                        // listTreeMenuData();
                                                                        // listTreeSelectMenuData();
                                                                    }
                                                                    else {
                                                                        message.error("删除失败");
                                                                    }
                                                                }).catch((e) => {
                                                                    console.log(e);
                                                                }).finally(() => {
                                                                    getDataSource(searchPar);
                                                                });
                                                            }
                                                        })
                                                    }}
                                                ><DeleteOutlined />删除</Link>

                                            </Space>
                                        </Space>
                                    </div>

                                </List.Item>
                            )}
                        />)}
                        {isShowList && (<ProTable<API.ListAccountBookVoItem, API.PageParams>
                            bordered={true}
                            columns={columns}
                            actionRef={actionRef}
                            cardBordered
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
                            headerTitle="文件列表"
                            rowSelection={rowSelection}
                            toolBarRender={false}
                        />
                        )}
                        <div style={{ float: 'right' }}>
                            <Pagination
                                total={total}
                                current={searchPar.pageNum}
                                pageSize={searchPar.pageSize}
                                showSizeChanger={true}
                                pageSizeOptions={['10', '20', '30', '50']} // 可选择的分页大小
                                onChange={handleListPageChange}
                            />
                        </div>
                    </Card>


                </Col>
            </Row>
            <Modal title="上传文件" open={isModalOpen} onOk={handleOk} onCancel={handleCancel}>
                <Dragger {...props}>
                    <p className="ant-upload-drag-icon">
                        <InboxOutlined />
                    </p>
                    <p className="ant-upload-text">单击或拖动文件到此区域进行上传</p>
                    <p className="ant-upload-hint">
                        支持单次或批量上传。严禁上传公司数据或其他
                        被禁止的文件。
                    </p>
                </Dragger>
            </Modal>

            <Modal title={"预览文件-" + picTitle} open={isModalOpenPrePic} onOk={handlePrePicCancel} onCancel={handlePrePicCancel} width={800}
                bodyStyle={{ height: 400 }}>
                {
                    isModalOpenPrePic && (
                        <Space direction="vertical" style={{ width: '100%', height: '100%' }}>
                            <a href={picUrl} target="_blank" rel="noopener noreferrer" style={{ float: 'right' }}>
                                新窗口访问
                            </a>

                            {isImage(extension) && (<img src={picUrl} style={{ width: 700, height: 350, objectFit: 'contain' }} alt={picTitle} />)
                            }
                            {isVideo(extension) && (
                                <video width="100%" controls ref={videoRef}>
                                    <source src={picUrl} type="video/mp4" />
                                    Your browser does not support the video tag.
                                </video>
                            )}
                        </Space>
                    )
                }
            </Modal>

            <Modal title={"创建文件夹"} open={isOpenCreateFolder} onOk={() => { formCreateFolder.submit() }} onCancel={() => { setIsOpenCreateFolder(false) }}
                afterClose={() => { formCreateFolder.resetFields(); }} >
                <ProForm
                    form={formCreateFolder}
                    onFinish={async (values: any) => {
                        await create({ ...values, pid: folderId }).then(response => {
                            if (response.data) {
                                message.success("创建成功");
                                setIsOpenCreateFolder(false);
                                const newChildNode: DataNode = {
                                    title: values.name,
                                    key: response.data.toString(), // Unique key for the new child node
                                    sort: values.sort,
                                    isLeaf: true, // Assuming it's a leaf node
                                };
                                const newTreeData = addTreeNodeAndExpand(treeData, folderId.toString(), newChildNode);
                                setTreeData(newTreeData);
                                // if (folderId) {
                                //     onLoadData({ key: parentFolderId });
                                //     setExpandedKeys(prevKeys => {
                                //         const keysSet = new Set(prevKeys);
                                //         keysSet.add(folderId.toString());
                                //         return Array.from(keysSet);
                                //     });
                                // }
                                // else {
                                //     onLoadData({ key: 0 });
                                // }
                            }
                            else {
                                message.error("创建失败");
                            }
                        }).catch(error => {
                            console.log(error);
                        })
                    }}
                    layout="horizontal"
                    submitter={false}
                    labelCol={{ span: 4 }}
                    wrapperCol={{ span: 14 }}
                >
                    <ProFormText
                        width="md"
                        name="parentName"
                        label="上级目录"
                        disabled
                    />

                    <ProFormText
                        width="md"
                        name="name"
                        label="目录名称"
                        placeholder="请输入目录名称"
                        rules={[{ required: true }]}
                    />
                    <ProFormText
                        width="md"
                        name="sort"
                        label="顺序"
                        rules={[{ required: true }]}
                        fieldProps={{
                            type: 'number',
                            onChange: (e) => {
                                const value = e.target.value;
                                if (/^\d*$/.test(value)) {
                                    form.setFieldsValue({ sort: value ? Number(value) : value });
                                }
                            }
                        }}
                    />

                </ProForm>
            </Modal>

            <Modal title={"修改文件夹"} open={isOpenUpdateFolder} onOk={() => { formUpdateFolder.submit() }} onCancel={() => { setIsOpenUpdateFolder(false) }}
                afterClose={() => { formUpdateFolder.resetFields(); }} // 清空表单字段
            >
                <ProForm
                    form={formUpdateFolder}
                    onFinish={async (values: any) => {
                        await updateFolder({ ...values, id: folderId }).then(response => {
                            if (response.data) {
                                message.success("修改成功");
                                setIsOpenUpdateFolder(false);
                                const newTreeData = updateTreeNode(treeData, folderId.toString(), values.name, values.sort);
                                setTreeData(newTreeData);
                            }
                            else {
                                message.error("修改失败");
                            }
                        }).catch(error => {
                            console.log(error);
                        })
                    }}
                    labelCol={{ span: 4 }}
                    wrapperCol={{ span: 14 }}
                    layout="horizontal"
                    submitter={false}
                >
                    <ProFormText
                        width="md"
                        name="parentName"
                        label="上级目录"
                        disabled
                    />

                    <ProFormText
                        width="md"
                        name="name"
                        label="目录名称"
                        placeholder="请输入目录名称"
                        rules={[{ required: true }]}
                    />
                    <ProFormText
                        width="md"
                        name="sort"
                        label="顺序"
                        rules={[{ required: true }]}
                        fieldProps={{
                            type: 'number',
                            onChange: (e) => {
                                const value = e.target.value;
                                if (/^\d*$/.test(value)) {
                                    form.setFieldsValue({ sort: value ? Number(value) : value });
                                }
                            }
                        }}
                    />

                </ProForm>
            </Modal>

            <Drawer onClose={onCloseSelectFile} open={isOpenSelectFile} width={1000}
                placement="right"
                title={"已选择的文件"}
            >

                <List
                    grid={{
                        gutter: 16,
                        xs: 1,
                        sm: 2,
                        md: 4,
                        lg: 4,
                        xl: 5,
                        xxl: 7,
                    }}
                    dataSource={selectDataSource}
                    renderItem={item => (
                        <List.Item>
                            <Space direction="vertical" align="center"
                            >
                                {isImage(item.extension) ?
                                    (<div style={{ border: '3px solid rgb(243,244,244)', padding: 5 }}><img src={item.picUrl} style={{ width: 150, height: 150, cursor: 'pointer', objectFit: 'contain' }} alt={item.name}
                                        onClick={() => {
                                            getPicUrl({ id: item.id }).then((response) => {
                                                setPicTitle(item.name);
                                                setIsModalOpenPrePir(true);
                                                setPicUrl(response.data);
                                            }).catch((e) => {
                                                console.log(e);
                                            })
                                        }}
                                    ></img></div>)
                                    :
                                    <div
                                        className='list_file'
                                        style={{
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            textAlign: 'center',
                                            border: '3px solid rgb(243,244,244)',
                                            padding: 5
                                        }} >
                                        {getFileIcon(item.extension)}
                                    </div>
                                }
                                <div style={{
                                    width: 150,
                                    textAlign: 'center',
                                    whiteSpace: 'nowrap',
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis'
                                }}
                                >{item.name}</div>
                                <Space>
                                    <Link to='#' style={{ color: 'red' }}
                                        onClick={() => {
                                            toggleFileId(item.id);
                                        }}
                                    ><DeleteOutlined />取消选择</Link>

                                </Space>
                            </Space>
                        </List.Item>
                    )}
                />

            </Drawer>
        </div>
    );
};
export default FileIndex;