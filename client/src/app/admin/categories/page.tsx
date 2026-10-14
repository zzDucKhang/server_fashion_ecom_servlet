'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { Plus, Edit3, Trash2, Layers, CheckCircle2, EyeOff, FolderTree } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import type { Category } from '@/types';

export default function AdminCategoriesPage() {
  const queryClient = useQueryClient();
  const [search, setSearch] = React.useState('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingCategory, setEditingCategory] = React.useState<Category | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Category | null>(null);

  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      defaultName: '',
      logo: '',
      parentCategoryId: '',
    },
  });

  const { data, isLoading } = useQuery({
    queryKey: ['admin-categories'],
    queryFn: () => catalogApi.getCategories(),
  });

  const allCategories: Category[] = data?.data ?? [];

  // Create mutation
  const createMutation = useMutation({
    mutationFn: async (values: any) => {
      return catalogApi.createCategory({
        defaultName: values.defaultName.trim(),
        logo: values.logo?.trim() || null,
        parentCategoryId: values.parentCategoryId ? Number(values.parentCategoryId) : null,
      });
    },
    onSuccess: () => {
      toast.success('Thêm danh mục mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-categories'] });
      setIsCreateOpen(false);
      reset();
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Thêm danh mục thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async ({ id, values }: { id: number; values: any }) => {
      return catalogApi.updateCategory(id, {
        defaultName: values.defaultName.trim(),
        logo: values.logo?.trim() || null,
        parentCategoryId: values.parentCategoryId ? Number(values.parentCategoryId) : null,
      });
    },
    onSuccess: () => {
      toast.success('Cập nhật danh mục thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-categories'] });
      setEditingCategory(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật danh mục thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      return catalogApi.deleteCategory(id);
    },
    onSuccess: () => {
      toast.success('Đã xóa danh mục!');
      queryClient.invalidateQueries({ queryKey: ['admin-categories'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa danh mục thất bại');
    },
  });

  const handleOpenEdit = (cat: Category) => {
    setEditingCategory(cat);
    setValue('defaultName', cat.defaultName);
    setValue('logo', (cat as any).logo || cat.iconUrl || '');
    setValue('parentCategoryId', (cat.parentId || (cat as any).parentCategoryId || '').toString());
  };

  const filteredCategories = React.useMemo(() => {
    if (!search.trim()) return allCategories;
    const q = search.toLowerCase();
    return allCategories.filter((c) => c.defaultName.toLowerCase().includes(q));
  }, [allCategories, search]);

  const columns: Column<Category>[] = [
    {
      header: 'ID',
      className: 'w-16 text-xs text-neutral-400 font-mono',
      cell: (cat) => `#${cat.id}`,
    },
    {
      header: 'Tên danh mục',
      cell: (cat) => {
        const iconUrl = (cat as any).logo || cat.iconUrl;
        const isImageUrl = !!iconUrl && /^https?:\/\//i.test(iconUrl);

        return (
          <div className="flex items-center gap-3">
            <div className="size-8 shrink-0 overflow-hidden rounded-lg bg-neutral-100 flex items-center justify-center text-neutral-400">
              {isImageUrl && iconUrl ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img src={iconUrl} alt="" loading="lazy" className="size-full object-cover" />
              ) : iconUrl ? (
                iconUrl
              ) : (
                <Layers className="size-4" />
              )}
            </div>
            <div>
              <span className="font-semibold text-sm text-neutral-900">{cat.defaultName}</span>
              {(cat as any).logo && (
                <span className="block text-[10px] text-neutral-400 truncate max-w-xs">
                  {(cat as any).logo}
                </span>
              )}
            </div>
          </div>
        );
      },
    },
    {
      header: 'Danh mục cha',
      cell: (cat) => {
        const parentId = cat.parentId || (cat as any).parentCategoryId;
        const parent = allCategories.find((c) => c.id === parentId);
        return (
          <span className="text-xs text-neutral-600">
            {parent ? (
              <span className="font-medium text-neutral-800">{parent.defaultName}</span>
            ) : parentId ? (
              `ID: #${parentId}`
            ) : (
              <span className="text-neutral-400 italic">Gốc (Root)</span>
            )}
          </span>
        );
      },
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (cat) => (
        <div className="flex justify-end gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(cat)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Sửa danh mục"
          >
            <Edit3 className="size-4" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTarget(cat)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa danh mục"
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
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Danh mục</h1>
          <p className="text-xs text-neutral-500 mt-1">Cấu trúc phân loại sản phẩm hiển thị trên Website.</p>
        </div>
        <Button
          onClick={() => {
            reset({ defaultName: '', logo: '', parentCategoryId: '' });
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2"
        >
          <Plus className="size-4" /> Thêm Danh mục
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs">
        <SearchInput
          value={search}
          onChange={setSearch}
          placeholder="Tìm kiếm danh mục theo tên..."
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={filteredCategories}
        isLoading={isLoading}
        emptyMessage="Không có danh mục nào."
      />

      {/* Modal Thêm Danh mục */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Plus className="size-5 text-neutral-700" />
              Thêm danh mục mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => createMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Tên danh mục *
              </label>
              <input
                type="text"
                {...register('defaultName', { required: true })}
                placeholder="VD: Áo sơ mi nam, Quần jeans..."
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Logo / Icon URL (Tùy chọn)
              </label>
              <input
                type="text"
                {...register('logo')}
                placeholder="https://example.com/category-icon.png"
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
              />
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Danh mục cha (Parent Category)
              </label>
              <select
                {...register('parentCategoryId')}
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
              >
                <option value="">Không có (Danh mục gốc - Root)</option>
                {allCategories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.defaultName} (ID: #{c.id})
                  </option>
                ))}
              </select>
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
                {createMutation.isPending ? 'Đang thêm...' : 'Thêm danh mục'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Sửa Danh mục */}
      <Dialog open={!!editingCategory} onOpenChange={(open) => !open && setEditingCategory(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Sửa danh mục #{editingCategory?.id}
            </DialogTitle>
          </DialogHeader>

          {editingCategory && (
            <form
              onSubmit={handleSubmit((values) =>
                updateMutation.mutate({ id: editingCategory.id, values })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Tên danh mục *
                </label>
                <input
                  type="text"
                  {...register('defaultName', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
                />
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Logo / Icon URL (Tùy chọn)
                </label>
                <input
                  type="text"
                  {...register('logo')}
                  placeholder="https://example.com/icon.png"
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Danh mục cha (Parent Category)
                </label>
                <select
                  {...register('parentCategoryId')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                >
                  <option value="">Không có (Danh mục gốc - Root)</option>
                  {allCategories
                    .filter((c) => c.id !== editingCategory.id)
                    .map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.defaultName} (ID: #{c.id})
                      </option>
                    ))}
                </select>
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingCategory(null)}
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
        title={`Xóa danh mục "${deleteTarget?.defaultName}"?`}
        description="Bạn có chắc chắn muốn xóa danh mục này? Các sản phẩm thuộc danh mục có thể bị ảnh hưởng."
        confirmText="Xóa danh mục"
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