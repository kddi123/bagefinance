import { PageContainer, ProForm, ProFormText, ProFormSelect, ProFormRadio, ProFormSwitch, ProFormCheckbox } from '@ant-design/pro-components';
import Voucher from '@/components/Voucher';
import { useLocation } from 'react-router-dom';
import { message }
    from 'antd';

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