'use client';

import * as React from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm, useFieldArray } from 'react-hook-form';
import { toast } from 'sonner';
import { ArrowLeft, Plus, Trash2, Loader2, Save, Boxes } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';
import { formatCurrency } from '@/lib/utils/format-currency';

interface ImportItem {
  variantId: number;
  quantityChange: number;
  importPrice: number;
}

interface ImportFormValues {
  supplier: string;
  note?: string;
  items: ImportItem[];
}

export default function AdminInventoryImportPage() {
  const router = useRouter();
  const queryClient = useQueryClient();

  // Lấy danh sách sản phẩm và biến thể để chọn
  const { data: prodRes, isLoading: isLoadingProds } = useQuery({
    queryKey: ['admin-products-variants-select'],
    queryFn: () => catalogApi.getProducts({ limit: 100 }),
  });

  const products = (prodRes?.data as any) || [];

  // Tạo danh sách phẳng tất cả SKU để đưa vào dropdown
  const availableVariants = React.useMemo(() => {
    const list: Array<{ id: number; label: string; currentStock: number }> = [];
    products.forEach((p: any) => {
      p.variants?.forEach((v: any) => {
        list.push({
          id: v.id,
          label: `${p.defaultName} — [${v.variantValue || 'Mặc định'}] (${v.sku})`,
          currentStock: v.stockQuantity,
        });
      });
    });
    return list;
  }, [products]);

  const { register, control, handleSubmit, watch, formState: { errors } } = useForm<ImportFormValues>({
    defaultValues: {
      supplier: '',
      note: '',
      items: [{ variantId: 0, quantityChange: 10, importPrice: 100000 }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'items',
  });

  const watchedItems = watch('items');
  const totalCost = watchedItems.reduce(
    (sum, item) => sum + (Number(item.quantityChange || 0) * Number(item.importPrice || 0)),
    0
  );

  const importMutation = useMutation({
    mutationFn: async (values: ImportFormValues) => {
      // Cập nhật tồn kho từng biến thể thông qua catalogApi
      const updatePromises = values.items.map((item) =>
        catalogApi.updateVariantStock(Number(item.variantId), Number(item.quantityChange))
      );
      return Promise.all(updatePromises);
    },
    onSuccess: () => {
      toast.success('Nhập kho thành công! Số lượng tồn đã được cập nhật.');
      queryClient.invalidateQueries({ queryKey: ['admin-inventory'] });
      queryClient.invalidateQueries({ queryKey: ['admin-products'] });
      router.push('/admin/inventory');
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi nhập kho');
    },
  });

  const onSubmit = (data: ImportFormValues) => {
    const hasInvalid = data.items.some((i) => !i.variantId || Number(i.variantId) === 0);
    if (hasInvalid) {
      toast.error('Vui lòng chọn biến thể sản phẩm cho tất cả các dòng');
      return;
    }
    importMutation.mutate(data);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-4 border-b border-[#EAE6DF] pb-4">
        <Link href="/admin/inventory" className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition">
          <ArrowLeft className="size-4 text-neutral-600" />
        </Link>
        <div>
          <span className="eyebrow block mb-0.5">VẬN HÀNH KHO</span>
          <h1 className="font-display text-2xl text-[#1D1F20] tracking-tight">Tạo phiếu nhập kho</h1>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
          <h3 className="text-sm font-bold text-neutral-900 border-b border-[#EAE6DF] pb-3">
            Thông tin nhà cung cấp & chứng từ
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Nhà cung cấp / Đối tác *
              </label>
              <input
                {...register('supplier', { required: 'Vui lòng nhập tên nhà cung cấp' })}
                placeholder="VD: Xưởng dệt may Hà Nội"
                className="w-full rounded-xl border border-[#EAE6DF] px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              />
              {errors.supplier && <p className="text-xs text-rose-500">{errors.supplier.message}</p>}
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Ghi chú phiếu nhập
              </label>
              <input
                {...register('note')}
                placeholder="VD: Nhập bổ sung đợt đầu tháng 10"
                className="w-full rounded-xl border border-[#EAE6DF] px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-3">
            <h3 className="text-sm font-bold text-neutral-900">Danh sách sản phẩm nhập</h3>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => append({ variantId: 0, quantityChange: 10, importPrice: 100000 })}
              className="rounded-xl border-[#EAE6DF] text-xs"
            >
              <Plus className="size-3.5 mr-1" /> Thêm dòng
            </Button>
          </div>

          {isLoadingProds ? (
            <div className="py-8 text-center text-xs text-neutral-400">Đang tải danh sách SKU...</div>
          ) : (
            <div className="space-y-3">
              {fields.map((field, idx) => (
                <div key={field.id} className="flex flex-col sm:flex-row gap-3 items-end bg-neutral-50/50 p-3.5 rounded-xl border border-[#EAE6DF]">
                  <div className="flex-1 space-y-1 w-full">
                    <label className="text-[11px] font-semibold text-neutral-500 uppercase">Sản phẩm / SKU *</label>
                    <select
                      {...register(`items.${idx}.variantId` as const, { required: true })}
                      className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-xs outline-none focus:border-neutral-900"
                    >
                      <option value={0}>-- Chọn SKU --</option>
                      {availableVariants.map((v) => (
                        <option key={v.id} value={v.id}>
                          {v.label} [Hiện có: {v.currentStock}]
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="w-full sm:w-28 space-y-1">
                    <label className="text-[11px] font-semibold text-neutral-500 uppercase">Số lượng</label>
                    <input
                      type="number"
                      min={1}
                      {...register(`items.${idx}.quantityChange` as const, { required: true, valueAsNumber: true })}
                      className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-xs font-mono outline-none"
                    />
                  </div>

                  <div className="w-full sm:w-36 space-y-1">
                    <label className="text-[11px] font-semibold text-neutral-500 uppercase">Giá nhập (₫)</label>
                    <input
                      type="number"
                      min={0}
                      step={1000}
                      {...register(`items.${idx}.importPrice` as const, { required: true, valueAsNumber: true })}
                      className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-xs font-mono outline-none"
                    />
                  </div>

                  <button
                    type="button"
                    onClick={() => remove(idx)}
                    className="p-2 text-neutral-400 hover:text-rose-600 transition self-center sm:self-end mb-1"
                    title="Xóa dòng"
                  >
                    <Trash2 className="size-4" />
                  </button>
                </div>
              ))}
            </div>
          )}

          <div className="flex justify-between items-center pt-3 border-t border-[#EAE6DF] text-sm">
            <span className="font-semibold text-neutral-700">Tổng giá trị phiếu nhập:</span>
            <span className="font-black text-lg text-neutral-900">{formatCurrency(totalCost)}</span>
          </div>
        </div>

        <div className="flex justify-end gap-3">
          <Link href="/admin/inventory">
            <Button type="button" variant="outline" className="rounded-xl border-[#EAE6DF]">Hủy</Button>
          </Link>
          <Button
            type="submit"
            disabled={importMutation.isPending}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800"
          >
            {importMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-1.5" /> Đang cập nhật kho...
              </>
            ) : (
              <>
                <Save className="size-4 mr-1.5" /> Xác nhận nhập kho
              </>
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}