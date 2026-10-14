'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { Boxes, AlertTriangle, ArrowRightLeft, FileDown } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import type { Product } from '@/types';

export default function AdminInventoryPage() {
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-inventory', page, search],
    queryFn: () => catalogApi.getProducts({ page, limit: 10, search: search || undefined }),
  });

  const columns: Column<Product>[] = [
    {
      header: 'Sản phẩm',
      cell: (product) => (
        <div className="flex items-center gap-3">
          <div className="min-w-0 max-w-[200px]">
            <p className="font-semibold text-sm text-neutral-900 truncate" title={product.defaultName}>{product.defaultName}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'Biến thể (SKU)',
      cell: (product) => (
        <div className="space-y-1">
          {product.variants?.slice(0, 3).map(v => (
            <div key={v.id} className="text-[11px] font-mono text-neutral-600 bg-neutral-50 px-2 py-0.5 rounded border border-[#EAE6DF] inline-block mr-1">
              {v.sku}
            </div>
          ))}
          {(product.variants?.length || 0) > 3 && <span className="text-[10px] text-neutral-400">+{product.variants!.length - 3}</span>}
        </div>
      ),
    },
    {
      header: 'Tổng Tồn kho',
      cell: (product) => {
        const total = product.variants?.reduce((acc, v) => acc + v.stockQuantity, 0) || 0;
        return (
          <span className={`font-mono text-sm font-bold ${total <= 5 ? 'text-rose-600' : 'text-neutral-900'}`}>
            {total.toLocaleString()}
          </span>
        );
      },
    },
    {
      header: 'Cảnh báo',
      cell: (product) => {
        const total = product.variants?.reduce((acc, v) => acc + v.stockQuantity, 0) || 0;
        if (total === 0) return <Badge variant="destructive" className="text-[10px]"><AlertTriangle className="size-3 mr-1"/> Hết hàng</Badge>;
        if (total <= 10) return <Badge variant="warning" className="text-[10px]"><AlertTriangle className="size-3 mr-1"/> Sắp hết</Badge>;
        return <Badge variant="success" className="text-[10px]">Đủ hàng</Badge>;
      },
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (product) => (
        <Link href={`/admin/products/${product.id}/variants`}>
          <Button variant="outline" size="sm" className="h-8 text-xs border-[#EAE6DF]">
            <ArrowRightLeft className="size-3 mr-1.5" /> Điều chỉnh
          </Button>
        </Link>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">VẬN HÀNH</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Kho hàng</h1>
          <p className="text-xs text-neutral-500 mt-1">Kiểm soát số lượng tồn kho và cảnh báo hết hàng.</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" className="h-10 px-4 rounded-xl border-[#EAE6DF]">
            <FileDown className="size-4 mr-2" /> Xuất báo cáo
          </Button>
          <Link href="/admin/inventory/import">
            <Button className="h-10 px-4 rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white">
              Nhập kho
            </Button>
          </Link>
        </div>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs">
        <SearchInput 
          value={search} 
          onChange={(val) => { setSearch(val); setPage(1); }} 
          placeholder="Tìm theo tên sản phẩm hoặc mã SKU..." 
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={(data?.data as any) || []}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy dữ liệu kho hàng."
      />
    </div>
  );
}