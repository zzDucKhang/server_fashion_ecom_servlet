import Link from 'next/link';
import { CartItem } from '@/types';
import { formatCurrency } from '@/lib/utils/format-currency';

interface CartSummaryProps {
  items: CartItem[];
  selectedItemIds: number[];
}

export function CartSummary({ items, selectedItemIds }: CartSummaryProps) {
  // Chỉ tính tiền các sản phẩm được chọn
  const selectedItems = items.filter((item) => selectedItemIds.includes(item.id));
  const subtotal = selectedItems.reduce((sum, item) => {
    const activeFlashSale = item.variant.flashSaleItems?.find((fsi: any) => {
      if (!fsi.campaign || !fsi.campaign.isActive) return false;
      const now = new Date().getTime();
      const start = new Date(fsi.campaign.startTime).getTime();
      const end = new Date(fsi.campaign.endTime).getTime();
      const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
      return now >= start && now <= end && remainingQty >= item.quantity;
    });

    const price = activeFlashSale
      ? Number(activeFlashSale.flashSalePrice)
      : Number(item.variant.sellingPrice || item.variant.originalPrice || 0);
      
    return sum + price * item.quantity;
  }, 0);

  const hasSelected = selectedItemIds.length > 0;
  const checkoutHref = hasSelected ? `/checkout?items=${selectedItemIds.join(',')}` : '#';

  return (
    <aside className="h-fit rounded-2xl bg-white p-6 border border-[#EAE6DF] shadow-xs lg:sticky lg:top-36">
      <h2 className="font-display text-3xl text-neutral-900">Tóm tắt đơn hàng</h2>
      
      <div className="mt-7 space-y-3 border-b border-[#EAE6DF] pb-5 text-sm">
        <div className="flex justify-between text-neutral-600">
          <span>Đã chọn</span>
          <span className="font-semibold text-neutral-900">{selectedItems.length} sản phẩm</span>
        </div>
        <div className="flex justify-between text-neutral-600">
          <span>Tạm tính</span>
          <span className="font-semibold text-neutral-900">{formatCurrency(subtotal)}</span>
        </div>
        <div className="flex justify-between text-neutral-600">
          <span>Phí vận chuyển</span>
          <span className="text-xs text-neutral-400 italic">Tính tại bước thanh toán</span>
        </div>
      </div>

      <div className="flex justify-between py-5 text-base font-bold text-neutral-900">
        <span>Tổng cộng</span>
        <span className="text-lg text-emerald-600">{formatCurrency(subtotal)}</span>
      </div>

      {hasSelected ? (
        <Link
          href={checkoutHref}
          className="block w-full rounded-xl bg-[#84EA53] py-3.5 text-center text-sm font-bold text-neutral-900 transition-transform hover:scale-[1.01] active:scale-[0.99] shadow-xs"
        >
          Mua hàng ({selectedItems.length})
        </Link>
      ) : (
        <button
          type="button"
          disabled
          className="block w-full rounded-xl bg-neutral-100 py-3.5 text-center text-sm font-bold text-neutral-400 cursor-not-allowed border border-[#EAE6DF]"
        >
          Vui lòng chọn sản phẩm
        </button>
      )}

      <p className="mt-4 text-center text-[11px] text-[#74736e]">
        Miễn phí vận chuyển cho đơn hàng từ 500.000 ₫
      </p>
    </aside>
  );
}