'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  Truck,
  Plus,
  Edit3,
  Trash2,
  Calendar,
  CheckCircle2,
  XCircle,
  Clock,
  ArrowRight,
  Package,
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
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { Shipment, ShipmentStatus } from '@/types';

const shipmentStatusMeta: Record<
  ShipmentStatus,
  { label: string; variant: 'warning' | 'lime' | 'secondary' | 'success' | 'destructive' }
> = {
  PENDING: { label: 'Chờ lấy hàng', variant: 'warning' },
  PICKED_UP: { label: 'Đã lấy hàng', variant: 'lime' },
  IN_TRANSIT: { label: 'Đang vận chuyển', variant: 'secondary' },
  DELIVERED: { label: 'Giao thành công', variant: 'success' },
  FAILED: { label: 'Giao thất bại', variant: 'destructive' },
};

const carrierOptions = [
  'Giao Hàng Nhanh (GHN)',
  'Giao Hàng Tiết Kiệm (GHTK)',
  'Viettel Post',
  'VNPost',
  'J&T Express',
  'Shopee Xpress (SPX)',
  'Ninja Van',
  'Hỏa tốc nội thành',
];

export default function AdminShipmentsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [statusFilter, setStatusFilter] = React.useState<ShipmentStatus | ''>('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingShipment, setEditingShipment] = React.useState<Shipment | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Shipment | null>(null);

  // Form for create/edit
  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      orderId: '',
      carrier: 'Giao Hàng Nhanh (GHN)',
      trackingCode: '',
      actualShippingFee: 30000,
      status: 'PENDING' as ShipmentStatus,
      estimatedDelivery: '',
    },
  });

  // Query shipments
  const { data, isLoading } = useQuery({
    queryKey: ['admin-shipments', page, search, statusFilter],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.FULFILLMENT.SHIPMENTS, {
        params: {
          page,
          limit: 10,
          search: search || undefined,
          status: statusFilter || undefined,
        },
      });
      return res.data;
    },
  });

  // Create mutation
  const createMutation = useMutation({
    mutationFn: async (values: any) => {
      const res = await apiClient.post(API_ENDPOINTS.FULFILLMENT.SHIPMENTS, {
        orderId: Number(values.orderId),
        carrier: values.carrier,
        trackingCode: values.trackingCode.trim(),
        actualShippingFee: Number(values.actualShippingFee),
        status: values.status || 'PENDING',
        estimatedDelivery: values.estimatedDelivery ? new Date(values.estimatedDelivery).toISOString() : undefined,
      });
      return res.data;
    },
    onSuccess: () => {
      toast.success('Tạo vận đơn mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-shipments'] });
      setIsCreateOpen(false);
      reset();
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Tạo vận đơn thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async ({
      shipmentId,
      values,
    }: {
      shipmentId: number;
      values: Partial<Shipment>;
    }) => {
      const res = await apiClient.put(
        `${API_ENDPOINTS.FULFILLMENT.SHIPMENTS}/${shipmentId}`,
        values
      );
      return res.data;
    },
    onSuccess: () => {
      toast.success('Cập nhật vận đơn thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-shipments'] });
      setEditingShipment(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật vận đơn thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (shipmentId: number) => {
      const res = await apiClient.delete(
        `${API_ENDPOINTS.FULFILLMENT.SHIPMENTS}/${shipmentId}`
      );
      return res.data;
    },
    onSuccess: () => {
      toast.success('Đã xóa vận đơn!');
      queryClient.invalidateQueries({ queryKey: ['admin-shipments'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa vận đơn thất bại');
    },
  });

  const handleOpenEdit = (s: Shipment) => {
    setEditingShipment(s);
    setValue('orderId', s.orderId.toString());
    setValue('carrier', s.carrier);
    setValue('trackingCode', s.trackingCode);
    setValue('actualShippingFee', Number(s.actualShippingFee));
    setValue('status', s.status);
    setValue(
      'estimatedDelivery',
      s.estimatedDelivery ? new Date(s.estimatedDelivery).toISOString().slice(0, 10) : ''
    );
  };

  const handleStatusChange = (shipmentId: number, newStatus: ShipmentStatus) => {
    updateMutation.mutate({ shipmentId, values: { status: newStatus } });
  };

  const shipments: Shipment[] = (data?.data as any) || [];

  const columns: Column<Shipment>[] = [
    {
      header: 'Mã vận đơn',
      cell: (s) => (
        <div>
          <div className="flex items-center gap-2">
            <Truck className="size-4 text-neutral-500" />
            <span className="font-mono font-bold text-xs text-neutral-900 bg-neutral-100 px-2 py-0.5 rounded">
              {s.trackingCode}
            </span>
          </div>
          <p className="text-[11px] text-neutral-400 font-mono mt-0.5">
            Đơn hàng #{s.orderId}
          </p>
        </div>
      ),
    },
    {
      header: 'Đơn vị vận chuyển',
      cell: (s) => <span className="font-semibold text-xs text-neutral-800">{s.carrier}</span>,
    },
    {
      header: 'Người nhận',
      cell: (s) => (
        <div>
          <p className="text-xs font-semibold text-neutral-900">{s.order?.receiverName || 'Khách hàng'}</p>
          <p className="text-[11px] text-neutral-500 font-mono">{s.order?.receiverPhone || '---'}</p>
        </div>
      ),
    },
    {
      header: 'Cước vận chuyển',
      cell: (s) => (
        <span className="text-xs font-semibold text-neutral-900">
          {formatCurrency(s.actualShippingFee)}
        </span>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (s) => {
        const meta = shipmentStatusMeta[s.status] || {
          label: s.status,
          variant: 'secondary' as const,
        };
        return (
          <div className="flex items-center gap-1.5">
            <Badge variant={meta.variant} className="text-[10px]">
              {meta.label}
            </Badge>
          </div>
        );
      },
    },
    {
      header: 'Cập nhật trạng thái',
      cell: (s) => (
        <select
          value={s.status}
          onChange={(e) => handleStatusChange(s.id, e.target.value as ShipmentStatus)}
          disabled={updateMutation.isPending}
          className="text-xs rounded-lg border border-[#EAE6DF] bg-white px-2 py-1 text-neutral-700 outline-none focus:border-neutral-900"
        >
          {Object.entries(shipmentStatusMeta).map(([stKey, stVal]) => (
            <option key={stKey} value={stKey}>
              {stVal.label}
            </option>
          ))}
        </select>
      ),
    },
    {
      header: 'Thời gian tạo',
      cell: (s) => <span className="text-xs text-neutral-400">{formatDate(s.createdAt)}</span>,
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (s) => (
        <div className="flex justify-end gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(s)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Chỉnh sửa thông tin"
          >
            <Edit3 className="size-4" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTarget(s)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa vận đơn"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">VẬN HÀNH</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Vận đơn & Giao hàng
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Tạo mã vận đơn, theo dõi hành trình và cập nhật trạng thái giao hàng thủ công.
          </p>
        </div>
        <Button
          onClick={() => {
            reset({
              orderId: '',
              carrier: 'Giao Hàng Nhanh (GHN)',
              trackingCode: `TRK${Date.now().toString().slice(-8)}`,
              actualShippingFee: 30000,
              status: 'PENDING',
              estimatedDelivery: '',
            });
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white flex items-center gap-2"
        >
          <Plus className="size-4" /> Tạo vận đơn mới
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm theo mã vận đơn hoặc số điện thoại..."
          className="max-w-md w-full"
        />

        <div className="flex items-center gap-2">
          <span className="text-xs text-neutral-500 shrink-0">Lọc theo trạng thái:</span>
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value as ShipmentStatus | '');
              setPage(1);
            }}
            className="text-xs rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-neutral-700 outline-none"
          >
            <option value="">Tất cả trạng thái</option>
            {Object.entries(shipmentStatusMeta).map(([k, v]) => (
              <option key={k} value={k}>
                {v.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <DataTable
        columns={columns}
        data={shipments}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Chưa có vận đơn nào."
      />

      {/* Modal Tạo Vận Đơn */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Truck className="size-5 text-neutral-700" />
              Tạo vận đơn giao hàng mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => createMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Mã đơn hàng (Order ID) *
              </label>
              <input
                type="number"
                {...register('orderId', { required: true, min: 1 })}
                placeholder="VD: 101"
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Đơn vị vận chuyển *
                </label>
                <select
                  {...register('carrier', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                >
                  {carrierOptions.map((c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Mã tracking vận đơn *
                </label>
                <input
                  type="text"
                  {...register('trackingCode', { required: true })}
                  placeholder="VD: GHN123456VN"
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 font-mono uppercase text-xs outline-none focus:border-neutral-900"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Cước thực tế (VNĐ) *
                </label>
                <input
                  type="number"
                  {...register('actualShippingFee', { required: true, min: 0 })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Trạng thái ban đầu
                </label>
                <select
                  {...register('status')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                >
                  {Object.entries(shipmentStatusMeta).map(([k, v]) => (
                    <option key={k} value={k}>
                      {v.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Dự kiến giao hàng
              </label>
              <input
                type="date"
                {...register('estimatedDelivery')}
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
              />
            </div>

            <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsCreateOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="rounded-xl bg-[#1D1F20] text-white"
              >
                {createMutation.isPending ? 'Đang tạo...' : 'Tạo vận đơn'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Chỉnh sửa Vận Đơn */}
      <Dialog
        open={!!editingShipment}
        onOpenChange={(open) => !open && setEditingShipment(null)}
      >
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Chỉnh sửa Vận đơn #{editingShipment?.trackingCode}
            </DialogTitle>
          </DialogHeader>

          {editingShipment && (
            <form
              onSubmit={handleSubmit((values) =>
                updateMutation.mutate({
                  shipmentId: editingShipment.id,
                  values: {
                    carrier: values.carrier,
                    trackingCode: values.trackingCode.trim(),
                    actualShippingFee: Number(values.actualShippingFee),
                    status: values.status as ShipmentStatus,
                    estimatedDelivery: values.estimatedDelivery
                      ? new Date(values.estimatedDelivery).toISOString()
                      : undefined,
                  },
                })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Đơn vị vận chuyển *
                  </label>
                  <select
                    {...register('carrier', { required: true })}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                  >
                    {carrierOptions.map((c) => (
                      <option key={c} value={c}>
                        {c}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Mã tracking *
                  </label>
                  <input
                    type="text"
                    {...register('trackingCode', { required: true })}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 font-mono uppercase text-xs outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Cước thực tế (VNĐ) *
                  </label>
                  <input
                    type="number"
                    {...register('actualShippingFee', { required: true, min: 0 })}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>

                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Trạng thái
                  </label>
                  <select
                    {...register('status')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                  >
                    {Object.entries(shipmentStatusMeta).map(([k, v]) => (
                      <option key={k} value={k}>
                        {v.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Dự kiến giao hàng
                </label>
                <input
                  type="date"
                  {...register('estimatedDelivery')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                />
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingShipment(null)}
                  className="rounded-xl border-[#EAE6DF]"
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  disabled={updateMutation.isPending}
                  className="rounded-xl bg-[#1D1F20] text-white"
                >
                  {updateMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                </Button>
              </DialogFooter>
            </form>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title={`Xóa vận đơn #${deleteTarget?.trackingCode}?`}
        description="Bạn có chắc chắn muốn xóa vận đơn này? Hành động này không thể hoàn tác."
        confirmText="Xóa vận đơn"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (deleteTarget) {
            deleteMutation.mutate(deleteTarget.id);
          }
        }}
      />
    </div>
  );
}