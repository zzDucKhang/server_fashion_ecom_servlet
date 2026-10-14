'use client';
import Image from 'next/image';
import Link from 'next/link';
import {
  Check,
  ChevronLeft,
  ChevronRight,
  Heart,
  Minus,
  Plus,
  Star,
} from 'lucide-react';
import { useState } from 'react';
import type { Product } from '@/modules/catalog/mock';
export function ProductDetail({ product }: { product: Product }) {
  const [image, setImage] = useState(0);
  const [size, setSize] = useState(product.sizes[0]);
  const [color, setColor] = useState(product.colors[0]);
  const [quantity, setQuantity] = useState(1);
  return (
    <main className="mx-auto max-w-[1280px] px-4 py-6 sm:px-8 lg:py-12">
      <Link
        href="/search"
        className="mb-6 inline-flex items-center gap-1 text-xs text-[#74736e]"
      >
        <ChevronLeft size={14} /> Back to edit
      </Link>
      <div className="grid gap-10 lg:grid-cols-[1.05fr_.95fr] lg:gap-16">
        <div>
          <div className="relative aspect-[.95] overflow-hidden rounded-2xl bg-white">
            <Image
              src={product.images[image]}
              alt={product.name}
              fill
              priority
              sizes="(max-width: 1024px) 100vw, 55vw"
              className="object-cover"
            />
            <button
              onClick={() =>
                setImage(image === 0 ? product.images.length - 1 : image - 1)
              }
              aria-label="Previous product image"
              className="absolute left-4 top-1/2 rounded-full bg-white/90 p-2"
            >
              <ChevronLeft size={18} />
            </button>
            <button
              onClick={() => setImage((image + 1) % product.images.length)}
              aria-label="Next product image"
              className="absolute right-4 top-1/2 rounded-full bg-white/90 p-2"
            >
              <ChevronRight size={18} />
            </button>
          </div>
          <div className="mt-3 grid grid-cols-2 gap-3">
            {product.images.map((src, index) => (
              <button
                key={src}
                onClick={() => setImage(index)}
                className={`relative aspect-square overflow-hidden rounded-xl ${image === index ? 'ring-2 ring-[#84EA53]' : 'opacity-60'}`}
              >
                <Image
                  src={src}
                  alt=""
                  fill
                  sizes="120px"
                  className="object-cover"
                />
              </button>
            ))}
          </div>
        </div>
        <div className="self-center rounded-2xl bg-white p-6 sm:p-10">
          <div className="mb-7 flex items-start justify-between">
            <div>
              <p className="eyebrow">
                {product.brand} · {product.category}
              </p>
              <h1 className="section-title mt-3">{product.name}</h1>
            </div>
            <button
              aria-label="Save product"
              className="rounded-full border border-[#EAE6DF] p-3"
            >
              <Heart size={18} />
            </button>
          </div>
          <div className="flex items-center gap-3 border-b border-[#EAE6DF] pb-7">
            <span className="text-xl font-bold">
              ${product.flashSalePrice ?? product.price}
            </span>
            {product.originalPrice && (
              <span className="text-sm text-[#9b9991] line-through">
                ${product.originalPrice}
              </span>
            )}
            <span className="ml-auto flex items-center gap-1 text-sm">
              <Star size={14} fill="#1D1F20" /> {product.rating}{' '}
              <span className="text-[#74736e]">({product.reviews})</span>
            </span>
          </div>
          <p className="py-7 text-sm leading-6 text-[#74736e]">
            {product.description}
          </p>
          <div className="space-y-6 border-t border-[#EAE6DF] pt-6">
            <div>
              <div className="mb-3 flex justify-between text-sm font-semibold">
                <span>Color</span>
                <span className="font-normal text-[#74736e]">Selected</span>
              </div>
              <div className="flex gap-3">
                {product.colors.map((item) => (
                  <button
                    key={item}
                    onClick={() => setColor(item)}
                    aria-label={`Select color ${item}`}
                    className={`h-8 w-8 rounded-full border-2 border-white outline outline-1 ${color === item ? 'outline-[#1D1F20]' : 'outline-[#EAE6DF]'}`}
                    style={{ backgroundColor: item }}
                  />
                ))}
              </div>
            </div>
            <div>
              <div className="mb-3 flex justify-between text-sm font-semibold">
                <span>Size</span>
                <button className="text-xs font-normal underline">
                  Size guide
                </button>
              </div>
              <div className="flex flex-wrap gap-2">
                {product.sizes.map((item) => (
                  <button
                    key={item}
                    onClick={() => setSize(item)}
                    className={`rounded-lg border px-4 py-2.5 text-xs ${size === item ? 'border-[#1D1F20] bg-[#1D1F20] !text-white [&_*]:!text-white' : 'border-[#EAE6DF]'}`}
                  >
                    {item}
                  </button>
                ))}
              </div>
            </div>
            <div className="flex gap-3">
              <div className="flex items-center rounded-xl border border-[#EAE6DF]">
                <button
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  className="p-3"
                  aria-label="Decrease quantity"
                >
                  <Minus size={14} />
                </button>
                <span className="w-5 text-center text-sm">{quantity}</span>
                <button
                  onClick={() => setQuantity(quantity + 1)}
                  className="p-3"
                  aria-label="Increase quantity"
                >
                  <Plus size={14} />
                </button>
              </div>
              <button className="flex flex-1 items-center justify-center gap-2 rounded-xl bg-[#84EA53] py-3.5 text-sm font-bold transition hover:brightness-95">
                Add to cart · $
                {((product.flashSalePrice ?? product.price) * quantity).toFixed(
                  2,
                )}{' '}
                <Check size={16} />
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
