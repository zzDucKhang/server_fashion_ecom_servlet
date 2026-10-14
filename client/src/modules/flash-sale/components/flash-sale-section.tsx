'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { Flame, ArrowRight, Sparkles } from 'lucide-react';
import { FlashSaleCampaign } from '@/types/inventory-sale';
import { flashSaleApi } from '@/api/flash-sale.api';
import { FlashSaleCountdown } from './countdown';
import { FlashSaleCard } from './flash-sale-card';

export function FlashSaleSection() {
  const [campaigns, setCampaigns] = useState<FlashSaleCampaign[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchActive() {
      try {
        const res = await flashSaleApi.getActiveCampaigns();
        if (res.data) {
          setCampaigns(res.data);
        }
      } catch (err) {
        console.error('Failed to fetch active flash sale campaigns:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchActive();
  }, []);

  if (loading) {
    return (
      <div className="mx-auto w-full max-w-[1440px] px-4 py-8 sm:px-8 lg:px-12">
        <div className="h-48 animate-pulse rounded-3xl bg-neutral-100" />
      </div>
    );
  }

  if (!campaigns.length) return null;

  const currentCampaign = campaigns[0];
  const items = currentCampaign.items || [];

  return (
    <section className="mx-auto w-full max-w-[1440px] px-4 py-8 sm:px-8 lg:px-12">
      <div className="overflow-hidden rounded-3xl border border-red-100 bg-gradient-to-b from-red-50 to-white p-6 shadow-sm sm:p-8">
        
        {/* Header Bar */}
        <div className="flex flex-col gap-6 sm:flex-row sm:items-center sm:justify-between border-b border-red-100 pb-6">
          <div className="flex items-center gap-4">
            <div className="flex size-12 shrink-0 items-center justify-center rounded-2xl bg-red-600 text-white shadow-lg shadow-red-600/30">
              <Flame className="size-7 fill-amber-300 text-amber-300 animate-bounce" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-black uppercase tracking-widest text-red-600">
                  Ưu đãi giới hạn
                </span>
                <span className="flex items-center gap-1 rounded-full bg-red-100 px-2 py-0.5 text-[10px] font-bold text-red-700">
                  <Sparkles className="size-3" /> HOT SALE
                </span>
              </div>
              <h2 className="text-2xl font-black tracking-tight text-neutral-900 sm:text-3xl mt-1">
                {currentCampaign.name}
              </h2>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-5 sm:justify-end">
            <FlashSaleCountdown
              targetTime={currentCampaign.endTime}
              startTime={currentCampaign.startTime}
              size="md"
            />
            <Link
              href="/flash-sale"
              className="inline-flex shrink-0 items-center justify-center gap-2 rounded-full bg-[#1D1F20] !text-white [&_*]:!text-white px-5 py-2.5 text-xs font-bold transition-colors hover:bg-red-600 shadow-sm"
            >
              Xem tất cả <ArrowRight className="size-4" />
            </Link>
          </div>
        </div>

        {/* Item Grid */}
        <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          {items.slice(0, 4).map((item) => (
            <FlashSaleCard key={item.id || item.variantId} item={item} />
          ))}
        </div>
      </div>
    </section>
  );
}