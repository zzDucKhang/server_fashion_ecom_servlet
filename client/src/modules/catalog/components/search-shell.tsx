'use client';
import { SlidersHorizontal } from 'lucide-react';
import { useState } from 'react';
import { ProductCard } from '@/modules/catalog/components/product-card';
import { FilterSidebar } from '@/modules/catalog/components/filter-sidebar';
import type { Product } from '@/modules/catalog/mock';
export function SearchShell({
  products,
  category,
}: {
  products: Product[];
  category?: string;
}) {
  const [open, setOpen] = useState(false);
  return (
    <div className="relative mx-auto max-w-[1440px] px-4 pb-20 sm:px-8 lg:px-12">
      <div className="mb-7 flex items-center justify-between border-b border-[#EAE6DF] py-5">
        <button
          onClick={() => setOpen(true)}
          className="flex items-center gap-2 rounded-full border border-[#1D1F20] px-4 py-2 text-sm font-semibold transition hover:bg-[#1D1F20] hover:!text-white [&_*]:!text-white"
        >
          <SlidersHorizontal size={16} /> Filters
        </button>
        <p className="hidden text-sm text-[#74736e] sm:block">
          {products.length * 20 + 4} pieces
        </p>
        <select className="rounded-full border border-[#EAE6DF] bg-transparent px-3 py-2 text-xs outline-none">
          <option>Sort: Recommended</option>
          <option>Price: low to high</option>
          <option>Newest first</option>
        </select>
      </div>
      <div className="flex gap-8">
        <FilterSidebar open={open} onClose={() => setOpen(false)} />
        {open && (
          <button
            aria-label="Close filter overlay"
            onClick={() => setOpen(false)}
            className="fixed inset-0 z-40 bg-[#1D1F20]/20 lg:hidden"
          />
        )}
        {open && <div className="hidden w-[300px] shrink-0 lg:block" />}
        <div className="min-w-0 flex-1">
          <div className="mb-5 flex items-end justify-between">
            <div>
              <p className="eyebrow">{category ?? 'All pieces'}</p>
              <h1 className="section-title mt-2">
                {category ? `${category}, considered` : 'Find your everyday'}
              </h1>
            </div>
            <span className="text-xs text-[#74736e] sm:hidden">
              {products.length * 20 + 4} items
            </span>
          </div>
          <div className="grid grid-cols-2 gap-x-3 gap-y-10 sm:grid-cols-3 lg:grid-cols-4">
            {products.map((product) => (
              <ProductCard key={product.slug} product={product} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
