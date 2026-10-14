import { Metadata } from 'next';
import { CartView } from '@/modules/cart/components/cart-view';

export const metadata: Metadata = {
  title: 'Giỏ hàng của bạn — Fashion Store',
  description: 'Xem lại các sản phẩm đã chọn và tiến hành đặt hàng.',
};

export default function CartPage() {
  return <CartView />;
}