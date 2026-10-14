'use client';

import * as React from 'react';
import { Search, X } from 'lucide-react';
import { cn } from '@/lib/utils';

interface SearchInputProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'onChange'> {
  value?: string;
  onChange?: (value: string) => void;
  debounceMs?: number;
}

export function SearchInput({
  value: initialValue = '',
  onChange,
  debounceMs = 300,
  className,
  placeholder = 'Tìm kiếm...',
  ...props
}: SearchInputProps) {
  const [searchTerm, setSearchTerm] = React.useState(initialValue);

  React.useEffect(() => {
    setSearchTerm(initialValue);
  }, [initialValue]);

  React.useEffect(() => {
    const timer = setTimeout(() => {
      onChange?.(searchTerm);
    }, debounceMs);

    return () => clearTimeout(timer);
  }, [searchTerm, debounceMs, onChange]);

  return (
    <div className={cn('relative flex items-center w-full max-w-sm', className)}>
      <Search className="absolute left-3 size-4 text-neutral-400 pointer-events-none" />
      <input
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder={placeholder}
        className="w-full rounded-xl border border-[#EAE6DF] bg-white pl-9 pr-9 py-2 text-sm text-neutral-800 placeholder:text-neutral-400 outline-none transition focus:border-neutral-900 focus:ring-1 focus:ring-neutral-900/10 shadow-xs"
        {...props}
      />
      {searchTerm && (
        <button
          type="button"
          onClick={() => {
            setSearchTerm('');
            onChange?.('');
          }}
          className="absolute right-3 text-neutral-400 hover:text-neutral-700 focus:outline-none"
        >
          <X className="size-3.5" />
        </button>
      )}
    </div>
  );
}

