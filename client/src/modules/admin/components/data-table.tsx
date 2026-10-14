import * as React from 'react';
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table';
import { Skeleton } from '@/components/ui/skeleton';
import { Pagination } from '@/components/ui/pagination';

export interface Column<T> {
  header: string;
  accessorKey?: keyof T;
  className?: string;
  cell?: (item: T, index: number) => React.ReactNode;
}

interface GenericDataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  isLoading?: boolean;
  page?: number;
  totalPages?: number;
  totalItems?: number;
  onPageChange?: (page: number) => void;
  emptyMessage?: string;
}

export function DataTable<T extends { id: string | number }>({
  columns,
  data,
  isLoading,
  page,
  totalPages,
  totalItems,
  onPageChange,
  emptyMessage = 'Không tìm thấy dữ liệu phù hợp.',
}: GenericDataTableProps<T>) {
  return (
    <div className="space-y-4">
      <div className="rounded-2xl border border-[#EAE6DF] bg-white overflow-hidden shadow-xs">
        <Table>
          <TableHeader>
            <TableRow>
              {columns.map((col, idx) => (
                <TableHead key={idx} className={col.className}>
                  {col.header}
                </TableHead>
              ))}
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              Array.from({ length: 5 }).map((_, rIdx) => (
                <TableRow key={`skeleton-row-${rIdx}`}>
                  {columns.map((_, cIdx) => (
                    <TableCell key={`skeleton-cell-${cIdx}`}>
                      <Skeleton className="h-5 w-full rounded" />
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : !Array.isArray(data) || data.length === 0 ? (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-32 text-center text-xs text-neutral-500"
                >
                  {emptyMessage}
                </TableCell>
              </TableRow>
            ) : (
              data.map((item, index) => (
                <TableRow key={item.id} className="transition-colors hover:bg-neutral-50/70">
                  {columns.map((col, cIdx) => (
                    <TableCell key={cIdx} className={col.className}>
                      {col.cell
                        ? col.cell(item, index)
                        : col.accessorKey
                        ? String(item[col.accessorKey] ?? '')
                        : null}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {totalPages !== undefined && page !== undefined && onPageChange && (
        <Pagination
          page={page}
          totalPages={totalPages}
          totalItems={totalItems}
          onPageChange={onPageChange}
        />
      )}
    </div>
  );
}

