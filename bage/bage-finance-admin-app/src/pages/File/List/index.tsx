import { PageContainer  } from '@ant-design/pro-components';
import { useLocation } from 'react-router-dom';
import FileIndex from '@/components/File/index';


const FileList: React.FC = () => {
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const copyId = searchParams.get('copyId');
    return (
        <PageContainer>
            <FileIndex
                p={{ id: 0, copyId: copyId ? copyId : 0 }}
            />
        </PageContainer >
    );
};
export default FileList;