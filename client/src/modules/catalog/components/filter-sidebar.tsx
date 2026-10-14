'use client';
import { ChevronDown, SlidersHorizontal, X } from 'lucide-react';
import { useState } from 'react';
export function FilterSidebar({
  open,
  onClose,
}: {
  open: boolean;
  onClose: () => void;
}) {
  const [expanded, setExpanded] = useState('Category');
  return (
    <aside
      className={`${open ? 'translate-x-0 opacity-100' : 'pointer-events-none -translate-x-5 opacity-0'} fixed inset-y-0 left-0 z-50 w-[min(360px,88vw)] overflow-y-auto border-r border-[#EAE6DF] bg-white p-6 shadow-[10px_0_40px_rgba(29,31,32,.08)] transition-all duration-300 lg:absolute lg:top-0 lg:h-full lg:shadow-none`}
    >
      <div className="mb-8 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <SlidersHorizontal size={18} />
          <h2 className="font-display text-3xl">Filters</h2>
        </div>
        <button
          onClick={onClose}
          aria-label="Close filters"
          className="rounded-full p-2 hover:bg-[#FBF9F4]"
        >
          <X size={18} />
        </button>
      </div>
      <div className="divide-y divide-[#EAE6DF]">
        {['Category', 'Brand', 'Size', 'Color'].map((name) => (
          <div key={name} className="py-5 first:pt-0">
            <button
              onClick={() => setExpanded(expanded === name ? '' : name)}
              className="flex w-full items-center justify-between text-sm font-bold"
            >
              {name}
              <ChevronDown
                size={16}
                className={
                  expanded === name ? 'rotate-180 transition' : 'transition'
                }
              />
            </button>
            {expanded === name && (
              <div className="mt-4 space-y-3 text-sm text-[#74736e]">
                {name === 'Category' && (
                  <>
                    <label className="flex justify-between">
                      <span>
                        <input
                          type="checkbox"
                          defaultChecked
                          className="mr-3 accent-[#84EA53]"
                        />
                        All products
                      </span>
                      <span>160</span>
                    </label>
                    <label className="ml-4 flex justify-between">
                      <span>
                        <input
                          type="checkbox"
                          className="mr-3 accent-[#84EA53]"
                        />
                        Apparel
                      </span>
                      <span>48</span>
                    </label>
                    <label className="ml-4 flex justify-between">
                      <span>
                        <input
                          type="checkbox"
                          className="mr-3 accent-[#84EA53]"
                        />
                        Objects
                      </span>
                      <span>32</span>
                    </label>
                  </>
                )}
                {name === 'Brand' &&
                  [
                    'Morrow Studio',
                    'Form & Field',
                    'House of Common',
                    'Sunday Objects',
                  ].map((brand, i) => (
                    <label key={brand} className="flex justify-between">
                      <span>
                        <input
                          type="checkbox"
                          className="mr-3 accent-[#84EA53]"
                        />
                        {brand}
                      </span>
                      <span>{[14, 9, 12, 7][i]}</span>
                    </label>
                  ))}
                {name === 'Size' && (
                  <div className="flex flex-wrap gap-2">
                    {['XS', 'S', 'M', 'L', '28', '30', '32', 'One size'].map(
                      (size) => (
                        <button
                          key={size}
                          className="rounded-lg border border-[#EAE6DF] px-3 py-2 text-xs hover:border-[#84EA53]"
                        >
                          {size}
                        </button>
                      ),
                    )}
                  </div>
                )}
                {name === 'Color' && (
                  <div className="flex flex-wrap gap-4">
                    {[
                      '#1D1F20',
                      '#ded5c8',
                      '#b8794b',
                      '#8d9a87',
                      '#d8c5ac',
                      '#e1e1dc',
                    ].map((color) => (
                      <button
                        key={color}
                        aria-label={`Color ${color}`}
                        className="h-7 w-7 rounded-full border-2 border-white outline outline-1 outline-[#EAE6DF]"
                        style={{ backgroundColor: color }}
                      />
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
        <div className="py-5">
          <p className="mb-5 text-sm font-bold">Price range</p>
          <input
            type="range"
            min="0"
            max="500"
            defaultValue="280"
            className="w-full accent-[#84EA53]"
          />
          <div className="mt-3 flex justify-between text-xs text-[#74736e]">
            <span>$0</span>
            <span>$500+</span>
          </div>
        </div>
      </div>
      <button
        onClick={onClose}
        className="mt-8 w-full rounded-xl bg-[#84EA53] py-3 text-sm font-bold"
      >
        Show 160 results
      </button>
    </aside>
  );
}
