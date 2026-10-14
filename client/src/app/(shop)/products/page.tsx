'use client';

import * as React from 'react';
import { Suspense } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'next/navigation';
import { catalogApi } from '@/api/catalog.api';
import { ProductCard } from '@/modules/catalog/components/product-card';
import { SearchInput } from '@/components/ui/search-input';
import { Skeleton } from '@/components/ui/skeleton';
import { ShoppingBag } from 'lucide-react';
import type { Product } from '@/modules/catalog/mock';

function ProductsContent() {
  const searchParams = useSearchParams();
  const initialSearch = searchParams.get('search') || '';
  const [search, setSearch] = React.useState(initialSearch);

  const { data: response, isLoading } = useQuery({
    queryKey: ['catalog-products-list', search],
    queryFn: () => catalogApi.getProducts({ search: search || undefined, limit: 20 }),
  });

  const apiProducts = (response?.data as any) || [];

  // Map dữ liệu phẳng sang Model hiển thị của ProductCard
  const productList: Product[] = apiProducts.map((p: any) => ({
    slug: String(p.id),
    name: p.defaultName,
    brand: p.brand?.defaultName || 'Fashion Studio',
    category: p.category?.defaultName || 'Thời trang',
    price: Number(p.variants?.[0]?.sellingPrice || p.variants?.[0]?.originalPrice || 0),
    originalPrice: Number(p.variants?.[0]?.originalPrice || 0),
    rating: 5.0, // Chỗ này tương lai có thể call API Review để lấy
    reviews: 12,
    images: p.images?.length > 0
      ? p.images.map((img: any) => img.imageUrl)
      : [p.thumbnailUrl || 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80'],
    colors: ['#1D1F20', '#ded5c8'],
    sizes: ['S', 'M', 'L'],
    description: p.defaultName || 'Sản phẩm thời trang cao cấp.',
  }));

  return (
    <div className="mx-auto max-w-[1440px] px-4 py-8 sm:px-8 lg:px-12">
      <div className="mb-8 flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#EAE6DF] pb-6">
        <div>
          <span className="eyebrow block mb-1">BỘ SƯU TẬP</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            {initialSearch ? `Kết quả cho "${initialSearch}"` : 'Tất cả sản phẩm'}
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Khám phá những thiết kế thời trang tối giản và hiện đại.
          </p>
        </div>

        <div className="w-full sm:w-80">
          <SearchInput
            value={search}
            onChange={setSearch}
            placeholder="Tìm kiếm sản phẩm..."
          />
        </div>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-2 gap-x-4 gap-y-8 sm:grid-cols-3 lg:grid-cols-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <div key={i} className="space-y-3">
              <Skeleton className="aspect-[.88] w-full rounded-xl" />
              <Skeleton className="h-4 w-3/4 rounded" />
              <Skeleton className="h-4 w-1/2 rounded" />
            </div>
          ))}
        </div>
      ) : productList.length === 0 ? (
        <div className="py-20 text-center">
          <ShoppingBag className="mx-auto size-12 text-neutral-300 mb-3" />
          <h3 className="text-sm font-semibold text-neutral-800">Không tìm thấy sản phẩm</h3>
          <p className="text-xs text-neutral-500 mt-1">Vui lòng thử tìm kiếm với từ khóa khác.</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-x-4 gap-y-8 sm:grid-cols-3 lg:grid-cols-4">
          {productList.map((product) => (
            <ProductCard key={product.slug} product={product} />
          ))}
        </div>
      )}
    </div>
  );
}

export default function ProductsCatalogPage() {
  return (
    <Suspense fallback={<div className="p-20 text-center text-sm text-neutral-500">Đang tải sản phẩm...</div>}>
      <ProductsContent />
    </Suspense>
  );
}