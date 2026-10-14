'use client';

import * as React from 'react';
import { useFormContext } from 'react-hook-form';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  MapPin, 
  Plus, 
  Trash2, 
  ChevronDown, 
  Check, 
  Building2, 
  Phone, 
  User, 
  Loader2 
} from 'lucide-react';
import { toast } from 'sonner';
import { authApi } from '@/api/auth.api';
import { CheckoutFormValues } from '../schemas';
import type { ShippingAddress } from '@/types';

interface ShippingFormProps {
  onNext: () => void;
}

export function ShippingForm({ onNext }: ShippingFormProps) {
  const queryClient = useQueryClient();
  const { register, trigger, setValue, getValues, formState: { errors } } = useFormContext<CheckoutFormValues>();

  const [selectedSavedAddressId, setSelectedSavedAddressId] = React.useState<number | 'NEW'>('NEW');
  const [isDropdownOpen, setIsDropdownOpen] = React.useState(false);
  const hasAutoSelectedRef = React.useRef(false);

  // 1. Tải danh sách sổ địa chỉ của User
  const { data: addressesData, isLoading } = useQuery({
    queryKey: ['checkout-addresses'],
    queryFn: async (): Promise<ShippingAddress[]> => {
      try {
        const res: any = await authApi.getAddresses();
        if (Array.isArray(res)) return res;
        if (Array.isArray(res?.data)) return res.data;
        if (Array.isArray(res?.data?.data)) return res.data.data;
        return [];
      } catch {
        return [];
      }
    },
  });

  const addresses: ShippingAddress[] = React.useMemo(() => addressesData || [], [addressesData]);
  const selectedAddress = React.useMemo(
    () => addresses.find((a) => a.id === selectedSavedAddressId),
    [addresses, selectedSavedAddressId]
  );

  // 2. Điền thông tin vào form khi chọn một địa chỉ
  const handleSelectAddress = React.useCallback(
    (val: number | 'NEW') => {
      setSelectedSavedAddressId(val);

      if (val === 'NEW') {
        setValue('shippingAddressId', undefined);
        setValue('shippingAddress.receiverName', '');
        setValue('shippingAddress.receiverPhone', '');
        setValue('shippingAddress.province', '');
        setValue('shippingAddress.district', '');
        setValue('shippingAddress.ward', '');
        setValue('shippingAddress.detailAddress', '');
      } else {
        setValue('shippingAddressId', val);
        const addr = addresses.find((a) => a.id === val);
        if (addr) {
          setValue('shippingAddress.receiverName', addr.receiverName);
          setValue('shippingAddress.receiverPhone', addr.phoneNumber);
          setValue('shippingAddress.province', addr.province);
          setValue('shippingAddress.district', addr.district);
          setValue('shippingAddress.ward', addr.ward);
          setValue('shippingAddress.detailAddress', addr.detailAddress);
        }
      }
    },
    [addresses, setValue]
  );

  // 3. Tự động gán địa chỉ mặc định (chỉ chạy 1 lần sau khi tải xong dữ liệu, tránh cascading render)
  React.useEffect(() => {
    if (!hasAutoSelectedRef.current && addresses.length > 0) {
      hasAutoSelectedRef.current = true;
      const defaultAddr = addresses.find((a) => a.isDefault) || addresses[0];
      if (defaultAddr) {
        Promise.resolve().then(() => {
          handleSelectAddress(defaultAddr.id);
        });
      }
    }
  }, [addresses, handleSelectAddress]);

  // 4. Bắt thao tác chỉnh sửa tay trong form để chuyển thành địa chỉ mới
  const registerWithDetectEdit = (name: any) => {
    const reg = register(name);
    return {
      ...reg,
      onChange: (e: any) => {
        reg.onChange(e);
        if (selectedSavedAddressId !== 'NEW') {
          setSelectedSavedAddressId('NEW');
          setValue('shippingAddressId', undefined);
        }
      },
    };
  };

  // 5. Mutation xóa địa chỉ trực tiếp
  const deleteAddressMutation = useMutation({
    mutationFn: (id: number) => authApi.deleteAddress(id),
    onSuccess: (_, deletedId) => {
      toast.success('Đã xóa địa chỉ thành công');
      queryClient.invalidateQueries({ queryKey: ['checkout-addresses'] });

      // Nếu xóa trúng địa chỉ đang chọn, chuyển sang chế độ nhập mới
      if (selectedSavedAddressId === deletedId) {
        handleSelectAddress('NEW');
      }
    },
    onError: () => {
      toast.error('Không thể xóa địa chỉ lúc này, vui lòng thử lại sau.');
    },
  });

  // 6. Mutation lưu địa chỉ mới vào Database
  const saveAddressMutation = useMutation({
    mutationFn: (data: any) => authApi.createAddress(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['checkout-addresses'] });
    },
  });

  // 7. Xác thực form và tiến hành tiếp tục
  const handleContinue = async () => {
    const isStepValid = await trigger([
      'shippingAddress.receiverName',
      'shippingAddress.receiverPhone',
      'shippingAddress.province',
      'shippingAddress.district',
      'shippingAddress.ward',
      'shippingAddress.detailAddress',
    ]);

    if (isStepValid) {
      const formVals = getValues();

      // Nếu là địa chỉ mới điền tay, tự động lưu vào Sổ địa chỉ người dùng
      if (selectedSavedAddressId === 'NEW') {
        try {
          await saveAddressMutation.mutateAsync({
            receiverName: formVals.shippingAddress?.receiverName || '',
            phoneNumber: formVals.shippingAddress?.receiverPhone || '',
            province: formVals.shippingAddress?.province || '',
            district: formVals.shippingAddress?.district || '',
            ward: formVals.shippingAddress?.ward || '',
            detailAddress: formVals.shippingAddress?.detailAddress || '',
            isDefault: addresses.length === 0,
          });
        } catch (e) {
          console.error('Không thể lưu địa chỉ vào DB:', e);
        }
      }

      onNext();
    }
  };

  if (isLoading) {
    return (
      <div className="py-12 text-center text-sm text-neutral-500">
        <Loader2 className="mx-auto size-5 animate-spin text-neutral-400 mb-2" />
        Đang tải thông tin giao hàng...
      </div>
    );
  }

  return (
    <div className="mt-6 space-y-6 animate-in fade-in duration-300">
      
      {/* =========================================================================
          KHỐI 1: KHUNG CHỌN ĐỊA CHỈ ĐÃ LƯU (STYLE SHOPEE / LAZADA CAO CẤP)
      ========================================================================= */}
      {addresses.length > 0 && (
        <div className="space-y-2">
          <label className="text-xs font-bold uppercase tracking-wider text-neutral-600 flex items-center justify-between">
            <span className="flex items-center gap-1.5">
              <MapPin className="size-3.5 text-neutral-700" />
              Sổ địa chỉ đã lưu ({addresses.length})
            </span>
            <span className="text-[11px] text-neutral-400 lowercase font-normal">
              (bấm để xem & đổi địa chỉ)
            </span>
          </label>

          <div className="relative rounded-2xl border border-[#EAE6DF] bg-white shadow-xs overflow-hidden transition-all">
            {/* Header / Thanh hiển thị địa chỉ đang chọn */}
            <div
              onClick={() => setIsDropdownOpen(!isDropdownOpen)}
              className="p-4 flex items-center justify-between cursor-pointer bg-white hover:bg-neutral-50/70 transition"
            >
              {selectedSavedAddressId !== 'NEW' && selectedAddress ? (
                <div className="flex items-start gap-3 min-w-0 flex-1">
                  <div className="mt-1 size-8 rounded-xl bg-[#84EA53]/20 flex items-center justify-center text-[#1D1F20] shrink-0 border border-[#84EA53]/40">
                    <MapPin className="size-4" />
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="text-sm font-bold text-neutral-900">
                        {selectedAddress.receiverName}
                      </span>
                      <span className="text-neutral-300">|</span>
                      <span className="text-xs font-mono font-medium text-neutral-600">
                        {selectedAddress.phoneNumber}
                      </span>
                      {selectedAddress.isDefault && (
                        <span className="rounded-full bg-[#84EA53]/20 border border-[#84EA53]/60 px-2 py-0.5 text-[10px] font-bold text-[#1D1F20]">
                          Mặc định
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-neutral-700 mt-1 font-medium truncate">
                      {selectedAddress.detailAddress}
                    </p>
                    <p className="text-[11px] text-neutral-400 mt-0.5 truncate">
                      Phường/Xã: {selectedAddress.ward} • Quận/Huyện: {selectedAddress.district} • Tỉnh/TP: {selectedAddress.province}
                    </p>
                  </div>
                </div>
              ) : (
                <div className="flex items-center gap-3">
                  <div className="size-8 rounded-xl bg-neutral-100 flex items-center justify-center text-neutral-500 shrink-0">
                    <Plus className="size-4" />
                  </div>
                  <div>
                    <span className="text-sm font-semibold text-neutral-900 block">
                      Đang nhập địa chỉ mới
                    </span>
                    <span className="text-xs text-neutral-400 block">
                      Điền các thông tin người nhận ở bảng bên dưới
                    </span>
                  </div>
                </div>
              )}

              <div className="flex items-center gap-1.5 ml-3 shrink-0">
                <span className="text-xs font-semibold text-neutral-600 hidden sm:inline">
                  {isDropdownOpen ? 'Thu gọn' : 'Thay đổi'}
                </span>
                <ChevronDown
                  className={`size-4 text-neutral-400 transition-transform duration-200 ${
                    isDropdownOpen ? 'rotate-180' : ''
                  }`}
                />
              </div>
            </div>

            {/* Menu danh sách sổ địa chỉ khi bấm mở ra */}
            {isDropdownOpen && (
              <div className="border-t border-[#EAE6DF] bg-neutral-50/50 divide-y divide-[#EAE6DF] max-h-80 overflow-y-auto">
                {addresses.map((addr) => {
                  const isCurrent = selectedSavedAddressId === addr.id;
                  return (
                    <div
                      key={addr.id}
                      onClick={() => {
                        handleSelectAddress(addr.id);
                        setIsDropdownOpen(false);
                      }}
                      className={`p-4 flex items-start justify-between gap-3 cursor-pointer transition hover:bg-white ${
                        isCurrent ? 'bg-white/90 border-l-4 border-l-[#1D1F20]' : 'border-l-4 border-l-transparent'
                      }`}
                    >
                      {/* Check radio indicator */}
                      <div className="pt-1 shrink-0">
                        <div
                          className={`size-4 rounded-full border flex items-center justify-center ${
                            isCurrent
                              ? 'border-[#84EA53] bg-[#84EA53]'
                              : 'border-[#EAE6DF] bg-white'
                          }`}
                        >
                          {isCurrent && <Check className="size-2.5 text-neutral-900 stroke-[3]" />}
                        </div>
                      </div>

                      {/* Thông tin chi tiết từng địa chỉ */}
                      <div className="min-w-0 flex-1 space-y-1">
                        <div className="flex items-center gap-2 flex-wrap">
                          <span className="text-xs font-bold text-neutral-900">
                            {addr.receiverName}
                          </span>
                          <span className="text-xs text-neutral-500 font-mono">
                            ({addr.phoneNumber})
                          </span>
                          {addr.isDefault && (
                            <span className="rounded bg-neutral-100 border border-[#EAE6DF] px-1.5 py-0.2 text-[9px] font-semibold text-neutral-600">
                              Mặc định
                            </span>
                          )}
                        </div>

                        <div className="text-xs text-neutral-800">
                          <span className="font-semibold text-neutral-500">Đường: </span>
                          {addr.detailAddress}
                        </div>

                        <div className="text-[11px] text-neutral-500">
                          <span className="font-semibold text-neutral-400">Khu vực: </span>
                          {addr.ward}, {addr.district}, {addr.province}
                        </div>
                      </div>

                      {/* Icon Xóa địa chỉ */}
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          if (confirm(`Bạn có chắc muốn xóa địa chỉ của "${addr.receiverName}"?`)) {
                            deleteAddressMutation.mutate(addr.id);
                          }
                        }}
                        disabled={deleteAddressMutation.isPending}
                        className="p-2 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition shrink-0"
                        title="Xóa địa chỉ này"
                      >
                        {deleteAddressMutation.isPending && deleteAddressMutation.variables === addr.id ? (
                          <Loader2 className="size-4 animate-spin" />
                        ) : (
                          <Trash2 className="size-4" />
                        )}
                      </button>
                    </div>
                  );
                })}

                {/* Dòng thêm địa chỉ mới ở đáy menu */}
                <div
                  onClick={() => {
                    handleSelectAddress('NEW');
                    setIsDropdownOpen(false);
                  }}
                  className="p-3.5 px-4 flex items-center gap-2.5 text-xs font-bold text-[#1D1F20] hover:bg-white transition cursor-pointer bg-neutral-100/50"
                >
                  <Plus className="size-4 text-[#84EA53]" />
                  <span>+ Nhập địa chỉ giao hàng mới khác</span>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* =========================================================================
          KHỐI 2: FORM CHI TIẾT ĐỊA CHỈ GIAO HÀNG (AUTO-FILL HOẶC TỰ ĐIỀN)
      ========================================================================= */}
      <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 sm:p-6 shadow-xs space-y-5">
        <div className="pb-3 border-b border-[#EAE6DF] flex items-center justify-between">
          <h4 className="text-sm font-bold text-neutral-900 flex items-center gap-2">
            <Building2 className="size-4 text-neutral-700" />
            Điền thông tin giao hàng
          </h4>

          {selectedSavedAddressId !== 'NEW' ? (
            <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full">
              Đang chọn từ sổ địa chỉ
            </span>
          ) : (
            <span className="text-[11px] font-semibold text-neutral-500 bg-neutral-100 px-2 py-0.5 rounded-full">
              Địa chỉ mới (sẽ được lưu tự động)
            </span>
          )}
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          {/* Họ tên người nhận */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-neutral-600 flex items-center gap-1.5">
              <User className="size-3 text-neutral-400" />
              Họ và tên người nhận *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.receiverName')}
              placeholder="Ví dụ: Nguyễn Văn A"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.receiverName ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.receiverName && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.receiverName.message}</p>
            )}
          </div>

          {/* Số điện thoại */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-neutral-600 flex items-center gap-1.5">
              <Phone className="size-3 text-neutral-400" />
              Số điện thoại người nhận *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.receiverPhone')}
              placeholder="Ví dụ: 0912345678"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.receiverPhone ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.receiverPhone && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.receiverPhone.message}</p>
            )}
          </div>

          {/* Tỉnh / Thành phố */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-neutral-600">
              Tỉnh / Thành phố *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.province')}
              placeholder="Ví dụ: TP. Hồ Chí Minh"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.province ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.province && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.province.message}</p>
            )}
          </div>

          {/* Quận / Huyện */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-neutral-600">
              Quận / Huyện *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.district')}
              placeholder="Ví dụ: Quận 1"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.district ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.district && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.district.message}</p>
            )}
          </div>

          {/* Phường / Xã */}
          <div className="sm:col-span-2 space-y-1">
            <label className="text-xs font-semibold text-neutral-600">
              Phường / Xã *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.ward')}
              placeholder="Ví dụ: Phường Bến Nghé"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.ward ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.ward && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.ward.message}</p>
            )}
          </div>

          {/* Địa chỉ chi tiết */}
          <div className="sm:col-span-2 space-y-1">
            <label className="text-xs font-semibold text-neutral-600">
              Địa chỉ chi tiết (Số nhà, tên tòa nhà, tên đường...) *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.detailAddress')}
              placeholder="Ví dụ: 123 Đường Lê Lợi"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.detailAddress ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.detailAddress && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.detailAddress.message}</p>
            )}
          </div>
        </div>
      </div>

      {/* =========================================================================
          KHỐI 3: GHI CHÚ ĐƠN HÀNG (TÙY CHỌN)
      ========================================================================= */}
      <div className="space-y-1.5">
        <label className="text-xs font-bold uppercase tracking-wider text-neutral-600">
          Ghi chú đơn hàng (Tùy chọn)
        </label>
        <textarea
          {...register('customerNote')}
          placeholder="Ví dụ: Giao hàng trong giờ hành chính, gọi trước khi đến..."
          rows={3}
          className="w-full rounded-2xl border border-[#EAE6DF] bg-white px-4 py-3 text-sm outline-none focus:border-[#84EA53] resize-none transition shadow-xs"
        />
      </div>

      {/* Nút hành động chính */}
      <button
        type="button"
        onClick={handleContinue}
        disabled={saveAddressMutation.isPending}
        className="w-full flex items-center justify-center gap-2 rounded-2xl bg-[#1D1F20] hover:bg-black py-4 text-sm font-bold !text-white [&_*]:!text-white transition-all shadow-sm active:scale-[0.99] cursor-pointer"
      >
        {saveAddressMutation.isPending ? (
          <>
            <Loader2 className="size-4 animate-spin !text-white [&_*]:!text-white" />
            <span>Đang lưu địa chỉ...</span>
          </>
        ) : (
          <span>Lưu địa chỉ & Tiếp tục thanh toán</span>
        )}
      </button>
    </div>
  );
}