'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { Eye, PackageSearch } from 'lucide-react';
import { orderApi } from '@/api/order.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Badge } from '@/components/ui/badge';
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { Order } from '@/types';

// Định nghĩa màu sắc cho từng trạng thái đơn hàng
const statusMap: Record<string, { label: string; variant: "default" | "secondary" | "destructive" | "outline" | "success" | "warning" | "lime" }> = {
  PENDING_PAYMENT: { label: 'Chờ thanh toán', variant: 'warning' },
  CONFIRMED: { label: 'Đã xác nhận', variant: 'lime' },
  PROCESSING: { label: 'Đang xử lý', variant: 'lime' },
  SHIPPED: { label: 'Đang giao', variant: 'secondary' },
  DELIVERED: { label: 'Đã giao', variant: 'success' },
  CANCELLED: { label: 'Đã hủy', variant: 'destructive' },
  RETURNED: { label: 'Đã trả hàng', variant: 'outline' },
};

export default function AdminOrdersPage() {
  const [page, setPage] = React.useState(1);
  const [status, setStatus] = React.useState<string>('ALL');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-orders', page, status],
    queryFn: () => orderApi.getOrders({ 
      page, 
      limit: 10, 
      all: true,
      ...(status !== 'ALL' ? { status } : {}) 
    }),
  });

  const columns: Column<Order>[] = [
    {
      header: 'Mã đơn',
      className: 'font-mono font-bold text-xs text-neutral-600',
      cell: (order) => order.orderCode,
    },
    {
      header: 'Thông tin Khách hàng',
      cell: (order) => (
        <div>
          <p className="font-semibold text-sm text-neutral-900">{order.shippingAddress?.split('|')[0]?.trim() || 'N/A'}</p>
          <p className="text-xs text-neutral-500 font-mono mt-0.5">{order.shippingAddress?.split('|')[1]?.trim() || ''}</p>
        </div>
      ),
    },
    {
      header: 'Tổng tiền',
      cell: (order) => (
        <span className="font-bold text-[#1D1F20]">
          {formatCurrency(order.totalAmount)}
        </span>
      ),
    },
    {
      header: 'Thanh toán',
      cell: (order) => (
        <span className="text-[11px] font-semibold text-neutral-600 bg-neutral-100 px-2 py-1 rounded-md">
          {order.paymentMethod}
        </span>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (order) => {
        const s = statusMap[order.status] || { label: order.status, variant: 'outline' };
        return <Badge variant={s.variant}>{s.label}</Badge>;
      },
    },
    {
      header: 'Ngày đặt',
      cell: (order) => (
        <span className="text-xs text-neutral-500">
          {formatDate(order.createdAt || order.orderDate)}
        </span>
      ),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (order) => (
        <div className="flex justify-end">
          <Link href={`/admin/orders/${order.id}`}>
            <button className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition" title="Xem chi tiết">
              <Eye size={18} />
            </button>
          </Link>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">VẬN HÀNH</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Quản lý Đơn hàng</h1>
          <p className="text-xs text-neutral-500 mt-1">Theo dõi, duyệt và xử lý các đơn hàng trên toàn hệ thống.</p>
        </div>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex flex-col sm:flex-row gap-4 justify-between items-center">
        <div className="flex items-center gap-2 w-full max-w-sm">
          <label className="text-xs font-semibold text-neutral-500 w-24">Trạng thái:</label>
          <select 
            value={status} 
            onChange={(e) => { setStatus(e.target.value); setPage(1); }}
            className="w-full text-sm rounded-xl border border-[#EAE6DF] px-3 py-2 bg-white focus:outline-none focus:ring-1 focus:ring-black"
          >
            <option value="ALL">Tất cả trạng thái</option>
            <option value="PENDING_PAYMENT">Chờ thanh toán</option>
            <option value="CONFIRMED">Đã xác nhận</option>
            <option value="PROCESSING">Đang xử lý</option>
            <option value="SHIPPED">Đang giao</option>
            <option value="DELIVERED">Đã giao</option>
            <option value="CANCELLED">Đã hủy</option>
            <option value="RETURNED">Đã trả hàng</option>
          </select>
        </div>
      </div>

      <DataTable
        columns={columns}
        data={(data?.data?.data as any) || []}
        isLoading={isLoading}
        page={page}
        totalPages={data?.data?.totalPages || 1}
        totalItems={data?.data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy đơn hàng nào phù hợp."
      />
    </div>
  );
}