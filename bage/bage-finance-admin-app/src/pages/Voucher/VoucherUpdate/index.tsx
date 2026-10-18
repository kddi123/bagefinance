import { PageContainer } from '@ant-design/pro-components';
import Voucher from '@/components/Voucher';
import { useLocation } from '@umijs/max';

const VoucherUpdate: React.FC = () => {
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const id = searchParams.get('id');
    return (
        <PageContainer>
            <Voucher
                p={{ id: id }}
            />
        </PageContainer >
    );
};

export default VoucherUpdate;
