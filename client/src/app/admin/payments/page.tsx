'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import { CreditCard, CheckCircle2, Clock, XCircle, RotateCcw } from 'lucide-react';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { Badge } from '@/components/ui/badge';
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { PaymentTransaction, PaymentStatus } from '@/types';

const paymentStatusMeta: Record<PaymentStatus, { label: string; variant: 'warning' | 'success' | 'destructive' | 'outline' }> = {
  PENDING: { label: 'Chờ thanh toán', variant: 'warning' },
  SUCCESS: { label: 'Thành công', variant: 'success' },
  FAILED: { label: 'Thất bại', variant: 'destructive' },
  REFUNDED: { label: 'Đã hoàn tiền', variant: 'outline' },
};

export default function AdminPaymentsPage() {
  const [page, setPage] = React.useState(1);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-payments', page],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.FULFILLMENT.PAYMENTS, {
        params: { page, limit: 10 },
      });
      return res.data;
    },
  });

  const transactions: PaymentTransaction[] = (data?.data as any) || [];

  const columns: Column<PaymentTransaction>[] = [
    {
      header: 'Mã GD Cổng',
      cell: (p) => (
        <span className="font-mono text-xs font-semibold text-neutral-800 bg-neutral-100 px-2.5 py-1 rounded-md">
          {p.providerTransId || `TXN-${p.id}`}
        </span>
      ),
    },
    {
      header: 'Cổng thanh toán',
      cell: (p) => (
        <div className="flex items-center gap-1.5 font-bold text-xs text-neutral-700">
          <CreditCard className="size-3.5 text-neutral-400" />
          <span>{p.provider}</span>
        </div>
      ),
    },
    {
      header: 'Đơn hàng',
      cell: (p) => <span className="font-mono text-xs font-semibold text-neutral-900">{p.order?.orderCode || `Đơn #${p.orderId}`}</span>,
    },
    {
      header: 'Số tiền thanh toán',
      cell: (p) => <span className="font-bold text-xs text-emerald-600">{formatCurrency(p.amount)}</span>,
    },
    {
      header: 'Trạng thái',
      cell: (p) => {
        const meta = paymentStatusMeta[p.status] || { label: p.status, variant: 'warning' as const };
        return <Badge variant={meta.variant} className="text-[10px]">{meta.label}</Badge>;
      },
    },
    {
      header: 'Thời gian giao dịch',
      cell: (p) => <span className="text-xs text-neutral-400">{formatDate(p.createdAt)}</span>,
    },
  ];

  return (
    <div className="space-y-6">
      <div>
        <span className="eyebrow block mb-1">TÀI CHÍNH & VẬN HÀNH</span>
        <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Giao dịch Thanh toán</h1>
        <p className="text-xs text-neutral-500 mt-1">Lịch sử thu tiền qua QR Ngân hàng (SePay), Thẻ ATM, Ví điện tử và COD.</p>
      </div>

      <DataTable
        columns={columns}
        data={transactions}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Chưa có giao dịch thanh toán nào phát sinh."
      />
    </div>
  );
}