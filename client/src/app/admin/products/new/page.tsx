'use client';

import * as React from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { ArrowLeft, Loader2, Plus, Sparkles, Upload } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';

interface CreateProductFormValues {
  defaultName: string;
  categoryId: number;
  brandId?: number;
  thumbnailUrl?: string;
  status: 'DRAFT' | 'PUBLISHED';
}

export default function AdminNewProductPage() {
  const router = useRouter();

  const { data: catRes } = useQuery({
    queryKey: ['admin-categories-select'],
    queryFn: () => catalogApi.getCategories(),
  });

  const { data: brandRes } = useQuery({
    queryKey: ['admin-brands-select'],
    queryFn: () => catalogApi.getBrands({ limit: 100 }),
  });

  const categories = (catRes?.data as any) || [];
  const brands = (brandRes?.data as any) || [];

  const { register, handleSubmit, formState: { errors } } = useForm<CreateProductFormValues>({
    defaultValues: {
      defaultName: '',
      status: 'PUBLISHED',
      thumbnailUrl: '',
    },
  });

  const createMutation = useMutation({
    mutationFn: (values: CreateProductFormValues) => catalogApi.createProduct({
      ...values,
      categoryId: Number(values.categoryId),
      brandId: values.brandId ? Number(values.brandId) : undefined,
    }),
    onSuccess: (res: any) => {
      const createdId = res?.data?.id || res?.id;
      toast.success('Khởi tạo sản phẩm thành công! Hãy tiếp tục thiết lập các biến thể SKU.');
      if (createdId) {
        router.push(`/admin/products/${createdId}/variants`);
      } else {
        router.push('/admin/products');
      }
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Không thể tạo sản phẩm mới');
    },
  });

  const onSubmit = (data: CreateProductFormValues) => {
    createMutation.mutate(data);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-4 border-b border-[#EAE6DF] pb-4">
        <Link href="/admin/products" className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition">
          <ArrowLeft className="size-4 text-neutral-600" />
        </Link>
        <div>
          <span className="eyebrow block mb-1">CATALOG</span>
          <h1 className="font-display text-3xl text-[#1D1F20] tracking-tight">Thêm sản phẩm mới</h1>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-5">
          <h2 className="text-sm font-bold text-neutral-900 border-b border-[#EAE6DF] pb-3">
            1. Thông tin chung
          </h2>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Tên sản phẩm *
            </label>
            <input
              {...register('defaultName', { required: 'Tên sản phẩm là bắt buộc' })}
              placeholder="VD: Áo Thun Oversize Minimalist Cotton 100%"
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            />
            {errors.defaultName && <p className="text-xs text-rose-500 mt-1">{errors.defaultName.message}</p>}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Danh mục *
              </label>
              <select
                {...register('categoryId', { required: 'Vui lòng chọn danh mục' })}
                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              >
                <option value="">-- Chọn danh mục --</option>
                {categories.map((c: any) => (
                  <option key={c.id} value={c.id}>{c.defaultName}</option>
                ))}
              </select>
              {errors.categoryId && <p className="text-xs text-rose-500 mt-1">{errors.categoryId.message}</p>}
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Thương hiệu (Tùy chọn)
              </label>
              <select
                {...register('brandId')}
                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              >
                <option value="">-- Không thương hiệu --</option>
                {brands.map((b: any) => (
                  <option key={b.id} value={b.id}>{b.defaultName}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Ảnh đại diện Thumbnail (URL)
            </label>
            <input
              {...register('thumbnailUrl')}
              placeholder="https://images.unsplash.com/... hoặc đường dẫn ảnh"
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Trạng thái mở bán
            </label>
            <select
              {...register('status')}
              className="w-full sm:w-60 rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            >
              <option value="PUBLISHED">Công khai (PUBLISHED)</option>
              <option value="DRAFT">Lưu nháp (DRAFT)</option>
            </select>
          </div>
        </div>

        <div className="flex justify-end gap-3">
          <Link href="/admin/products">
            <Button type="button" variant="outline" className="rounded-xl border-[#EAE6DF]">Hủy</Button>
          </Link>
          <Button
            type="submit"
            disabled={createMutation.isPending}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800"
          >
            {createMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-2" /> Đang tạo...
              </>
            ) : (
              'Lưu & Thiết lập Biến thể SKU →'
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}