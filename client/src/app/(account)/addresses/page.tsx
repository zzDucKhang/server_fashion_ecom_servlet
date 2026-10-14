import { Metadata } from 'next';
import { AddressList } from '@/modules/user/components/address-list';

export const metadata: Metadata = {
  title: 'Sổ địa chỉ — Fashion Store',
  description: 'Quản lý các địa chỉ nhận hàng của bạn.',
};

export default function AddressesPage() {
  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <AddressList />
    </div>
  );
}

