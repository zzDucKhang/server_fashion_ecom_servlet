'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { Tag as TagIcon, Plus, Edit3, Trash2, Hash } from 'lucide-react';
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
import type { Tag } from '@/types';

export default function AdminTagsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingTag, setEditingTag] = React.useState<Tag | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Tag | null>(null);

  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      name: '',
    },
  });

  const { data, isLoading } = useQuery({
    queryKey: ['admin-tags', page, search],
    queryFn: () => catalogApi.getTags({ page, limit: 10, search: search || undefined }),
  });

  // Create mutation
  const createMutation = useMutation({
    mutationFn: async (values: { name: string }) => {
      return catalogApi.createTag({ name: values.name.trim() });
    },
    onSuccess: () => {
      toast.success('Thêm thẻ (tag) mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-tags'] });
      setIsCreateOpen(false);
      reset();
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Thêm tag thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async ({ id, name }: { id: number; name: string }) => {
      return catalogApi.updateTag(id, { name: name.trim() });
    },
    onSuccess: () => {
      toast.success('Cập nhật thẻ tag thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-tags'] });
      setEditingTag(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật tag thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      return catalogApi.deleteTag(id);
    },
    onSuccess: () => {
      toast.success('Đã xóa thẻ tag!');
      queryClient.invalidateQueries({ queryKey: ['admin-tags'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa tag thất bại');
    },
  });

  const handleOpenEdit = (t: Tag) => {
    setEditingTag(t);
    setValue('name', t.name);
  };

  const tags: Tag[] = (data?.data as any) || [];

  const columns: Column<Tag>[] = [
    {
      header: 'ID',
      className: 'w-16 text-xs text-neutral-400 font-mono',
      cell: (t) => `#${t.id}`,
    },
    {
      header: 'Tên thẻ (Tag)',
      cell: (t) => (
        <div className="flex items-center gap-2">
          <span className="flex size-7 items-center justify-center rounded-lg bg-neutral-100 text-neutral-600">
            <Hash className="size-3.5" />
          </span>
          <span className="font-semibold text-xs text-neutral-900 bg-neutral-100/80 px-2.5 py-1 rounded-md border border-[#EAE6DF]">
            {t.name}
          </span>
        </div>
      ),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (t) => (
        <div className="flex justify-end gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(t)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Sửa tag"
          >
            <Edit3 className="size-4" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTarget(t)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa tag"
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
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Thẻ sản phẩm (Tags)
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Quản lý các hashtag, nhãn tìm kiếm giúp phân loại và lọc sản phẩm nhanh chóng.
          </p>
        </div>
        <Button
          onClick={() => {
            reset({ name: '' });
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2"
        >
          <Plus className="size-4" /> Thêm Tag mới
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex justify-between items-center">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm kiếm thẻ tag..."
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={tags}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy thẻ tag nào."
      />

      {/* Modal Thêm Tag */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Plus className="size-5 text-neutral-700" />
              Thêm thẻ tag mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => createMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Tên thẻ tag *
              </label>
              <input
                type="text"
                {...register('name', { required: true })}
                placeholder="VD: Trending, Summer, Bestseller, Eco-friendly..."
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
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
                {createMutation.isPending ? 'Đang thêm...' : 'Thêm Tag'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Sửa Tag */}
      <Dialog open={!!editingTag} onOpenChange={(open) => !open && setEditingTag(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Sửa thẻ tag #{editingTag?.id}
            </DialogTitle>
          </DialogHeader>

          {editingTag && (
            <form
              onSubmit={handleSubmit((values) =>
                updateMutation.mutate({ id: editingTag.id, name: values.name })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Tên thẻ tag *
                </label>
                <input
                  type="text"
                  {...register('name', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
                />
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingTag(null)}
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
        title={`Xóa thẻ tag "${deleteTarget?.name}"?`}
        description="Bạn có chắc chắn muốn xóa thẻ tag này? Các sản phẩm được gắn thẻ này sẽ không còn hiển thị tag này nữa."
        confirmText="Xóa tag"
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
