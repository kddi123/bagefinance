import { PageContainer } from '@ant-design/pro-components';
import Voucher from '@/components/Voucher';
import { useLocation } from '@umijs/max';


const VoucherCreate: React.FC = () => {
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const copyId = searchParams.get('copyId');
    return (
        <PageContainer>
            <Voucher
                p={{ id: 0, copyId: copyId ? copyId : 0 }}
            />
        </PageContainer >
    );
};

export default VoucherCreate;
