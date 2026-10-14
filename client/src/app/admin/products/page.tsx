'use client';

import * as React from 'react';
import Link from 'next/link';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { Plus, Edit3, Trash2, Layers, Search, EyeOff, CheckCircle2 } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { formatCurrency } from '@/lib/utils/format-currency';
import type { Product } from '@/types';

export default function AdminProductsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [deleteTargetId, setDeleteTargetId] = React.useState<number | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-products', page, search],
    queryFn: () => catalogApi.getProducts({ page, limit: 10, search: search || undefined }),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => catalogApi.deleteProduct(id),
    onSuccess: () => {
      toast.success('Xóa sản phẩm thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-products'] });
      setDeleteTargetId(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Không thể xóa sản phẩm này');
    }
  });

  const columns: Column<Product>[] = [
    {
      header: 'Sản phẩm',
      cell: (product) => (
        <div className="flex items-center gap-3">
          {product.thumbnailUrl ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img src={product.thumbnailUrl} alt={product.defaultName} className="size-12 rounded-lg object-cover border border-[#EAE6DF]" />
          ) : (
            <div className="size-12 rounded-lg bg-neutral-100 flex items-center justify-center text-[10px] text-neutral-400">IMG</div>
          )}
          <div className="min-w-0 max-w-[250px]">
            <p className="font-semibold text-sm text-neutral-900 truncate" title={product.defaultName}>{product.defaultName}</p>
            <p className="text-[11px] text-neutral-500 mt-1">{product.brand?.defaultName || 'No Brand'}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'Danh mục',
      cell: (product) => <span className="text-xs text-neutral-600">{product.category?.defaultName || '---'}</span>,
    },
    {
      header: 'Giá bán cơ bản',
      cell: (product) => {
        const basePrice = product.variants?.[0]?.sellingPrice || product.variants?.[0]?.originalPrice || 0;
        return <span className="font-bold text-[#1D1F20] text-xs">{formatCurrency(basePrice as number)}</span>;
      },
    },
    {
      header: 'Kho',
      cell: (product) => {
        const totalStock = product.variants?.reduce((acc, v) => acc + v.stockQuantity, 0) || 0;
        return <span className="text-xs font-mono font-medium">{totalStock}</span>;
      },
    },
    {
      header: 'Trạng thái',
      cell: (product) => (
        <Badge variant={product.status === 'PUBLISHED' ? 'success' : 'secondary'} className="text-[10px]">
          {product.status === 'PUBLISHED' ? <CheckCircle2 className="size-3 mr-1" /> : <EyeOff className="size-3 mr-1" />}
          {product.status}
        </Badge>
      ),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (product) => (
        <div className="flex justify-end gap-1">
          <Link href={`/admin/products/${product.id}/variants`}>
            <button className="p-1.5 text-neutral-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition" title="Quản lý biến thể (Variants)">
              <Layers className="size-4" />
            </button>
          </Link>
          <Link href={`/admin/products/${product.id}/edit`}>
            <button className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition" title="Chỉnh sửa sản phẩm">
              <Edit3 className="size-4" />
            </button>
          </Link>
          <button 
            onClick={() => setDeleteTargetId(product.id)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition" 
            title="Xóa sản phẩm"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">CATALOG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Danh sách Sản phẩm</h1>
          <p className="text-xs text-neutral-500 mt-1">Quản lý mặt hàng, tồn kho và các biến thể phân loại (SKU).</p>
        </div>
        <Link href="/admin/products/new">
          <Button className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2 shrink-0">
            <Plus className="size-4" />
            <span>Thêm sản phẩm mới</span>
          </Button>
        </Link>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex justify-between items-center">
        <SearchInput 
          value={search} 
          onChange={(val) => { setSearch(val); setPage(1); }} 
          placeholder="Tìm theo tên sản phẩm hoặc mã SKU..." 
          className="w-full max-w-md"
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
        emptyMessage="Không có sản phẩm nào trong kho."
      />

      <ConfirmDialog
        open={deleteTargetId !== null}
        onOpenChange={(open) => !open && setDeleteTargetId(null)}
        title="Xóa sản phẩm"
        description="Bạn có chắc chắn muốn xóa sản phẩm này? Mọi biến thể (Variants) và tồn kho liên quan cũng sẽ bị xóa vĩnh viễn."
        variant="destructive"
        confirmText="Xóa vĩnh viễn"
        isLoading={deleteMutation.isPending}
        onConfirm={() => {
          if (deleteTargetId) deleteMutation.mutate(deleteTargetId);
        }}
      />
    </div>
  );
}