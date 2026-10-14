'use client';

import { useEffect, useState } from 'react';
import { Flame, ShieldCheck, Zap, RefreshCw, ShoppingBag, Clock, Search } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { FlashSaleCountdown } from '@/modules/flash-sale/components/countdown';
import { FlashSaleCard } from '@/modules/flash-sale/components/flash-sale-card';
import { Button } from '@/components/ui/button';
import { useFlashSaleStore } from '@/modules/flash-sale/store';
import { FlashSaleProvider, useFlashSaleContext } from '@/modules/flash-sale/context';

// Sub-component sử dụng Context
function FlashSaleItemList({ imageByVariantId }: { imageByVariantId: Record<number, string> }) {
  const { filteredItems } = useFlashSaleContext();

  if (filteredItems.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center rounded-3xl border border-dashed border-zinc-300 bg-white p-12 text-center dark:border-zinc-800 dark:bg-zinc-900">
        <div className="flex size-16 items-center justify-center rounded-full bg-red-100 text-red-600 dark:bg-red-950 dark:text-red-400">
          <Flame className="size-8" />
        </div>
        <h3 className="mt-4 text-lg font-bold text-zinc-900 dark:text-zinc-100">
          Không tìm thấy sản phẩm Flash Sale nào
        </h3>
        <p className="mt-1 text-sm text-zinc-500 max-w-sm">
          Thử tìm kiếm với từ khóa khác hoặc quay lại sau!
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
      {filteredItems.map((item) => (
        <FlashSaleCard
          key={item.id || item.variantId}
          item={item}
          imageUrl={imageByVariantId[item.variantId]}
        />
      ))}
    </div>
  );
}

// Sub-component sử dụng Context cho Toolbar Tìm kiếm
function FlashSaleToolbar({ onRefresh }: { onRefresh: () => void }) {
  const { searchQuery, setSearchQuery, filteredItems } = useFlashSaleContext();

  return (
    <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between rounded-2xl bg-white p-4 shadow-xs dark:bg-zinc-900 border border-zinc-200/80 dark:border-zinc-800">
      <div className="flex items-center gap-2">
        <ShoppingBag className="size-5 text-red-600" />
        <h2 className="text-base font-bold text-zinc-900 dark:text-zinc-100">
          Sản phẩm Flash Sale ({filteredItems.length})
        </h2>
      </div>

      <div className="flex items-center gap-3">
        <div className="relative w-full sm:w-64">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-zinc-400" />
          <input
            type="text"
            placeholder="Tìm kiếm sản phẩm trong sale..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 rounded-xl border border-zinc-300 bg-zinc-50 px-3.5 py-1.5 text-sm focus:border-red-500 focus:outline-none dark:border-zinc-700 dark:bg-zinc-800"
          />
        </div>
        <Button onClick={onRefresh} variant="outline" size="sm" className="gap-1.5">
          <RefreshCw className="size-3.5" />
          Làm mới
        </Button>
      </div>
    </div>
  );
}

