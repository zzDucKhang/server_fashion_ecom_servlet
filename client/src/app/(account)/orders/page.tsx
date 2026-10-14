'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { PackageSearch, ArrowUpRight, ShoppingBag } from 'lucide-react';
import { orderApi } from '@/api/order.api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import type { Order } from '@/types';
import { cn } from '@/lib/utils';

const statusMeta: Record<
  string,
  {
    label: string;
    variant: 'success' | 'lime' | 'secondary' | 'warning' | 'outline';
  }
> = {
  PENDING_PAYMENT: { label: 'Chờ thanh toán', variant: 'warning' },
  CONFIRMED: { label: 'Đã xác nhận', variant: 'lime' },
  PROCESSING: { label: 'Đang xử lý', variant: 'lime' },
  SHIPPED: { label: 'Đang giao', variant: 'secondary' },
  DELIVERED: { label: 'Đã giao', variant: 'success' },
  CANCELLED: { label: 'Đã hủy', variant: 'outline' },
  RETURNED: { label: 'Đã trả hàng', variant: 'outline' },
};

const TABS = [
  { id: 'ALL', label: 'Tất cả' },
  { id: 'PENDING_PAYMENT', label: 'Chờ thanh toán' },
  { id: 'PROCESSING', label: 'Đang xử lý' },
  { id: 'SHIPPED', label: 'Đang giao' },
  { id: 'DELIVERED', label: 'Hoàn thành' },
  { id: 'CANCELLED', label: 'Đã hủy' },
  { id: 'RETURNED', label: 'Trả hàng' },
];

function formatCurrency(value: number | string) {
  const numeric = typeof value === 'number' ? value : Number(value || 0);
  return `${numeric.toLocaleString('vi-VN')} ₫`;
}

export default function OrdersPage() {
  const [activeTab, setActiveTab] = useState('ALL');

  const { data, isLoading } = useQuery({
    queryKey: ['account-orders', activeTab],
    queryFn: () =>
      orderApi.getOrders({
        page: 1,
        limit: 20,
        ...(activeTab !== 'ALL' ? { status: activeTab } : {}),
      }),
  });

  const orders = (data?.data?.data ?? data?.data ?? []) as Order[];

  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4">
        <div>
          <h2 className="text-xl font-semibold text-neutral-900">Đơn mua</h2>
          <p className="text-xs text-neutral-500 mt-1">
            Theo dõi tiến độ, kiểm tra chi tiết và quản lý các đơn hàng của bạn.
          </p>
        </div>

        <div className="flex items-center gap-2 rounded-xl bg-neutral-100 px-3 py-2 text-xs font-medium text-neutral-600">
          <ShoppingBag className="size-4" />
          <span>{orders.length} đơn hàng</span>
        </div>
      </div>

      <div className="flex overflow-x-auto border-b border-[#EAE6DF] mb-6 scrollbar-hide pb-1">
        {TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={cn(
              'whitespace-nowrap px-4 py-3 text-sm font-medium transition-colors border-b-2',
              activeTab === tab.id
                ? 'border-neutral-900 text-neutral-900'
                : 'border-transparent text-neutral-500 hover:text-neutral-700',
            )}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((item) => (
            <div
              key={item}
              className="h-32 rounded-2xl border border-[#EAE6DF] bg-neutral-50 animate-pulse"
            />
          ))}
        </div>
      ) : orders.length === 0 ? (
        <div className="mt-8 rounded-2xl border border-dashed border-[#EAE6DF] bg-neutral-50/40 px-6 py-12 text-center">
          <PackageSearch className="mx-auto size-8 text-neutral-400" />
          <h3 className="mt-4 text-sm font-semibold text-neutral-800">
            Bạn chưa có đơn hàng nào
          </h3>
          <p className="mt-1 text-xs text-neutral-500">
            Hãy mua sắm và các đơn hàng của bạn sẽ hiển thị tại đây.
          </p>
          <Link href="/" className="mt-5 inline-block">
            <Button className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white">
              Tiếp tục mua sắm
            </Button>
          </Link>
        </div>
      ) : (
        <div className="mt-6 space-y-4">
          {orders.map((order) => {
            const meta = statusMeta[order.status] ?? {
              label: order.status,
              variant: 'outline',
            };
            const firstItem = order.orderItems?.[0];

            return (
              <div
                key={order.id}
                className="rounded-2xl border border-[#EAE6DF] bg-neutral-50/30 p-5 transition hover:border-neutral-300"
              >
                <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                  <div>
                    <div className="flex flex-wrap items-center gap-2">
                      <p className="text-sm font-semibold text-neutral-900">
                        {order.orderCode}
                      </p>
                      <Badge variant={meta.variant}>{meta.label}</Badge>
                    </div>
                    <p className="mt-1 text-[11px] text-neutral-500">
                      Đặt ngày{' '}
                      {order.orderDate
                        ? new Date(order.orderDate).toLocaleDateString('vi-VN')
                        : '—'}
                    </p>
                  </div>

                  <div className="flex items-center gap-2 md:justify-end">
                    <div className="text-right">
                      <p className="text-[11px] text-neutral-500">
                        Tổng thanh toán
                      </p>
                      <p className="text-sm font-semibold text-neutral-900">
                        {formatCurrency(order.totalAmount)}
                      </p>
                    </div>
                    <Link href={`/orders/${order.id}`}>
                      <Button
                        variant="outline"
                        size="sm"
                        className="rounded-xl border-[#EAE6DF]"
                      >
                        Xem chi tiết
                        <ArrowUpRight className="ml-2 size-3.5" />
                      </Button>
                    </Link>
                  </div>
                </div>

                {/* Phần thông tin sản phẩm trong từng thẻ đơn hàng */}
                <div className="mt-4 flex flex-col gap-3 border-t border-[#EAE6DF] pt-4 md:flex-row md:items-center md:justify-between">
                  <div className="min-w-0 flex-1">
                    <p className="text-xs text-neutral-500 mb-1.5">Sản phẩm</p>
                    <div className="flex flex-col gap-1.5">
                      {order.orderItems?.length ? (
                        order.orderItems.map((item) => {
                          // Lấy slug của sản phẩm (hoặc fallback về productId nếu backend chưa trả slug)
                          const productHref = item.productSlug
                            ? `/products/${item.productSlug}`
                            : item.productId
                              ? `/products/${item.productId}`
                              : '#';

                          return (
                            <div
                              key={item.id}
                              className="flex items-center gap-2"
                            >
                              <Link
                                href={productHref}
                                className="text-sm font-medium text-neutral-800 hover:text-black hover:underline line-clamp-1 transition"
                                title="Bấm để xem chi tiết sản phẩm"
                              >
                                {item.productName}
                              </Link>
                              <span className="text-xs text-neutral-400">
                                x{item.quantity}
                              </span>
                            </div>
                          );
                        })
                      ) : (
                        <p className="text-sm text-neutral-500">
                          Không có thông tin sản phẩm
                        </p>
                      )}
                    </div>
                  </div>

                  <div className="text-xs text-neutral-500 md:text-right shrink-0">
                    {firstItem ? (
                      <span>
                        {firstItem.quantity} sản phẩm ·{' '}
                        {formatCurrency(firstItem.unitPrice)} / món
                      </span>
                    ) : (
                      <span>Đang cập nhật</span>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
