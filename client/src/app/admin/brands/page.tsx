'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { Plus, Edit3, Trash2, Sparkles, Image as ImageIcon } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import type { Brand } from '@/types';

export default function AdminBrandsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingBrand, setEditingBrand] = React.useState<Brand | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Brand | null>(null);

  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      defaultName: '',
      logo: '',
    },
  });

  const { data, isLoading } = useQuery({
    queryKey: ['admin-brands', page, search],
    queryFn: () => catalogApi.getBrands({ page, limit: 10, search: search || undefined }),
  });

  // Create mutation
  const createMutation = useMutation({
    mutationFn: async (values: any) => {
      return catalogApi.createBrand({
        defaultName: values.defaultName.trim(),
        logo: values.logo?.trim() || undefined,
      });
    },
    onSuccess: () => {
      toast.success('Thêm thương hiệu mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-brands'] });
      setIsCreateOpen(false);
      reset();
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Thêm thương hiệu thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async ({ id, values }: { id: number; values: any }) => {
      return catalogApi.updateBrand(id, {
        defaultName: values.defaultName.trim(),
        logo: values.logo?.trim() || undefined,
      });
    },
    onSuccess: () => {
      toast.success('Cập nhật thương hiệu thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-brands'] });
      setEditingBrand(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật thương hiệu thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      return catalogApi.deleteBrand(id);
    },
    onSuccess: () => {
      toast.success('Đã xóa thương hiệu!');
      queryClient.invalidateQueries({ queryKey: ['admin-brands'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa thương hiệu thất bại');
    },
  });

  const handleOpenEdit = (brand: Brand) => {
    setEditingBrand(brand);
    setValue('defaultName', brand.defaultName);
    setValue('logo', (brand as any).logo || brand.logoUrl || '');
  };

  const columns: Column<Brand>[] = [
    {
      header: 'ID',
      className: 'w-16 text-xs text-neutral-400 font-mono',
      cell: (brand) => `#${brand.id}`,
    },
    {
      header: 'Thương hiệu',
      cell: (brand) => {
        const logoUrl = (brand as any).logo || brand.logoUrl;
        return (
          <div className="flex items-center gap-3">
            {logoUrl ? (
              // eslint-disable-next-line @next/next/no-img-element
              <img
                src={logoUrl}
                alt={brand.defaultName}
                className="size-10 rounded-lg object-contain border border-[#EAE6DF] bg-white p-1"
              />
            ) : (
              <div className="size-10 rounded-lg bg-neutral-100 flex items-center justify-center text-neutral-400">
                <ImageIcon className="size-4" />
              </div>
            )}
            <div>
              <span className="font-semibold text-sm text-neutral-900">{brand.defaultName}</span>
              {logoUrl && (
                <span className="block text-[10px] text-neutral-400 truncate max-w-xs">
                  {logoUrl}
                </span>
              )}
            </div>
          </div>
        );
      },
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (brand) => (
        <div className="flex justify-end gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(brand)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Sửa thương hiệu"
          >
            <Edit3 className="size-4" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTarget(brand)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa thương hiệu"
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
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Thương hiệu</h1>
          <p className="text-xs text-neutral-500 mt-1">Quản lý danh sách các thương hiệu sản phẩm đang kinh doanh.</p>
        </div>
        <Button
          onClick={() => {
            reset({ defaultName: '', logo: '' });
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2"
        >
          <Plus className="size-4" /> Thêm Thương hiệu
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex justify-between items-center">
        <SearchInput 
          value={search} 
          onChange={(val) => { setSearch(val); setPage(1); }} 
          placeholder="Tìm kiếm thương hiệu..." 
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
        emptyMessage="Không tìm thấy thương hiệu nào."
      />

      {/* Modal Thêm Thương hiệu */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Plus className="size-5 text-neutral-700" />
              Thêm thương hiệu mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => createMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Tên thương hiệu *
              </label>
              <input
                type="text"
                {...register('defaultName', { required: true })}
                placeholder="VD: Nike, Adidas, Zara, Uniqlo..."
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Logo URL (Tùy chọn)
              </label>
              <input
                type="text"
                {...register('logo')}
                placeholder="https://example.com/logo.png"
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
              />
            </div>

            <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsCreateOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="rounded-xl bg-[#1D1F20] text-white"
              >
                {createMutation.isPending ? 'Đang thêm...' : 'Thêm thương hiệu'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Sửa Thương hiệu */}
      <Dialog open={!!editingBrand} onOpenChange={(open) => !open && setEditingBrand(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Sửa thương hiệu #{editingBrand?.id}
            </DialogTitle>
          </DialogHeader>

          {editingBrand && (
            <form
              onSubmit={handleSubmit((values) =>
                updateMutation.mutate({ id: editingBrand.id, values })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Tên thương hiệu *
                </label>
                <input
                  type="text"
                  {...register('defaultName', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
                />
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Logo URL (Tùy chọn)
                </label>
                <input
                  type="text"
                  {...register('logo')}
                  placeholder="https://example.com/logo.png"
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingBrand(null)}
                  className="rounded-xl border-[#EAE6DF]"
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  disabled={updateMutation.isPending}
                  className="rounded-xl bg-[#1D1F20] text-white"
                >
                  {updateMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                </Button>
              </DialogFooter>
            </form>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title={`Xóa thương hiệu "${deleteTarget?.defaultName}"?`}
        description="Bạn có chắc chắn muốn xóa thương hiệu này? Các sản phẩm thuộc thương hiệu có thể bị ảnh hưởng."
        confirmText="Xóa thương hiệu"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (deleteTarget) {
            deleteMutation.mutate(deleteTarget.id);
          }
        }}
      />
    </div>
  );
}