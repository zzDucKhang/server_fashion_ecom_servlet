'use client';

import * as React from 'react';
// 1. Thêm import useSearchParams
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import Image from 'next/image';
import Link from 'next/link';
import { ChevronLeft, Plus, Minus, ShoppingBag, Loader2, Flame } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { useAddToCart } from '@/modules/cart/hooks/use-cart';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { formatCurrency } from '@/lib/utils/format-currency';
import { ProductReviewList } from '@/modules/loyalty-engage';

export default function ProductDetailPage() {
  const params = useParams();
  const searchParams = useSearchParams();
  const router = useRouter();
  
  const productId = Number(params.productId);
  // 2. Lấy variantId từ URL truyền qua
  const urlVariantId = Number(searchParams.get('variantId'));

  const [selectedVariantIndex, setSelectedVariantIndex] = React.useState(0);
  const [quantity, setQuantity] = React.useState(1);

  const { data: res, isLoading } = useQuery({
    queryKey: ['product-detail', productId],
    queryFn: () => catalogApi.getProductById(productId),
    enabled: !!productId,
  });

  const addToCartMutation = useAddToCart();
  const product = res?.data;
  const variants = product?.variants || [];

  // 3. Tự động chọn đúng Variant nếu có truyền từ trang Flash Sale
  React.useEffect(() => {
    if (variants.length > 0 && urlVariantId) {
      const index = variants.findIndex((v: any) => v.id === urlVariantId);
      if (index !== -1) {
        setSelectedVariantIndex(index);
      }
    }
  }, [variants, urlVariantId]);

  const selectedVariant = variants[selectedVariantIndex] || variants[0];

  // Logic Flash Sale
  const activeFlashSale = React.useMemo(() => {
    if (!selectedVariant?.flashSaleItems?.length) return null;
    const now = new Date().getTime();
    return selectedVariant.flashSaleItems.find((fsi: any) => {
      if (!fsi.campaign || !fsi.campaign.isActive) return false;
      const start = new Date(fsi.campaign.startTime).getTime();
      const end = new Date(fsi.campaign.endTime).getTime();
      const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
      return now >= start && now <= end && remainingQty > 0;
    });
  }, [selectedVariant]);

  const currentPrice = activeFlashSale 
    ? Number(activeFlashSale.flashSalePrice)
    : Number(selectedVariant?.sellingPrice || selectedVariant?.originalPrice || 0);

  const displayOriginalPrice = activeFlashSale
    ? Number(selectedVariant?.sellingPrice || selectedVariant?.originalPrice || 0)
    : Number(selectedVariant?.originalPrice || 0);

  const handleAddToCart = () => {
    if (!selectedVariant) return;
    addToCartMutation.mutate({
      variantId: selectedVariant.id,
      quantity,
    });
  };

  const handleBuyNow = () => {
    if (!selectedVariant) return;
    router.push(`/checkout?directBuy=true&productId=${product?.id}&variantId=${selectedVariant.id}&quantity=${quantity}`);
  };

  if (isLoading) {
    return (
      <div className="mx-auto max-w-[1280px] px-4 py-16 text-center text-sm text-neutral-500">
        <Loader2 className="mx-auto size-6 animate-spin mb-2" />
        Đang tải thông tin sản phẩm...
      </div>
    );
  }

  if (!product) {
    return (
      <div className="mx-auto max-w-[1280px] px-4 py-16 text-center">
        <p className="text-sm text-neutral-500 mb-4">Không tìm thấy sản phẩm này.</p>
        <Button onClick={() => router.push('/products')} variant="outline">
          Quay lại danh sách
        </Button>
      </div>
    );
  }

  return (
    <main className="mx-auto max-w-[1280px] px-4 py-6 sm:px-8 lg:py-12">
      <Link
        href="/products"
        className="mb-6 inline-flex items-center gap-1 text-xs text-[#74736e] hover:text-neutral-900 transition"
      >
        <ChevronLeft size={14} /> Quay lại danh mục
      </Link>

      <div className="grid gap-10 lg:grid-cols-2 lg:gap-16">
        <div className="relative aspect-square overflow-hidden rounded-2xl bg-white border border-[#EAE6DF]">
          <Image
            src={selectedVariant?.imageUrl || product.thumbnailUrl || '/placeholder.png'}
            alt={product.defaultName}
            fill
            priority
            className="object-cover"
          />
        </div>

        <div className="flex flex-col justify-center rounded-2xl bg-white p-6 sm:p-10 border border-[#EAE6DF] shadow-xs relative overflow-hidden">
          {activeFlashSale && (
            <div className="absolute top-0 right-0 bg-red-600 text-white px-4 py-1.5 rounded-bl-xl font-black text-xs uppercase tracking-wider flex items-center gap-1.5 shadow-md">
              <Flame className="size-3.5 fill-amber-300 text-amber-300 animate-pulse" />
              Đang Flash Sale
            </div>
          )}

          <div className="mb-4 mt-2">
            <span className="eyebrow block mb-2">{product.brand?.defaultName || 'FASHION STORE'}</span>
            <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20]">{product.defaultName}</h1>
          </div>

          <div className="flex items-baseline gap-3 pb-6 border-b border-[#EAE6DF]">
            <span className={activeFlashSale ? "text-3xl font-black text-red-600" : "text-2xl font-bold text-neutral-900"}>
              {formatCurrency(currentPrice)}
            </span>
            {displayOriginalPrice > currentPrice && (
              <span className="text-sm text-neutral-400 line-through">{formatCurrency(displayOriginalPrice)}</span>
            )}
            <Badge variant="lime" className="ml-auto text-[10px]">Chính hãng 100%</Badge>
          </div>

          <p className="py-6 text-xs text-neutral-600 leading-relaxed">
            {(product as any)?.translations?.[0]?.shortDesc ||
              'Sản phẩm được gia công từ chất liệu cao cấp mang lại cảm giác thoáng mát và tôn dáng.'}
          </p>

          {/* 4. LUÔN HIỂN THỊ CHỌN PHÂN LOẠI: Xóa điều kiện variants.length > 1 đi */}
          {variants.length > 0 && (
            <div className="space-y-3 mb-6">
              <span className="text-xs font-semibold text-neutral-800">
                Phân loại sản phẩm (Chọn để xem giá ưu đãi)
              </span>
              <div className="flex flex-wrap gap-2">
                {variants.map((v: any, idx: number) => {
                  const isSelected = selectedVariantIndex === idx;
                  const outOfStock = v.stockQuantity <= 0;
                  
                  // Kiểm tra xem phân loại này có đang được sale không để tô màu nổi bật
                  const hasSale = v.flashSaleItems?.some((fsi: any) => fsi.campaign?.isActive);
                  
                  return (
                    <button
                      key={v.id}
                      onClick={() => setSelectedVariantIndex(idx)}
                      disabled={outOfStock}
                      className={`rounded-xl border px-3.5 py-2 text-xs font-medium transition flex items-center gap-1.5 ${
                        isSelected
                          ? 'border-neutral-900 bg-neutral-900 !text-white [&_*]:!text-white shadow-sm'
                          : outOfStock 
                          ? 'border-[#EAE6DF] bg-neutral-50 text-neutral-400 cursor-not-allowed opacity-60'
                          : hasSale
                          ? 'border-red-200 bg-red-50 text-red-700 hover:border-red-400'
                          : 'border-[#EAE6DF] bg-white text-neutral-700 hover:border-neutral-400'
                      }`}
                    >
                      {v.variantValue || v.sku} 
                      {hasSale && !isSelected && <Flame className="size-3" />}
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          <div className="flex items-center gap-4 pt-4 border-t border-[#EAE6DF]">
            <div className="flex items-center rounded-xl border border-[#EAE6DF] bg-white">
              <button
                type="button"
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                className="p-3 text-neutral-600 hover:text-neutral-900"
              >
                <Minus size={14} />
              </button>
              <span className="w-8 text-center text-xs font-semibold">{quantity}</span>
              <button
                type="button"
                onClick={() => setQuantity(quantity + 1)}
                className="p-3 text-neutral-600 hover:text-neutral-900"
              >
                <Plus size={14} />
              </button>
            </div>

            <Button
              onClick={handleAddToCart}
              variant="outline"
              disabled={addToCartMutation.isPending || !selectedVariant || selectedVariant.stockQuantity <= 0}
              className="flex-1 h-12 rounded-xl border-[#EAE6DF] hover:bg-neutral-50 font-medium gap-2 text-neutral-800"
            >
              {addToCartMutation.isPending ? (
                <>
                  <Loader2 className="size-4 animate-spin" /> Đang thêm...
                </>
              ) : (
                <>
                  <ShoppingBag size={16} /> Thêm vào giỏ
                </>
              )}
            </Button>
            <Button
              onClick={handleBuyNow}
              disabled={!selectedVariant || selectedVariant.stockQuantity <= 0}
              className={`flex-1 h-12 rounded-xl !text-white [&_*]:!text-white font-bold gap-2 ${
                activeFlashSale ? 'bg-red-600 hover:bg-red-700' : 'bg-[#1D1F20] hover:bg-neutral-800'
              }`}
            >
              Mua ngay ({formatCurrency(currentPrice * quantity)})
            </Button>
          </div>
        </div>
      </div>

      <div className="mt-12 sm:mt-16">
        <ProductReviewList productId={product.id} />
      </div>
    </main>
  );
}