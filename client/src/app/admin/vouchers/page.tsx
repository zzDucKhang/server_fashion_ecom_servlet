'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  Ticket,
  Plus,
  Edit3,
  Trash2,
  Power,
  CheckCircle2,
  XCircle,
  Percent,
  DollarSign,
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
import { updateVoucher, deleteVoucher } from '@/modules/loyalty-engage/api';
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { Voucher } from '@/types';

export default function AdminVouchersPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingVoucher, setEditingVoucher] = React.useState<Voucher | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Voucher | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-vouchers', page, search],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS, {
        params: { page, limit: 10, search: search || undefined },
      });
      return res.data;
    },
  });

  const defaultFormValues = React.useMemo(() => {
    const now = new Date();
    const nextWeek = new Date(now.getTime() + 7 * 86400000);

    return {
      code: '',
      discountType: 'FIXED_AMOUNT',
      discountValue: 50000,
      minOrderValue: 200000,
      totalUsageLimit: 100,
      userUsageLimit: 1,
      startDate: now.toISOString().slice(0, 16),
      endDate: nextWeek.toISOString().slice(0, 16),
    };
  }, []);

  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: defaultFormValues,
  });

  // Create mutation
  const createMutation = useMutation({
    mutationFn: (values: any) =>
      apiClient.post(API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS, {
        ...values,
        code: values.code.trim().toUpperCase(),
        discountValue: Number(values.discountValue),
        minOrderValue: Number(values.minOrderValue),
        totalUsageLimit: Number(values.totalUsageLimit),
        userUsageLimit: Number(values.userUsageLimit),
        startDate: new Date(values.startDate).toISOString(),
        endDate: new Date(values.endDate).toISOString(),
      }),
    onSuccess: () => {
      toast.success('Tạo Voucher mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-vouchers'] });
      setIsCreateOpen(false);
      reset(defaultFormValues);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Tạo Voucher thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: ({ id, values }: { id: number; values: any }) =>
      updateVoucher(id, values),
    onSuccess: () => {
      toast.success('Cập nhật Voucher thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-vouchers'] });
      setEditingVoucher(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật Voucher thất bại');
    },
  });

  // Toggle active mutation
  const toggleActiveMutation = useMutation({
    mutationFn: ({ id, isActive }: { id: number; isActive: boolean }) =>
      updateVoucher(id, { isActive }),
    onSuccess: (_, variables) => {
      toast.success(variables.isActive ? 'Đã kích hoạt mã Voucher!' : 'Đã vô hiệu hóa mã Voucher!');
      queryClient.invalidateQueries({ queryKey: ['admin-vouchers'] });
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Thao tác thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: (id: number) => deleteVoucher(id),
    onSuccess: () => {
      toast.success('Đã xóa Voucher thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-vouchers'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa Voucher thất bại');
    },
  });

  const handleOpenEdit = (v: Voucher) => {
    setEditingVoucher(v);
    setValue('code', v.code);
    setValue('discountType', v.discountType);
    setValue('discountValue', Number(v.discountValue));
    setValue('minOrderValue', Number(v.minOrderValue));
    setValue('totalUsageLimit', v.totalUsageLimit);
    setValue('userUsageLimit', v.userUsageLimit);
    setValue(
      'startDate',
      v.startDate ? new Date(v.startDate).toISOString().slice(0, 16) : ''
    );
    setValue(
      'endDate',
      v.endDate ? new Date(v.endDate).toISOString().slice(0, 16) : ''
    );
  };

  const vouchers: Voucher[] = Array.isArray(data?.data?.data)
    ? data.data.data
    : Array.isArray(data?.data)
    ? data.data
    : [];

  const totalPages = data?.data?.totalPages ?? data?.totalPages ?? 1;
  const totalItems = data?.data?.totalItems ?? data?.totalItems ?? 0;

  const columns: Column<Voucher>[] = [
    {
      header: 'Mã Voucher',
      cell: (v) => (
        <div className="flex items-center gap-2">
          <Ticket className="size-4 text-amber-500" />
          <span className="font-mono font-bold text-xs text-neutral-900 bg-neutral-100 px-2.5 py-1 rounded-md">
            {v.code}
          </span>
        </div>
      ),
    },
    {
      header: 'Loại giảm giá',
      cell: (v) => (
        <span className="text-xs font-semibold text-neutral-800">
          {v.discountType === 'PERCENTAGE'
            ? `${v.discountValue}%`
            : formatCurrency(v.discountValue)}
        </span>
      ),
    },
    {
      header: 'Đơn tối thiểu',
      cell: (v) => <span className="text-xs text-neutral-500">{formatCurrency(v.minOrderValue)}</span>,
    },
    {
      header: 'Lượt sử dụng',
      cell: (v) => (
        <span className="text-xs font-mono">
          {v.usedCount} / {v.totalUsageLimit || '∞'}
        </span>
      ),
    },
    {
      header: 'Thời hạn',
      cell: (v) => (
        <div className="text-[11px] text-neutral-500 leading-tight">
          <p>{formatDate(v.startDate)}</p>
          <p className="text-neutral-400">đến {formatDate(v.endDate)}</p>
        </div>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (v) => (
        <Badge variant={v.isActive ? 'success' : 'outline'} className="text-[10px]">
          {v.isActive ? 'Đang bật' : 'Vô hiệu hóa'}
        </Badge>
      ),
    },
    {
      header: 'Thao tác',
      className: 'text-right',
      cell: (v) => (
        <div className="flex justify-end items-center gap-1">
          <Button
            size="sm"
            variant="outline"
            onClick={() => toggleActiveMutation.mutate({ id: v.id, isActive: !v.isActive })}
            disabled={toggleActiveMutation.isPending}
            className={`h-8 px-2 text-xs rounded-lg transition ${
              v.isActive
                ? 'text-amber-700 hover:bg-amber-50 border-amber-200'
                : 'text-emerald-700 hover:bg-emerald-50 border-emerald-200'
            }`}
            title={v.isActive ? 'Vô hiệu hóa Voucher' : 'Kích hoạt Voucher'}
          >
            <Power className="size-3.5 mr-1" />
            {v.isActive ? 'Tắt' : 'Bật'}
          </Button>

          <button
            type="button"
            onClick={() => handleOpenEdit(v)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Sửa Voucher"
          >
            <Edit3 className="size-4" />
          </button>

          <button
            type="button"
            onClick={() => setDeleteTarget(v)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa Voucher"
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
          <span className="eyebrow block mb-1">MARKETING</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Voucher & Khuyến mãi
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Cấu hình mã giảm giá, kiểm soát thời hạn và kích hoạt/vô hiệu hóa linh hoạt.
          </p>
        </div>
        <Button
          onClick={() => {
            reset(defaultFormValues);
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white flex items-center gap-2"
        >
          <Plus className="size-4" /> Tạo Voucher mới
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm theo mã Voucher..."
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={vouchers}
        isLoading={isLoading}
        page={page}
        totalPages={totalPages}
        totalItems={totalItems}
        onPageChange={setPage}
        emptyMessage="Chưa có mã giảm giá nào."
      />

      {/* Modal Tạo Voucher */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-lg bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Ticket className="size-5 text-amber-500" />
              Tạo mã Voucher mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((d) => createMutation.mutate(d))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">Mã Code *</label>
              <input
                {...register('code', { required: true })}
                placeholder="VD: SALE50K, TET2026..."
                className="w-full rounded-xl border border-[#EAE6DF] px-4 py-2 font-mono uppercase text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">Loại giảm *</label>
                <select
                  {...register('discountType')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs bg-white outline-none"
                >
                  <option value="FIXED_AMOUNT">Số tiền cố định (VNĐ)</option>
                  <option value="PERCENTAGE">Phần trăm (%)</option>
                </select>
              </div>
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Giá trị giảm *
                </label>
                <input
                  type="number"
                  {...register('discountValue', { required: true, min: 1 })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Đơn tối thiểu (VNĐ)
                </label>
                <input
                  type="number"
                  {...register('minOrderValue')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Tổng lượt dùng tối đa
                </label>
                <input
                  type="number"
                  {...register('totalUsageLimit')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Ngày bắt đầu *
                </label>
                <input
                  type="datetime-local"
                  {...register('startDate', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                />
              </div>
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Ngày kết thúc *
                </label>
                <input
                  type="datetime-local"
                  {...register('endDate', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                />
              </div>
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
                {createMutation.isPending ? 'Đang tạo...' : 'Tạo Voucher'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Sửa Voucher */}
      <Dialog
        open={!!editingVoucher}
        onOpenChange={(open) => !open && setEditingVoucher(null)}
      >
        <DialogContent className="sm:max-w-lg bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Sửa mã Voucher #{editingVoucher?.code}
            </DialogTitle>
          </DialogHeader>

          {editingVoucher && (
            <form
              onSubmit={handleSubmit((d) =>
                updateMutation.mutate({
                  id: editingVoucher.id,
                  values: {
                    discountType: d.discountType,
                    discountValue: Number(d.discountValue),
                    minOrderValue: Number(d.minOrderValue),
                    totalUsageLimit: Number(d.totalUsageLimit),
                    startDate: d.startDate ? new Date(d.startDate).toISOString() : undefined,
                    endDate: d.endDate ? new Date(d.endDate).toISOString() : undefined,
                  },
                })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">Loại giảm *</label>
                  <select
                    {...register('discountType')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs bg-white outline-none"
                  >
                    <option value="FIXED_AMOUNT">Số tiền cố định (VNĐ)</option>
                    <option value="PERCENTAGE">Phần trăm (%)</option>
                  </select>
                </div>
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Giá trị giảm *
                  </label>
                  <input
                    type="number"
                    {...register('discountValue', { required: true })}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Đơn tối thiểu (VNĐ)
                  </label>
                  <input
                    type="number"
                    {...register('minOrderValue')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Tổng lượt dùng
                  </label>
                  <input
                    type="number"
                    {...register('totalUsageLimit')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Ngày bắt đầu
                  </label>
                  <input
                    type="datetime-local"
                    {...register('startDate')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Ngày kết thúc
                  </label>
                  <input
                    type="datetime-local"
                    {...register('endDate')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingVoucher(null)}
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
        title={`Xóa mã Voucher "${deleteTarget?.code}"?`}
        description="Bạn có chắc chắn muốn xóa mã giảm giá này? Thao tác này không thể hoàn tác."
        confirmText="Xóa Voucher"
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