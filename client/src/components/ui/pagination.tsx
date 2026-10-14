import * as React from 'react';
import { ChevronLeft, ChevronRight, MoreHorizontal } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from './button';

interface PaginationProps {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  className?: string;
  totalItems?: number;
}

export function Pagination({
  page,
  totalPages,
  onPageChange,
  className,
  totalItems,
}: PaginationProps) {
  if (totalPages <= 1 && !totalItems) return null;

  // Generate page numbers to show
  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
    } else {
      pages.push(1);
      if (page > 3) pages.push('ellipsis-start');

      const start = Math.max(2, page - 1);
      const end = Math.min(totalPages - 1, page + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (page < totalPages - 2) pages.push('ellipsis-end');
      pages.push(totalPages);
    }
    return pages;
  };

  return (
    <div className={cn('flex flex-col sm:flex-row items-center justify-between gap-4 py-3', className)}>
      {totalItems !== undefined && (
        <p className="text-xs text-neutral-500">
          Hiển thị trang <span className="font-semibold text-neutral-800">{page}</span> / <span className="font-semibold text-neutral-800">{totalPages || 1}</span> (Tổng cộng <span className="font-semibold text-neutral-800">{totalItems}</span> bản ghi)
        </p>
      )}

      <div className="flex items-center gap-1.5 ml-auto">
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(page - 1)}
          disabled={page <= 1}
          className="rounded-lg h-8 px-2 text-xs"
        >
          <ChevronLeft className="size-4" />
          <span className="hidden sm:inline">Trước</span>
        </Button>

        {getPageNumbers().map((p, idx) => {
          if (typeof p === 'string') {
            return (
              <span key={`ellipsis-${idx}`} className="px-2 text-neutral-400">
                <MoreHorizontal className="size-4" />
              </span>
            );
          }
          return (
            <Button
              key={p}
              variant={page === p ? 'default' : 'outline'}
              size="sm"
              onClick={() => onPageChange(p)}
              className={cn(
                'size-8 p-0 text-xs rounded-lg font-medium',
                page === p
                  ? 'bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800'
                  : 'text-neutral-700 hover:bg-neutral-100 border-[#EAE6DF]'
              )}
            >
              {p}
            </Button>
          );
        })}

        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(page + 1)}
          disabled={page >= totalPages}
          className="rounded-lg h-8 px-2 text-xs"
        >
          <span className="hidden sm:inline">Sau</span>
          <ChevronRight className="size-4" />
        </Button>
      </div>
    </div>
  );
}

