'use client';

import Link from 'next/link';
import Image from 'next/image';
import { ChevronRight, ShoppingBag, ArrowRight, Layers } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { catalogApi } from '@/api/catalog.api';
import { ProductCard } from '@/modules/catalog/components/product-card';
import { FlashSaleSection } from '@/modules/flash-sale/components/flash-sale-section';
import { Skeleton } from '@/components/ui/skeleton';
import type { Product } from '@/modules/catalog/mock';
import { SiteHeader } from '@/components/layouts/site-header';
import { SiteFooter } from '@/components/layouts/site-footer'; // Nhớ import Footer để đủ khung nền

// Danh sách ảnh fallback minh họa chất lượng cao khi danh mục chưa cấu hình iconUrl
const CATEGORY_IMAGES = [
  'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80',
];

export default function Home() {
  // 1. Fetch danh mục từ server
  const { data: categoryResponse, isLoading: isLoadingCategories } = useQuery({
    queryKey: ['home-categories'],
    queryFn: () => catalogApi.getCategories(),
  });

  // 2. Fetch danh sách sản phẩm từ server
  const { data: productResponse, isLoading: isLoadingProducts } = useQuery({
    queryKey: ['home-products'],
    queryFn: () => catalogApi.getProducts({ limit: 8, status: 'PUBLISHED' }),
  });

  const categories = categoryResponse?.data || [];
  const apiProducts = productResponse?.data || [];

  // Map dữ liệu sản phẩm từ Server API sang model hiển thị của ProductCard
  const products: Product[] = apiProducts.map((p: any) => {
    const firstVariant = p.variants?.[0];
    const originalPrice = firstVariant?.originalPrice ? Number(firstVariant.originalPrice) : 350000;
    const sellingPrice = firstVariant?.sellingPrice ? Number(firstVariant.sellingPrice) : undefined;
    const images =
      p.images && p.images.length > 0
        ? p.images.map((img: any) => img.imageUrl)
        : [p.thumbnailUrl || 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80'];

    return {
      slug: String(p.id),
      name: p.defaultName, 
      brand: p.brand?.defaultName || 'Fashion Studio',
      category: p.category?.defaultName || 'Thời trang',
      price: sellingPrice || originalPrice,
      originalPrice: sellingPrice && sellingPrice < originalPrice ? originalPrice : undefined,
      flashSalePrice: undefined,
      rating: 5.0,
      reviews: 12,
      tag: p.tags?.[0]?.name || undefined,
      images,
      colors: ['#1D1F20', '#EAE6DF', '#a38a75'],
      sizes: ['S', 'M', 'L', 'XL'],
      description: p.defaultName,
    };
  });

  return (
    <main className="min-h-screen flex flex-col bg-[#FBF9F4]">
      <SiteHeader />

      {/* ========================================= */}
      {/* 1. HERO SECTION (Banner trên cùng cực cháy) */}
      {/* ========================================= */}
      <section className="relative overflow-hidden bg-[#1D1F20] text-white py-24 px-4 sm:px-8 lg:px-12 text-center">
        {/* Background Image với lớp phủ mờ */}
        <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1445205170230-053b83016050?q=80&w=2000&auto=format&fit=crop')] bg-cover bg-center opacity-20" />
        
        <div className="relative z-10 max-w-3xl mx-auto space-y-6">
          <h1 className="font-display text-5xl sm:text-7xl font-bold tracking-tight">
            Thời Trang Đương Đại
          </h1>
          <p className="text-neutral-300 text-lg sm:text-xl">
            Khám phá các bộ sưu tập tối giản, tinh tế, phù hợp cho mọi khoảnh khắc trong cuộc sống của bạn.
          </p>
          <div className="pt-4">
            <Link 
              href="/products" 
              className="inline-flex items-center gap-2 bg-[#84EA53] text-[#1D1F20] px-8 py-3.5 rounded-full font-bold hover:bg-[#72d244] transition-transform hover:scale-105 active:scale-95"
            >
              Khám phá ngay <ArrowRight className="size-4" />
            </Link>
          </div>
        </div>
      </section>

      {/* ========================================= */}
      {/* 2. FLASH SALE SECTION (Khuyến mãi Giờ Vàng) */}
      {/* ========================================= */}
      {/* Component này sẽ tự gọi API lấy danh sách Flash Sale đang diễn ra và tự ẩn nếu không có */}
      <FlashSaleSection />

      {/* ========================================= */}
      {/* 3. CATEGORIES SECTION (Danh mục nổi bật)  */}
      {/* ========================================= */}
      <section className="mx-auto w-full max-w-[1440px] px-4 py-12 sm:px-8 lg:px-12 lg:py-16">
        <div className="mb-7 flex items-end justify-between border-b border-[#EAE6DF] pb-4">
          <div>
            <p className="eyebrow text-[#74736e]">BROWSE THE EDIT</p>
            <h2 className="section-title mt-1 text-[#1D1F20]">Danh mục mua sắm</h2>
          </div>
          <Link
            href="/products"
            className="hidden items-center gap-1 text-sm font-semibold sm:flex hover:text-[#1D1F20] text-neutral-500 transition-colors"
          >
            Tất cả danh mục <ChevronRight size={16} />
          </Link>
        </div>

        {isLoadingCategories ? (
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-4 lg:grid-cols-6">
            {Array.from({ length: 6 }).map((_, i) => (
              <Skeleton key={i} className="min-h-[160px] rounded-2xl sm:min-h-[200px]" />
            ))}
          </div>
        ) : categories.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-[#EAE6DF] bg-white p-8 text-center text-sm text-neutral-500">
            Chưa có danh mục nào từ máy chủ.
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-4 lg:grid-cols-6">
            {categories.slice(0, 6).map((category, index) => {
              const name = category.defaultName;
              const image = category.iconUrl || CATEGORY_IMAGES[index % CATEGORY_IMAGES.length];

              return (
                <Link
                  key={category.id}
                  href={`/products?search=${encodeURIComponent(name)}`} // Chuyển sang tìm kiếm theo tên
                  className="group relative min-h-[160px] overflow-hidden rounded-2xl bg-white border border-[#EAE6DF] sm:min-h-[200px]"
                >
                  <Image
                    src={image}
                    alt={name}
                    fill
                    sizes="(max-width: 640px) 50vw, (max-width: 1024px) 25vw, 16vw"
                    className="object-cover transition-transform duration-700 group-hover:scale-110"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent opacity-80" />
                  
                  <div className="absolute bottom-4 left-4 right-4 text-center">
                    <p className="font-display text-lg sm:text-xl font-bold !text-white [&_*]:!text-white tracking-wide">
                      {name}
                    </p>
                    <span className="inline-block mt-1.5 opacity-0 -translate-y-2 group-hover:opacity-100 group-hover:translate-y-0 transition-all duration-300 text-[10px] uppercase font-bold text-[#84EA53] bg-white/20 backdrop-blur px-2 py-0.5 rounded-full">
                      Khám phá
                    </span>
                  </div>
                </Link>
              );
            })}
          </div>
        )}
      </section>

      {/* ========================================= */}
      {/* 4. PRODUCTS SECTION (Sản phẩm nổi bật)    */}
      {/* ========================================= */}
      <section className="mx-auto w-full max-w-[1440px] px-4 pb-24 sm:px-8 lg:px-12">
        <div className="mb-7 flex items-end justify-between border-b border-[#EAE6DF] pb-4">
          <div>
            <p className="eyebrow text-[#74736e]">NEW ARRIVALS</p>
            <h2 className="section-title mt-1 text-[#1D1F20]">Sản phẩm mới nhất</h2>
          </div>
          <Link
            href="/products"
            className="flex items-center gap-1 text-sm font-semibold hover:text-[#1D1F20] text-neutral-500 transition-colors"
          >
            Xem tất cả <ChevronRight size={16} />
          </Link>
        </div>

        {isLoadingProducts ? (
          <div className="grid grid-cols-2 gap-x-4 gap-y-10 sm:grid-cols-3 lg:grid-cols-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="space-y-3">
                <Skeleton className="aspect-[.88] w-full rounded-2xl" />
                <Skeleton className="h-4 w-3/4 rounded" />
                <Skeleton className="h-4 w-1/2 rounded" />
              </div>
            ))}
          </div>
        ) : products.length === 0 ? (
          <div className="py-16 text-center bg-white rounded-3xl border border-dashed border-[#EAE6DF]">
            <ShoppingBag className="mx-auto size-12 text-neutral-300 mb-4" />
            <p className="text-sm font-medium text-neutral-600">Hiện chưa có sản phẩm nào.</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-x-4 gap-y-10 sm:grid-cols-3 lg:grid-cols-4">
            {products.slice(0, 8).map((product) => (
              <ProductCard key={product.slug} product={product} />
            ))}
          </div>
        )}
      </section>

      {/* Tích hợp Footer toàn trang */}
      <SiteFooter />
    </main>
  );
}