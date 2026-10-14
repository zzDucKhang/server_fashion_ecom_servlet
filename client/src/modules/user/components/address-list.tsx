'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'sonner';
import { Plus, MapPin, Trash2, Edit3, Check, Loader2, AlertCircle, WifiOff } from 'lucide-react';
import { authApi } from '@/api/auth.api';
import { addressSchema, type AddressFormData } from '@/modules/auth/schemas';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { FormField } from '@/components/ui/form-field';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import type { ShippingAddress } from '@/types';

export function AddressList() {
  const queryClient = useQueryClient();
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [editingAddress, setEditingAddress] = React.useState<ShippingAddress | null>(null);
  const [deleteTargetId, setDeleteTargetId] = React.useState<number | null>(null);
  
  // State quản lý thông báo lỗi hiển thị trực tiếp trong Form
  const [formError, setFormError] = React.useState<string | null>(null);

  const { data: addressesData, isLoading } = useQuery({
    queryKey: ['addresses'],
    queryFn: async (): Promise<ShippingAddress[]> => {
      try {
        const res: any = await authApi.getAddresses();
        if (Array.isArray(res)) return res;
        if (Array.isArray(res?.data)) return res.data;
        if (Array.isArray(res?.data?.data)) return res.data.data;
        if (Array.isArray(res?.addresses)) return res.addresses;
        return [];
      } catch (error) {
        console.error('Lỗi khi tải danh sách địa chỉ:', error);
        return [];
      }
    },
  });

  const {
    control,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<AddressFormData>({
    resolver: zodResolver(addressSchema),
    defaultValues: {
      receiverName: '',
      phoneNumber: '',
      province: '',
      district: '',
      ward: '',
      detailAddress: '',
      isDefault: false,
    },
  });

  const isDefaultWatched = watch('isDefault');

  const openCreateDialog = () => {
    setEditingAddress(null);
    setFormError(null);
    reset({
      receiverName: '',
      phoneNumber: '',
      province: '',
      district: '',
      ward: '',
      detailAddress: '',
      isDefault: false,
    });
    setIsDialogOpen(true);
  };

  const openEditDialog = (addr: ShippingAddress) => {
    setEditingAddress(addr);
    setFormError(null);
    reset({
      receiverName: addr.receiverName,
      phoneNumber: addr.phoneNumber,
      province: addr.province,
      district: addr.district,
      ward: addr.ward,
      detailAddress: addr.detailAddress,
      isDefault: addr.isDefault,
    });
    setIsDialogOpen(true);
  };

  // Helper trích xuất thông báo lỗi chính xác
  const getErrorMessage = (err: any) => {
    if (!navigator.onLine || err?.message === 'Network Error' || err?.code === 'ERR_NETWORK') {
      return 'Mất kết nối mạng! Vui lòng kiểm tra lại đường truyền internet của bạn.';
    }
    return (
      err?.response?.data?.message ||
      err?.message ||
      'Thao tác thất bại do máy chủ không phản hồi, vui lòng thử lại sau.'
    );
  };

  const saveMutation = useMutation({
    mutationFn: async (data: AddressFormData) => {
      // Bắt ngay lỗi nếu trình duyệt đang ở trạng thái Offline
      if (typeof window !== 'undefined' && !navigator.onLine) {
        throw new Error('Mất kết nối mạng! Vui lòng kiểm tra lại đường truyền internet.');
      }

      if (editingAddress) {
        return authApi.updateAddress(editingAddress.id, data);
      } else {
        return authApi.createAddress(data);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      toast.success(editingAddress ? 'Cập nhật địa chỉ thành công!' : 'Thêm địa chỉ mới thành công!');
      setIsDialogOpen(false);
      setFormError(null);
    },
    onError: (err: any) => {
      const msg = getErrorMessage(err);
      setFormError(msg);
      toast.error(msg);
    },
    retry: false, // Tắt retry để khi offline lập tức trả về lỗi, không quay vòng vòng
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      if (typeof window !== 'undefined' && !navigator.onLine) {
        throw new Error('Mất kết nối mạng! Không thể thực hiện xóa.');
      }
      return authApi.deleteAddress(id);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      toast.success('Xóa địa chỉ thành công!');
      setDeleteTargetId(null);
    },
    onError: (err: any) => {
      const msg = getErrorMessage(err);
      toast.error(msg);
    },
    retry: false,
  });

  const onSubmit = (data: AddressFormData) => {
    setFormError(null);
    saveMutation.mutate(data);
  };

  const addresses: ShippingAddress[] = Array.isArray(addressesData) ? addressesData : [];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-6 border-b border-[#EAE6DF]">
        <div>
          <h2 className="text-xl font-semibold text-neutral-900">Sổ địa chỉ nhận hàng</h2>
          <p className="text-xs text-neutral-500 mt-1">
            Quản lý danh sách địa chỉ nhận hàng để tiện lợi khi thanh toán.
          </p>
        </div>
        <Button
          onClick={openCreateDialog}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2 shrink-0"
        >
          <Plus className="size-4" />
          <span>Thêm địa chỉ mới</span>
        </Button>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Skeleton className="h-44 rounded-2xl" />
          <Skeleton className="h-44 rounded-2xl" />
        </div>
      ) : addresses.length === 0 ? (
        <div className="text-center py-12 px-4 rounded-2xl border border-dashed border-[#EAE6DF] bg-neutral-50/40">
          <MapPin className="size-8 mx-auto text-neutral-400 mb-2" />
          <h3 className="text-sm font-semibold text-neutral-800">Chưa có địa chỉ nào</h3>
          <p className="text-xs text-neutral-500 mt-1 max-w-sm mx-auto">
            Bạn chưa lưu địa chỉ nhận hàng nào. Hãy thêm địa chỉ đầu tiên để bắt đầu đặt hàng.
          </p>
          <Button
            onClick={openCreateDialog}
            variant="outline"
            size="sm"
            className="mt-4 rounded-xl border-[#EAE6DF]"
          >
            Thêm ngay
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {addresses.map((addr) => (
            <div
              key={addr.id}
              className={`p-5 rounded-2xl border transition relative flex flex-col justify-between ${
                addr.isDefault
                  ? 'border-neutral-900 bg-neutral-50/30'
                  : 'border-[#EAE6DF] bg-white hover:border-neutral-300'
              }`}
            >
              <div className="space-y-2">
                <div className="flex items-center justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <h4 className="text-sm font-semibold text-neutral-900">
                      {addr.receiverName}
                    </h4>
                    {addr.isDefault && (
                      <Badge variant="lime" className="text-[10px] py-0">
                        Mặc định
                      </Badge>
                    )}
                  </div>
                  <div className="flex items-center gap-1">
                    <button
                      type="button"
                      onClick={() => openEditDialog(addr)}
                      className="p-1.5 text-neutral-400 hover:text-neutral-700 transition rounded-lg hover:bg-neutral-100"
                    >
                      <Edit3 className="size-3.5" />
                    </button>
                    <button
                      type="button"
                      onClick={() => setDeleteTargetId(addr.id)}
                      className="p-1.5 text-neutral-400 hover:text-rose-600 transition rounded-lg hover:bg-rose-50"
                    >
                      <Trash2 className="size-3.5" />
                    </button>
                  </div>
                </div>

                <p className="text-xs text-neutral-600">
                  <span className="text-neutral-400">Điện thoại: </span>
                  {addr.phoneNumber}
                </p>

                <p className="text-xs text-neutral-600 leading-relaxed">
                  <span className="text-neutral-400">Địa chỉ: </span>
                  {addr.detailAddress}, {addr.ward}, {addr.district}, {addr.province}
                </p>
              </div>

              {!addr.isDefault && (
                <div className="pt-3 mt-3 border-t border-[#EAE6DF]/70">
                  <button
                    type="button"
                    onClick={() =>
                      saveMutation.mutate({
                        ...addr,
                        isDefault: true,
                      })
                    }
                    className="text-xs text-neutral-500 hover:text-neutral-900 font-medium transition flex items-center gap-1"
                  >
                    <Check className="size-3.5" />
                    Đặt làm mặc định
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Add / Edit Dialog */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-lg bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900">
              {editingAddress ? 'Cập nhật địa chỉ' : 'Thêm địa chỉ nhận hàng'}
            </DialogTitle>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-2">
            {/* HỘP HIỂN THỊ LỖI KHI MẤT MẠNG HOẶC GỌI API THẤT BẠI */}
            {formError && (
              <div className="flex items-start gap-2.5 rounded-xl border border-rose-200 bg-rose-50/80 p-3.5 text-xs text-rose-800 animate-in fade-in-50">
                <WifiOff className="size-4 shrink-0 text-rose-600 mt-0.5" />
                <div className="flex-1">
                  <p className="font-semibold">Lỗi gửi dữ liệu</p>
                  <p className="mt-0.5 text-rose-700 leading-relaxed">{formError}</p>
                </div>
              </div>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <FormField
                control={control}
                name="receiverName"
                label="Tên người nhận"
                placeholder="Nguyễn Văn A"
                error={errors.receiverName?.message}
              />
              <FormField
                control={control}
                name="phoneNumber"
                label="Số điện thoại"
                placeholder="0912345678"
                error={errors.phoneNumber?.message}
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <FormField
                control={control}
                name="province"
                label="Tỉnh / Thành"
                placeholder="TP. Hồ Chí Minh"
                error={errors.province?.message}
              />
              <FormField
                control={control}
                name="district"
                label="Quận / Huyện"
                placeholder="Quận 1"
                error={errors.district?.message}
              />
              <FormField
                control={control}
                name="ward"
                label="Phường / Xã"
                placeholder="Bến Nghé"
                error={errors.ward?.message}
              />
            </div>

            <FormField
              control={control}
              name="detailAddress"
              label="Địa chỉ chi tiết (Số nhà, tên đường)"
              placeholder="123 Đường Lê Lợi"
              error={errors.detailAddress?.message}
            />

            <label className="flex items-center gap-2 cursor-pointer pt-1">
              <input
                type="checkbox"
                checked={isDefaultWatched}
                onChange={(e) => setValue('isDefault', e.target.checked)}
                className="rounded border-[#EAE6DF] accent-neutral-900 size-4"
              />
              <span className="text-xs text-neutral-700">Đặt làm địa chỉ giao hàng mặc định</span>
            </label>

            <DialogFooter className="mt-6 flex flex-row justify-end gap-2.5 pt-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsDialogOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={saveMutation.isPending}
                className="rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium"
              >
                {saveMutation.isPending ? (
                  <>
                    <Loader2 className="size-4 animate-spin mr-2" />
                    Đang lưu...
                  </>
                ) : (
                  'Lưu địa chỉ'
                )}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Delete Confirm */}
      <ConfirmDialog
        open={deleteTargetId !== null}
        onOpenChange={(open) => !open && setDeleteTargetId(null)}
        title="Xóa địa chỉ"
        description="Bạn có chắc chắn muốn xóa địa chỉ nhận hàng này? Thao tác này không thể hoàn tác."
        variant="destructive"
        confirmText="Xóa vĩnh viễn"
        isLoading={deleteMutation.isPending}
        onConfirm={() => {
          if (deleteTargetId) deleteMutation.mutate(deleteTargetId);
        }}
      />
    </div>
  );
}