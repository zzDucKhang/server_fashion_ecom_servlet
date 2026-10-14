'use client';
import Link from 'next/link';
import { Search, ShoppingBag } from 'lucide-react';
import { categories } from '@/modules/catalog/data';
export function SiteHeader() {
  return (
    <header className="sticky top-0 z-40 border-b border-[#EAE6DF] bg-[#FBF9F4]/95 backdrop-blur">
      <div className="mx-auto flex h-[72px] max-w-[1440px] items-center gap-5 px-4 sm:px-8 lg:px-12">
        <Link
          href="/"
          className="shrink-0 font-display text-3xl font-semibold tracking-[-.06em]"
        >
          common<span className="text-[#84EA53]">.</span>
        </Link>
        <div className="hidden h-11 max-w-[430px] flex-1 items-center rounded-full border border-[#EAE6DF] bg-white px-4 shadow-[0_5px_20px_rgba(29,31,32,.04)] sm:flex">
          <Search size={17} className="mr-3 text-[#74736e]" />
          <input
            aria-label="Search products"
            placeholder="Search for an object, a feeling..."
            className="w-full bg-transparent text-sm outline-none placeholder:text-[#9b9991]"
          />
          <kbd className="rounded bg-[#FBF9F4] px-2 py-1 text-[10px] text-[#74736e]">
            ⌘ K
          </kbd>
        </div>
        <div className="ml-auto flex items-center gap-2">
          <Link
            href="/search"
            className="rounded-full p-2.5 hover:bg-white sm:hidden"
          >
            <Search size={20} />
          </Link>
          <Link
            href="/cart"
            className="relative rounded-full p-2.5 hover:bg-white"
          >
            <ShoppingBag size={20} />
            <span className="absolute right-0 top-0 flex h-4 w-4 items-center justify-center rounded-full bg-[#84EA53] text-[9px] font-bold">
              2
            </span>
          </Link>
        </div>
      </div>
      <nav className="scrollbar-none mx-auto flex max-w-[1440px] gap-7 overflow-x-auto px-4 pb-3 sm:px-8 lg:px-12">
        {categories.map((category) => (
          <Link
            key={category.name}
            href={`/search?category=${category.name}`}
            className="group flex min-w-fit flex-col items-center gap-1 text-[#74736e] transition-colors hover:text-[#1D1F20]"
          >
            <span className="flex h-8 w-8 items-center justify-center rounded-full border border-[#EAE6DF] bg-white text-base group-hover:border-[#84EA53]">
              {category.icon}
            </span>
            <span className="text-[10px] font-medium">{category.name}</span>
          </Link>
        ))}
      </nav>
    </header>
  );
}
