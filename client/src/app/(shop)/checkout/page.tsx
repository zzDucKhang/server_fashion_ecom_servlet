import { Suspense } from 'react';
import { Metadata } from 'next';
import { CheckoutView } from '@/modules/checkout/components/checkout-view';

export const metadata: Metadata = {
  title: 'Thanh toán đơn hàng — Fashion Store',
  description: 'Xác nhận thông tin giao nhận và thanh toán đơn hàng an toàn.',
};

export default function CheckoutPage() {
  return (
    <Suspense fallback={<div className="py-20 text-center text-sm text-neutral-500">Đang tải trang thanh toán...</div>}>
      <CheckoutView />
    </Suspense>
  );
}