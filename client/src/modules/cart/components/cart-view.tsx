'use client';

import { useState, useMemo } from 'react';
import Link from 'next/link';
import { useCart } from '../hooks/use-cart';
import { CartItemComponent } from './cart-item';
import { CartSummary } from './cart-summary';

export function CartView() {
  const { data: cartResponse, isLoading, isError } = useCart();
  const items = cartResponse?.data?.cartItems || [];

  // Quản lý danh sách ID các món BỊ BỎ CHỌN (mặc định rỗng = chọn tất cả)
  const [unselectedIds, setUnselectedIds] = useState<number[]>([]);

  // Tính toán trực tiếp selectedIds ngay khi render (Pure computation - không cần useEffect)
  const unselectedSet = useMemo(() => new Set(unselectedIds), [unselectedIds]);

  const selectedIds = useMemo(() => {
    return items
      .filter((item) => !unselectedSet.has(item.id))
      .map((item) => item.id);
  }, [items, unselectedSet]);

  const isAllSelected = items.length > 0 && selectedIds.length === items.length;

  const handleToggleSelectAll = () => {
    if (isAllSelected) {
      // Đang chọn tất cả -> Bỏ chọn tất cả (thêm toàn bộ ID vào danh sách unselected)
      setUnselectedIds(items.map((i) => i.id));
    } else {
      // Chọn tất cả -> Làm rỗng danh sách unselected
      setUnselectedIds([]);
    }
  };

  const handleToggleItem = (id: number) => {
    setUnselectedIds((prev) =>
      prev.includes(id) ? prev.filter((itemId) => itemId !== id) : [...prev, id]
    );
  };

  if (isLoading) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16">
        <div className="animate-pulse space-y-8">
          <div className="h-8 w-48 rounded-xl bg-neutral-200" />
          <div className="h-64 rounded-2xl bg-neutral-200" />
        </div>
      </main>
    );
  }

  if (isError) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16 text-center">
        <p className="text-red-500 text-sm">Không thể tải dữ liệu giỏ hàng. Vui lòng thử lại sau.</p>
      </main>
    );
  }

  if (items.length === 0) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-20 sm:px-8 text-center">
        <h1 className="font-display text-4xl mb-4 text-neutral-900">Giỏ hàng của bạn đang trống</h1>
        <p className="text-[#74736e] mb-8 text-sm">Hãy chọn các sản phẩm thời trang yêu thích của bạn.</p>
        <Link
          href="/products"
          className="inline-block rounded-xl bg-[#1D1F20] px-8 py-3.5 text-sm font-bold !text-white [&_*]:!text-white transition-transform hover:scale-[1.02] active:scale-[0.98]"
        >
          Khám phá sản phẩm
        </Link>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16">
      <div className="mb-8">
        <p className="eyebrow">DANH SÁCH ĐÃ CHỌN</p>
        <h1 className="section-title mt-2">Giỏ hàng</h1>
        <p className="mt-2 text-xs text-[#74736e]">
          {items.length} món trong giỏ hàng · Sẵn sàng đặt bất cứ lúc nào.
        </p>
      </div>

      <div className="grid gap-10 lg:grid-cols-[1fr_360px]">
        <div className="rounded-2xl bg-white px-6 py-4 border border-[#EAE6DF] shadow-xs h-fit">
          {/* Thanh công cụ Chọn tất cả */}
          <div className="flex items-center justify-between pb-4 border-b border-[#EAE6DF]">
            <label className="flex items-center gap-3 text-xs font-semibold text-neutral-800 cursor-pointer select-none">
              <input
                type="checkbox"
                checked={isAllSelected}
                onChange={handleToggleSelectAll}
                className="size-4 rounded border-[#EAE6DF] accent-[#84EA53] cursor-pointer"
              />
              <span>Chọn tất cả ({items.length} sản phẩm)</span>
            </label>
            <span className="text-xs text-neutral-500">
              Đã chọn: <strong className="text-neutral-900">{selectedIds.length}</strong>
            </span>
          </div>

          <div className="divide-y divide-[#EAE6DF]">
            {items.map((item) => (
              <CartItemComponent
                key={item.id}
                item={item}
                isSelected={selectedIds.includes(item.id)}
                onToggleSelect={handleToggleItem}
              />
            ))}
          </div>
        </div>

        <CartSummary items={items} selectedItemIds={selectedIds} />
      </div>
    </main>
  );
}