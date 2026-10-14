'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import {
  RotateCcw,
  CheckCircle2,
  XCircle,
  Clock,
  Eye,
  Package,
  AlertCircle,
  FileText,
  Filter,
} from 'lucide-react';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { formatDate } from '@/lib/utils/format-date';
import type { ReturnRequest, ReturnStatus } from '@/types';

const statusMeta: Record<
  ReturnStatus,
  { label: string; variant: 'warning' | 'success' | 'destructive' | 'secondary' }
> = {
  PENDING: { label: 'Chờ duyệt', variant: 'warning' },
  APPROVED: { label: 'Đã duyệt', variant: 'success' },
  REJECTED: { label: 'Từ chối', variant: 'destructive' },
  COMPLETED: { label: 'Hoàn tất', variant: 'secondary' },
};

export default function AdminReturnsPage() {
  const queryClient = useQueryClient();
  const [selectedStatus, setSelectedStatus] = React.useState<ReturnStatus | 'ALL'>('ALL');
  const [search, setSearch] = React.useState('');
  const [page, setPage] = React.useState(1);
  const [viewingReturn, setViewingReturn] = React.useState<ReturnRequest | null>(null);
  const [rejectTarget, setRejectTarget] = React.useState<ReturnRequest | null>(null);

  // Fetch returns
  const { data, isLoading } = useQuery({
    queryKey: ['admin-returns', selectedStatus],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.FULFILLMENT.RETURNS, {
        params: {
          status: selectedStatus === 'ALL' ? undefined : selectedStatus,
        },
      });
      return res.data;
    },
  });

  // Mutation to update return status
  const updateStatusMutation = useMutation({
    mutationFn: async ({ returnId, status }: { returnId: number; status: ReturnStatus }) => {
      const res = await apiClient.put(
        `${API_ENDPOINTS.FULFILLMENT.RETURNS}/${returnId}`,
        { status }
      );
      return res.data;
    },
    onSuccess: (_, variables) => {
      const label = statusMeta[variables.status]?.label || variables.status;
      toast.success(`Đã cập nhật yêu cầu sang: "${label}"!`);
      queryClient.invalidateQueries({ queryKey: ['admin-returns'] });
      setViewingReturn(null);
      setRejectTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật trạng thái thất bại');
    },
  });

  const rawReturns: ReturnRequest[] = (data?.data as any) || [];

  // Filter returns by search query client-side if needed
  const filteredReturns = React.useMemo(() => {
    if (!search.trim()) return rawReturns;
    const q = search.toLowerCase();
    return rawReturns.filter(
      (r) =>
        r.id.toString().includes(q) ||
        r.orderItem?.orderId?.toString().includes(q) ||
        r.orderItem?.productName?.toLowerCase().includes(q) ||
        r.reason?.toLowerCase().includes(q) ||
        r.description?.toLowerCase().includes(q)
    );
  }, [rawReturns, search]);

  const columns: Column<ReturnRequest>[] = [
    {
      header: 'Mã yêu cầu',
      className: 'w-24',
      cell: (r) => (
        <span className="font-mono text-xs font-bold text-neutral-900 bg-neutral-100 px-2 py-0.5 rounded">
          #{r.id}
        </span>
      ),
    },
    {
      header: 'Sản phẩm hoàn trả',
      cell: (r) => (
        <div className="max-w-xs">
          <div className="flex items-center gap-1.5">
            <span className="font-mono text-[11px] font-semibold text-neutral-500">
              Đơn #{r.orderItem?.orderId || 'N/A'}
            </span>
          </div>
          <p className="font-semibold text-xs text-neutral-900 line-clamp-1 mt-0.5">
            {r.orderItem?.productName || `Item #${r.orderItemId}`}
          </p>
          {r.orderItem?.variantValue && (
            <p className="text-[11px] text-neutral-500">
              Phân loại: <span className="font-medium text-neutral-700">{r.orderItem.variantValue}</span>
            </p>
          )}
        </div>
      ),
    },
    {
      header: 'Số lượng',
      className: 'text-center w-20',
      cell: (r) => (
        <span className="font-semibold text-xs text-neutral-800">
          {r.quantity} {r.orderItem ? `/ ${r.orderItem.quantity}` : ''}
        </span>
      ),
    },
    {
      header: 'Lý do đổi trả',
      cell: (r) => (
        <div className="max-w-sm">
          <p className="text-xs font-semibold text-neutral-800 line-clamp-1">{r.reason}</p>
          {r.description && (
            <p className="text-[11px] text-neutral-500 line-clamp-1 mt-0.5">{r.description}</p>
          )}
          {r.medias && r.medias.length > 0 && (
            <div className="flex gap-1.5 mt-1.5">
              {r.medias.map((m, idx) => {
                const isVideo =
                  m.mediaType?.includes('video') ||
                  m.mediaUrl.endsWith('.mp4') ||
                  m.mediaUrl.includes('video');
                return isVideo ? (
                  <span
                    key={m.id || idx}
                    className="text-[10px] bg-neutral-100 text-neutral-600 px-1.5 py-0.5 rounded border border-neutral-200"
                  >
                    Video ({idx + 1})
                  </span>
                ) : (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img
                    key={m.id || idx}
                    src={m.mediaUrl}
                    alt="Chứng từ đổi trả"
                    className="size-8 rounded object-cover border border-neutral-200 cursor-pointer hover:opacity-80"
                    onClick={() => setViewingReturn(r)}
                  />
                );
              })}
            </div>
          )}
        </div>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (r) => {
        const meta = statusMeta[r.status] || { label: r.status, variant: 'secondary' as const };
        return <Badge variant={meta.variant} className="text-[10px]">{meta.label}</Badge>;
      },
    },
    {
      header: 'Ngày yêu cầu',
      cell: (r) => <span className="text-xs text-neutral-400">{formatDate(r.createdAt)}</span>,
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (r) => (
        <div className="flex justify-end items-center gap-1.5">
          <button
            type="button"
            onClick={() => setViewingReturn(r)}
            className="p-1.5 text-neutral-500 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Xem chi tiết"
          >
            <Eye className="size-4" />
          </button>

          {r.status === 'PENDING' && (
            <>
              <Button
                size="sm"
                variant="outline"
                onClick={() => updateStatusMutation.mutate({ returnId: r.id, status: 'APPROVED' })}
                disabled={updateStatusMutation.isPending}
                className="h-8 px-2.5 text-xs text-emerald-700 hover:text-emerald-800 hover:bg-emerald-50 border-emerald-200 rounded-lg"
              >
                <CheckCircle2 className="size-3.5 mr-1" /> Duyệt
              </Button>
              <Button
                size="sm"
                variant="outline"
                onClick={() => setRejectTarget(r)}
                disabled={updateStatusMutation.isPending}
                className="h-8 px-2.5 text-xs text-rose-700 hover:text-rose-800 hover:bg-rose-50 border-rose-200 rounded-lg"
              >
                <XCircle className="size-3.5 mr-1" /> Từ chối
              </Button>
            </>
          )}

          {r.status === 'APPROVED' && (
            <Button
              size="sm"
              variant="outline"
              onClick={() => updateStatusMutation.mutate({ returnId: r.id, status: 'COMPLETED' })}
              disabled={updateStatusMutation.isPending}
              className="h-8 px-2.5 text-xs text-neutral-700 hover:bg-neutral-100 border-neutral-300 rounded-lg"
            >
              Hoàn tất
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">GIAO VẬN & ĐỔI TRẢ</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Yêu cầu Đổi trả hàng
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Xem xét và phê duyệt hoặc từ chối các yêu cầu hoàn trả sản phẩm từ khách hàng.
          </p>
        </div>
      </div>

      {/* Filter Tabs & Search */}
      <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs">
        <div className="flex items-center gap-1 overflow-x-auto pb-1 md:pb-0">
          {(
            [
              { key: 'ALL', label: 'Tất cả' },
              { key: 'PENDING', label: 'Chờ duyệt' },
              { key: 'APPROVED', label: 'Đã duyệt' },
              { key: 'REJECTED', label: 'Từ chối' },
              { key: 'COMPLETED', label: 'Hoàn tất' },
            ] as const
          ).map((tab) => (
            <button
              key={tab.key}
              type="button"
              onClick={() => {
                setSelectedStatus(tab.key);
                setPage(1);
              }}
              className={`px-3 py-1.5 text-xs font-medium rounded-xl transition shrink-0 ${
                selectedStatus === tab.key
                  ? 'bg-[#1D1F20] text-white shadow-xs'
                  : 'text-neutral-600 hover:text-neutral-900 hover:bg-neutral-100'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm theo mã đơn, mã yêu cầu, lý do..."
          className="max-w-md w-full"
        />
      </div>

      <DataTable
        columns={columns}
        data={filteredReturns}
        isLoading={isLoading}
        page={page}
        totalPages={1}
        totalItems={filteredReturns.length}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy yêu cầu đổi trả nào."
      />

      {/* Detail Dialog */}
      <Dialog open={!!viewingReturn} onOpenChange={(open) => !open && setViewingReturn(null)}>
        <DialogContent className="sm:max-w-lg bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <RotateCcw className="size-5 text-neutral-700" />
              Chi tiết Yêu cầu Đổi trả #{viewingReturn?.id}
            </DialogTitle>
          </DialogHeader>

          {viewingReturn && (
            <div className="space-y-4 my-2 text-xs text-neutral-700">
              <div className="grid grid-cols-2 gap-3 bg-neutral-50 p-3 rounded-xl border border-neutral-100">
                <div>
                  <span className="text-[11px] text-neutral-400 block">Đơn hàng</span>
                  <span className="font-mono font-bold text-neutral-900">
                    #{viewingReturn.orderItem?.orderId || 'N/A'}
                  </span>
                </div>
                <div>
                  <span className="text-[11px] text-neutral-400 block">Trạng thái</span>
                  <Badge
                    variant={statusMeta[viewingReturn.status]?.variant || 'secondary'}
                    className="text-[10px] mt-0.5"
                  >
                    {statusMeta[viewingReturn.status]?.label || viewingReturn.status}
                  </Badge>
                </div>
                <div>
                  <span className="text-[11px] text-neutral-400 block">Sản phẩm</span>
                  <span className="font-semibold text-neutral-900">
                    {viewingReturn.orderItem?.productName || `Item #${viewingReturn.orderItemId}`}
                  </span>
                </div>
                <div>
                  <span className="text-[11px] text-neutral-400 block">Số lượng hoàn trả</span>
                  <span className="font-bold text-neutral-900">{viewingReturn.quantity} món</span>
                </div>
              </div>

              <div>
                <span className="text-[11px] font-bold text-neutral-500 uppercase block mb-1">
                  Lý do từ khách hàng
                </span>
                <p className="p-3 bg-neutral-50 rounded-xl border border-neutral-100 font-medium text-neutral-900">
                  {viewingReturn.reason}
                </p>
              </div>

              {viewingReturn.description && (
                <div>
                  <span className="text-[11px] font-bold text-neutral-500 uppercase block mb-1">
                    Mô tả chi tiết
                  </span>
                  <p className="p-3 bg-neutral-50 rounded-xl border border-neutral-100 text-neutral-700 whitespace-pre-wrap">
                    {viewingReturn.description}
                  </p>
                </div>
              )}

              {viewingReturn.medias && viewingReturn.medias.length > 0 && (
                <div>
                  <span className="text-[11px] font-bold text-neutral-500 uppercase block mb-1.5">
                    Ảnh / Video bằng chứng ({viewingReturn.medias.length})
                  </span>
                  <div className="grid grid-cols-3 gap-2">
                    {viewingReturn.medias.map((m, idx) => {
                      const isVideo =
                        m.mediaType?.includes('video') ||
                        m.mediaUrl.endsWith('.mp4') ||
                        m.mediaUrl.includes('video');
                      return isVideo ? (
                        <video
                          key={m.id || idx}
                          src={m.mediaUrl}
                          controls
                          className="w-full h-24 object-cover rounded-xl border border-neutral-200"
                        />
                      ) : (
                        <a
                          key={m.id || idx}
                          href={m.mediaUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="block group relative"
                        >
                          {/* eslint-disable-next-line @next/next/no-img-element */}
                          <img
                            src={m.mediaUrl}
                            alt="Hình ảnh bằng chứng"
                            className="w-full h-24 object-cover rounded-xl border border-neutral-200 group-hover:opacity-90 transition"
                          />
                        </a>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          )}

          <DialogFooter className="mt-4 flex justify-end gap-2 border-t border-[#EAE6DF] pt-3">
            <Button
              type="button"
              variant="outline"
              onClick={() => setViewingReturn(null)}
              className="rounded-xl border-[#EAE6DF]"
            >
              Đóng
            </Button>
            {viewingReturn?.status === 'PENDING' && (
              <>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setRejectTarget(viewingReturn)}
                  className="rounded-xl text-rose-700 hover:bg-rose-50 border-rose-200"
                >
                  <XCircle className="size-4 mr-1.5" /> Từ chối
                </Button>
                <Button
                  type="button"
                  onClick={() =>
                    updateStatusMutation.mutate({
                      returnId: viewingReturn.id,
                      status: 'APPROVED',
                    })
                  }
                  disabled={updateStatusMutation.isPending}
                  className="rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white"
                >
                  <CheckCircle2 className="size-4 mr-1.5" /> Phê duyệt
                </Button>
              </>
            )}
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Confirm Reject Dialog */}
      <ConfirmDialog
        open={!!rejectTarget}
        onOpenChange={(open) => !open && setRejectTarget(null)}
        title={`Từ chối yêu cầu đổi trả #${rejectTarget?.id}?`}
        description="Bạn có chắc chắn muốn từ chối yêu cầu đổi trả này? Hành động này sẽ thông báo cho khách hàng."
        confirmText="Xác nhận từ chối"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (rejectTarget) {
            updateStatusMutation.mutate({ returnId: rejectTarget.id, status: 'REJECTED' });
          }
        }}
      />
    </div>
  );
}