export default function FlashSaleShopPage() {
  // 1. GLOAL STATE (Zustand Store)
  const {
    activeCampaigns,
    selectedCampaignId,
    loading,
    fetchActiveCampaigns,
    setSelectedCampaignId,
  } = useFlashSaleStore();

  // 2. LOCAL STATE (useState) cho UI trợ lý đếm ngược / tab local
  const [refreshing, setRefreshing] = useState(false);
  const [imageByVariantId, setImageByVariantId] = useState<Record<number, string>>({});

  useEffect(() => {
    fetchActiveCampaigns();
  }, [fetchActiveCampaigns]);

  useEffect(() => {
    if (activeCampaigns.length === 0) return;

    let cancelled = false;

    async function fetchProductImages() {
      try {
        const firstPage = await catalogApi.getProducts({
          page: 1,
          limit: 100,
          status: 'PUBLISHED',
        });
        const remainingPages = await Promise.all(
          Array.from({ length: Math.max(firstPage.totalPages - 1, 0) }, (_, index) =>
            catalogApi.getProducts({
              page: index + 2,
              limit: 100,
              status: 'PUBLISHED',
            })
          )
        );
        const products = [firstPage, ...remainingPages].flatMap((page) => page.data);
        const imageMap: Record<number, string> = {};

        for (const product of products) {
          const imageUrl = product.images?.[0]?.imageUrl || product.thumbnailUrl;
          if (!imageUrl) continue;

          for (const variant of product.variants || []) {
            imageMap[variant.id] = imageUrl;
          }
        }

        if (!cancelled) setImageByVariantId(imageMap);
      } catch (error) {
        console.error('Failed to fetch Flash Sale product images:', error);
      }
    }

    void fetchProductImages();

    return () => {
      cancelled = true;
    };
  }, [activeCampaigns]);

  const activeCampaign =
    activeCampaigns.find((campaign) => campaign.id === selectedCampaignId) ??
    activeCampaigns[0];

  const handleRefresh = async () => {
    setRefreshing(true);
    await fetchActiveCampaigns();
    setRefreshing(false);
  };

  return (
    <main className="min-h-screen bg-zinc-50 dark:bg-zinc-950 pb-20">
      {/* Hero Banner Section */}
      <section className="relative overflow-hidden bg-zinc-950 !text-white [&_*]:!text-white py-12 px-4 sm:px-8 lg:px-12">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_30%_30%,rgba(239,68,68,0.25),transparent_60%)]" />
        <div className="absolute right-0 top-0 h-full w-1/2 bg-[radial-gradient(circle_at_80%_20%,rgba(245,158,11,0.2),transparent_50%)]" />

        <div className="relative z-10 mx-auto max-w-[1440px]">
          <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
            <div className="max-w-2xl">
              <div className="inline-flex items-center gap-2 rounded-full border border-red-500/30 bg-red-500/10 px-3.5 py-1 text-xs font-bold text-red-400 backdrop-blur-md">
                <Flame className="size-4 fill-red-500 text-red-500 animate-pulse" />
                <span>CHƯƠNG TRÌNH KHUYẾN MÃI GIỜ VÀNG</span>
              </div>
              <h1 className="mt-4 font-display text-4xl font-black tracking-tight sm:text-6xl !text-white [&_*]:!text-white">
                FLASH SALE SĂN DEAL KHỦNG
              </h1>
              <p className="mt-3 text-sm sm:text-base text-zinc-300">
                Sở hữu ngay những sản phẩm thời trang cao cấp với mức giá giảm tới 70%. Số lượng có hạn theo từng khung giờ vàng!
              </p>

              {/* Guarantees */}
              <div className="mt-6 flex flex-wrap gap-4 text-xs font-semibold text-zinc-300">
                <div className="flex items-center gap-1.5 rounded-lg bg-zinc-900/80 px-3 py-1.5 border border-zinc-800">
                  <ShieldCheck className="size-4 text-emerald-400" />
                  <span>Sản phẩm chính hãng 100%</span>
                </div>
                <div className="flex items-center gap-1.5 rounded-lg bg-zinc-900/80 px-3 py-1.5 border border-zinc-800">
                  <Zap className="size-4 text-amber-400" />
                  <span>Giao hàng thần tốc</span>
                </div>
                <div className="flex items-center gap-1.5 rounded-lg bg-zinc-900/80 px-3 py-1.5 border border-zinc-800">
                  <Clock className="size-4 text-red-400" />
                  <span>Cập nhật theo khung giờ</span>
                </div>
              </div>
            </div>

            {/* Countdown Box */}
            {activeCampaign && (
              <div className="flex flex-col items-start md:items-end rounded-2xl border border-red-500/20 bg-zinc-900/90 p-6 backdrop-blur-md shadow-2xl">
                <span className="text-xs font-bold text-zinc-400 uppercase tracking-widest">
                  Thời gian diễn ra
                </span>
                <h3 className="mt-1 text-lg font-bold !text-white [&_*]:!text-white mb-3">
                  {activeCampaign.name}
                </h3>
                <FlashSaleCountdown
                  targetTime={activeCampaign.endTime}
                  startTime={activeCampaign.startTime}
                  size="lg"
                />
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Main Container */}
      <div className="mx-auto max-w-[1440px] px-4 pt-8 sm:px-8 lg:px-12">
        {/* Campaign Tabs */}
        {activeCampaigns.length > 1 && (
          <div className="mb-8 flex gap-3 overflow-x-auto pb-2 scrollbar-none">
            {activeCampaigns.map((c) => {
              const isSelected = c.id === activeCampaign?.id;
              return (
                <button
                  key={c.id}
                  onClick={() => setSelectedCampaignId(c.id)}
                  className={`flex shrink-0 items-center gap-2 rounded-2xl px-6 py-3.5 text-sm font-bold transition-all shadow-xs ${isSelected
                      ? 'bg-red-600 !text-white [&_*]:!text-white shadow-lg shadow-red-600/30 scale-105'
                      : 'bg-white text-zinc-700 hover:bg-zinc-100 dark:bg-zinc-900 dark:text-zinc-300 dark:hover:bg-zinc-800'
                    }`}
                >
                  <Flame className={`size-4 ${isSelected ? 'fill-amber-300 text-amber-300' : 'text-zinc-400'}`} />
                  <span>{c.name}</span>
                </button>
              );
            })}
          </div>
        )}

        {/* 3. CONTEXT PROVIDER bao bọc danh sách sản phẩm & toolbar */}
        <FlashSaleProvider campaign={activeCampaign || null}>
          <FlashSaleToolbar onRefresh={handleRefresh} />

          {loading || refreshing ? (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
              {Array.from({ length: 8 }).map((_, i) => (
                <div key={i} className="h-80 animate-pulse rounded-2xl bg-zinc-200 dark:bg-zinc-800" />
              ))}
            </div>
          ) : (
            <FlashSaleItemList imageByVariantId={imageByVariantId} />
          )}
        </FlashSaleProvider>
      </div>
    </main>
  );
}
