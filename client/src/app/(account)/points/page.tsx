'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import {
  Coins,
  ArrowUpRight,
  ArrowDownLeft,
  RefreshCw,
  Gift,
  HelpCircle,
  ShoppingBag,
  Clock,
  Sparkles,
} from 'lucide-react';
import { getPointBalance, getPointHistory } from '@/modules/loyalty-engage/api';
import { useAuth } from '@/components/providers/auth-provider';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { formatDate } from '@/lib/utils/format-date';
import type { PointTransaction } from '@/types';

const transTypeMeta: Record<
  string,
  { label: string; isCredit: boolean; variant: 'success' | 'destructive' | 'secondary' | 'warning' }
> = {
  EARN: { label: 'Tích điểm đơn hàng', isCredit: true, variant: 'success' },
  REDEEM: { label: 'Tiêu điểm thanh toán', isCredit: false, variant: 'destructive' },
  REFUND: { label: 'Hoàn trả điểm', isCredit: true, variant: 'warning' },
  EXPIRE: { label: 'Điểm hết hạn', isCredit: false, variant: 'secondary' },
};

export default function AccountPointsPage() {
  const { user } = useAuth();
  const [page, setPage] = React.useState(1);

  // Fetch balance
  const { data: balanceData } = useQuery({
    queryKey: ['user-point-balance'],
    queryFn: () => getPointBalance(),
  });

  // Fetch history
  const { data: historyData, isLoading } = useQuery({
    queryKey: ['user-point-history', page],
    queryFn: () => getPointHistory({ page, limit: 10 }),
  });

  const currentPoints =
    balanceData?.data?.rewardPoints ?? user?.rewardPoints ?? 0;

  const transactions: PointTransaction[] = (historyData?.data as any) || [];
  const pagination = historyData?.pagination || { page: 1, limit: 10, total: 0, totalPages: 1 };

  return (
    <div className="space-y-8">
      {/* Header card: Point balance banner */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-[#1D1F20] to-[#2D3032] p-8 text-white shadow-xl">
        <div className="absolute right-0 top-0 -mr-10 -mt-10 size-60 rounded-full bg-[#84EA53]/10 blur-3xl" />
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="flex size-7 items-center justify-center rounded-full bg-[#84EA53]/20 text-[#84EA53]">
                <Coins className="size-4" />
              </span>
              <span className="text-xs font-semibold uppercase tracking-wider text-[#84EA53]">
                COMMON LOYALTY REWARDS
              </span>
            </div>
            <h2 className="text-sm text-neutral-400 font-medium">Số dư điểm hiện tại của bạn</h2>
            <div className="flex items-baseline gap-2 mt-1">
              <span className="font-display text-4xl sm:text-5xl font-bold tracking-tight text-white">
                {currentPoints.toLocaleString('vi-VN')}
              </span>
              <span className="text-base text-neutral-400 font-medium">điểm</span>
            </div>
            <p className="text-xs text-neutral-400 mt-2 max-w-md leading-relaxed">
              Tương đương{' '}
              <strong className="text-white font-semibold">
                {currentPoints.toLocaleString('vi-VN')} VNĐ
              </strong>{' '}
              có thể khấu trừ trực tiếp khi thanh toán đơn hàng tiếp theo.
            </p>
          </div>

          <div className="flex flex-col sm:flex-row gap-3">
            <Link
              href="/products"
              className="inline-flex items-center justify-center rounded-xl bg-[#84EA53] text-[#1D1F20] hover:bg-[#72d244] font-semibold text-xs px-5 py-2.5 transition shadow-xs"
            >
              <ShoppingBag className="size-4 mr-2" /> Dùng điểm mua sắm
            </Link>
          </div>
        </div>

        {/* Perks ribbon */}
        <div className="mt-8 pt-6 border-t border-white/10 grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
          <div className="flex items-center gap-2.5 text-neutral-300">
            <Sparkles className="size-4 text-[#84EA53] shrink-0" />
            <span>Tích 1% giá trị mỗi đơn hàng hoàn tất</span>
          </div>
          <div className="flex items-center gap-2.5 text-neutral-300">
            <Coins className="size-4 text-[#84EA53] shrink-0" />
            <span>1 điểm thưởng = 1 VNĐ giảm trực tiếp</span>
          </div>
          <div className="flex items-center gap-2.5 text-neutral-300">
            <Gift className="size-4 text-[#84EA53] shrink-0" />
            <span>Áp dụng đồng thời cùng Voucher</span>
          </div>
        </div>
      </div>

      {/* History table card */}
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-semibold text-base text-neutral-900">Lịch sử tích & tiêu điểm</h3>
            <p className="text-xs text-neutral-500">
              Chi tiết các giao dịch cộng điểm từ đơn hàng và trừ điểm khi mua sắm.
            </p>
          </div>
        </div>

        {isLoading ? (
          <div className="py-12 text-center text-xs text-neutral-400">Đang tải lịch sử điểm...</div>
        ) : transactions.length === 0 ? (
          <div className="py-12 text-center text-xs text-neutral-500">
            <Coins className="size-8 text-neutral-300 mx-auto mb-2" />
            Bạn chưa có giao dịch tích điểm nào. Hãy mua sắm để nhận điểm thưởng ngay!
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-[#EAE6DF] text-neutral-400 font-semibold uppercase text-[10px]">
                  <th className="pb-3 pl-2">Loại giao dịch</th>
                  <th className="pb-3">Mô tả</th>
                  <th className="pb-3 text-right">Biến động điểm</th>
                  <th className="pb-3 text-right">Số dư sau GD</th>
                  <th className="pb-3 text-right pr-2">Thời gian</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#EAE6DF]/60">
                {transactions.map((t) => {
                  const meta = transTypeMeta[t.transactionType] || {
                    label: t.transactionType,
                    isCredit: t.pointChange > 0,
                    variant: 'secondary' as const,
                  };
                  const isPositive = t.pointChange > 0;

                  return (
                    <tr key={t.id} className="hover:bg-neutral-50/60 transition">
                      <td className="py-3.5 pl-2 font-medium">
                        <Badge variant={meta.variant} className="text-[10px]">
                          {meta.label}
                        </Badge>
                      </td>
                      <td className="py-3.5 text-neutral-800">
                        <p className="font-medium">{t.description}</p>
                        {t.orderId && (
                          <Link
                            href={`/orders/${t.orderId}`}
                            className="text-[11px] text-neutral-500 font-mono hover:underline"
                          >
                            Đơn hàng #{t.orderId}
                          </Link>
                        )}
                      </td>
                      <td className="py-3.5 text-right font-mono font-bold">
                        <span
                          className={`inline-flex items-center gap-0.5 ${
                            isPositive ? 'text-emerald-600' : 'text-rose-600'
                          }`}
                        >
                          {isPositive ? (
                            <ArrowUpRight className="size-3.5" />
                          ) : (
                            <ArrowDownLeft className="size-3.5" />
                          )}
                          {isPositive ? `+${t.pointChange}` : t.pointChange}
                        </span>
                      </td>
                      <td className="py-3.5 text-right font-mono text-neutral-600">
                        {t.currentPoint?.toLocaleString('vi-VN') || '---'}
                      </td>
                      <td className="py-3.5 text-right pr-2 text-neutral-400">
                        {formatDate(t.createdAt)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {pagination.totalPages > 1 && (
          <div className="flex items-center justify-between border-t border-[#EAE6DF] pt-4 text-xs text-neutral-500">
            <span>
              Trang {pagination.page} / {pagination.totalPages} (Tổng {pagination.total} giao dịch)
            </span>
            <div className="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPage((p) => Math.max(1, p - 1))}
                disabled={page <= 1}
                className="h-8 px-3 rounded-xl border-[#EAE6DF]"
              >
                Trước
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPage((p) => Math.min(pagination.totalPages, p + 1))}
                disabled={page >= pagination.totalPages}
                className="h-8 px-3 rounded-xl border-[#EAE6DF]"
              >
                Sau
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
