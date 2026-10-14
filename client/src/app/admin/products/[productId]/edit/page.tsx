'use client';

import * as React from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  ArrowLeft,
  Loader2,
  Save,
  Layers,
  Tag as TagIcon,
  X,
  Search,
  ChevronDown,
} from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import type { Tag } from '@/types';

interface EditProductFormValues {
  defaultName: string;
  categoryId: number | string;
  brandId: number | string;
  thumbnailUrl: string;
  status: 'DRAFT' | 'PUBLISHED' | 'OUT_OF_STOCK' | 'ARCHIVED';
  tagIds: number[];
}

export default function AdminEditProductPage() {
  const params = useParams<{ productId: string }>();
  const router = useRouter();
  const queryClient = useQueryClient();
  const productId = Number(params.productId);

  const [isDropdownOpen, setIsDropdownOpen] = React.useState(false);
  const [tagSearch, setTagSearch] = React.useState('');
  const dropdownRef = React.useRef<HTMLDivElement>(null);

  // 1. Chi tiết sản phẩm
  const { data: prodRes, isLoading: isLoadingProd } = useQuery({
    queryKey: ['admin-product-edit', productId],
    queryFn: () => catalogApi.getProductById(productId),
    enabled: !!productId,
  });

  // 2. Danh mục
  const { data: catRes } = useQuery({
    queryKey: ['admin-categories-select'],
    queryFn: () => catalogApi.getCategories(),
  });

  // 3. Thương hiệu
  const { data: brandRes } = useQuery({
    queryKey: ['admin-brands-select'],
    queryFn: () => catalogApi.getBrands({ limit: 100 }),
  });

  // 4. Tags
  const { data: tagRes } = useQuery({
    queryKey: ['admin-tags-select'],
    queryFn: () => catalogApi.getTags(),
  });

  const product = prodRes?.data as any;
  const categories = (catRes?.data as any) || [];
  const brands = (brandRes?.data as any) || [];
  const allTags: Tag[] = (tagRes?.data as any) || [];

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<EditProductFormValues>({
    defaultValues: {
      defaultName: '',
      categoryId: '',
      brandId: '',
      thumbnailUrl: '',
      status: 'PUBLISHED',
      tagIds: [],
    },
  });

  const selectedTagIds = watch('tagIds') || [];

  // =========================================================================
  // BỘ NẠP DỮ LIỆU ĐA TƯƠNG THÍCH (CHO CẢ NODE.JS LẪN JAVA SERVLET)
  // =========================================================================
  React.useEffect(() => {
    if (!product) return;

    // 1. Chuẩn hóa CategoryId:
    // - Node.js: product.categoryId
    // - Java: product.category?.id hoặc product.category (nếu là số)
    const rawCatId =
      product.categoryId ??
      product.category?.id ??
      (typeof product.category === 'number' ? product.category : null);
    const resolvedCategoryId = rawCatId !== null && rawCatId !== undefined ? Number(rawCatId) : '';

    // 2. Chuẩn hóa BrandId:
    // - Node.js: product.brandId
    // - Java: product.brand?.id hoặc product.brand (nếu là số)
    const rawBrandId =
      product.brandId ??
      product.brand?.id ??
      (typeof product.brand === 'number' ? product.brand : null);
    const resolvedBrandId = rawBrandId !== null && rawBrandId !== undefined ? Number(rawBrandId) : '';

    // 3. Chuẩn hóa TagIds:
    // - Hỗ trợ cả: [{ id: 1 }], [{ tag: { id: 1 } }], [1, 2], [{ tagId: 1 }]
    let resolvedTagIds: number[] = [];
    if (Array.isArray(product.tags)) {
      resolvedTagIds = product.tags
        .map((t: any) => {
          if (typeof t === 'number') return t;
          return t?.id ?? t?.tag?.id ?? t?.tagId ?? null;
        })
        .filter((id: any) => typeof id === 'number' && !isNaN(id));
    } else if (Array.isArray(product.tagIds)) {
      resolvedTagIds = product.tagIds.map(Number).filter((id: number) => !isNaN(id));
    }

    reset({
      defaultName: product.defaultName || '',
      categoryId: resolvedCategoryId,
      brandId: resolvedBrandId,
      thumbnailUrl: product.thumbnailUrl || '',
      status: product.status || 'PUBLISHED',
      tagIds: resolvedTagIds,
    });
  }, [product, categories.length, brands.length, reset]);

  // Click outside đóng Dropdown Tag
  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Lọc tag hiển thị
  const selectedTags = React.useMemo(() => {
    return allTags.filter((t) => selectedTagIds.includes(t.id));
  }, [allTags, selectedTagIds]);

  const filteredAvailableTags = React.useMemo(() => {
    return allTags
      .filter((t) => !selectedTagIds.includes(t.id))
      .filter((t) => t.name.toLowerCase().includes(tagSearch.toLowerCase().trim()));
  }, [allTags, selectedTagIds, tagSearch]);

  const addTag = (id: number) => {
    setValue('tagIds', [...selectedTagIds, id], { shouldDirty: true });
    setTagSearch('');
  };

  const removeTag = (id: number) => {
    setValue(
      'tagIds',
      selectedTagIds.filter((tId) => tId !== id),
      { shouldDirty: true }
    );
  };

  // =========================================================================
  // GỬI DỮ LIỆU ĐA TƯƠNG THÍCH (AN TOÀN CHO CẢ 2 SERVER)
  // =========================================================================
  const updateMutation = useMutation({
    mutationFn: (values: EditProductFormValues) => {
      // Ép kiểu chuẩn:
      // - Java không nhận "" cho kiểu Integer -> chuyển thành null
      // - Node.js Prisma nhận null để disconnect quan hệ
      const payload = {
        defaultName: values.defaultName.trim(),
        categoryId: Number(values.categoryId),
        brandId: values.brandId !== '' && values.brandId !== undefined ? Number(values.brandId) : null,
        thumbnailUrl: values.thumbnailUrl?.trim() || null,
        status: values.status,
        tagIds: values.tagIds || [],
      };

      return catalogApi.updateProduct(productId, payload as any);
    },
    onSuccess: () => {
      toast.success('Cập nhật thông tin sản phẩm thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-product-edit', productId] });
      queryClient.invalidateQueries({ queryKey: ['admin-products'] });
      router.push('/admin/products');
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật sản phẩm thất bại');
    },
  });

  if (isLoadingProd) {
    return (
      <div className="flex justify-center py-20">
        <Loader2 className="size-8 animate-spin text-neutral-400" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-4">
        <div className="flex items-center gap-4">
          <Link
            href="/admin/products"
            className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition"
          >
            <ArrowLeft className="size-4 text-neutral-600" />
          </Link>
          <div>
            <span className="eyebrow block mb-0.5">CATALOG</span>
            <h1 className="font-display text-2xl text-[#1D1F20] tracking-tight">
              Chỉnh sửa sản phẩm #{productId}
            </h1>
          </div>
        </div>

        <Link href={`/admin/products/${productId}/variants`}>
          <Button variant="outline" className="rounded-xl border-[#EAE6DF] text-xs">
            <Layers className="size-4 mr-1.5 text-emerald-600" /> Quản lý SKU Biến thể
          </Button>
        </Link>
      </div>

      <form onSubmit={handleSubmit((d) => updateMutation.mutate(d))} className="space-y-6">
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-5">
          {/* Tên sản phẩm */}
          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Tên sản phẩm *
            </label>
            <input
              {...register('defaultName', { required: 'Tên sản phẩm là bắt buộc' })}
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            />
            {errors.defaultName && (
              <p className="text-xs text-rose-500 mt-1">{errors.defaultName.message}</p>
            )}
          </div>

          {/* Danh mục & Thương hiệu */}
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
                  <option key={c.id} value={c.id}>
                    {c.defaultName}
                  </option>
                ))}
              </select>
              {errors.categoryId && (
                <p className="text-xs text-rose-500 mt-1">{errors.categoryId.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Thương hiệu
              </label>
              <select
                {...register('brandId')}
                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              >
                <option value="">-- Không thương hiệu --</option>
                {brands.map((b: any) => (
                  <option key={b.id} value={b.id}>
                    {b.defaultName}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Quản lý Thẻ Tag (Dropdown chọn tự động nhảy lên hàng trên) */}
          <div className="space-y-2.5 pt-1 border-t border-[#EAE6DF]" ref={dropdownRef}>
            <div className="flex items-center justify-between">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600 flex items-center gap-1.5">
                <TagIcon className="size-3.5 text-neutral-500" />
                Thẻ Tag sản phẩm (Tags)
              </label>
              {selectedTagIds.length > 0 && (
                <button
                  type="button"
                  onClick={() => setValue('tagIds', [], { shouldDirty: true })}
                  className="text-[11px] font-semibold text-rose-500 hover:text-rose-700 transition"
                >
                  Bỏ chọn tất cả ({selectedTagIds.length})
                </button>
              )}
            </div>

            {/* Hàng chứa các tag đã chọn */}
            <div className="flex flex-wrap gap-1.5 min-h-8 rounded-xl border border-dashed border-[#EAE6DF] bg-neutral-50/50 p-2">
              {selectedTags.length === 0 ? (
                <span className="text-xs text-neutral-400 italic px-1.5 py-0.5">
                  Chưa chọn thẻ tag nào cho sản phẩm...
                </span>
              ) : (
                selectedTags.map((tag) => (
                  <span
                    key={tag.id}
                    className="inline-flex items-center gap-1 bg-[#1D1F20] text-white text-[11px] font-semibold pl-2.5 pr-1.5 py-1 rounded-lg animate-in zoom-in-95 duration-150"
                  >
                    <span>{tag.name}</span>
                    <button
                      type="button"
                      onClick={() => removeTag(tag.id)}
                      className="p-0.5 hover:bg-white/20 rounded text-[#84EA53] transition cursor-pointer"
                    >
                      <X className="size-3" />
                    </button>
                  </span>
                ))
              )}
            </div>

            {/* Ô tìm kiếm & Dropdown chọn tag */}
            <div className="relative">
              <div
                onClick={() => setIsDropdownOpen(true)}
                className={cn(
                  'flex items-center gap-2 w-full rounded-xl border bg-white pl-3.5 pr-4 py-2 text-sm shadow-xs cursor-pointer transition',
                  isDropdownOpen ? 'border-neutral-900 ring-1 ring-neutral-900' : 'border-[#EAE6DF]'
                )}
              >
                <Search className="size-4 text-neutral-400 shrink-0" />
                <input
                  type="text"
                  placeholder="Tìm kiếm hoặc Chọn thẻ tag..."
                  value={tagSearch}
                  onChange={(e) => {
                    setTagSearch(e.target.value);
                    setIsDropdownOpen(true);
                  }}
                  onFocus={() => setIsDropdownOpen(true)}
                  className="w-full bg-transparent outline-none text-xs text-neutral-900 font-medium placeholder:text-neutral-400"
                />
                <ChevronDown
                  className={cn(
                    'size-4 text-neutral-400 transition-transform duration-200 shrink-0',
                    isDropdownOpen && 'rotate-180'
                  )}
                />
              </div>

              {isDropdownOpen && (
                <div className="absolute left-0 mt-1.5 w-full max-h-60 overflow-y-auto rounded-xl border border-[#EAE6DF] bg-white shadow-lg p-1.5 z-40 animate-in fade-in slide-in-from-top-1 duration-150">
                  {filteredAvailableTags.length === 0 ? (
                    <div className="px-3.5 py-2.5 text-xs text-neutral-400 italic">
                      {tagSearch.trim() ? 'Không tìm thấy tag phù hợp' : 'Tất cả các thẻ tag đã được thêm'}
                    </div>
                  ) : (
                    filteredAvailableTags.map((tag) => (
                      <button
                        key={tag.id}
                        type="button"
                        onClick={() => addTag(tag.id)}
                        className="flex items-center justify-between w-full px-3 py-2 text-xs font-semibold text-neutral-700 hover:text-neutral-900 hover:bg-neutral-50 rounded-lg transition text-left cursor-pointer"
                      >
                        <span>{tag.name}</span>
                        <span className="text-[10px] text-neutral-400 font-mono font-normal">
                          (id: {tag.id})
                        </span>
                      </button>
                    ))
                  )}
                </div>
              )}
            </div>
          </div>

          {/* Thumbnail */}
          <div className="space-y-1.5 pt-1 border-t border-[#EAE6DF]">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Ảnh đại diện Thumbnail (URL)
            </label>
            <input
              {...register('thumbnailUrl')}
              placeholder="https://images.unsplash.com/... hoặc đường dẫn ảnh"
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            />
          </div>

          {/* Trạng thái */}
          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Trạng thái sản phẩm
            </label>
            <select
              {...register('status')}
              className="w-full sm:w-60 rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            >
              <option value="PUBLISHED">Công khai (PUBLISHED)</option>
              <option value="DRAFT">Lưu nháp (DRAFT)</option>
              <option value="OUT_OF_STOCK">Hết hàng (OUT_OF_STOCK)</option>
              <option value="ARCHIVED">Lưu trữ (ARCHIVED)</option>
            </select>
          </div>
        </div>

        <div className="flex justify-end gap-3">
          <Link href="/admin/products">
            <Button type="button" variant="outline" className="rounded-xl border-[#EAE6DF]">
              Hủy
            </Button>
          </Link>
          <Button
            type="submit"
            disabled={updateMutation.isPending}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800"
          >
            {updateMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-1.5" /> Đang cập nhật...
              </>
            ) : (
              <>
                <Save className="size-4 mr-1.5" /> Lưu thay đổi
              </>
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}