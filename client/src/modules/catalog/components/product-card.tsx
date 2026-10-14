'use client';
import Image from 'next/image';
import Link from 'next/link';
import { ChevronLeft, ChevronRight, Heart, Star } from 'lucide-react';
import { useState } from 'react';
import type { Product } from '@/modules/catalog/mock';
import { formatCurrency } from '@/lib/utils/format-currency';

export function ProductCard({ product }: { product: Product }) {
  const [index, setIndex] = useState(0);
  const formatPrice = (val?: number) =>
    val !== undefined && val !== null ? (val > 1000 ? formatCurrency(val) : `$${val}`) : '';
  return (
    <article className="group min-w-0">
      <div className="relative overflow-hidden rounded-xl bg-[#e9e5dc]">
        <Link href={`/products/${product.slug}`}>
          <div className="relative aspect-[.88] w-full">
            <Image
              src={product.images[index]}
              alt={product.name}
              fill
              sizes="(max-width: 640px) 50vw, (max-width: 1024px) 33vw, 25vw"
              className="object-cover transition-transform duration-500 group-hover:scale-[1.03]"
            />
          </div>
        </Link>
        {product.tag && (
          <span className="absolute left-3 top-3 rounded-full bg-[#84EA53] px-2.5 py-1 text-[10px] font-bold uppercase tracking-wide text-[#1D1F20]">
            {product.tag}
          </span>
        )}
        <button
          aria-label="Save product"
          className="absolute right-3 top-3 rounded-full bg-white/85 p-2 backdrop-blur transition hover:bg-white"
        >
          <Heart size={15} />
        </button>
        {product.images.length > 1 && (
          <>
            <button
              aria-label="Previous image"
              onClick={() =>
                setIndex(index === 0 ? product.images.length - 1 : index - 1)
              }
              className="absolute left-2 top-1/2 rounded-full bg-white/85 p-1.5 opacity-0 transition group-hover:opacity-100"
            >
              <ChevronLeft size={15} />
            </button>
            <button
              aria-label="Next image"
              onClick={() => setIndex((index + 1) % product.images.length)}
              className="absolute right-2 top-1/2 rounded-full bg-white/85 p-1.5 opacity-0 transition group-hover:opacity-100"
            >
              <ChevronRight size={15} />
            </button>
          </>
        )}
      </div>
      <div className="px-0.5 pt-3">
        <div className="flex items-center justify-between gap-2">
          <h3 className="truncate text-sm font-semibold">{product.name}</h3>
          <span className="flex shrink-0 items-center gap-1 text-xs">
            <Star size={11} fill="#1D1F20" /> {product.rating}
          </span>
        </div>
        <p className="mt-1 text-xs text-[#74736e]">
          {product.brand} · {product.reviews} reviews
        </p>
        <div className="mt-2 flex items-center gap-2">
          <span className="text-sm font-bold">
            {formatPrice(product.flashSalePrice ?? product.price)}
          </span>
          {product.originalPrice && (
            <span className="text-xs text-[#9b9991] line-through">
              {formatPrice(product.originalPrice)}
            </span>
          )}
        </div>
        <div className="mt-2 flex gap-1.5">
          {product.colors.map((color) => (
            <span
              key={color}
              className="h-3 w-3 rounded-full border border-black/10"
              style={{ backgroundColor: color }}
            />
          ))}
        </div>
      </div>
    </article>
  );
}